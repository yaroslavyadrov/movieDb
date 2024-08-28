package app.moviedb.data

import app.moviedb.data.remote.MoviesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MoviesRepository @Inject constructor(
    private val moviesService: MoviesService,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend fun getMovies() = withContext(dispatcher) { moviesService.getMovies().results }
}
