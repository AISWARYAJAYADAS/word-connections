package com.aiswarya.wordconnections.data.local.dao

import androidx.room.*
import com.aiswarya.wordconnections.data.local.entity.PuzzleEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleGroupEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDao {

    // Puzzle operations
    @Query("SELECT * FROM puzzles WHERE puzzleId = :puzzleId")
    suspend fun getPuzzleById(puzzleId: String): PuzzleEntity?

    @Query("SELECT * FROM puzzles ORDER BY createdAt DESC")
    fun getAllPuzzles(): Flow<List<PuzzleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzle: PuzzleEntity)

    @Update
    suspend fun updatePuzzle(puzzle: PuzzleEntity)

    @Delete
    suspend fun deletePuzzle(puzzle: PuzzleEntity)

    // Group operations
    @Query("SELECT * FROM puzzle_groups WHERE puzzleId = :puzzleId")
    suspend fun getGroupsForPuzzle(puzzleId: String): List<PuzzleGroupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<PuzzleGroupEntity>)

    // Word operations
    @Query("SELECT * FROM puzzle_words WHERE puzzleId = :puzzleId ORDER BY position")
    suspend fun getWordsForPuzzle(puzzleId: String): List<PuzzleWordEntity>

    @Query("SELECT * FROM puzzle_words WHERE groupId = :groupId")
    suspend fun getWordsForGroup(groupId: String): List<PuzzleWordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<PuzzleWordEntity>)

    // Complex queries
    @Query("""
        SELECT pw.* FROM puzzle_words pw
        INNER JOIN puzzle_groups pg ON pw.groupId = pg.groupId
        WHERE pw.puzzleId = :puzzleId
        ORDER BY pg.difficulty, pw.position
    """)
    suspend fun getPuzzleWordsWithGroups(puzzleId: String): List<PuzzleWordEntity>

    @Transaction
    suspend fun insertCompletePuzzle(
        puzzle: PuzzleEntity,
        groups: List<PuzzleGroupEntity>,
        words: List<PuzzleWordEntity>
    ) {
        insertPuzzle(puzzle)
        insertGroups(groups)
        insertWords(words)
    }

    @Query("SELECT DISTINCT theme FROM puzzle_groups WHERE puzzleId = :puzzleId")
    suspend fun getSolvedCategories(puzzleId: String): List<String>
}