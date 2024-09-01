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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MoviesListUiState(
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

    val uiState: StateFlow<MoviesListUiState> = _uiState
        .asStateFlow()

    init {
        loadData()
    }

    fun searchMovies(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                searchResults = moviesRepository.searchMovies(query),
            )
        }
    }

    fun loadData() {
        val moviesFlow = moviesRepository.getMovies().cachedIn(viewModelScope)
        _uiState.update {
            it.copy(
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
}
