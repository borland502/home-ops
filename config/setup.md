# Setup ZX Scripts

The scripts folder contains both typed zx scripts `*.mts` and executable markdown documents `*.md` like this one. The zx command simply executes all code blocks marked with `js`, `bash`, or `sh`. All other code blocks are ignored, as is any text not fenced in as a code block.

## Install the ZX Scripts and TSX (not react related) parser

```bash
#!/usr/bin/env bash

for pkg in "npm npx tsx typescript ts-node nx volta"; do
  if ! [[ $(command -v "$pkg") ]]; then
    if [[ $pkg == "np[mx]" ]]; then
      echo "Installing Volta to bootstrap Node Dev Env.  Press CTRL+C in 10s to abort"
      sleep 10
      curl https://get.volta.sh | bash || exit 2
      
      volta install node@lts
    else
      volta install $pkg
    fi
  fi
done;

npm install
```

## Execute the ZX Environment Check

> In executable scripts `#!/usr/bin/env -S npx tsx` is preferred because transpiling is directly to memory and executed there

```js
#!/usr/bin/env zx


```
