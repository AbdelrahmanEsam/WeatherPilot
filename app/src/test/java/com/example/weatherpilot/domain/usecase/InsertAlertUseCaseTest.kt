package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class InsertAlertUseCaseTest
{

    private lateinit var insertAlertUseCase: InsertAlertUseCase
    private lateinit var fakeRepository: Repository

    @Before
    fun setUp()  = runTest{

        insertAlertUseCase = InsertAlertUseCase(fakeRepository)
    }



    @Test
   fun  `insert alert to database should return contained`()
    {

   }

}