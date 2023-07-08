package com.example.weatherpilot.domain.usecase.transformers

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TempTransformerUseCaseTest{



    private lateinit var useCase : TempTransformerUseCase

    @Before
    fun setUp()
    {
        useCase = TempTransformerUseCase()
    }


    @Test
    fun `execute with transform from positive C to F returns valid temperature`() {
        val temp = 20
        val expectedTemp = "68 F"
        val actualTemp = useCase.execute(Temperature.Fahrenheit(temp))
        assertEquals(expectedTemp, actualTemp)
    }


    @Test
    fun `execute with transform from negative C to F returns valid temperature`() {
        val temp = -20
        val expectedTemp = "-4 F"
        val actualTemp = useCase.execute(Temperature.Fahrenheit(temp))
        assertEquals(expectedTemp, actualTemp)
    }

    @Test
    fun `execute with transform from positive C to K with valid values  returns valid temperature`() {
        val temp = 20
        val expectedTemp = "293 K"
        val actualTemp = useCase.execute(Temperature.Kelvin(temp))
        assertEquals(expectedTemp, actualTemp)
    }


    @Test
    fun `execute with transform from negative C to K with valid values  returns valid temperature`() {
        val temp = -50
        val expectedTemp = "223 K"
        val actualTemp = useCase.execute(Temperature.Kelvin(temp))
        assertEquals(expectedTemp, actualTemp)
    }

    @Test
    fun `execute with Celsius returns Celsius temperature`() {
        val temp = 20
        val expectedTemp = "20 C"
        val actualTemp = useCase.execute(Temperature.Celsius(temp))
        assertEquals(expectedTemp, actualTemp)
    }
}
