package com.example.pants.data.repository

import com.example.pants.domain.model.ColorModel
import com.example.pants.utils.generateRandomColor
import com.example.pants.data.mapper.toColorModel
import com.example.pants.service.ColorApiService
import com.example.pants.domain.repository.ColorRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.Locale

class ColorRepositoryImpl(
    private val apiService: ColorApiService,
) : ColorRepository {


    override suspend fun getRandomColors(count: Int): Result<Set<ColorModel>> = runCatching {
        coroutineScope {
            (1..count).map {
                async {
                    generateValidRandomColor()
                }
            }.awaitAll().filter { it.saturation > 0.30 && it.value > 0.40  }.distinctBy { it.name }.toSet()
        }
    }

    private suspend fun generateValidRandomColor(): ColorModel {
        var randomColor: ColorModel
        do {
            randomColor = apiService.getColor(generateRandomColor()).toColorModel()
        } while (isCommonName(randomColor.name))
        return randomColor
    }

    fun isCommonName(name: String): Boolean {
        return name.lowercase(Locale.getDefault()) in COMMON_USE_NAMES
    }

    private companion object {
        val COMMON_USE_NAMES = setOf(
            "beige",
            "black",
            "blue violet",
            "blue",
            "brown",
            "crimson",
            "cyan",
            "gold",
            "gray",
            "green",
            "indigo",
            "khaki",
            "lavender",
            "lime green",
            "magenta",
            "maroon",
            "navy blue",
            "olive",
            "orange",
            "pink",
            "plum",
            "purple",
            "red",
            "salmon",
            "silver",
            "sky blue",
            "teal",
            "violet",
            "white",
            "yellow",
        )
    }
}
