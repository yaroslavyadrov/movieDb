package app.moviedb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviedb.data.MoviesRepository
import app.moviedb.data.remote.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MoviesListUiState {
    data object Loading : MoviesListUiState()
    data object Error : MoviesListUiState()
    data class Data(val movies: List<Movie>) : MoviesListUiState()
}

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MoviesListUiState>(MoviesListUiState.Loading)

    val uiState: StateFlow<MoviesListUiState> = _uiState
        .asStateFlow()
//        .onStart {
//            loadData()
//        }.stateIn(
//            viewModelScope,
//            SharingStarted.Eagerly,
//            _uiState.value
//        )

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "${throwable.message}", throwable)
        _uiState.update { MoviesListUiState.Error }
    }

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { MoviesListUiState.Loading }
            val movies = moviesRepository.getMovies()
            Log.d("TAGGG", "movies: $movies")
            _uiState.update {
                MoviesListUiState.Data(movies)
            }
        }
    }

    companion object {
        private const val TAG = "MoviesListViewModel"
    }
}
