# Scripts

## Overview

These utility scripts use the [ZX](https://github.com/google/zx) scripting extensions from Google.  They operate 
within the path structure of the [NX]() monorepo.  The scripts either need to be executed with nx (e.g. build scripts) 

```shell
nx reset && nx run watchyourlan-api:build && nx run watchyourlan-api:serve
```

or with [TSX](https://tsx.is) and the following flag:

```shell
npx tsx --tsconfig ./script/tsconfig.app.json ./scripts/init/init.mts
```
or
```shell
./scripts/init/init.mts
```

