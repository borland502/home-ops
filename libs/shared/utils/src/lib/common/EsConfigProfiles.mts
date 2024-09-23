export const cjsConfig = {
  external: ["deno", "zx", "pg-hstore","crypto","node:*","fsevents"],
  bundle: true,
  minify: false,
  sourcemap: true,
  sourcesContent: true,
  platform: "node",
  target: "ES2022",
  format: "iife"
};

export const backendConfig = {
  ...cjsConfig,
  external: ["deno", "zx", "pg-hstore", "morgan", "express", "express-async-errors", "express-jwt", "jsonwebtoken",
    "bcryptjs", "cors", "helmet", "cookie-parser", "cookie-signature","node:*","fsevents", "config"],
  // https://github.com/evanw/esbuild/issues/1921#issuecomment-1152991694
  banner: {
    js: "import { createRequire } from 'module';const require = createRequire(import.meta.url);",
  },
};
