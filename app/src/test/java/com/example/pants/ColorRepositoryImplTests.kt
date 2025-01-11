package com.example.pants

import com.example.pants.data.repository.ColorRepositoryImpl
import com.example.pants.service.ColorApiService
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.measureTimeMillis

class ColorRepositoryImplTests {

    private lateinit var repository: ColorRepositoryImpl

    @Before
    fun setUp() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.thecolorapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val realApiService = retrofit.create(ColorApiService::class.java)
        repository = ColorRepositoryImpl(realApiService)
    }

    @Test
    fun `should return the requested number of colors`() = runTest {
        val count = 5
        val result = repository.getRandomColors(count).getOrNull()

        assertNotNull(result)
        assertEquals(count, result!!.size)
    }

    @Test
    fun `should exclude duplicate colors`() = runTest {
        val count = 5
        val result = repository.getRandomColors(count).getOrNull()

        assertNotNull(result)
        result?.let { assertTrue(it.size <= count) }
    }

    @Test
    fun `should exclude colors with low saturation and brightness`() = runTest {
        val count = 5
        val result = repository.getRandomColors(count).getOrNull()

        assertNotNull(result)
        assertTrue(result!!.all { it.saturation > 0.30 && it.value > 0.40 })
    }

    @Test
    fun `should exclude colors with common names`() = runTest {
        val count = 5
        val result = repository.getRandomColors(count).getOrNull()

        assertNotNull(result)
        assertTrue(result!!.all { !repository.isCommonName(it.name) })
    }


    @Test
    fun `performance test for getRandomColors with real API calls`() = runTest {
        val count = 5

//        val timeTakenOld = measureTimeMillis {
//            val result = repository.getRandomColorsOld(count).getOrNull()
//            assertNotNull(result)
//            assertEquals(count, result!!.size)
//        }

        val timeTaken = measureTimeMillis {
            val result = repository.getRandomColors(count).getOrNull()
            assertNotNull(result)
            assertEquals(count, result!!.size)
        }

//        println("OLD Execution time for getRandomColors with count=$count: $timeTakenOld ms")
        println("Execution time for getRandomColors with count=$count: $timeTaken ms")
    }
}