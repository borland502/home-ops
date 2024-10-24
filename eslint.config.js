import tseslint from "typescript-eslint"
import eslint from '@eslint/js';

export default tseslint.config(
  {
    ignores: ["**/*.js",".eslintrc.*", ".eslintrc", "**/*.spec.mts"]
  },
  {
    files: ["**/*.mts"],
  },
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  ...tseslint.configs.recommendedTypeChecked,
  {
    languageOptions: {
      parserOptions: {
        projectService: true,
        tsconfigRootDir: "."
      }
    },
  }
)
