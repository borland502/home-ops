import esbuildPluginTsc from 'esbuild-plugin-tsc';

// TODO: Create a dictionary with keys that match `nx watch`

export function createExpressBuildSettings(options) {
  return {
    entryPoints: ['apps/backend/src/main.mts'],
    outfile: 'dist/backend.mjs',
    bundle: true,
    plugins: [
      esbuildPluginTsc({
        force: true
      }),
    ],
    ...options
  };
}