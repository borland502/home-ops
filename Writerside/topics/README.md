
# Home Ops

## Overview

Home Ops is a free-ranging attempt at learning the monorepo framework [NX](https://nx.dev/).  

## Apps

- [](watchyourlan-api_README.md)
- [scripts](scripts_README.md)

## Libs

### Shared

- [log](log_README.md)
- [pkg-install](pkg-install_README.md)
- [utils](utils_README.md)
- [zx-utils](zx-utils_README.md)

### Project

- [watchyourlan-api](watchyourlan-api_README.md)

## Run tasks

### Build all projects

```shell
npm run build:all
```
### Lint all projects
```shell
npm run lint:all
```

## Frameworks Used

- [NX](https://nx.dev/)
- [ESBuild](https://esbuild.github.io/)
- [TypeScript](https://www.typescriptlang.org/)
- [ESLint](https://eslint.org/)
- [Prettier](https://prettier.io/)
- [Jest](https://jestjs.io/)
- [Playwright](https://playwright.dev/)
- [Zx](https://google.github.io/zx/)
- [TSX](https://tsx.is/)

## Links

Sources:

- https://github.com/bhouston/template-typescript-monorepo/blob/main/nx.json
- https://dev.to/a0viedo/nodejs-typescript-and-esm-it-doesnt-have-to-be-painful-438e
- https://eisenbergeffect.medium.com/an-esbuild-setup-for-typescript-3b24852479fe
- https://dev.to/zauni/create-a-zx-nodejs-script-as-binary-with-pkg-5abf
- https://esbuild.github.io/content-types/#typescript-caveats

## TODO:

- Don't use esbuild for production bundles
- Figure out mts --> cjs conversion if possible:  https://github.com/microsoft/TypeScript/issues/51990
- Add jest
- Add playwright
- Sort out Documentation

  https://esbuild.github.io/content-types/#file
