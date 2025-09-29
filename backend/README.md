# Word Connections Backend API

[![TypeScript](https://img.shields.io/badge/TypeScript-4.0+-007ACC.svg)](https://www.typescriptlang.org/)
[![Node.js](https://img.shields.io/badge/Node.js-16+-339933.svg)](https://nodejs.org/)
[![Express](https://img.shields.io/badge/Express-4.x-000000.svg)](https://expressjs.com/)

A robust backend API for a Word Connections puzzle game inspired by NYT Connections. Generate and validate word puzzles with intelligent categorized groups, difficulty levels, and real-time validation.

## 🌟 Features

- **Dynamic Puzzle Generation** - Create unique word puzzles with seeded randomization
- **Intelligent Categorization** - Four difficulty levels with themed word groups
- **Real-time Validation** - Instant feedback on player guesses with "one away" hints
- **Session Management** - Stateful puzzle sessions with attempt tracking
- **Production Ready** - Comprehensive logging, error handling, and health monitoring
- **Fully Tested** - Complete test coverage with Jest
- **Type Safe** - Built with TypeScript for robust development

## 🏗️ Architecture

### Project Structure
```
src/
├── config/       # Configuration management
├── controllers/  # HTTP request handlers  
├── data/         # Static puzzle datasets
├── interfaces/   # TypeScript type definitions
├── services/     # Core business logic
├── utils/        # Utility functions & helpers
├── app.ts        # Express application setup
└── server.ts     # Application entry point
```

### Complete Folder Structure
```
word-connections/
└── backend/
    ├── .env                    # Environment variables
    ├── .env.example           # Environment template
    ├── .eslintrc.config.mjs   # ESLint configuration
    ├── .gitignore             # Git ignore rules
    ├── .prettierrc            # Code formatting rules
    ├── package.json           # Dependencies & scripts
    ├── package-lock.json      # Dependency lock file
    ├── README.md              # Project documentation
    ├── render.yaml            # Deployment configuration
    ├── tsconfig.json          # TypeScript configuration
    ├── coverage/              # Test coverage reports
    ├── dist/                  # Compiled JavaScript output
    ├── logs/                  # Application log files
    ├── node_modules/          # Installed dependencies
    ├── scripts/
    │   └── copy-assets.js     # Build asset management
    └── src/
        ├── config/
        │   ├── config.ts           # App configuration
        │   └── module-alias.ts     # Path aliasing
        ├── controllers/
        │   └── puzzle.controller.ts # API route handlers
        ├── data/
        │   └── puzzleData.json     # Word puzzle datasets
        ├── interfaces/
        │   └── puzzle.types.ts     # Type definitions
        ├── services/
        │   └── puzzle.service.ts   # Business logic
        ├── tests/
        │   ├── setup.ts                    # Test configuration
        │   ├── puzzle.controller.test.ts   # Controller tests
        │   └── puzzle.service.test.ts      # Service tests
        ├── utils/
        │   └── shuffle.ts          # Utility functions
        ├── app.ts                  # Express app setup
        └── server.ts               # Server entry point
```

## 🚀 API Reference

### Get Basic Puzzle
```http
GET /api/puzzle?seed=123
```

**Response:**
```json
{
  "puzzleWords": ["Pancake", "Waffle", "Omelet", "Cereal", "Function", "Variable", "Array", "Object"],
  "categories": ["Breakfast Foods", "Programming Terms", "Ocean Creatures", "Musical Instruments"],
  "meta": {
    "seed": 123,
    "generatedAt": "2023-10-20T12:00:00Z",
    "totalGroups": 4,
    "puzzleId": "uuid-1234"
  }
}
```

### Get Enhanced Puzzle
```http
GET /api/puzzle/enhanced?seed=123
```

**Response:**
```json
{
  "puzzleWords": ["Pancake", "Waffle", "Omelet", "Cereal"],
  "groups": [
    {
      "theme": "Breakfast Foods",
      "words": ["Pancake", "Waffle", "Omelet", "Cereal"],
      "difficulty": "yellow"
    }
  ],
  "solution": {
    "Breakfast Foods": ["Pancake", "Waffle", "Omelet", "Cereal"]
  },
  "difficultyOrder": ["yellow", "green", "blue", "purple"],
  "meta": {
    "seed": 123,
    "generatedAt": "2023-10-20T12:00:00Z",
    "puzzleId": "uuid-1234"
  }
}
```

### Validate Guess
```http
POST /api/puzzle/validate
Content-Type: application/json

{
  "puzzleId": "uuid-1234",
  "words": ["Pancake", "Waffle", "Omelet", "Cereal"]
}
```

**Success Response:**
```json
{
  "isCorrect": true,
  "category": "Breakfast Foods",
  "remainingAttempts": 4,
  "solvedCategories": ["Breakfast Foods"],
  "isOneAway": false
}
```

**Error Response:**
```json
{
  "error": "Invalid puzzle session",
  "timestamp": "2023-10-20T12:00:00Z",
  "statusCode": 400
}
```

### Health Check
```http
GET /health
```

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2023-10-20T12:00:00Z",
  "uptime": 123.45,
  "version": "1.0.0"
}
```

## 📋 API Endpoints Summary

| Method | Endpoint                | Description                                    | Parameters |
|--------|-------------------------|------------------------------------------------|------------|
| GET    | `/api/puzzle`           | Get basic puzzle with words and categories     | `?seed=123` |
| GET    | `/api/puzzle/enhanced`  | Get puzzle with full solution metadata         | `?seed=123` |
| POST   | `/api/puzzle/validate`  | Validate player's word group submission        | JSON body |
| GET    | `/health`               | Service health check and status                | None |

## ⚙️ Configuration

### Environment Variables (.env)

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `10000` | Server port number |
| `NODE_ENV` | `development` | Runtime environment |
| `LOG_LEVEL` | `info` | Logging verbosity level |
| `ALLOWED_ORIGINS` | `*` | CORS allowed origins |
| `RATE_LIMIT_MAX` | `100` | Max requests per 15 minutes |
| `PUZZLE_EXPIRE_MINUTES` | `30` | Puzzle session timeout |
| `MAX_ATTEMPTS` | `4` | Maximum guesses per puzzle |

### Example Configuration
```ini
PORT=10000
NODE_ENV=development
LOG_LEVEL=info
ALLOWED_ORIGINS=http://localhost:3000,https://yourapp.com
RATE_LIMIT_MAX=100
PUZZLE_EXPIRE_MINUTES=30
MAX_ATTEMPTS=4
```

## 🛠️ Development

### Quick Start
```bash
# Clone and install dependencies
npm install

# Copy environment template
cp .env.example .env

# Start development server with hot-reload
npm run dev
```

### Available Scripts
```bash
# Development
npm run dev          # Start development server with hot-reload
npm start           # Start production server

# Testing
npm test            # Run all tests
npm run test:cov    # Run tests with coverage report
npm run test:watch  # Run tests in watch mode

# Code Quality
npm run lint        # Run ESLint checks
npm run lint:fix    # Fix ESLint issues automatically
npm run format      # Format code with Prettier

# Build
npm run build       # Compile TypeScript to JavaScript
npm run clean       # Clean build artifacts
```

### Development Workflow
1. **Setup Environment** - Configure `.env` file with your settings
2. **Start Development** - Run `npm run dev` for hot-reload development
3. **Write Tests** - Add tests in `src/tests/` directory
4. **Code Quality** - Use `npm run lint` and `npm run format` before commits
5. **Build & Test** - Run `npm run build` and `npm test` before deployment

## 🚀 Deployment

### Render.com (Recommended)
Pre-configured for seamless deployment:

1. **Connect Repository** - Link your GitHub repository to Render
2. **Automatic Builds** - Production builds trigger on push to main branch
3. **Health Monitoring** - Built-in health checks at `/health` endpoint
4. **Environment Management** - Configure variables via Render dashboard

### Deployment Configuration (render.yaml)
```yaml
services:
  - type: web
    name: word-connections-backend
    env: node
    buildCommand: npm run render-build
    startCommand: npm start
    envVars:
      - key: NODE_ENV
        value: production
      - key: PORT
        value: 10000
    healthCheckPath: /health
    autoDeploy: true
```

### Manual Deployment
```bash
# Build for production
npm run build

# Set production environment
export NODE_ENV=production

# Start production server
npm start
```

## 🧪 Testing

### Test Structure
- **Unit Tests** - Service logic and utility functions
- **Integration Tests** - API endpoints and request/response cycles
- **Coverage Reports** - Comprehensive test coverage tracking

### Running Tests
```bash
# Run all tests
npm test

# Watch mode during development
npm run test:watch

# Generate coverage report
npm run test:cov

# View coverage in browser
open coverage/lcov-report/index.html
```

## 🔧 Tech Stack

### Core Technologies
- **TypeScript** - Type-safe JavaScript development
- **Node.js 16+** - JavaScript runtime environment
- **Express.js 4.x** - Web application framework

### Development Tools
- **Jest** - Testing framework with coverage reporting
- **ESLint** - Code linting and style enforcement
- **Prettier** - Code formatting and consistency
- **Winston** - Structured logging with multiple transports

### Production Features
- **CORS** - Cross-origin resource sharing configuration
- **Rate Limiting** - API abuse prevention
- **Health Checks** - Service monitoring and uptime tracking
- **Error Handling** - Comprehensive error catching and reporting

## 📊 Game Mechanics

### Difficulty Levels
- **Yellow** - Easiest category (most straightforward connections)
- **Green** - Medium difficulty 
- **Blue** - Harder connections (more abstract relationships)
- **Purple** - Hardest category (trickiest wordplay/connections)

### Puzzle Features
- **Seeded Generation** - Reproducible puzzles using seed values
- **Session Management** - Track player progress and attempts
- **One Away Hints** - Feedback when player is close to correct answer
- **Attempt Limiting** - Maximum 4 incorrect guesses per puzzle



---

