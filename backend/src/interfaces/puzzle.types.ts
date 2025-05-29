export enum Difficulty {
  YELLOW = 'yellow',
  GREEN = 'green',
  BLUE = 'blue',
  PURPLE = 'purple',
}

export interface PuzzleGroup {
  theme: string;
  words: string[];
  difficulty: Difficulty;
}

export interface PuzzleResponse {
  puzzleWords: string[];
  categories: string[];
  meta: {
    seed?: number;
    generatedAt: string;
    totalGroups: number;
    puzzleId: string;
  };
}

export interface EnhancedPuzzleResponse extends PuzzleResponse {
  solution: Record<string, string[]>;
  wordToCategory: Record<string, string>;
  groups: PuzzleGroup[];
  difficultyOrder: Difficulty[];
}

export interface ValidationRequest {
  words: string[];
  puzzleId: string;
}

export interface ValidationResponse {
  isCorrect: boolean;
  category?: string;
  remainingAttempts: number;
  isOneAway: boolean; // Make required, not optional
  solvedCategories: string[];
  isGameComplete?: boolean; // Add game completion status
  allSolved?: boolean; // Add all categories solved status
}

export interface GameSession {
  puzzle: EnhancedPuzzleResponse;
  remainingAttempts: number;
  solvedCategories: string[];
}

export interface PuzzleError {
  error: string;
  timestamp: string;
}
