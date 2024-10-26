import {
  xdgData as _xdgData,
  xdgCache as _xdgCache,
  xdgState as _xdgState,
  xdgConfig as _xdgConfig,
  xdgConfigDirectories as _xdgConfigDirectories,
  xdgDataDirectories as _xdgDataDirectories,
  xdgRuntime as _xdgRuntime,
} from "xdg-basedir";
import os from "node:os";

/** Export resolved XDG paths with fallback defaults */
export const xdgData = _xdgData || `${os.homedir()}/.local/share`;
export const xdgCache = _xdgCache || `${os.homedir()}/.cache`;
export const xdgState = _xdgState || `${os.homedir()}/.local/state`;
export const xdgConfig = _xdgConfig || `${os.homedir()}/.config`;
export const xdgConfigDirectories = _xdgConfigDirectories || [
  `${os.homedir()}/.config`,
];
export const xdgDataDirectories = _xdgDataDirectories || [
  `${os.homedir()}/.local/share`,
];
export const xdgRuntime = _xdgRuntime || `${os.homedir()}/.local/run`;
