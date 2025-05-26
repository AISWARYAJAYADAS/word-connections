import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import rateLimit from 'express-rate-limit';
import { CORS_OPTIONS, RATE_LIMIT, logger, NODE_ENV } from '@config/config';

import { handlePuzzleRequest } from '@controllers/puzzle.controller';

export const createApp = (): express.Application => {
  const app = express();

  // Security middleware
  app.use(helmet());
  app.use(express.json());
  app.use(cors(CORS_OPTIONS));
  app.disable('x-powered-by');

  // Rate limiting
  app.use('/api', rateLimit(RATE_LIMIT));

  // Request logging
  app.use((req, res, next) => {
    logger.info(`${req.method} ${req.path}`);
    next();
  });

  // Routes
  app.get('/api/puzzle', handlePuzzleRequest);
  app.get('/health', (req, res) => {
    res.status(200).json({
      status: 'healthy',
      uptime: process.uptime(),
      timestamp: new Date().toISOString(),
    });
  });

  // 404 handler
  app.use((req, res) => {
    res.status(404).json({
      error: 'Not Found',
      path: req.path,
    });
  });

  // Error handler
  app.use((err: Error, req: express.Request, res: express.Response) => {
    logger.error('Error:', {
      path: req.path,
      error: err.message,
      stack: NODE_ENV === 'development' ? err.stack : undefined,
    });

    res.status(500).json({
      error: 'Internal Server Error',
      ...(NODE_ENV === 'development' && { details: err.message }),
    });
  });

  return app;
};
