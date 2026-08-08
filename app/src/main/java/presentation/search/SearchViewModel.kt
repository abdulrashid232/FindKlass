package presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.models.Building
import data.models.CampusRoom
import data.models.Course
import data.repository.CampusRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class SearchResults(
    val buildings: List<Building> = emptyList(),
    val rooms: List<CampusRoom> = emptyList(),
    val courses: List<Course> = emptyList()
) {
    val isEmpty get() = buildings.isEmpty() && rooms.isEmpty() && courses.isEmpty()
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow(SearchResults())
    val results: StateFlow<SearchResults> = _results.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                // Skip blank queries — show empty results immediately
                .debounce(300L)
                .collectLatest { q ->
                    if (q.isBlank()) {
                        _results.value = SearchResults()
                        _isSearching.value = false
                        return@collectLatest
                    }
                    _isSearching.value = true
                    // Combine the three parallel search flows into one emission
                    combine(
                        repository.searchBuildings(q),
                        repository.searchRooms(q),
                        repository.searchCourses(q)
                    ) { buildings, rooms, courses ->
                        SearchResults(buildings, rooms, courses)
                    }.collectLatest { results ->
                        _results.value = results
                        _isSearching.value = false
                    }
                }
        }
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }
}
