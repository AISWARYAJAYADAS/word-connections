import { PORT, NODE_ENV, logger } from '@config/config';
import { createApp } from './app';
import http from 'http';

const app = createApp();
const server = http.createServer(app);

const shutdown = (signal: string) => {
  logger.info(`Received ${signal} - shutting down`);
  server.close(() => {
    logger.info('Server closed');
    process.exit(0);
  });
};

process.on('SIGTERM', () => shutdown('SIGTERM'));
process.on('SIGINT', () => shutdown('SIGINT'));

process.on('unhandledRejection', (err) => {
  logger.error('Unhandled rejection:', err);
});

process.on('uncaughtException', (err) => {
  logger.error('Uncaught exception:', err);
  shutdown('UNCAUGHT_EXCEPTION');
});

server.listen(PORT, () => {
  logger.info(`Server running in ${NODE_ENV} on port ${PORT}`);
});
