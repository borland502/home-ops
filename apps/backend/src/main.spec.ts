import sinon from 'sinon';
import express, { Request, Response } from 'express';
import config from 'config';
import { expect } from '@jest/globals';

describe('src/main', () => {
  let app: express.Express;
  let server: any;

  beforeEach(() => {
    // Mock dependencies or setup environment
    // For example:
    sinon.stub(config, 'get').callsFake((key: string) => {
      switch (key) {
        case 'express.port':
          return '3333';
        case 'express.assetsFolder':
          return '/assets';
        case 'express.apiBase':
          return '/api';
        default:
          return undefined;
      }
    });

    // Create a new Express app before each test
    app = express();
  });

  afterEach(() => {
    // Restore stubs and reset environment
    sinon.restore();

    // Close the server after each test if it's running
    if (server && server.close) {
      server.close();
    }
  });

  it('should start the server and listen on the specified port', (done) => {
    // Start the server
    server = app.listen(3333, () => {
      // Assertions
      expect(server).toBeTruthy();
      expect(server.address()).toHaveProperty('port', 3333);

      // Close the server and signal that the test is done
      server.close(done);
    });
  });

});
