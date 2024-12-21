# Home Ops

## Overview

Home Ops is a free-ranging attempt at learning the monorepo framework [NX](https://nx.dev/) and to see if by using some focal points (NX, Taskfiles, Ansible, XDG Spec, submodules) I can gather all my automation sandbox projects into a usable old-style monolith.  All personal automation is local in nature, but this project will be even more so -- centered on products I use and coherent enough to provide a leg up when the next personal project takes my fancy.

While I do hope useful elements can be mined for similar projects, if only by counter-example, you might be better served by attempts which shoot for a coherent meta-framework:

### Examples

* [DebOps](https://docs.debops.org/)
* [Install Doctor](https://install.doctor)
* [Polylith](https://polylith.gitbook.io/polylith)

> Generic, less-opinionated, frameworks (ZX, NX, Chezmoi, etc.) are referenced at the end

## Apps

* [watchyourlan-api](./apps/watchyourlan-api/README.md)
* [scripts](./scripts/README.md)

## Libs

### Shared

* [log](./libs/shared/log/README.md)
* [pkg-install](./libs/shared/pkg-install/README.md)
* [utils](./libs/shared/utils/README.md)
* [zx-utils](./libs/shared/zx-utils/README.md)

### Project

* [watchyourlan-api](./libs/watchyourlan-api/README.md)

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

* [NX](https://nx.dev/)
* [ESBuild](https://esbuild.github.io/)
* [Chezmoi](https://www.chezmoi.io/quick-start/)
* [Gomplate](https://docs.gomplate.ca)
* [TypeScript](https://www.typescriptlang.org/)
* [ESLint](https://eslint.org/)
* [Prettier](https://prettier.io/)
* [Jest](https://jestjs.io/)
* [Poetry](https://python-poetry.org/docs/)
* [Playwright](https://playwright.dev/)
* [ZX](https://google.github.io/zx/)
* [Taskfiles](https://taskfiles.dev)
* [TSX](https://tsx.is/)

## Links

Sources:

* [Gomplate/Taskfiles](https://github.com/luismayta/dotfiles)

## TODO:

- Add home assistant integration
- Add secrets management via google drive & keepass
- Complete integrating gomplate into the project
- Add jest
- Sort out documentation publishing
- Add swagger docs
- Fold functioning parts of dasbootstrap project into the python sections
  https://esbuild.github.io/content-types/#file
