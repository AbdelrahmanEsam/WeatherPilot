package com.example.weatherpilot.domain.usecase.datastore

import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.times

class ReadStringFromDataStoreUseCaseTest {


//    private lateinit var repository: Repository
    private lateinit var readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase


    @Mock
    private lateinit var repository: Repository

    private val dataStore: MutableMap<String, String?> =
        mutableMapOf(("keyOne" to "valueOne"), ("keyTwo" to "valueTwo"))

    @Before
    fun setUp() {
//        repository = FakeRepository(dataStore = dataStore)
        repository = Mockito.mock()
        readStringFromDataStoreUseCase = ReadStringFromDataStoreUseCase(repository)
    }


//    @Test
//    fun `read data from datastore with valid key should return flow with it's value`() = runTest {
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//            val valueFlow = readStringFromDataStoreUseCase.execute<String>(dataStore.keys.first())
//            valueFlow.collectLatest {
//                MatcherAssert.assertThat(it.data, equalTo(dataStore.values.first()))
//            }
//        }
//    }
//
//    @Test
//    fun `read data from datastore with inValid key should return flow with error message`() = runTest {
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//            val valueFlow = readStringFromDataStoreUseCase.execute<String>("")
//            valueFlow.collectLatest {
//                MatcherAssert.assertThat(it.error, equalTo("error"))
//            }
//        }
//    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `call execute from use case should call saveStringToDataStore`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            readStringFromDataStoreUseCase.execute<String>("any")
            Mockito.verify(repository, times(1)).getStringFromDataStore<String>("any")
        }
    }


}