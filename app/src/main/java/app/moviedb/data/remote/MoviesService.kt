package app.moviedb.data.remote

import app.moviedb.data.remote.model.MoviesResponse
import retrofit2.http.GET

interface MoviesService {

    @GET("discover/movie?include_adult=false&include_video=false&page=1&sort_by=popularity.desc")
    suspend fun getMovies(): MoviesResponse
}
