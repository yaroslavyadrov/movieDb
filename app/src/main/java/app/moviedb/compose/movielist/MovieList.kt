package app.moviedb.compose.movielist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.moviedb.R
import app.moviedb.data.remote.model.Movie
import app.moviedb.viewmodel.MoviesListUiState
import app.moviedb.viewmodel.MoviesListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@RootNavGraph(start = true)
@Destination
fun MovieList(
    viewModel: MoviesListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesListContent(
        state = state,
        onMovieClick = { movieId ->
//            navigator.navigate(Destination.MovieDetail(movieId))
        }
    )
}

@Composable
fun MoviesListContent(
    modifier: Modifier = Modifier,
    state: MoviesListUiState,
    onMovieClick: (Int) -> Unit,
) {

    val listState = rememberLazyListState()

    when (state) {
        MoviesListUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                LinearProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }

        is MoviesListUiState.Data -> {
            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                ) {
                    items(state.movies.size) { i ->
                        val movie = state.movies[i]
                        MovieCard(
                            movie = movie,
                            onMovieClick = onMovieClick
                        )
                    }
                }

            }
        }

        is MoviesListUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.something_went_wrong),
                    modifier = modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
fun MovieCard(
    modifier: Modifier = Modifier,
    movie: Movie,
    onMovieClick: (Int) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onMovieClick(movie.id)
            }
    ) {
        Text(
            text = movie.title,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

