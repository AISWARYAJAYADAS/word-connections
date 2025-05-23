import dotenv from 'dotenv';
import winston from 'winston';

dotenv.config();

export const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: 'logs/error.log', level: 'error' }),
    new winston.transports.File({ filename: 'logs/combined.log' }),
  ],
});

logger.info('Config module loaded');

export const PUZZLE_CONFIG = {
  REQUIRED_GROUPS: 4,
  WORDS_PER_GROUP: 4,
  MAX_SEED_VALUE: 1_000_000,
  MAX_THEME_LENGTH: 50,
  MAX_WORD_LENGTH: 50,
};

export const CORS_OPTIONS = {
  origin: process.env.ALLOWED_ORIGINS?.split(',') || '*',
  methods: ['GET'],
  optionsSuccessStatus: 204,
};

export const PORT = Number(process.env.PORT) || 3000;
export const NODE_ENV = process.env.NODE_ENV || 'development';
