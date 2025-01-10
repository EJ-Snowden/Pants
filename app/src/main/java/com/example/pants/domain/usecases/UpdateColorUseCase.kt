package com.example.pants.domain.usecases

import com.example.pants.domain.model.ColorModel

class UpdateColorUseCase {
    operator fun invoke(color: ColorModel, newHue: Float): ColorModel {
        return color.updateHue(newHue)
    }
}
