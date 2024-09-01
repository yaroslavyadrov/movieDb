package app.moviedb.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.moviedb.data.remote.MoviesApi
import app.moviedb.data.remote.MoviesListPagingSource
import app.moviedb.data.remote.SearchMoviesPagingSource
import app.moviedb.data.remote.model.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val moviesApi: MoviesApi,
    private val moviesListPagingSource: MoviesListPagingSource,
) {

    fun getMovies(): Flow<PagingData<Movie>> =
        Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            )
        ) {
            moviesListPagingSource
        }.flow

    fun searchMovies(query: String): Flow<PagingData<Movie>> =
        Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            )
        ) {
            SearchMoviesPagingSource(moviesApi, query)
        }.flow

    companion object {
        private const val PAGE_SIZE = 20
    }
}
