## Performance Comparison: Old vs. New Implementation

#Old version:
override suspend fun getRandomColors(count: Int): Result<Set<ColorModel>> = runCatching {
        val colorList = mutableListOf<ColorModel>()

        while (colorList.size < count) {
            val color = apiService.getColor(generateRandomColor()).toColorModel()
            val doesntContainCommon = color.name.lowercase(Locale.getDefault()) !in COMMON_USE_NAMES
            val isDistinct = color !in colorList
            if (doesntContainCommon && isDistinct) {
                colorList.add(color)
            }
        }
        colorList.toSet()
    }

#New version:
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

#Test:
@Test
    fun `performance test for getRandomColors with real API calls`() = runTest {
        val count = 5

        val timeTakenOld = measureTimeMillis {
            val result = repository.getRandomColorsOld(count).getOrNull()
            assertNotNull(result)
            assertEquals(count, result!!.size)
        }

        val timeTaken = measureTimeMillis {
            val result = repository.getRandomColors(count).getOrNull()
            assertNotNull(result)
            assertEquals(count, result!!.size)
        }

        println("OLD Execution time for getRandomColors with count=$count: $timeTakenOld ms")
        println("Execution time for getRandomColors with count=$count: $timeTaken ms")
    }

#Results
Execution time for getRandomColors with count=5: 654 ms
OLD Execution time for getRandomColors with count=5: 1124 ms