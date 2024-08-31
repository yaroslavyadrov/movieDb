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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MoviesListUiState(
    val loading: Boolean = true,
    val movies: Flow<PagingData<Movie>> = flowOf(),
    val searchActive: Boolean = false,
    val query: String = "",
    val searchResults: Flow<PagingData<Movie>> = flowOf(),
)

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoviesListUiState())

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

    fun searchMovies(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                searchResults = moviesRepository.searchMovies(query),
            )
        }
    }

    private fun loadData() {
        _uiState.update { it.copy(loading = true) }
        val moviesFlow = moviesRepository.getMovies().cachedIn(viewModelScope)
        _uiState.update {
            it.copy(
                loading = false,
                movies = moviesFlow
            )
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        _uiState.update {
            if (!active) {
                it.copy(
                    searchActive = false,
                    query = "",
                    searchResults = flowOf(),
                )
            } else {
                it.copy(
                    searchActive = true,
                )
            }
        }
    }

    companion object {
        private const val TAG = "MoviesListViewModel"
    }
}
