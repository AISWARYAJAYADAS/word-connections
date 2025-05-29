import puzzleData from '@data/puzzleData.json';
import { shuffle } from '@utils/shuffle';
import {
  PuzzleGroup,
  PuzzleResponse,
  EnhancedPuzzleResponse,
  Difficulty,
  GameSession,
  ValidationRequest,
  ValidationResponse,
} from '@interfaces/puzzle.types';
import { PUZZLE_CONFIG } from '@config/config';
import { v4 as uuidv4 } from 'uuid';

// Validate and transform data
const validatedData: PuzzleGroup[] = puzzleData.map((group) => ({
  ...group,
  difficulty: group.difficulty.toLowerCase() as Difficulty,
}));

export class PuzzleService {
  private sessions = new Map<string, GameSession>();
  private cleanupInterval?: NodeJS.Timeout;

  constructor() {
    this.startCleanupInterval();
  }

  startCleanupInterval(): void {
    this.cleanupInterval = setInterval(
      () => {
        this.cleanupExpiredSessions();
      },
      30 * 60 * 1000
    );
  }

  stopCleanupInterval(): void {
    if (this.cleanupInterval) {
      clearInterval(this.cleanupInterval);
    }
  }

  generatePuzzle(seed?: number): PuzzleResponse {
    const selectedGroups = this.selectRandomGroups(validatedData, seed);
    return {
      puzzleWords: this.shuffleWords(selectedGroups, seed),
      categories: selectedGroups.map((g) => g.theme),
      meta: {
        seed,
        generatedAt: new Date().toISOString(),
        totalGroups: validatedData.length,
        puzzleId: uuidv4(),
      },
    };
  }

  generateEnhancedPuzzle(seed?: number): EnhancedPuzzleResponse {
    const selectedGroups = this.selectRandomGroups(validatedData, seed);
    const shuffledWords = this.shuffleWords(selectedGroups, seed);

    const solution: Record<string, string[]> = {};
    const wordToCategory: Record<string, string> = {};

    selectedGroups.forEach((group) => {
      solution[group.theme] = group.words;
      group.words.forEach((word) => {
        wordToCategory[word] = group.theme;
      });
    });

    return {
      puzzleWords: shuffledWords,
      categories: selectedGroups.map((g) => g.theme),
      solution,
      wordToCategory,
      groups: selectedGroups,
      difficultyOrder: this.getDifficultyOrder(selectedGroups),
      meta: {
        seed,
        generatedAt: new Date().toISOString(),
        totalGroups: validatedData.length,
        puzzleId: uuidv4(),
      },
    };
  }

  createSession(puzzle: EnhancedPuzzleResponse): string {
    const sessionId = puzzle.meta.puzzleId;
    this.sessions.set(sessionId, {
      puzzle,
      remainingAttempts: PUZZLE_CONFIG.MAX_ATTEMPTS,
      solvedCategories: [],
    });
    return sessionId;
  }

  validateGuess(request: ValidationRequest): ValidationResponse {
    const session = this.sessions.get(request.puzzleId);
    if (!session) {
      throw new Error('Invalid puzzle session');
    }

    const { puzzle, remainingAttempts, solvedCategories } = session;
    const { words } = request;

    if (words.length !== 4) {
      return {
        isCorrect: false,
        remainingAttempts,
        solvedCategories,
        isOneAway: false,
      };
    }

    // Check for exact match
    for (const [category, correctWords] of Object.entries(puzzle.solution)) {
      if (this.arraysEqual(words.sort(), correctWords.sort())) {
        const updatedSolved = [...solvedCategories, category];
        this.sessions.set(request.puzzleId, {
          ...session,
          solvedCategories: updatedSolved,
        });

        return {
          isCorrect: true,
          category,
          remainingAttempts,
          solvedCategories: updatedSolved,
          isOneAway: false,
          isGameComplete: updatedSolved.length === 4,
          allSolved: updatedSolved.length === 4,
        };
      }
    }

    // Check for "one away"
    const isOneAway = Object.values(puzzle.solution).some((correctWords) => {
      return words.filter((w) => correctWords.includes(w)).length === 3;
    });

    const newAttempts = Math.max(0, remainingAttempts - 1);
    this.sessions.set(request.puzzleId, {
      ...session,
      remainingAttempts: newAttempts,
    });

    return {
      isCorrect: false,
      remainingAttempts: newAttempts,
      isOneAway,
      solvedCategories,
      isGameComplete: false,
      allSolved: false,
    };
  }

  cleanupExpiredSessions(): void {
    const now = Date.now();
    const expireTime = PUZZLE_CONFIG.SESSION_EXPIRE_MINUTES * 60 * 1000;

    for (const [sessionId, session] of this.sessions.entries()) {
      const sessionAge =
        now - new Date(session.puzzle.meta.generatedAt).getTime();
      if (sessionAge > expireTime) {
        this.sessions.delete(sessionId);
      }
    }
  }

  clearAllSessions(): void {
    this.sessions.clear();
  }

  getSessionCount(): number {
    return this.sessions.size;
  }

  hasSession(sessionId: string): boolean {
    return this.sessions.has(sessionId);
  }

  private selectRandomGroups(
    groups: PuzzleGroup[],
    seed?: number
  ): PuzzleGroup[] {
    const shuffled = shuffle([...groups], seed);
    return shuffled.slice(0, PUZZLE_CONFIG.REQUIRED_GROUPS);
  }

  private shuffleWords(groups: PuzzleGroup[], seed?: number): string[] {
    const allWords = groups.flatMap((g) => g.words);
    return shuffle(allWords, seed);
  }

  private getDifficultyOrder(groups: PuzzleGroup[]): Difficulty[] {
    const difficulties = [...new Set(groups.map((g) => g.difficulty))];
    return difficulties.sort(
      (a, b) =>
        Object.values(Difficulty).indexOf(a) -
        Object.values(Difficulty).indexOf(b)
    );
  }

  private arraysEqual(a: string[], b: string[]): boolean {
    return a.length === b.length && a.every((val, i) => val === b[i]);
  }
}

export const puzzleService = new PuzzleService();
