package com.example.pants.domain.usecases

import com.example.pants.domain.model.ColorModel
import com.example.pants.domain.repository.ColorRepository

class GetColorBoardUseCase(
    private val colorRepository: ColorRepository,
) {

    suspend operator fun invoke(colorCount: Int): Result<Set<ColorModel>> =
        colorRepository.getRandomColors(colorCount)
}
