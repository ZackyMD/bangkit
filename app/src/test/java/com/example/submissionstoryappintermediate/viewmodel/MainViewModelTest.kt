package com.example.submissionstoryappintermediate.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.example.submissionstoryappintermediate.repository.StoryRepository
import com.example.submissionstoryappintermediate.request.StoryRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val storyRepository = Mockito.mock(StoryRepository::class.java)
    private lateinit var mainViewModel: MainViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(storyRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getStories() should not be null and return correct data`() {
        // Persiapkan data palsu
        val expectedStories = listOf(
            StoryRequest(id = "1", name = "Story 1", description = "Description 1", createdAt = "3 Juni 2024", photoUrl = "url1", lat = 0.0, lon = 0.5),
        )

        // Buat objek mock untuk repository
        val mockRepository = mock(StoryRepository::class.java)

        // Ketika metode getStories() dipanggil, kembalikan LiveData yang berisi data palsu
        `when`(mockRepository.getStories()).thenReturn(MutableLiveData(PagingData.from(expectedStories)))

        // Inisialisasi MainViewModel dengan objek mock repository
        val mainViewModel = MainViewModel(mockRepository)

        // Panggil metode getStories() dari MainViewModel
        val actualStories = mainViewModel.getStories().listData()

        // Pastikan data yang dikembalikan tidak null dan sesuai dengan yang diharapkan
        assertNotNull(actualStories)
        assertEquals(expectedStories.size, actualStories.size)
        assertEquals(expectedStories[0], actualStories[0])
    }

    @Test
    fun `when getStories() returns empty data`() = runTest(testDispatcher) {
        val pagingData = PagingData.empty<StoryRequest>()
        val liveDataPagingData: LiveData<PagingData<StoryRequest>> = flowOf(pagingData).asLiveData()

        Mockito.`when`(storyRepository.getStories()).thenReturn(liveDataPagingData)

        val observer = Observer<PagingData<StoryRequest>> {}
        try {
            mainViewModel.getStories().observeForever(observer)
            val actualStories = mainViewModel.getStories().getOrAwaitValue().collectData()

            assertNotNull(actualStories)
            assertTrue(actualStories.isEmpty())
        } finally {
            mainViewModel.getStories().removeObserver(observer)
        }
    }
}


// Helper function to collect PagingData into a list
fun <T : Any> PagingData<T>.collectData(): List<T> {
    val items = mutableListOf<T>()
    this.map { item -> items.add(item) }
    return items
}

// Helper function to collect LiveData into a list
fun <T> LiveData<T>.listData(): List<T> {
    val list = mutableListOf<T>()
    observeForever { data ->
        data?.let {
            list.add(it)
        }
    }
    return list
}


//untuk menunggu sampai Livedata mendapatkan nilai pertama
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    try {
        afterObserve.invoke()
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

//observe Livedata sampai block selesai dieksekusi
suspend fun <T> LiveData<T>.observeForTesting(block: suspend  () -> Unit) {
    val observer = Observer<T> { }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}