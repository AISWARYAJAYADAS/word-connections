import { PORT, NODE_ENV, logger } from '@config/config';
import { createApp } from './app';
import http from 'http';

const app = createApp();
const server = http.createServer(app);

const shutdown = async (signal: string) => {
  logger.info(`Received ${signal} - shutting down`);
  try {
    await new Promise<void>((resolve, reject) => {
      server.close((err) => (err ? reject(err) : resolve()));
    });
    process.exit(0);
  } catch (err) {
    logger.error('Shutdown error:', err);
    process.exit(1);
  }
};

['SIGTERM', 'SIGINT'].forEach((signal) => {
  process.on(signal, () => shutdown(signal));
});

process.on('unhandledRejection', (err) => {
  logger.error('Unhandled rejection:', err);
});

process.on('uncaughtException', (err) => {
  logger.error('Uncaught exception:', err);
  shutdown('UNCAUGHT_EXCEPTION');
});

const startServer = async (port: number, retries = 3): Promise<void> => {
  try {
    await new Promise<void>((resolve, reject) => {
      // Removed the unused serverInstance assignment
      server
        .listen(port)
        .on('listening', () => {
          logger.info(`Server running in ${NODE_ENV} on port ${port}`);
          resolve();
        })
        .on('error', (err: NodeJS.ErrnoException) => {
          if (err.code === 'EADDRINUSE' && retries > 0) {
            logger.warn(`Port ${port} in use, retrying...`);
            setTimeout(() => startServer(port + 1, retries - 1), 1000);
          } else {
            reject(err);
          }
        });
    });
  } catch (err) {
    logger.error('Server startup failed:', err);
    process.exit(1);
  }
};

// Start with the configured port
startServer(PORT).catch((err) => {
  logger.error('Fatal error:', err);
  process.exit(1);
});
