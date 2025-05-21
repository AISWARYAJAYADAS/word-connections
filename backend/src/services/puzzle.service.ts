import puzzleData from '@data/puzzleData.json';
import { shuffle } from '@utils/shuffle';
import { PuzzleGroup, PuzzleResponse } from '@interfaces/puzzle.types';
import { PUZZLE_CONFIG, logger } from '@config/config';

logger.info(`Puzzle service loaded: ${puzzleData.length} groups`);

export const generatePuzzle = (seed?: number): PuzzleResponse => {
  validateDataStructure(puzzleData);
  const selectedGroups = selectRandomGroups(puzzleData, seed);

  return {
    puzzleWords: shuffleWords(selectedGroups, seed),
    categories: selectedGroups.map((g) => g.theme),
    meta: {
      seed,
      generatedAt: new Date().toISOString(),
      totalGroups: puzzleData.length,
    },
  };
};

const validateDataStructure = (groups: PuzzleGroup[]) => {
  if (!Array.isArray(groups)) {
    throw new Error('Puzzle data must be an array');
  }

  if (groups.length < PUZZLE_CONFIG.REQUIRED_GROUPS) {
    throw new Error(`Minimum ${PUZZLE_CONFIG.REQUIRED_GROUPS} groups required`);
  }

  const allWords = new Set<string>();

  for (const group of groups) {
    if (!group || typeof group !== 'object' || !group.theme || !group.words) {
      throw new Error('Each group must have theme and words properties');
    }

    if (
      typeof group.theme !== 'string' ||
      group.theme.trim() === '' ||
      group.theme.length > PUZZLE_CONFIG.MAX_THEME_LENGTH
    ) {
      throw new Error(`Invalid theme: ${group.theme || 'empty'}`);
    }

    if (
      !Array.isArray(group.words) ||
      group.words.length !== PUZZLE_CONFIG.WORDS_PER_GROUP
    ) {
      throw new Error(
        `Group "${group.theme}" must have exactly ${PUZZLE_CONFIG.WORDS_PER_GROUP} words`,
      );
    }

    for (const word of group.words) {
      if (
        typeof word !== 'string' ||
        word.trim() === '' ||
        word.length > PUZZLE_CONFIG.MAX_WORD_LENGTH
      ) {
        throw new Error(`Invalid word in group "${group.theme}": ${word || 'empty'}`);
      }

      const wordLower = word.toLowerCase();
      if (allWords.has(wordLower)) {
        throw new Error(`Duplicate word found: ${word}`);
      }
      allWords.add(wordLower);
    }
  }
};

const selectRandomGroups = (groups: PuzzleGroup[], seed?: number) => {
  return shuffle([...groups], seed).slice(0, PUZZLE_CONFIG.REQUIRED_GROUPS);
};

const shuffleWords = (groups: PuzzleGroup[], seed?: number) => {
  return shuffle(groups.flatMap((g) => g.words), seed);
};