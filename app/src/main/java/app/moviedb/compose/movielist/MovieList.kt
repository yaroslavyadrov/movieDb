package app.moviedb.compose.movielist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import app.moviedb.R
import app.moviedb.compose.destinations.MovieDetailsDestination
import app.moviedb.compose.imagePrefix
import app.moviedb.data.remote.model.Movie
import app.moviedb.viewmodel.MoviesListUiState
import app.moviedb.viewmodel.MoviesListViewModel
import coil.compose.AsyncImage
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
        onMovieClick = { movie ->
            navigator.navigate(MovieDetailsDestination(movie))
        }
    )
}

@Composable
fun MoviesListContent(
    modifier: Modifier = Modifier,
    state: MoviesListUiState,
    onMovieClick: (Movie) -> Unit,
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
            val movies by rememberUpdatedState(newValue = state.movies.collectAsLazyPagingItems())

            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                ) {
                    items(movies.itemCount) { i ->
                        movies[i]?.let {
                            MovieCard(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                ),
                                movie = it,
                                onMovieClick = onMovieClick
                            )
                        }
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
    onMovieClick: (Movie) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onMovieClick(movie)
            }
    ) {
        Row {
            AsyncImage(
                modifier = Modifier
                    .width(72.dp)
                    .aspectRatio(2 / 3f),
                model = "$imagePrefix${movie.posterPath}",
                contentDescription = movie.title,
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = movie.title,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

