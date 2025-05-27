import dotenv from 'dotenv';
import winston from 'winston';
import { join } from 'path';

dotenv.config();

// Enhanced logger configuration
export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console({
      silent: process.env.NODE_ENV === 'test',
    }),
    new winston.transports.File({
      filename: join(__dirname, '../../logs/error.log'),
      level: 'error',
      maxsize: 5 * 1024 * 1024,
      maxFiles: 3,
    }),
  ],
});

// Environment variables with better handling
export const PORT = process.env.PORT ? parseInt(process.env.PORT, 10) : 10000;
export const NODE_ENV = process.env.NODE_ENV || 'development';
export const IS_RENDER = process.env.RENDER === 'true';

// Enhanced configuration objects
export const PUZZLE_CONFIG = {
  REQUIRED_GROUPS: 4,
  WORDS_PER_GROUP: 4,
  MAX_SEED_VALUE: 2 ** 35 - 32,
  SHUFFLE_ALGORITHM: 'LCG',
};

export const CORS_OPTIONS = {
  origin: process.env.ALLOWED_ORIGINS?.split(',') || '*',
  methods: ['GET'],
  allowedHeaders: ['Content-Type'],
};

export const RATE_LIMIT = {
  windowMs: 15 * 60 * 1000,
  max: process.env.RATE_LIMIT_MAX
    ? parseInt(process.env.RATE_LIMIT_MAX, 10)
    : 100,
  standardHeaders: true,
  legacyHeaders: false,
};
