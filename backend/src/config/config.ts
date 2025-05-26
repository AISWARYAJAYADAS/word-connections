import dotenv from 'dotenv';
import winston from 'winston';
import { join } from 'path';

dotenv.config();

// Logger with production-friendly settings
export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console({
      silent: process.env.NODE_ENV === 'test', // Disable logs during tests
    }),
    new winston.transports.File({
      filename: join(__dirname, '../../logs/error.log'),
      level: 'error',
      maxsize: 5 * 1024 * 1024, // 5MB
      maxFiles: 3,
    }),
  ],
});

// Validate environment variables
export const PORT = Number(process.env.PORT) || 3000;
export const NODE_ENV = process.env.NODE_ENV || 'development';

if (!process.env.ALLOWED_ORIGINS) {
  logger.warn('ALLOWED_ORIGINS not set - allowing all origins');
}

// Puzzle configuration
export const PUZZLE_CONFIG = {
  REQUIRED_GROUPS: 4,
  WORDS_PER_GROUP: 4,
  MAX_SEED_VALUE: 2 ** 35 - 32, // Match the shuffle algorithm's math
  SHUFFLE_ALGORITHM: 'LCG', // Linear Congruential Generator
};

// CORS with mobile support
export const CORS_OPTIONS = {
  origin: process.env.ALLOWED_ORIGINS?.split(',') || '*',
  methods: ['GET'],
  allowedHeaders: ['Content-Type'],
};

// Rate limiting
export const RATE_LIMIT = {
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP
  standardHeaders: true,
  legacyHeaders: false,
};
