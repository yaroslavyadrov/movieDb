package app.moviedb.di

import app.moviedb.data.MoviesRepository
import app.moviedb.data.remote.MoviesListPagingSource
import app.moviedb.data.remote.MoviesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun moviesRepository(
        moviesApi: MoviesApi,
        moviesListPagingSource: MoviesListPagingSource
    ): MoviesRepository {
        return MoviesRepository(moviesApi, moviesListPagingSource)
    }
}
