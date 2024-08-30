package app.moviedb.di

import app.moviedb.data.MoviesRepository
import app.moviedb.data.remote.MoviesPagingSource
import app.moviedb.data.remote.MoviesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun moviesRepository(
        moviesService: MoviesService,
//        @DefaultDispatcher dispatcher: CoroutineContext
        moviesPagingSource: MoviesPagingSource
    ): MoviesRepository {
        return MoviesRepository(moviesService, moviesPagingSource)
    }
}
