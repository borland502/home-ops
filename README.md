# Home Ops

## Overview

Home Ops is a free-ranging attempt at learning the monorepo framework [NX](https://nx.dev/) and to see if by using some focal points (NX, Taskfiles, Ansible, XDG Spec, submodules) I can gather all my automation sandbox projects into a usable old-style monolith.  All personal automation is local in nature, but this project will be even more so -- centered on products I use and coherent enough to provide a leg up when the next personal project takes my fancy.

While I do hope useful elements can be mined for similar projects, if only by counter-example, you might be better served by attempts which shoot for a coherent meta-framework:

### Examples

* [DebOps](https://docs.debops.org/)
* [Install Doctor](https://install.doctor)
* [Polylith](https://polylith.gitbook.io/polylith)

> Generic, less-opinionated, frameworks (ZX, NX, Chezmoi, etc.) are referenced at the end

## Script Languages (A stretch for many of these)

### Ansible

### Devcontainer-features

### Docker

### Dotfiles

### Shell

### Spring Shell

### Taskfiles

### ZX

## Run tasks

```console
task: Available tasks for this project:

* mcv:pack:                                       Pack all the projects
* mcv:unpack:                                     Unpack all the projects
* nodeapp:create:                                 Create a new NodeJS application
* nodelib:create:                                 Create a new NodeJS library
* pyapp:create:                                   Create a new Python application
* pylib:create:                                   Create a new Python library
```

## Frameworks Used

* [Spring Shell](https://spring.io/projects/spring-shell)
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

- Reorganize project now that primary bootstrap language and framework selected (Java/Spring Shell -- Can't beat what you know best)
    - Strip out nx
    - Strip out jbang -- much more compact than spring shell, but not what I am as familar with
    - Move folders with no scripting function
    - Create a templates folder for ansible/gomplete/jinja2
    - Redistribute python apps/libs into a Python script subfolder
    - Repair Python & ZX apps/libraries in (hopefully) their new and final location
- Add secrets management via google drive & keepass
- Complete integrating gomplate into the project
- Sort out documentation publishing
- Fold functioning parts of dasbootstrap project into the python sections
  https://esbuild.github.io/content-types/#file
