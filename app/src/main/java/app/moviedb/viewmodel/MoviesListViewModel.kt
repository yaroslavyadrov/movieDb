package app.moviedb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.moviedb.data.MoviesRepository
import app.moviedb.data.remote.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class MoviesListUiState {
    data object Loading : MoviesListUiState()
    data object Error : MoviesListUiState()
    data class Data(val movies: Flow<PagingData<Movie>>) : MoviesListUiState()
}

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MoviesListUiState>(MoviesListUiState.Loading)

//    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        Log.e(TAG, "${throwable.message}", throwable)
//        _uiState.update { MoviesListUiState.Error }
//    }

    val uiState: StateFlow<MoviesListUiState> = _uiState
        .onStart {
            loadData()
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _uiState.value
        )

    private fun loadData() {
        _uiState.update { MoviesListUiState.Loading }
        val moviesFlow = moviesRepository.getMovies().cachedIn(viewModelScope)
        _uiState.update {
            MoviesListUiState.Data(moviesFlow)
        }
    }

    companion object {
        private const val TAG = "MoviesListViewModel"
    }
}
