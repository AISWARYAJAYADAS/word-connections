package com.aiswarya.wordconnections.data.mapper

import com.aiswarya.wordconnections.data.local.entity.PuzzleEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleGroupEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleWordEntity
import com.aiswarya.wordconnections.data.remote.dto.EnhancedPuzzleResponseDto
import com.aiswarya.wordconnections.domain.model.Difficulty
import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.model.PuzzleEntities
import com.aiswarya.wordconnections.domain.model.PuzzleGroup
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleMapper @Inject constructor() {

    fun toDomain(dto: EnhancedPuzzleResponseDto): Puzzle {
        return Puzzle(
            puzzleId = dto.meta.puzzleId,
            title = "Puzzle ${dto.meta.puzzleId.take(4)}",
            words = dto.puzzleWords,
            groups = dto.groups.map { groupDto ->
                PuzzleGroup(
                    groupId = UUID.randomUUID().toString(),
                    theme = groupDto.theme,
                    words = groupDto.words,
                    difficulty = Difficulty.fromString(groupDto.difficulty),
                    color = groupDto.difficulty
                )
            },
            seed = dto.meta.puzzleId.hashCode(),
            generatedAt = dto.meta.generatedAt,
            difficulty = dto.difficultyOrder.joinToString(", ")
        )
    }

    fun toEntity(dto: EnhancedPuzzleResponseDto): PuzzleEntities {
        val puzzleEntity = PuzzleEntity(
            puzzleId = dto.meta.puzzleId,
            title = "Puzzle ${dto.meta.puzzleId.take(4)}",
            difficulty = dto.difficultyOrder.joinToString(", "),
            createdAt = System.currentTimeMillis(),
            isCompleted = false,
            score = 0,
            remainingAttempts = 4
        )

        val groupEntities = dto.groups.map { group ->
            PuzzleGroupEntity(
                groupId = UUID.randomUUID().toString(),
                puzzleId = dto.meta.puzzleId,
                theme = group.theme,
                difficulty = Difficulty.fromString(group.difficulty).ordinal + 1,
                color = group.difficulty
            )
        }

        val wordEntities = dto.groups.flatMap { group ->
            group.words.mapIndexed { index, word ->
                PuzzleWordEntity(
                    wordId = "${dto.meta.puzzleId}_${group.theme}_$index",
                    puzzleId = dto.meta.puzzleId,
                    groupId = groupEntities.first { it.theme == group.theme }.groupId,
                    word = word,
                    position = index,
                    isSolved = false
                )
            }
        }

        return PuzzleEntities(puzzleEntity, groupEntities, wordEntities)
    }

    fun toDomain(
        puzzleEntity: PuzzleEntity,
        groupEntities: List<PuzzleGroupEntity>,
        wordEntities: List<PuzzleWordEntity>
    ): Puzzle {
        val groups = groupEntities.map { groupEntity ->
            val groupWords = wordEntities
                .filter { it.groupId == groupEntity.groupId }
                .sortedBy { it.position }
                .map { it.word }
            PuzzleGroup(
                groupId = groupEntity.groupId,
                theme = groupEntity.theme,
                words = groupWords,
                difficulty = Difficulty.entries.toTypedArray().getOrElse(groupEntity.difficulty - 1) { Difficulty.YELLOW },
                color = groupEntity.color
            )
        }

        return Puzzle(
            puzzleId = puzzleEntity.puzzleId,
            title = puzzleEntity.title,
            words = wordEntities.map { it.word }.distinct(),
            groups = groups,
            seed = puzzleEntity.puzzleId.hashCode(),
            generatedAt = puzzleEntity.createdAt.toString(),
            difficulty = puzzleEntity.difficulty
        )
    }
}