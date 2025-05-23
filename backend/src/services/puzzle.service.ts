import puzzleData from '@data/puzzleData.json';
import { shuffle } from '@utils/shuffle';
import { PuzzleGroup, PuzzleResponse } from '@interfaces/puzzle.types';
import { PUZZLE_CONFIG, logger } from '@config/config';

// Validate data immediately when module loads
const validatedData = (() => {
  if (puzzleData.length < PUZZLE_CONFIG.REQUIRED_GROUPS) {
    throw new Error(`Minimum ${PUZZLE_CONFIG.REQUIRED_GROUPS} groups required`);
  }
  return puzzleData;
})();

logger.info(`Puzzle service loaded: ${validatedData.length} groups`);

export const generatePuzzle = (seed?: number): PuzzleResponse => {
  const selectedGroups = selectRandomGroups(validatedData, seed);

  return {
    puzzleWords: shuffleWords(selectedGroups, seed),
    categories: selectedGroups.map((g) => g.theme),
    meta: {
      seed,
      generatedAt: new Date().toISOString(),
      totalGroups: validatedData.length,
    },
  };
};

const selectRandomGroups = (groups: PuzzleGroup[], seed?: number) => {
  return shuffle([...groups], seed).slice(0, PUZZLE_CONFIG.REQUIRED_GROUPS);
};

const shuffleWords = (groups: PuzzleGroup[], seed?: number) => {
  return shuffle(
    groups.flatMap((g) => g.words),
    seed
  );
};
