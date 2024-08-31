package app.moviedb.viewmodel

import app.cash.turbine.test
import app.moviedb.data.MoviesRepository
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MoviesListViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    lateinit var viewModel: MoviesListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @RelaxedMockK
    private lateinit var moviesRepository: MoviesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MoviesListViewModel(moviesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search should call repository`() {
            viewModel.searchMovies("query")
            verify { moviesRepository.searchMovies("query") }
    }

    @Test
    fun `proper search flow with query reset after close`() = runTest {
        viewModel.uiState.test {
            Assert.assertEquals(false, awaitItem().searchActive)
            viewModel.onSearchActiveChange(true)
            Assert.assertEquals(true, awaitItem().searchActive)
            viewModel.searchMovies("query")
            Assert.assertEquals("query", awaitItem().query)
            viewModel.onSearchActiveChange(false)
            Assert.assertEquals("", awaitItem().query)
        }
    }
}
