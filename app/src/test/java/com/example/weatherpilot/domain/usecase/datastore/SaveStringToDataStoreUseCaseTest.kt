package com.example.weatherpilot.domain.usecase.datastore

import com.example.weatherpilot.data.local.FakeLocalDataSourceImpl
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.times

class SaveStringToDataStoreUseCaseTest{

//    private lateinit var repository: Repository
    private lateinit var saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase


    @Mock
    private lateinit var repository: Repository

    private val dataStore : MutableMap<String,String?> = mutableMapOf(("keyOne" to "valueOne") , ("keyTwo" to "valueTwo"))

    @Before
    fun setUp()
    {

//        repository = FakeRepository(dataStore = dataStore)
        repository = Mockito.mock()
        saveStringToDataStoreUseCase = SaveStringToDataStoreUseCase(repository)


    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `add value with valid key  to dataStore should return flow of response with success`() = runTest{
//
//            val saveDataStoreResult = saveStringToDataStoreUseCase.execute(key = "new key", value = "new value")
//            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//                saveDataStoreResult.collect {
//                    MatcherAssert.assertThat((it as Response.Success<String>).data, CoreMatchers.equalTo("success"))
//                    MatcherAssert.assertThat(dataStore.size, CoreMatchers.equalTo(3))
//                }
//            }
//
//        }
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `add value with  invalid key  to dataStore should return flow of response with error`() = runTest{
//
//            (repository as FakeRepository).setShouldReturnGeneralError(true)
//            val saveDataStoreResult =
//             saveStringToDataStoreUseCase.execute(key = "notification","any")
//            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//                saveDataStoreResult.collect {
//                    MatcherAssert.assertThat((it as Response.Failure<String>).error, CoreMatchers.equalTo("error"))
//                    MatcherAssert.assertThat(dataStore.size, CoreMatchers.equalTo(2))
//                }
//            }
//
//        }
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `add value with  valid key and empty value to dataStore should return flow of response with error`() = runTest{
//
//            (repository as FakeRepository).setShouldReturnGeneralError(true)
//            val saveDataStoreResult =
//              saveStringToDataStoreUseCase.execute(key = "windType","any")
//            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//                saveDataStoreResult.collect {
//                    MatcherAssert.assertThat((it as Response.Failure<String>).error, CoreMatchers.equalTo("error"))
//                    MatcherAssert.assertThat(dataStore.size, CoreMatchers.equalTo(2))
//                }
//            }
//
//        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `call execute from use case should call saveStringToDataStore`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            saveStringToDataStoreUseCase.execute("any" , "any")
            Mockito.verify(repository, times(1)).saveStringToDataStore<String>("any","any")
        }
    }
}