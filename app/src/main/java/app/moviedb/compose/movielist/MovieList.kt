package app.moviedb.compose.movielist

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
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
import kotlinx.coroutines.launch

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
        },
        onSearch = { query ->
            viewModel.searchMovies(query)
        },
        onSearchActiveChange = { searchActive ->
            viewModel.onSearchActiveChange(searchActive)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesListContent(
    modifier: Modifier = Modifier,
    state: MoviesListUiState,
    onMovieClick: (Movie) -> Unit,
    onSearch: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
) {

    val listState = rememberLazyListState()
    val searchListState = rememberLazyListState()

    when {
        state.loading -> {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                LinearProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }

        else -> {
            val movies by rememberUpdatedState(newValue = state.movies.collectAsLazyPagingItems())
            val searchResults by rememberUpdatedState(newValue = state.searchResults.collectAsLazyPagingItems())
            val coroutineScope = rememberCoroutineScope()

            val contentPadding: Dp by animateDpAsState(
                if (state.searchActive) 0.dp else 16.dp, label = ""
            )

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        start = contentPadding,
                        end = contentPadding
                    )
            ) {
                SearchBar(
                    modifier = Modifier.padding(bottom = 8.dp),
                    query = state.query,
                    onQueryChange = {
                        coroutineScope.launch {
                            searchListState.scrollToItem(0)
                        }
                        onSearch(it)
                    },
                    onSearch = {

                    },
                    placeholder = {
                        Text(text = stringResource(R.string.search_movies))
                    },
                    active = state.searchActive,
                    onActiveChange = {
                        onSearchActiveChange(it)
                    }
                ) {
                    when {
                        state.query.isEmpty() -> {
                            Text(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                text = stringResource(R.string.start_typing_to_search)
                            )
                        }
                        searchResults.loadState.refresh is LoadState.Loading -> {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        searchResults.itemCount == 0 -> {
                            Text(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                text = stringResource(R.string.no_results_found)
                            )
                        }
                        else -> {
                            LazyColumn(
                                state = searchListState,
                            ) {
                                items(searchResults.itemCount) { i ->
                                    searchResults[i]?.let {
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
                }
                LazyColumn(
                    state = listState,
                ) {
                    items(movies.itemCount) { i ->
                        movies[i]?.let {
                            MovieCard(
                                modifier = Modifier.padding(
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

