import dotenv from 'dotenv';
import winston from 'winston';
import { join } from 'path';
import { existsSync, mkdirSync } from 'fs';

dotenv.config();

// Ensure logs directory exists
const logsDir = join(__dirname, '../../logs');
if (!existsSync(logsDir)) {
  mkdirSync(logsDir, { recursive: true });
}

export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.simple()
      ),
    }),
    new winston.transports.File({
      filename: join(logsDir, 'error.log'),
      level: 'error',
    }),
    new winston.transports.File({
      filename: join(logsDir, 'combined.log'),
    }),
  ],
});

export const PORT = parseInt(process.env.PORT || '10000', 10);
export const NODE_ENV = process.env.NODE_ENV || 'development';
export const IS_RENDER = process.env.RENDER === 'true';

export const PUZZLE_CONFIG = {
  REQUIRED_GROUPS: 4,
  WORDS_PER_GROUP: 4,
  MAX_SEED_VALUE: 2 ** 31 - 1, // Reduced for safety
  MAX_ATTEMPTS: 4,
  SESSION_EXPIRE_MINUTES: parseInt(
    process.env.PUZZLE_EXPIRE_MINUTES || '30',
    10
  ),
};

export const CORS_OPTIONS = {
  origin:
    process.env.ALLOWED_ORIGINS === '*'
      ? true
      : process.env.ALLOWED_ORIGINS?.split(',') || ['http://localhost:3000'],
  methods: ['GET', 'POST'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true,
};

export const RATE_LIMIT = {
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: parseInt(process.env.RATE_LIMIT_MAX || '100', 10),
  standardHeaders: true,
  legacyHeaders: false,
  message: {
    error: 'Too many requests, please try again later.',
  },
};
