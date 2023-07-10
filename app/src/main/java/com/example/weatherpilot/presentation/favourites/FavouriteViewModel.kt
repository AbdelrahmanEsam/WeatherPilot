package com.example.weatherpilot.presentation.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.usecase.favourites.DeleteFavouriteUseCase
import com.example.weatherpilot.domain.usecase.favourites.GetAllFavouritesUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavouriteViewModel
@Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getAllFavouritesUseCase: GetAllFavouritesUseCase,
    private val deleteFavouriteFavouriteUseCase: DeleteFavouriteUseCase,

    ): ViewModel() {


    private val _favouriteState : MutableStateFlow<FavouritesState> = MutableStateFlow(FavouritesState())
    val favouriteState = _favouriteState.asStateFlow()







    fun onEvent(intent : FavouritesIntent)
    {
        when(intent){
            FavouritesIntent.FetchFavouritesFromDatabase -> fetchFavouritesFromDatabase()
            is FavouritesIntent.DeleteItem -> deleteFavouriteItem(intent.location)
        }
    }




    private fun fetchFavouritesFromDatabase()
    {
        viewModelScope.launch(ioDispatcher) {
            getAllFavouritesUseCase.execute().collect{ favourites ->
                _favouriteState.update { it.copy(favourites = favourites) }
            }
        }
    }


    private fun deleteFavouriteItem(location: com.example.weatherpilot.domain.model.Location)
    {
        viewModelScope.launch(ioDispatcher) {
            deleteFavouriteFavouriteUseCase.execute(location.id).collect()
        }
    }



}