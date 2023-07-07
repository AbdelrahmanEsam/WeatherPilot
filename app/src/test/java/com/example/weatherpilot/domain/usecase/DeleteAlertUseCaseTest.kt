package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteAlertUseCaseTest
{
    private lateinit var deleteAlertUseCase: DeleteAlertUseCase
    private lateinit var fakeRepository: Repository

    @Before
    fun setUp()  = runTest{

        deleteAlertUseCase = DeleteAlertUseCase(fakeRepository)
    }



    @Test
    fun `delete item from database shouldn't make it in my database`()
    {

    }




}