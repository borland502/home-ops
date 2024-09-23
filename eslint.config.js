import globals from "globals";
import path from "node:path";
import nxEslintPlugin from "@nx/eslint-plugin";
import { FlatCompat } from "@eslint/eslintrc";
import prettierConfig from "eslint-config-prettier";
import { fileURLToPath } from "url";
import js from "@eslint/js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export const compat = new FlatCompat({
  baseDirectory: __dirname,
  recommendedConfig: js.configs.recommended,
});

export default [
  { plugins: {"@nx": nxEslintPlugin} },
  {
    files: ["**/*.ts", "**/*.tsx", "**/*.js", "**/*.jsx"],
    rules: {
      "@nx/enforce-module-boundaries": [
        "error",
        {
          enforceBuildableLibDependency: true,
          allow: [],
          depConstraints: [
            {
              sourceTag: "type:plugin",
              onlyDependOnLibsWithTags: [
                "type:util",
                "type:ui",
                "type:feature",
              ],
            },
            {
              sourceTag: "type:app",
              onlyDependOnLibsWithTags: [
                "type:util",
                "type:ui",
                "type:feature",
              ],
            },
            {
              sourceTag: "type:feature",
              onlyDependOnLibsWithTags: [
                "type:feature",
                "type:ui",
                "type:util",
              ],
            },
            {
              sourceTag: "type:ui",
              onlyDependOnLibsWithTags: ["type:ui", "type:util"],
            },
            {
              sourceTag: "type:util",
              onlyDependOnLibsWithTags: ["type:util"],
            },
            {
              sourceTag: "type:e2e",
              onlyDependOnLibsWithTags: ["type:ui", "type:util"],
            },
          ],
        },
      ],
    },
    languageOptions: {
      globals: {
        penpot: "readonly",
        repairIntrinsics: "readonly",
        hardenIntrinsics: "readonly",
        Compartment: "readonly",
        harden: "readonly",
      },
    },
  },
  ...compat
    .config({
      extends: [
        "plugin:@typescript-eslint/recommended",
        "plugin:@typescript-eslint/recommended-requiring-type-checking",
        "plugin:deprecation/recommended",
        "prettier",
      ],
    })
    .map((config) => ({
      ...config,
      files: ["**/*.ts", "**/*.tsx"],
      rules: {
        "@typescript-eslint/no-unused-vars": ["error"],
        "no-multiple-empty-lines": [2, { max: 1 }],
        quotes: ["error", "single", { avoidEscape: true }],
        "no-unused-vars": "off"
      },
    })),
  ...compat
    .config({
      extends: [
        "plugin:@typescript-eslint/recommended",
        "plugin:@typescript-eslint/recommended-requiring-type-checking",
        "plugin:deprecation/recommended",
        "prettier",
      ],
    })
    .map((config) => ({
      ...config,
      files: ["**/*.spec.ts"],
      rules: {
        "@typescript-eslint/no-unused-vars": ["error"],
        "no-multiple-empty-lines": [2, { max: 1 }],
        quotes: ["error", "single", { avoidEscape: true }],
        "@typescript-eslint/no-unsafe-member-access": "off",
        "@typescript-eslint/no-unsafe-call": "off",
        "@typescript-eslint/no-unsafe-assignment": "off",
        "@typescript-eslint/no-explicit-any": "off",
        "@typescript-eslint/no-unsafe-argument": "off",
        "@typescript-eslint/no-unsafe-return": "off",
        "@ngrx/prefix-selectors-with-select": "off",
      },
      languageOptions: {
        globals: {
          ...globals.jest,
        },
      },
    })),
  {
    files: ["**/*.js", "**/*.jsx"],
    rules: {},
  },
  js.configs.recommended,
  prettierConfig,
  {
    ignores: ["eslint.config.js"],
  }
];
