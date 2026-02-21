package presentation.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.SampleDataProvider
import data.relations.ClassWithDetails
import data.repository.CampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Maps dayOfWeek int (1–7, Mon–Sun) to display name */
fun dayName(dayOfWeek: Int): String = when (dayOfWeek) {
    1 -> "Monday"
    2 -> "Tuesday"
    3 -> "Wednesday"
    4 -> "Thursday"
    5 -> "Friday"
    6 -> "Saturday"
    7 -> "Sunday"
    else -> "Unknown"
}

data class TimetableUiState(
    // Schedule grouped by day name for easy UI rendering
    val scheduleByDay: Map<String, List<ClassWithDetails>> = emptyMap(),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)

class TimetableViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            // Using the demo student ID from SampleDataProvider
            repository.getUserSchedule(SampleDataProvider.DEMO_STUDENT_ID)
                .collect { scheduleList ->
                    // Group by day name, preserving weekday order via sortedBy
                    val grouped = scheduleList
                        .sortedWith(compareBy({ it.classSchedule.dayOfWeek }, { it.classSchedule.startTime }))
                        .groupBy { dayName(it.classSchedule.dayOfWeek) }
                    _uiState.value = TimetableUiState(
                        scheduleByDay = grouped,
                        isLoading = false,
                        isEmpty = grouped.isEmpty()
                    )
                }
        }
    }
}
