 Backend API for the Word Connections game, inspired by NYT Connections. Built with Node.js, Express, and TypeScript.

 ## Setup

 1. **Install Dependencies**:
    - Open a terminal in the `backend` folder.
    - Run: `npm install`

 2. **Create `.env` File**:
    - Create a file named `.env` in the `backend` folder.
    - Add:
      ```env
      PORT=3000
      ALLOWED_ORIGINS=http://localhost:3000
      NODE_ENV=development
      ```

 3. **Run the Server**:
    - Run: `npm run dev`

 4. **Test the API**:
    - Open a browser or Postman.
    - Visit: `http://localhost:3000/api/puzzle` or `http://localhost:3000/health`

 ## API Endpoints

 - `GET /api/puzzle`: Returns a puzzle with optional `seed` query (e.g., `/api/puzzle?seed=123`).
 - `GET /health`: Returns server status.

 ## Folder Structure

 ```
backend/
├── src/
│   ├── config/
│   │   └── config.ts
│   ├── controllers/
│   │   └── puzzle.controller.ts
│   ├── data/
│   │   └── puzzleData.json
│   ├── services/
│   │   └── puzzle.service.ts
│   ├── interfaces/
│   │   └── puzzle.types.ts
│   ├── utils/
│   │   └── shuffle.ts
│   ├── tests/
│   │   └── puzzle.controller.test.ts
│   │   └── puzzle.service.test.ts
│   ├── app.ts
│   ├── server.ts
├── .env
├── .eslintrc.js
├── .gitignore
├── .prettierrc
├── package.json
├── tsconfig.json
└── README.md
 ```

 ## Scripts

 - `npm start`: Run the compiled server.
 - `npm run dev`: Run with nodemon for development.
 - `npm run build`: Compile TypeScript to JavaScript.
 - `npm test`: Run tests with Jest.
 - `npm run lint`: Run ESLint to check code.
 - `npm run format`: Run Prettier to format code.