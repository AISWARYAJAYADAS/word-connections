import type { PuzzleResponse } from '@interfaces/puzzle.types';

// Mock the puzzle data before any imports
jest.mock(
  '@data/puzzleData.json',
  () => [
    { theme: 'Colors', words: ['Red', 'Blue', 'Green', 'Yellow'] },
    { theme: 'Genres', words: ['Action', 'Drama', 'Horror', 'Comedy'] },
    { theme: 'Fruits', words: ['Apple', 'Banana', 'Grape', 'Mango'] },
    { theme: 'Emotions', words: ['Happy', 'Sad', 'Angry', 'Calm'] },
  ],
  { virtual: true }
);

describe('Puzzle Service', () => {
  let puzzleService: typeof import('@services/puzzle.service');

  beforeAll(() => {
    // Import the service after mocking
    puzzleService = require('@services/puzzle.service');
  });

  afterAll(() => {
    jest.unmock('@data/puzzleData.json');
  });

  describe('with valid data', () => {
    test('returns valid puzzle with no seed', () => {
      const puzzle: PuzzleResponse = puzzleService.generatePuzzle();
      expect(puzzle.puzzleWords).toHaveLength(16); // 4 groups × 4 words
      expect(puzzle.categories).toHaveLength(4);
    });

    test('returns consistent puzzle with seed', () => {
      const seed = 123;
      const puzzle1 = puzzleService.generatePuzzle(seed);
      const puzzle2 = puzzleService.generatePuzzle(seed);
      expect(puzzle1.puzzleWords).toEqual(puzzle2.puzzleWords);
      expect(puzzle1.categories).toEqual(puzzle2.categories);
    });
  });

  describe('with invalid data', () => {
    beforeAll(() => {
      jest.resetModules();
      jest.mock('@data/puzzleData.json', () => [], { virtual: true });
    });

    test('fails to load with insufficient groups', () => {
      expect(() => {
        require('@services/puzzle.service');
      }).toThrow('Minimum 4 groups required');
    });

    afterAll(() => {
      jest.unmock('@data/puzzleData.json');
    });
  });
});
