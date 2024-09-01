package app.moviedb.compose.moviedetails

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.moviedb.R
import app.moviedb.compose.imagePrefix
import app.moviedb.data.remote.model.Movie
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@RootNavGraph
@Destination
fun MovieDetails(
    movie: Movie,
    navigator: DestinationsNavigator
) {
    val scrollState = rememberScrollState()
    var movieScroller by remember {
        mutableStateOf(MovieDetailsScroller(scrollState, Float.MIN_VALUE))
    }
    val transitionState =
        remember(movieScroller) { movieScroller.toolbarTransitionState }
    val toolbarState = movieScroller.getToolbarState(LocalDensity.current)

    // Transition that fades in/out the header with the image and the Toolbar
    val transition = updateTransition(transitionState, label = "")
    val toolbarAlpha = transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) }, label = ""
    ) { toolbarTransitionState ->
        if (toolbarTransitionState == ToolbarState.HIDDEN) 0f else 1f
    }
    val contentAlpha = transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) }, label = ""
    ) { toolbarTransitionState ->
        if (toolbarTransitionState == ToolbarState.HIDDEN) 1f else 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MovieDetailsContent(
            movie = movie,
            scrollState = scrollState,
            contentAlpha = { contentAlpha.value },
            onNamePosition = { newNamePosition ->
                // Comparing to Float.MIN_VALUE as we are just interested on the original
                // position of name on the screen
                if (movieScroller.namePosition == Float.MIN_VALUE) {
                    movieScroller =
                        movieScroller.copy(namePosition = newNamePosition)
                }
            }
        )

        MovieToolbar(
            toolbarState,
            movie.title,
            onBackClick = { navigator.navigateUp() },
            toolbarAlpha = { toolbarAlpha.value },
            contentAlpha = { contentAlpha.value }
        )
    }

}

@Composable
private fun MovieDetailsContent(
    movie: Movie,
    scrollState: ScrollState,
    contentAlpha: () -> Float,
    onNamePosition: (Float) -> Unit,
) {
    Box(Modifier.verticalScroll(scrollState)) {

        AsyncImage(
            model = "$imagePrefix${movie.backdropPath}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = scrollState.value * 0.5f
                }
                .alpha(contentAlpha()),
            contentScale = ContentScale.FillWidth,
            alpha = 0.5f
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            AsyncImage(
                model = "$imagePrefix${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 16.dp
                    )
                    .align(Alignment.CenterHorizontally)
                    .onGloballyPositioned { onNamePosition(it.positionInWindow().y) }
                    .alpha(contentAlpha())
            )
            Text(
                text = stringResource(R.string.release_date, movie.releaseDate),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            val rating = stringResource(id = R.string.rating, movie.voteAverage)
            val votesString = pluralStringResource(R.plurals.votes, movie.voteCount, movie.voteCount)
            Text(
                text = "$rating $votesString",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(R.string.language, movie.originalLanguage),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun MovieToolbar(
    toolbarState: ToolbarState,
    movieName: String,
    onBackClick: () -> Unit,
    toolbarAlpha: () -> Float,
    contentAlpha: () -> Float
) {
    if (toolbarState.isShown) {
        MovieDetailsToolbar(
            movieName = movieName,
            onBackClick = onBackClick,
            modifier = Modifier.alpha(toolbarAlpha())
        )
    } else {
        MovieHeaderActions(
            onBackClick = onBackClick,
            modifier = Modifier.alpha(contentAlpha())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetailsToolbar(
    movieName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        TopAppBar(
            modifier = modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colorScheme.surface),
            title = {
                Row {
                    IconButton(
                        onBackClick,
                        Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                    Text(
                        text = movieName,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        )
    }
}

@Composable
private fun MovieHeaderActions(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconModifier = Modifier
            .sizeIn(
                maxWidth = 32.dp,
                maxHeight = 32.dp
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(start = 12.dp)
                .then(iconModifier)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.navigate_up)
            )
        }
    }
}


