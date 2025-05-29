import { randomInt } from 'crypto';

export const shuffle = <T>(array: T[], seed?: number): T[] => {
  const shuffled = [...array];

  // Seeded shuffle
  if (seed !== undefined) {
    let state = seed;
    for (let i = shuffled.length - 1; i > 0; i--) {
      state = (state * 1664525 + 1013904223) % 2 ** 32;
      const j = Math.floor((state / 2 ** 32) * (i + 1));
      [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
  }

  // Crypto-shuffle
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = randomInt(0, i + 1);
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};
