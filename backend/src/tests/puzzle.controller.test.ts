import request from 'supertest';
import { createApp } from '../app';
import { generatePuzzle } from '@services/puzzle.service';
import { PuzzleResponse } from '@interfaces/puzzle.types';

jest.mock('@services/puzzle.service');

const app = createApp();

describe('Puzzle Controller', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('GET /api/puzzle returns 200 with puzzle data', async () => {
    const mockPuzzle: PuzzleResponse = {
      puzzleWords: ['Red', 'Blue', 'Green', 'Yellow'],
      categories: ['Colors'],
      meta: {
        generatedAt: new Date().toISOString(),
        totalGroups: 20,
      },
    };
    (generatePuzzle as jest.Mock).mockReturnValue(mockPuzzle);

    const response = await request(app).get('/api/puzzle');

    expect(response.status).toBe(200);
    expect(response.body).toEqual(mockPuzzle);
  });

  test('GET /api/puzzle with valid seed returns 200', async () => {
    const seed = 123;
    const mockPuzzle: PuzzleResponse = {
      puzzleWords: ['Red', 'Blue', 'Green', 'Yellow'],
      categories: ['Colors'],
      meta: {
        seed,
        generatedAt: new Date().toISOString(),
        totalGroups: 20,
      },
    };
    (generatePuzzle as jest.Mock).mockReturnValue(mockPuzzle);

    const response = await request(app).get(`/api/puzzle?seed=${seed}`);

    expect(response.status).toBe(200);
    expect(generatePuzzle).toHaveBeenCalledWith(seed);
    expect(response.body).toEqual(mockPuzzle);
  });

  test('GET /api/puzzle with invalid seed returns 400', async () => {
    const response = await request(app).get('/api/puzzle?seed=invalid');

    expect(response.status).toBe(400);
    expect(response.body.error).toContain('Seed must be a positive integer');
  });

  test('GET /health returns 200 with health status', async () => {
    const response = await request(app).get('/health');

    expect(response.status).toBe(200);
    expect(response.body).toHaveProperty('status', 'healthy');
    expect(response.body).toHaveProperty('timestamp');
  });
});