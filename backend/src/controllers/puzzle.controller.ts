import { Router } from 'express';
import { Request, Response } from 'express';
import { puzzleService } from '@services/puzzle.service';
import { PUZZLE_CONFIG } from '@config/config';
import { PuzzleError, ValidationRequest } from '@interfaces/puzzle.types';

// Create router instance
export const puzzleRouter = Router();

// Route handlers
export const handlePuzzleRequest = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const seed = validateSeed(req.query.seed);
    const puzzle = puzzleService.generatePuzzle(seed);
    res.status(200).json(puzzle);
  } catch (error) {
    handlePuzzleError(error, res);
  }
};

export const handleEnhancedPuzzleRequest = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const seed = validateSeed(req.query.seed);
    const puzzle = puzzleService.generateEnhancedPuzzle(seed);
    puzzleService.createSession(puzzle);
    res.status(200).json(puzzle);
  } catch (error) {
    handlePuzzleError(error, res);
  }
};

export const handleValidationRequest = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const request = req.body as ValidationRequest;

    // Validate request body
    if (
      !request.words ||
      !Array.isArray(request.words) ||
      request.words.length !== 4
    ) {
      res.status(400).json({
        error: 'Request must contain exactly 4 words',
      });
      return;
    }

    if (!request.puzzleId) {
      res.status(400).json({
        error: 'puzzleId is required',
      });
      return;
    }

    const response = puzzleService.validateGuess(request);
    res.status(200).json(response);
  } catch (error) {
    handlePuzzleError(error, res);
  }
};

// Register routes
puzzleRouter.get('/', handlePuzzleRequest);
puzzleRouter.get('/enhanced', handleEnhancedPuzzleRequest);
puzzleRouter.post('/validate', handleValidationRequest);

// Helper functions
const validateSeed = (seed: unknown): number | undefined => {
  if (seed === undefined || seed === '') return undefined;
  if (typeof seed !== 'string' || !/^\d+$/.test(seed)) {
    throw new Error('Seed must be a positive integer');
  }
  const num = Number(seed);
  if (isNaN(num) || num < 0 || num > PUZZLE_CONFIG.MAX_SEED_VALUE) {
    throw new Error(
      `Seed must be between 0 and ${PUZZLE_CONFIG.MAX_SEED_VALUE}`
    );
  }
  return Math.floor(num);
};

const handlePuzzleError = (error: unknown, res: Response): void => {
  const statusCode =
    error instanceof Error && error.message.includes('Seed') ? 400 : 500;
  const response: PuzzleError = {
    error: error instanceof Error ? error.message : 'Unknown error',
    timestamp: new Date().toISOString(),
  };
  res.status(statusCode).json(response);
};
