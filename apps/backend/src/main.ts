/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */

import express from 'express';
import * as path from 'path';
import { fileURLToPath } from 'node:url';
import { info } from '@technohouser/log';
import config from 'config';
import * as core from 'express-serve-static-core';
import { toInt } from 'radash';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const portNum = toInt(config.get('express.port'));
const assetsFolder: core.PathParams = config.get('express.assetsFolder');
const apiBase: core.PathParams = config.get('express.apiBase');

app.use(assetsFolder, express.static(path.join(__dirname, 'assets')));

app.get(apiBase, (req, res) => {
  res.send({ message: 'Welcome to backend!' });
});

const port = portNum || 3333;
const server = app.listen(port, () => {
  info(`Listening at http://localhost:${port}/api`);
});
server.on('error', console.error);
