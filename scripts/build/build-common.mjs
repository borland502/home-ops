import { transformHookPlugin } from "esbuild-plugin-transform-hook";
import esbuildResolvePlugin from "esbuild-plugin-resolve";
import copy from "esbuild-plugin-copy-watch";
import path from "node:path";
import { cwd } from "process";
import extractHelpersPlugin from "esbuild-plugin-extract-helpers";
import { injectFile } from "esbuild-plugin-utils";
import { argv, glob } from "zx";

const absWorkingDir = cwd().toString();

const entry = argv.entry || "*.mts";
const entries = argv.entry.split(/:\s?/);

console.log("entries:", entries);
const entryPoints = entry.includes("*")
  ? await glob(entries, {
      absolute: false,
      onlyFiles: true,
      absWorkingDir,
      root: absWorkingDir,
    })
  : entries.map((p) =>
      path.relative(absWorkingDir, path.resolve(absWorkingDir, p))
    );

console.log("entries:", entryPoints);

const plugins = [
  esbuildResolvePlugin({
    zx: path.resolve(absWorkingDir, "node_modules/zx/build"),
  }),
  copy({
    paths: [{ from: "./config/*", to: "config/" }],
  }),
  transformHookPlugin({
    hooks: [
      {
        on: "end",
        pattern: entryPointsToRegexp(entryPoints),
        transform(contents) {
          const extras = [
            // https://github.com/evanw/esbuild/issues/1633
            contents.includes("import_meta")
              ? "./scripts/import-meta-url.polyfill.mjs"
              : "",

            //https://github.com/evanw/esbuild/issues/1921
            // p.includes('vendor') ? './scripts/require.polyfill.js' : '',
          ].filter(Boolean);
          return injectFile(contents, ...extras);
        },
      },
      {
        on: "end",
        pattern: entryPointsToRegexp(entryPoints),
        transform(contents) {
          return contents
            .toString()
            .replaceAll("import.meta.url", "import_meta_url")
            .replaceAll("import_meta.url", "import_meta_url")
            .replaceAll('"node:', '"')
            .replaceAll(
              'require("stream/promises")',
              'require("stream").promises'
            )
            .replaceAll('require("fs/promises")', 'require("fs").promises')
            .replaceAll("}).prototype", "}).prototype || {}")
            .replace(/DISABLE_NODE_FETCH_NATIVE_WARN/, ($0) => `${$0} || true`)
            .replace(
              /\/\/ Annotate the CommonJS export names for ESM import in node:/,
              ($0) => `/* c8 ignore next 100 */\n${$0}`
            );
        },
      },
    ],
  }),
  extractHelpersPlugin({
    cwd: "dist",
    include: /\.cjs/,
  }),
];

export const cjsConfig = {
  absWorkingDir: absWorkingDir,
  external: ["deno", "zx"],
  bundle: true,
  minify: false,
  sourcemap: true,
  sourcesContent: true,
  platform: "node",
  target: "ES2020",
  format: "iife",
  plugins: plugins,
};

export const backendConfig = {
  ...cjsConfig,
  entryPoints: ["apps/backend/src/main.mts"],
  outfile: "dist/apps/backend/main.cjs",
  tsconfig: "apps/backend/tsconfig.app.json",
};

export const logConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/log/src/index.mts"],
  outfile: "dist/libs/shared/log/index.cjs",
  tsconfig: "libs/shared/log/tsconfig.lib.json",
};

export const zxUtilsConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/zx-utils/src/index.mts"],
  outfile: "dist/libs/shared/zx-utils/index.cjs",
  tsconfig: "libs/shared/zx-utils/tsconfig.lib.json",
};

export const utilsConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/utils/src/index.mts"],
  outfile: "dist/libs/shared/utils/index.cjs",
  tsconfig: "libs/shared/utils/tsconfig.lib.json",
};

export const pkgInstallConfig = {
  ...cjsConfig,
  entryPoints: ["libs/shared/pkg-install/src/index.mts"],
  outfile: "dist/libs/shared/pkg-install/index.cjs",
  tsconfig: "libs/shared/pkg-install/tsconfig.lib.json",
};

function entryPointsToRegexp(entryPoints) {
  return new RegExp(
    `(${entryPoints.map((e) => path.parse(e).name).join("|")})\\.cjs$`
  );
}
