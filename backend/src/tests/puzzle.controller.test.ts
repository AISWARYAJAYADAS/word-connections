import request from 'supertest';
import { createApp } from '../app';
import { puzzleService } from '@services/puzzle.service';
import {
  EnhancedPuzzleResponse,
  ValidationResponse,
  Difficulty,
} from '@interfaces/puzzle.types';

// Mock the puzzle service
jest.mock('@services/puzzle.service', () => ({
  puzzleService: {
    generatePuzzle: jest.fn(),
    generateEnhancedPuzzle: jest.fn(),
    createSession: jest.fn(),
    validateGuess: jest.fn(),
    stopCleanupInterval: jest.fn(),
    clearAllSessions: jest.fn(),
  },
}));

const mockedPuzzleService = puzzleService as jest.Mocked<typeof puzzleService>;

// Create Express app
const app = createApp();

describe('Puzzle Controller', () => {
  const mockPuzzle: EnhancedPuzzleResponse = {
    puzzleWords: ['Red', 'Blue', 'Green', 'Yellow'],
    categories: ['Colors'],
    solution: {
      Colors: ['Red', 'Blue', 'Green', 'Yellow'],
    },
    wordToCategory: {
      Red: 'Colors',
      Blue: 'Colors',
      Green: 'Colors',
      Yellow: 'Colors',
    },
    groups: [
      {
        theme: 'Colors',
        words: ['Red', 'Blue', 'Green', 'Yellow'],
        difficulty: Difficulty.YELLOW,
      },
    ],
    difficultyOrder: [Difficulty.YELLOW],
    meta: {
      seed: 123,
      generatedAt: new Date().toISOString(),
      totalGroups: 20,
      puzzleId: 'test-puzzle-id',
    },
  };

  const mockBasicPuzzle = {
    puzzleWords: ['Red', 'Blue', 'Green', 'Yellow'],
    categories: ['Colors'],
    meta: {
      seed: 123,
      generatedAt: new Date().toISOString(),
      totalGroups: 20,
      puzzleId: 'test-puzzle-id',
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();

    // Setup mock implementations
    mockedPuzzleService.generatePuzzle.mockReturnValue(mockBasicPuzzle);
    mockedPuzzleService.generateEnhancedPuzzle.mockReturnValue(mockPuzzle);
    mockedPuzzleService.createSession.mockReturnValue('test-session-id');

    mockedPuzzleService.validateGuess.mockImplementation(({ words }) => {
      const isCorrect =
        JSON.stringify(words.sort()) ===
        JSON.stringify(mockPuzzle.groups[0].words.sort());
      return {
        isCorrect,
        category: isCorrect ? mockPuzzle.groups[0].theme : undefined,
        remainingAttempts: isCorrect ? 4 : 3,
        solvedCategories: isCorrect ? [mockPuzzle.groups[0].theme] : [],
        isOneAway:
          !isCorrect &&
          words.filter((w: string) => mockPuzzle.groups[0].words.includes(w))
            .length === 3,
      } as ValidationResponse;
    });
  });

  describe('GET /api/puzzle', () => {
    it('should return basic puzzle data', async () => {
      const response = await request(app).get('/api/puzzle');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockBasicPuzzle);
      expect(mockedPuzzleService.generatePuzzle).toHaveBeenCalledWith(
        undefined
      );
    });

    it('should handle seed parameter', async () => {
      const mockWithSeed = {
        ...mockBasicPuzzle,
        meta: { ...mockBasicPuzzle.meta, seed: 42 },
      };
      mockedPuzzleService.generatePuzzle.mockReturnValue(mockWithSeed);

      const response = await request(app).get('/api/puzzle?seed=42');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockWithSeed);
      expect(mockedPuzzleService.generatePuzzle).toHaveBeenCalledWith(42);
    });

    it('should return 400 for invalid seed', async () => {
      const response = await request(app).get('/api/puzzle?seed=invalid');

      expect(response.status).toBe(400);
      expect(response.body).toHaveProperty('error');
    });
  });

  describe('GET /api/puzzle/enhanced', () => {
    it('should return enhanced puzzle data', async () => {
      const response = await request(app).get('/api/puzzle/enhanced');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockPuzzle);
      expect(mockedPuzzleService.generateEnhancedPuzzle).toHaveBeenCalledWith(
        undefined
      );
      expect(mockedPuzzleService.createSession).toHaveBeenCalledWith(
        mockPuzzle
      );
    });

    it('should handle seed parameter', async () => {
      const mockWithSeed = {
        ...mockPuzzle,
        meta: { ...mockPuzzle.meta, seed: 42 },
      };
      mockedPuzzleService.generateEnhancedPuzzle.mockReturnValue(mockWithSeed);

      const response = await request(app).get('/api/puzzle/enhanced?seed=42');

      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockWithSeed);
      expect(mockedPuzzleService.generateEnhancedPuzzle).toHaveBeenCalledWith(
        42
      );
    });
  });

  describe('POST /api/puzzle/validate', () => {
    it('should validate correct guesses', async () => {
      const response = await request(app)
        .post('/api/puzzle/validate')
        .send({
          words: ['Red', 'Blue', 'Green', 'Yellow'],
          puzzleId: 'test-puzzle-id',
        });

      expect(response.status).toBe(200);
      expect(response.body).toEqual({
        isCorrect: true,
        category: 'Colors',
        remainingAttempts: 4,
        solvedCategories: ['Colors'],
        isOneAway: false,
      });

      expect(mockedPuzzleService.validateGuess).toHaveBeenCalledWith({
        puzzleId: 'test-puzzle-id',
        words: expect.arrayContaining(['Red', 'Blue', 'Green', 'Yellow']),
      });
      expect(
        mockedPuzzleService.validateGuess.mock.calls[0][0].words
      ).toHaveLength(4);
    });

    it('should detect "one away" guesses', async () => {
      const response = await request(app)
        .post('/api/puzzle/validate')
        .send({
          words: ['Red', 'Blue', 'Green', 'Wrong'],
          puzzleId: 'test-puzzle-id',
        });

      expect(response.status).toBe(200);
      expect(response.body).toEqual({
        isCorrect: false,
        remainingAttempts: 3,
        isOneAway: true,
        solvedCategories: [],
        category: undefined,
      });
    });

    it('should reject invalid guesses', async () => {
      const response = await request(app)
        .post('/api/puzzle/validate')
        .send({
          words: ['Red', 'Blue', 'Wrong1', 'Wrong2'],
          puzzleId: 'test-puzzle-id',
        });

      expect(response.status).toBe(200);
      expect(response.body).toEqual({
        isCorrect: false,
        remainingAttempts: 3,
        isOneAway: false,
        solvedCategories: [],
        category: undefined,
      });
    });

    it('should require exactly 4 words', async () => {
      const response = await request(app)
        .post('/api/puzzle/validate')
        .send({
          words: ['Red', 'Blue', 'Green'],
          puzzleId: 'test-puzzle-id',
        });

      expect(response.status).toBe(400);
      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toBe('Request must contain exactly 4 words');
    });

    it('should require puzzleId', async () => {
      const response = await request(app)
        .post('/api/puzzle/validate')
        .send({
          words: ['Red', 'Blue', 'Green', 'Yellow'],
        });

      expect(response.status).toBe(400);
      expect(response.body).toHaveProperty('error');
      expect(response.body.error).toBe('puzzleId is required');
    });

    it('should handle invalid session', async () => {
      mockedPuzzleService.validateGuess.mockImplementation(() => {
        throw new Error('Invalid puzzle session');
      });

      const response = await request(app)
        .post('/api/puzzle/validate')
        .send({
          words: ['Red', 'Blue', 'Green', 'Yellow'],
          puzzleId: 'invalid-id',
        });

      expect(response.status).toBe(500);
      expect(response.body).toHaveProperty('error');
    });
  });

  describe('404 handler', () => {
    it('should return 404 for unknown routes', async () => {
      const response = await request(app).get('/api/puzzle/unknown');

      expect(response.status).toBe(404);
      expect(response.body).toEqual({
        error: 'Route not found',
        path: '/api/puzzle/unknown',
      });
    });
  });
});
