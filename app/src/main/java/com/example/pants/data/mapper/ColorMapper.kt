package com.example.pants.data.mapper

import com.example.pants.data.ColorResponse
import com.example.pants.domain.model.ColorModel

fun ColorResponse.toColorModel(): ColorModel {
    return ColorModel(
        name = this.name.value,
        realHue = this.hsv.fraction.h,
        guessHue = null,
        saturation = this.hsv.fraction.s,
        value = this.hsv.fraction.v,
    )
}
