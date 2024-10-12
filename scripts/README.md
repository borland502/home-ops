# Scripts

## Overview

These utility scripts use [ZX](https://github.com/google/zx), python, and shell.  Shell scripts are simply copied to ${HOME}/.local/bin, as are python scripts.  
ZX Scripts (i.e. nodejs) have dependencies that eliminate their portability, but aren't going to be published to npm.  So they use a 
cheap shim to invoke the ZX script within the monorepo directly.

## ZX shim

```shell
#!/usr/bin/env zsh
exec ${XDG_DATA_HOME}/automation/home-ops/scripts/bin/zx/vscode.mts
```

The scripts themselves ditch the simple zx hashbang to references libraries in the monorepo.  If you are old school shell, I apologize for
the inflicted trauma

```shell
#!/usr/bin/env -S npx tsx --tsconfig ./tsconfig.base.json
echo "Hello, World!"
```
