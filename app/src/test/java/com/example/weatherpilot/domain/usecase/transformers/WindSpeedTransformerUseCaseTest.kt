package com.example.weatherpilot.domain.usecase.transformers

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test


class WindSpeedTransformerUseCaseTest{



    private lateinit var useCase : WindSpeedTransformerUseCase

    @Before
    fun setUp()
    {
        useCase = WindSpeedTransformerUseCase()
    }

    @Test
    fun `transform to M per S return valid speed`()
    {
        val speed = 50
        val expectedSpeed = "22 M/S"
        val actualSpeed = useCase.execute(speed,"M/S")
        MatcherAssert.assertThat(actualSpeed, CoreMatchers.equalTo(expectedSpeed))
    }


    @Test
    fun `transform to M per H return valid speed`()
    {
        val speed = 50
        val expectedSpeed = "112 M/H"
        val actualSpeed = useCase.execute(speed,"M/H")
        MatcherAssert.assertThat(actualSpeed, CoreMatchers.equalTo(expectedSpeed))
    }


    @Test
    fun `transform to whatever with 0 speed return 0 speed`()
    {
        val speed = 0
        val expectedSpeed = "0 M/H"
        val actualSpeed = useCase.execute(speed,"M/H")
        MatcherAssert.assertThat(actualSpeed, CoreMatchers.equalTo(expectedSpeed))
    }


    @Test
    fun `transform to whatever with negative speed return 0 speed`()
    {
        val speed = -1
        val expectedSpeed = "0 M/H"
        val actualSpeed = useCase.execute(speed,"M/H")
        MatcherAssert.assertThat(actualSpeed, CoreMatchers.equalTo(expectedSpeed))
    }

}