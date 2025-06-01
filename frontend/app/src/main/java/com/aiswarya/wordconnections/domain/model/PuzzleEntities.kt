package com.aiswarya.wordconnections.domain.model

import com.aiswarya.wordconnections.data.local.entity.PuzzleEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleGroupEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleWordEntity


data class PuzzleEntities(
    val puzzle: PuzzleEntity,
    val groups: List<PuzzleGroupEntity>,
    val words: List<PuzzleWordEntity>
)