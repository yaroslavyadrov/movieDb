package app.moviedb.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.moviedb.data.remote.model.Movie
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(
    private val remoteService: MoviesService,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentPage = params.key ?: 1
            val response = remoteService.getMovies(page = currentPage)
            val movies = response.results

            LoadResult.Page(
                data = movies,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < response.totalPages) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return null
    }
}
