package app.moviedb.data.remote

import app.moviedb.data.remote.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("3/discover/movie?include_adult=false&include_video=false&sort_by=popularity.desc")
    suspend fun getMovies(@Query("page") page: Int): MoviesResponse

    @GET("3/search/movie?include_adult=false&language=en-US")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int): MoviesResponse

//todo: If for some reason we will need to implement more details about the movie we can add this method
//    @GET("movie/{movie_id}")
//    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetailsResponse
}
