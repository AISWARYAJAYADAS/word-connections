import { PORT, NODE_ENV, logger, IS_RENDER } from '@config/config';
import { createApp } from './app';
import http from 'http';

const app = createApp();
const server = http.createServer(app);

// Enhanced shutdown handler
const shutdown = async (signal: string) => {
  logger.info(`Received ${signal} - shutting down`);
  try {
    await new Promise<void>((resolve, reject) => {
      server.close((err) => {
        if (err) {
          logger.error('Error during shutdown:', err);
          reject(err);
        } else {
          logger.info('Server closed');
          resolve();
        }
      });
    });
    process.exit(0);
  } catch (err) {
    logger.error('Forcing shutdown due to error:', err);
    process.exit(1);
  }
};

// Signal handlers
process.on('SIGTERM', () => shutdown('SIGTERM'));
process.on('SIGINT', () => shutdown('SIGINT'));

// Error handlers
process.on('unhandledRejection', (err) => {
  logger.error('Unhandled rejection:', err);
});

process.on('uncaughtException', (err) => {
  logger.error('Uncaught exception:', err);
  shutdown('UNCAUGHT_EXCEPTION');
});

// Enhanced server startup with port handling
const startServer = async (
  port: number,
  maxRetries = 3,
  retryCount = 0
): Promise<void> => {
  try {
    await new Promise<void>((resolve, reject) => {
      server
        .listen(port)
        .on('listening', () => {
          logger.info(
            `Server running in ${NODE_ENV} on port ${port}${IS_RENDER ? ' (Render)' : ''}`
          );
          resolve();
        })
        .on('error', (err: NodeJS.ErrnoException) => {
          if (err.code === 'EADDRINUSE') {
            if (retryCount < maxRetries) {
              const newPort = port + 1;
              logger.warn(
                `Port ${port} in use, retrying with ${newPort} (attempt ${retryCount + 1}/${maxRetries})`
              );
              setTimeout(
                () => startServer(newPort, maxRetries, retryCount + 1),
                1000
              );
            } else {
              reject(
                new Error(`Failed to start server after ${maxRetries} retries`)
              );
            }
          } else {
            reject(err);
          }
        });
    });
  } catch (err) {
    logger.error('Failed to start server:', err);
    process.exit(1);
  }
};

// Start the server
startServer(PORT).catch((err) => {
  logger.error('Fatal server error:', err);
  process.exit(1);
});
