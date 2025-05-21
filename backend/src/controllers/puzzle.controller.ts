import { Request, Response } from 'express';
import { generatePuzzle } from '@services/puzzle.service';
import { PUZZLE_CONFIG } from '@config/config';
import { PuzzleError } from '@interfaces/puzzle.types';

export const handlePuzzleRequest = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const seed = validateSeed(req.query.seed);
    const puzzle = generatePuzzle(seed);
    res.status(200).json(puzzle);
  } catch (error) {
    const statusCode =
      error instanceof Error && error.message.includes('Seed') ? 400 : 500;
    const response: PuzzleError = {
      error: error instanceof Error ? error.message : 'Unknown error occurred',
      timestamp: new Date().toISOString(),
    };
    res.status(statusCode).json(response);
  }
};

const validateSeed = (seed: unknown): number | undefined => {
  if (seed === undefined || seed === '') return undefined;

  if (typeof seed !== 'string' || !/^\d+$/.test(seed)) {
    throw new Error('Seed must be a positive integer');
  }

  const num = Number(seed);
  if (isNaN(num)) {
    throw new Error('Seed must be a number');
  }
  if (num < 0) {
    throw new Error('Seed must be positive');
  }
  if (num > PUZZLE_CONFIG.MAX_SEED_VALUE) {
    throw new Error(`Seed must be less than ${PUZZLE_CONFIG.MAX_SEED_VALUE}`);
  }

  return Math.floor(num);
};
