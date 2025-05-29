import { puzzleService } from '@services/puzzle.service';
import { Difficulty } from '@interfaces/puzzle.types';

describe('PuzzleService', () => {
  beforeEach(() => {
    puzzleService.clearAllSessions();
  });

  afterAll(() => {
    puzzleService.clearAllSessions();
    puzzleService.stopCleanupInterval();
  });

  describe('generatePuzzle', () => {
    it('should generate a basic puzzle', () => {
      const puzzle = puzzleService.generatePuzzle();

      expect(puzzle).toHaveProperty('puzzleWords');
      expect(puzzle).toHaveProperty('categories');
      expect(puzzle).toHaveProperty('meta');
      expect(puzzle.puzzleWords).toHaveLength(16); // 4 groups × 4 words
      expect(puzzle.categories).toHaveLength(4);
      expect(puzzle.meta).toHaveProperty('puzzleId');
      expect(puzzle.meta).toHaveProperty('generatedAt');
      expect(puzzle.meta).toHaveProperty('totalGroups');
    });

    it('should generate consistent puzzles with same seed', () => {
      const puzzle1 = puzzleService.generatePuzzle(123);
      const puzzle2 = puzzleService.generatePuzzle(123);

      expect(puzzle1.puzzleWords).toEqual(puzzle2.puzzleWords);
      expect(puzzle1.categories).toEqual(puzzle2.categories);
    });

    it('should generate different puzzles with different seeds', () => {
      const puzzle1 = puzzleService.generatePuzzle(123);
      const puzzle2 = puzzleService.generatePuzzle(456);

      expect(puzzle1.puzzleWords).toHaveLength(puzzle2.puzzleWords.length);
      expect(puzzle1.puzzleWords).not.toEqual(puzzle2.puzzleWords);
    });
  });

  describe('generateEnhancedPuzzle', () => {
    it('should return puzzle with all enhanced properties', () => {
      const puzzle = puzzleService.generateEnhancedPuzzle();

      expect(puzzle).toHaveProperty('puzzleWords');
      expect(puzzle).toHaveProperty('categories');
      expect(puzzle).toHaveProperty('solution');
      expect(puzzle).toHaveProperty('wordToCategory');
      expect(puzzle).toHaveProperty('groups');
      expect(puzzle).toHaveProperty('difficultyOrder');
      expect(puzzle).toHaveProperty('meta');

      expect(puzzle.groups).toHaveLength(4);
      expect(puzzle.difficultyOrder.length).toBeGreaterThan(0);
      expect(Object.values(Difficulty)).toContain(puzzle.difficultyOrder[0]);
    });

    it('should have consistent word mappings', () => {
      const puzzle = puzzleService.generateEnhancedPuzzle();

      Object.entries(puzzle.solution).forEach(([category, words]) => {
        words.forEach((word) => {
          expect(puzzle.wordToCategory[word]).toBe(category);
        });
      });

      puzzle.puzzleWords.forEach((word) => {
        expect(puzzle.wordToCategory).toHaveProperty(word);
      });
    });

    it('should generate consistent puzzles with same seed', () => {
      const puzzle1 = puzzleService.generateEnhancedPuzzle(123);
      const puzzle2 = puzzleService.generateEnhancedPuzzle(123);

      expect(puzzle1.puzzleWords).toEqual(puzzle2.puzzleWords);
      expect(puzzle1.groups).toEqual(puzzle2.groups);
      expect(puzzle1.solution).toEqual(puzzle2.solution);
      expect(puzzle1.difficultyOrder).toEqual(puzzle2.difficultyOrder);
    });

    it('should have valid difficulty order', () => {
      const puzzle = puzzleService.generateEnhancedPuzzle();

      puzzle.difficultyOrder.forEach((difficulty) => {
        expect(Object.values(Difficulty)).toContain(difficulty);
      });

      const uniqueDifficulties = [...new Set(puzzle.difficultyOrder)];
      expect(uniqueDifficulties).toEqual(puzzle.difficultyOrder);
    });
  });

  describe('createSession', () => {
    it('should create a session and return session ID', () => {
      const puzzle = puzzleService.generateEnhancedPuzzle();
      const sessionId = puzzleService.createSession(puzzle);

      expect(typeof sessionId).toBe('string');
      expect(sessionId).toBe(puzzle.meta.puzzleId);
    });

    it('should create multiple unique sessions', () => {
      const puzzle1 = puzzleService.generateEnhancedPuzzle();
      const puzzle2 = puzzleService.generateEnhancedPuzzle();

      const sessionId1 = puzzleService.createSession(puzzle1);
      const sessionId2 = puzzleService.createSession(puzzle2);

      expect(sessionId1).not.toBe(sessionId2);
    });
  });

  describe('validateGuess', () => {
    let puzzle: any;
    let sessionId: string;

    beforeEach(() => {
      puzzle = puzzleService.generateEnhancedPuzzle();
      sessionId = puzzleService.createSession(puzzle);
    });

    it('should validate correct guesses', () => {
      const correctWords = puzzle.groups[0].words;

      const response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: correctWords,
      });

      expect(response.isCorrect).toBe(true);
      expect(response.category).toBe(puzzle.groups[0].theme);
      expect(response.remainingAttempts).toBe(4);
      expect(response.solvedCategories).toContain(puzzle.groups[0].theme);
      expect(response.isOneAway).toBe(false);
    });

    it('should detect "one away" guesses', () => {
      const correctWords = puzzle.groups[0].words.slice(0, 3);
      const wrongWord = 'WrongWord';

      const response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: [...correctWords, wrongWord],
      });

      expect(response.isCorrect).toBe(false);
      expect(response.isOneAway).toBe(true);
      expect(response.remainingAttempts).toBe(3);
      expect(response.solvedCategories).toEqual([]);
    });

    it('should handle completely wrong guesses', () => {
      const wrongWords = ['Wrong1', 'Wrong2', 'Wrong3', 'Wrong4'];

      const response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: wrongWords,
      });

      expect(response.isCorrect).toBe(false);
      expect(response.isOneAway).toBe(false);
      expect(response.remainingAttempts).toBe(3);
      expect(response.solvedCategories).toEqual([]);
    });

    it('should reject guesses with wrong number of words', () => {
      const response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: ['Word1', 'Word2', 'Word3'], // Only 3 words
      });

      expect(response.isCorrect).toBe(false);
      expect(response.isOneAway).toBe(false);
    });

    it('should throw error for invalid session', () => {
      expect(() => {
        puzzleService.validateGuess({
          puzzleId: 'invalid-session-id',
          words: ['Word1', 'Word2', 'Word3', 'Word4'],
        });
      }).toThrow('Invalid puzzle session');
    });

    it('should track remaining attempts', () => {
      const wrongWords = ['Wrong1', 'Wrong2', 'Wrong3', 'Wrong4'];

      // First wrong guess
      let response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: wrongWords,
      });
      expect(response.remainingAttempts).toBe(3);

      // Second wrong guess
      response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: wrongWords,
      });
      expect(response.remainingAttempts).toBe(2);

      // Third wrong guess
      response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: wrongWords,
      });
      expect(response.remainingAttempts).toBe(1);

      // Fourth wrong guess
      response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: wrongWords,
      });
      expect(response.remainingAttempts).toBe(0);
    });

    it('should accumulate solved categories', () => {
      // Solve first category
      const firstCorrectWords = puzzle.groups[0].words;
      let response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: firstCorrectWords,
      });

      expect(response.isCorrect).toBe(true);
      expect(response.solvedCategories).toContain(puzzle.groups[0].theme);

      // Solve second category
      const secondCorrectWords = puzzle.groups[1].words;
      response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: secondCorrectWords,
      });

      expect(response.isCorrect).toBe(true);
      expect(response.solvedCategories).toHaveLength(2);
      expect(response.solvedCategories).toContain(puzzle.groups[0].theme);
      expect(response.solvedCategories).toContain(puzzle.groups[1].theme);
    });
  });

  describe('cleanupExpiredSessions', () => {
    it('should remove expired sessions', (done) => {
      const puzzle = puzzleService.generateEnhancedPuzzle();
      const sessionId = puzzleService.createSession(puzzle);

      // Verify session exists
      expect(() => {
        puzzleService.validateGuess({
          puzzleId: sessionId,
          words: puzzle.groups[0].words,
        });
      }).not.toThrow();

      // Mock expiration by modifying the generatedAt timestamp
      const session = puzzleService['sessions'].get(sessionId);
      if (session) {
        session.puzzle.meta.generatedAt = new Date(
          Date.now() - 31 * 60 * 1000
        ).toISOString();
      }

      puzzleService.cleanupExpiredSessions();

      expect(() => {
        puzzleService.validateGuess({
          puzzleId: sessionId,
          words: puzzle.groups[0].words,
        });
      }).toThrow('Invalid puzzle session');
      done();
    });
  });

  describe('edge cases', () => {
    it('should handle case-insensitive word matching', () => {
      const puzzle = puzzleService.generateEnhancedPuzzle();
      const sessionId = puzzleService.createSession(puzzle);

      const correctWords = puzzle.groups[0].words.map((word) =>
        word.toLowerCase()
      );

      const response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: correctWords,
      });

      expect(response.isCorrect).toBe(false); // Assuming case-sensitive
    });

    it('should handle duplicate words in guess', () => {
      const puzzle = puzzleService.generateEnhancedPuzzle();
      const sessionId = puzzleService.createSession(puzzle);

      const duplicateWords = [
        puzzle.groups[0].words[0],
        puzzle.groups[0].words[0], // Duplicate
        puzzle.groups[0].words[1],
        puzzle.groups[0].words[2],
      ];

      const response = puzzleService.validateGuess({
        puzzleId: sessionId,
        words: duplicateWords,
      });

      expect(response.isCorrect).toBe(false);
    });
  });
});
