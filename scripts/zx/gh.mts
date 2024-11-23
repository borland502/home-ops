#!/usr/bin/env -S npx tsx

import { $, argv, cd, os, fs, ProcessOutput, syncProcessCwd } from "zx";
import config from "config";
import path from "node:path";
import { Dirent, type PathLike } from "node:fs";
import { readdir } from "node:fs/promises";

// NOTE: This script requires NODE_CONFIG_DIR to be set (or unset) outside the script.
// $.env.NODE_CONFIG_DIR = './config';

$.shell = config.get("zx.shell");
$.verbose = config.get("zx.verbose");

syncProcessCwd(true);

// Get the GitHub organization from the config file
const ghOrg = config.get("gh.org");
const ghLimit = config.get("gh.limit");
const rootDir = path.resolve(os.homedir(), config.get("gh.rootDir"));

async function processRepos(
  action: (repo: string) => Promise<void>,
  basePath: PathLike,
): Promise<void> {
  if (!fs.existsSync(basePath)) {
    console.error(`The base path ${basePath} does not exist`);
    return;
  }

  const repos = await getRepoPaths(basePath);

  for (const repo of repos) {
    console.log(`Processing ${repo}...`);
    await action(repo.toString())
      .then(() => {
        const actionName = action.name.replace(/^_/, "").replace(/Repo$/, "");
        console.log(`${actionName} for ${repo} succeeded`);
      })
      .catch((error) => {
        console.error(`Failed to process ${repo}:`, error);
      });
  }
}

async function _yarnInstall(repo: PathLike): Promise<void> {
  console.log(`Installing dependencies for ${repo}...`);
  await $`yarn`;
}

async function _stashRepo(repo: PathLike): Promise<void> {
  console.log(`Stashing ${repo}...`);
  await $`git stash`;
}

async function _checkoutRepo(repo: PathLike): Promise<void> {
  console.log(`Checking out ${repo}...`);
  const mainDate = await $`git log -1 --format=%ct main`.catch(() => null);
  const developDate = await $`git log -1 --format=%ct develop`.catch(
    () => null,
  );

  if (mainDate && (!developDate || mainDate > developDate)) {
    await $`git checkout main`;
    console.log("Checked out main branch");
  } else if (developDate) {
    await $`git checkout develop`;
    console.log("Checked out develop branch");
  } else {
    throw new Error("Failed to checkout either the develop or main branch");
  }
}

async function _bootstrapRepo(repo: PathLike): Promise<void> {
  const pwd = await $`pwd`;
  console.log(`Bootstrapping ${repo}... in ${pwd}`);
  await $`yarn bootstrap iqies`;
}

async function _pullRepo(repo: PathLike): Promise<void> {
  console.log(`Pulling ${repo}...`);
  await $`git pull`;
}

async function isLoggedIntoQnet(): Promise<boolean> {
  return (await $`gh auth status --active | grep -qo 'qnet'`.exitCode) === 0;
}

// Recursively search for folders containing a .git directory
async function findGitRepos(basePath: string): Promise<PathLike[]> {
  const dirFilter = (dir: Dirent) =>
    dir.isDirectory() && dir.name !== "node_modules";

  const files = await readdir(basePath, { withFileTypes: true });

  const nestedRepos = await Promise.all(
    files.filter(dirFilter).map(async (dir) => {
      const dirPath = path.join(basePath, dir.name);
      return dir.name === ".git" ? [basePath] : await findGitRepos(dirPath);
    }),
  );
  // flatten the nested arrays and convert to absolute paths
  return nestedRepos.flat();
}

async function getAllRepos(): Promise<string[]> {
  if (!(await isLoggedIntoQnet())) {
    console.error("You need to be logged into qnet to fetch repositories");
    return [];
  }

  let results: string[] = [];
  // Fetch all repositories the user has access to
  await $`gh repo list ${ghOrg} --json name --limit ${ghLimit} --no-archived --jq '.[].name'`
    .then((result: ProcessOutput) => {
      results = result.lines();
    })
    .catch((error: ProcessOutput) => {
      console.error(error);
      return results;
    });

  if (!!results && results.length > 0) {
    return results;
  }

  return [];
}

