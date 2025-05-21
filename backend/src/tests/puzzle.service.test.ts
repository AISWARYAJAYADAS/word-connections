import { generatePuzzle } from '@services/puzzle.service';
import { PuzzleResponse } from '@interfaces/puzzle.types';
import puzzleData from '@data/puzzleData.json';

describe('Puzzle Service', () => {
  beforeEach(() => {
    jest.resetModules(); // Reset module cache to clear mocks
  });

  test('generatePuzzle returns valid puzzle with no seed', () => {
    const puzzle: PuzzleResponse = generatePuzzle();
    expect(puzzle.puzzleWords).toHaveLength(16);
    expect(puzzle.categories).toHaveLength(4);
    expect(puzzle.meta.totalGroups).toBe(puzzleData.length);
    expect(puzzle.meta.generatedAt).toBeDefined();
  });

  test('generatePuzzle returns consistent puzzle with seed', () => {
    const seed = 123;
    const puzzle1: PuzzleResponse = generatePuzzle(seed);
    const puzzle2: PuzzleResponse = generatePuzzle(seed);
    expect(puzzle1.puzzleWords).toEqual(puzzle2.puzzleWords);
    expect(puzzle1.categories).toEqual(puzzle2.categories);
  });

  test('generatePuzzle throws error for insufficient groups', () => {
    jest.doMock('@data/puzzleData.json', () => []);
    const { generatePuzzle } = require('@services/puzzle.service');
    expect(() => generatePuzzle()).toThrow('Minimum 4 groups required');
  });
});