import express, { Request, Response, NextFunction } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import rateLimit from 'express-rate-limit';
import { puzzleRouter } from '@controllers/puzzle.controller';
import { CORS_OPTIONS, RATE_LIMIT } from '@config/config';
import { logger } from '@config/config';

export const createApp = () => {
  const app = express();

  // Security middleware
  app.use(helmet());
  app.use(cors(CORS_OPTIONS));

  // Rate limiting
  app.use(rateLimit(RATE_LIMIT));

  // Compression
  app.use(compression());

  // Body parsing
  app.use(express.json({ limit: '10mb' }));
  app.use(express.urlencoded({ extended: true }));

  // Request logging
  app.use((req, res, next) => {
    logger.info(`${req.method} ${req.path}`, {
      ip: req.ip,
      userAgent: req.get('User-Agent'),
    });
    next();
  });

  // Health check endpoint
  app.get('/health', (req: Request, res: Response) => {
    res.json({
      status: 'healthy',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
    });
  });

  // API routes
  app.use('/api/puzzle', puzzleRouter);

  // 404 handler
  app.use('*', (req: Request, res: Response) => {
    res.status(404).json({
      error: 'Route not found',
      path: req.originalUrl,
    });
  });

  // Global error handler
  app.use((err: Error, req: Request, res: Response, _next: NextFunction) => {
    logger.error('Unhandled error:', err);
    res.status(500).json({
      error: 'Internal server error',
      timestamp: new Date().toISOString(),
    });
  });

  return app;
};
