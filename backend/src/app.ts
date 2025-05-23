import express, { Express, Request, Response, NextFunction } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import { CORS_OPTIONS, logger } from '@config/config';
import { handlePuzzleRequest } from '@controllers/puzzle.controller';
import { PuzzleError } from '@interfaces/puzzle.types';

export const createApp = (): Express => {
  const app = express();

  app.use(helmet());
  app.use(express.json());
  app.use(cors(CORS_OPTIONS));
  app.use((req: Request, _res: Response, next: NextFunction) => {
    logger.info(`${req.method} ${req.url}`);
    next();
  });

  app.get('/api/puzzle', handlePuzzleRequest);
  app.get('/health', (_req: Request, res: Response) => {
    res.status(200).json({
      status: 'healthy',
      timestamp: new Date().toISOString(),
    });
  });

  app.use((_req: Request, res: Response) => {
    res.status(404).json({
      error: 'Not Found',
      timestamp: new Date().toISOString(),
    });
  });

  app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
    logger.error('Server Error:', err);
    const response: PuzzleError = {
      error: err.message || 'Internal Server Error',
      timestamp: new Date().toISOString(),
    };
    res.status(500).json(response);
  });

  return app;
};
