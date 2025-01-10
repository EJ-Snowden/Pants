package com.example.pants.domain.usecases

import com.example.pants.domain.model.ColorModel

class SaveColorUseCase {
    operator fun invoke(board: List<ColorModel>, colorName: String, newHue: Float): List<ColorModel> {
        return board.map {
            if (it.name == colorName) {
                // Update the color with the new hue
                it.updateHue(newHue)
            } else {
                it
            }
        }
    }
}
