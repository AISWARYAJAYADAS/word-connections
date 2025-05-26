import { randomInt } from 'crypto';

/**
 * Fisher-Yates shuffle with seed support
 * Uses cryptographic randomness if seed is undefined
 * @template T
 * @param {T[]} array - The array to shuffle
 * @param {number} [seed] - Optional seed for deterministic shuffling
 * @returns {T[]} The shuffled array
 */
export const shuffle = <T>(array: T[], seed?: number): T[] => {
  const shuffled = [...array];

  // Use crypto-grade randomness if no seed provided
  if (seed === undefined) {
    return cryptoShuffle(shuffled);
  }

  // Seeded shuffle (deterministic)
  const random = seededRandom(seed);
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

/**
 * Cryptographically secure shuffle using Node.js crypto module
 * @template T
 * @param {T[]} array - The array to shuffle
 * @returns {T[]} The shuffled array
 */
const cryptoShuffle = <T>(array: T[]): T[] => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = randomInt(0, i + 1); // Using imported randomInt directly
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
};

/**
 * Seeded random number generator (Linear Congruential Generator)
 * @param {number} seed - The seed value
 * @returns {() => number} A random number generator function
 */
const seededRandom = (seed: number): (() => number) => {
  const m = 2 ** 35 - 31;
  const a = 185852;
  let state = seed % m;

  return (): number => {
    state = (state * a) % m;
    return state / m;
  };
};
