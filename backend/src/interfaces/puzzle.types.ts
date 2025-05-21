export interface PuzzleGroup {
  theme: string;
  words: string[];
}

export interface PuzzleResponse {
  puzzleWords: string[];
  categories: string[];
  meta: {
    seed?: number;
    generatedAt: string;
    totalGroups: number;
  };
}

export interface PuzzleError {
  error: string;
  timestamp: string;
}