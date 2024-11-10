#!/usr/bin/env -S npx tsx

import { $, ProcessOutput, argv, fs, cd } from "zx"
import { PathLike } from "fs";
import config from "config"
import path from 'path';
import { Url } from "url";

// NOTE: This script requires NODE_CONFIG_DIR to be set (or unset) outside the script.
// $.env.NODE_CONFIG_DIR = './config';

$.shell = config.get('zx.shell');
$.verbose = config.get('zx.verbose');

// Get the GitHub organization from the config file
const ghOrg = config.get('gh.org');
const ghLimit = config.get('gh.limit');

async function processRepos(action: (repo: string) => Promise<void>, basePath: PathLike): Promise<void> {
  if (!fs.existsSync(basePath)) {
    console.error(`The base path ${basePath} does not exist`);
    return;
  }

  const repos = await getRepoPaths(basePath);

  for (const repo of repos) {
    console.log(`Processing ${repo}...`);
    await action(repo.toString()).then(() => {
      const actionName = action.name.replace(/^_/, '').replace(/Repo$/, '');
      console.log(`${actionName} for ${repo} succeeded`);
    }).catch((error) => {
      console.error(`Failed to process ${repo}:`, error);
    });
  }
}

async function _stashRepo(repo: string): Promise<void> {
  console.log(`Stashing ${repo}...`);
  // await $`git stash --all`;
}

async function _pullRepo(repo: string): Promise<void> {
  console.log(`Pulling ${repo}...`);
  // await $`git pull`;
}

    // Recursively search for folders containing a .git directory
async function findGitRepos(basePath: PathLike): Promise<PathLike[]> {
  let results: string[] = [];
  const list = await fs.readdir(basePath, { withFileTypes: true });
  for (const file of list) {
    const filePath = path.join(basePath.toString(), file.name);
    if (file.isDirectory()) {
      if (file.name === '.git') {
        results.push(basePath.toString());
      } else {
        results = results.concat((await findGitRepos(filePath)).map(p => p.toString()));
      }
    }
  }

  return results;
}

async function getAllRepos(): Promise<string[]> {
  let results: string[] = [];
  // Fetch all repositories the user has access to
  await $`gh repo list ${ghOrg} --json name --limit ${ghLimit} --no-archived --jq '.[].name'`.then((result: ProcessOutput) => {
    results = result.lines();
  }).catch((error: ProcessOutput) => {
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

  // Verify that the directory exists and is a valid git repository
  const existingRepos = await findGitRepos(basePath);
  if (!!existingRepos && existingRepos.length <= 0) {
    console.error(`The directory ${basePath} does not exist or is not a valid git repository`);
    return [];
  }

  const existingRepoNames = existingRepos.map(repoPath => path.basename(repoPath.toString()));
  const missingRepos = existingRepoNames.filter(repoName => !repos.includes(repoName));
  
  if (missingRepos.length === 0) {
    return existingRepos;
  } else {
    console.error(`There are missing repos: ${missingRepos.join(', ')}}`);
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
  const prs = await $`gh search prs --author '${author}' --state closed ${jiraNum} --json url --jq '.[].url'`;

  for (const pr of prs.lines()) {
    $`
    gh pr view ${pr} --json number,title,files,url --template \
    '{{printf "#%v" .number}} {{hyperlink .url .title}}
    {{range .files}}
    Additions: {{.additions}}
    Deletions: {{.deletions}}
    Path: {{.path}}
    {{end}}'
    `.then((result: ProcessOutput) => {
      console.log(result.lines().join('\n'));
    }, (error: ProcessOutput) => {
      console.error(error);
    });
  }
}

const command = argv._[0];

if (command === 'get-repos') {
  const all = argv.all;
  const basePath = argv.basePath;
  if (all) {
    const repos = await getAllRepos();
    console.log(repos.join('\n'));
    process.exit(0);
  } else if (!basePath) {
    console.error('Either --all or --basePath arguments are required');
    process.exit(1);
  }
  const repos = await getRepoPaths(basePath);
  console.log(repos.join('\n'));
} else if (command === 'get-repo-paths') {
  const basePath = argv.basePath;
  if (!basePath) {
    console.error('--basePath argument is required');
    process.exit(1);
  }
  const repoPaths = await getRepoPaths(basePath);
  console.log(repoPaths.join('\n'));
} else if (command === 'get-repo-urls') {
  const basePath = argv.basePath;
  if (!basePath) {
    console.error('--basePath argument is required');
    process.exit(1);
  }
  const repoUrls = await getRepoUrls(basePath);
  console.log(repoUrls.map(url => url.toString()).join('\n'));
} else if (command === 'update-repos') {
  await processRepos(async (repo: string) => {
    await _stashRepo(repo);
    await _pullRepo(repo);
  }, argv.basePath);
} else if (command === 'print-closed-prs') {
  const jiraNum = argv.jiraNum;
  const author = argv.author;
  if (!jiraNum || !author) {
    console.error('Both jiraNum and author arguments are required');
    process.exit(1);
  }
  await printClosedPrs(jiraNum, author);
} else {
  console.error('Unknown command');
  process.exit(1);
}


