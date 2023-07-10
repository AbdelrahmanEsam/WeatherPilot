package com.example.weatherpilot.domain.usecase.transformers

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetTimeStampUseCaseTest{

    lateinit var timeStampUseCase: GetTimeStampUseCase

    @Before
    fun setUp()
    {
        timeStampUseCase = GetTimeStampUseCase()
    }

    @Test
    fun `execute with valid date and time returns correct timestamp`() {
        val dateAndTime = "2023 07 09 19 16"
        val expectedTimestamp = 1688922960000L
        val transformerResult = timeStampUseCase.execute(dateAndTime)
        MatcherAssert.assertThat(transformerResult, CoreMatchers.equalTo(expectedTimestamp))
    }

    @Test
    fun `execute with null string returns -1`() {
        val dateAndTime: String? = null
        val transformerResult = timeStampUseCase.execute(dateAndTime)
        MatcherAssert.assertThat(transformerResult, CoreMatchers.equalTo(-1))
    }

    @Test
    fun `execute with blank string returns -1`() {
        val dateAndTime = ""
        val transformerResult = timeStampUseCase.execute(dateAndTime)
        MatcherAssert.assertThat(transformerResult, CoreMatchers.equalTo(-1))
    }

    @Test
    fun `execute with invalid date and time format string returns null`() {
        val dateAndTime = "2023 07 08 12:34"
        val transformerResult = timeStampUseCase.execute(dateAndTime)
        MatcherAssert.assertThat(transformerResult, CoreMatchers.equalTo(null))
    }
}