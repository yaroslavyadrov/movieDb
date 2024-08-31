package app.moviedb.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.moviedb.data.remote.MoviesListPagingSource
import app.moviedb.data.remote.MoviesApi
import app.moviedb.data.remote.SearchMoviesPagingSource
import app.moviedb.data.remote.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MoviesRepository @Inject constructor(
    private val moviesApi: MoviesApi,
    private val moviesListPagingSource: MoviesListPagingSource,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) {

//    suspend fun getMovies() = withContext(dispatcher) { moviesService.getMovies().results }

    fun getMovies(): Flow<PagingData<Movie>> =
        Pager(
            PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false
            )
        ) {
            moviesListPagingSource
        }.flow

    fun searchMovies(query: String): Flow<PagingData<Movie>> =
        Pager(
            PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false
            )
        ) {
            SearchMoviesPagingSource(moviesApi, query)
        }.flow
}