async function getRepoPaths(basePath: PathLike): Promise<PathLike[]> {
  const repos = await getAllRepos();
  if (repos.length <= 0) {
    console.error("No repositories found");
    return [];
  }

  const existingRepos = await findGitRepos(basePath.toString());
  if (!!existingRepos && existingRepos.length <= 0) {
    console.error(
      `The directory ${basePath} does not exist or is not a valid git repository`,
    );
    return [];
  }

  const existingRepoNames = existingRepos.map((repoPath) =>
    path.basename(repoPath.toString()),
  );
  const missingRepos = existingRepoNames.filter(
    (repoName) => !repos.includes(repoName),
  );

  if (missingRepos.length === 0) {
    return existingRepos;
  } else {
    console.error(`There are missing repos: ${missingRepos.join(", ")}}`);
    return [];
  }
}

async function getRepoUrls(basePath: PathLike): Promise<URL[]> {
  const repos = await getRepoPaths(basePath);
  const repoUrls: URL[] = [];

  for (const repo of repos) {
    cd(repo.toString());
    let remoteUrl: URL;
    try {
      const result = await $`git config --get remote.origin.url`;
      remoteUrl = new URL(result.stdout.trim());
    } catch (error) {
      console.error(`Failed to get remote URL for ${repo}:`, error);
      continue;
    }

    if (remoteUrl) {
      repoUrls.push(remoteUrl);
    }
  }

  return repoUrls;
}

async function printClosedPrs(jiraNum: string, author: string): Promise<void> {
  const prs =
    await $`gh search prs --author '${author}' --state closed ${jiraNum} --json url --jq '.[].url'`;

  for (const pr of prs.lines()) {
    $`
    gh pr view ${pr} --json number,title,files,url --template \
    '{{printf "#%v" .number}} {{hyperlink .url .title}}
    {{range .files}}
    Additions: {{.additions}}
    Deletions: {{.deletions}}
    Path: {{.path}}
    {{end}}'
    `.then(
      (result: ProcessOutput) => {
        console.log(result.lines().join("\n"));
      },
      (error: ProcessOutput) => {
        console.error(error);
      },
    );
  }
}

const command = argv._[0];

let basePath = rootDir;
if (argv.basePath) {
  if (path.isAbsolute(argv.basePath)) {
    basePath = argv.basePath;
  } else {
    // remove any leading ./ or ../ from the path
    const argPath = argv.basePath.replace(/^(\.\/|\.\.\/)/, "");
    basePath = path.resolve(rootDir, argPath);
  }
  cd(basePath.toString());
}

async function _configureRepo(repo: PathLike) {
  console.log(`Configuring ${repo}...`);
  await $`yarn run config-local iqies`;
}

if (command === "get-repos") {
  const all = argv.all;
  const basePath = argv.basePath;
  if (all) {
    const repos = await getAllRepos();
    console.log(repos.join("\n"));
    process.exit(0);
  } else if (!basePath) {
    console.error("Either --all or --basePath arguments are required");
    process.exit(1);
  }
  const repos = await getRepoPaths(basePath);
  console.log(repos.join("\n"));
} else if (command === "get-repo-paths") {
  const basePath = argv.basePath;
  if (!basePath) {
    console.error("--basePath argument is required");
    process.exit(1);
  }
  const repoPaths = await getRepoPaths(basePath);
  console.log(repoPaths.join("\n"));
} else if (command === "get-repo-urls") {
  const basePath = argv.basePath;
  if (!basePath) {
    console.error("--basePath argument is required");
    process.exit(1);
  }
  const repoUrls = await getRepoUrls(basePath);
  console.log(repoUrls.map((url) => url.toString()).join("\n"));
} else if (command === "update-repos") {
  await processRepos(async (repo: PathLike) => {
    // remove any leading ./ or ../ from the path
    const argPath = repo.toString().replace(/^(\.\/|\.\.\/)/, "");
    const repoPath = path.normalize(path.join(rootDir, argPath));
    const devPath = path.resolve(repoPath, "../..");

    // several automation landmines launch learna, husky, etc with a post checkout hook
    cd(repoPath);
    await _yarnInstall(repo);
    await _stashRepo(repo);
    await _checkoutRepo(repo);
    await _pullRepo(repo);

    cd(devPath);
    await _bootstrapRepo(repo);
    await _configureRepo(repo);
  }, argv.basePath);
} else if (command === "print-closed-prs") {
  const jiraNum = argv.jiraNum;
  const author = argv.author;
  if (!jiraNum || !author) {
    console.error("Both jiraNum and author arguments are required");
    process.exit(1);
  }
  await printClosedPrs(jiraNum, author);
} else {
  console.error("Unknown command");
  process.exit(1);
}
