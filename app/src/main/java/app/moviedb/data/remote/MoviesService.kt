package app.moviedb.data.remote

import app.moviedb.data.remote.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {

    @GET("discover/movie?include_adult=false&include_video=false&sort_by=popularity.desc")
    suspend fun getMovies(@Query("page") page: Int): MoviesResponse

//    @GET("movie/{movie_id}")
//    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetailsResponse
}
