import { puzzleService } from '@services/puzzle.service';

// Clear all sessions before each test
beforeEach(() => {
  puzzleService.clearAllSessions();
});

afterAll(async () => {
  puzzleService.clearAllSessions();
  puzzleService.stopCleanupInterval();

  // Close any open handles
  await new Promise<void>((resolve) => {
    setTimeout(() => resolve(), 100);
  });
});

// Suppress console logs during tests
beforeAll(() => {
  jest.spyOn(console, 'log').mockImplementation(() => {});
  jest.spyOn(console, 'warn').mockImplementation(() => {});
  jest.spyOn(console, 'error').mockImplementation(() => {});
});

afterAll(() => {
  jest.restoreAllMocks();
});
