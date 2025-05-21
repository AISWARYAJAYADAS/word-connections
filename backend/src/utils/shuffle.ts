export const shuffle = <T>(array: T[], seed?: number): T[] => {
  const shuffled = [...array];
  const random = seed !== undefined ? seededRandom(seed) : Math.random;

  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }

  return shuffled;
};

const seededRandom = (seed: number) => {
  const m = 2 ** 35 - 31;
  const a = 185852;
  let state = seed % m;
  return () => {
    state = (state * a) % m;
    return state / m;
  };
};