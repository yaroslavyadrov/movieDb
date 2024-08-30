package app.moviedb.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun MovieDbAppContent() {
    val engine = rememberAnimatedNavHostEngine()
    val navController = engine.rememberNavController()
    val startRoute = NavGraphs.root.startRoute
    DestinationsNavHost(
        engine = engine,
        navController = navController,
        navGraph = NavGraphs.root,
        modifier = Modifier
            .fillMaxSize(),
        startRoute = startRoute,
    )
}
