import express from 'express';
import cors from 'cors';

const app = express();
const PORT = 3000;

app.use(cors());

app.get('/', (_req, res) => {
  res.send('Server is running!');
});

app.get('/puzzle', (req, res) => {
  res.json({ puzzleWords: ['Tiger', 'Lion', 'Jaguar', 'Panther', 'Apple', 'Banana'] });
});

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});

