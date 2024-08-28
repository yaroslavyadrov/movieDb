package app.moviedb.di

import app.moviedb.BuildConfig
import app.moviedb.data.remote.AuthInterceptor
import app.moviedb.data.remote.MoviesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun moviesService(
        okHttpClient: OkHttpClient
    ): MoviesService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesService::class.java)
    }

    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    fun providesAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor(BuildConfig.AUTH_TOKEN)
    }

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineContext = Dispatchers.Default

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineContext = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineContext = Dispatchers.Main
}
