package presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        SearchBar(
            query = query,
            onQueryChange = viewModel::setQuery,
            onSearch = { /* triggered by keyboard action; debounce already handles it */ },
            active = false,
            onActiveChange = {},
            placeholder = { Text("Search buildings, rooms, courses…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = viewModel::clearQuery) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {}

        Spacer(modifier = Modifier.height(8.dp))

        if (query.isBlank()) {
            Text(
                "Type to search across campus",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // ---- Buildings section ----
                if (results.buildings.isNotEmpty()) {
                    item {
                        SectionHeader("Buildings")
                    }
                    items(results.buildings, key = { it.id }) { building ->
                        ListItem(
                            headlineContent = { Text(building.name) },
                            supportingContent = { Text("${building.code} · ${building.address}") }
                        )
                        HorizontalDivider()
                    }
                }

                // ---- Rooms section ----
                if (results.rooms.isNotEmpty()) {
                    item { SectionHeader("Rooms") }
                    items(results.rooms, key = { it.id }) { room ->
                        ListItem(
                            headlineContent = { Text("Room ${room.roomNumber}") },
                            supportingContent = { Text("Floor ${room.floor}") }
                        )
                        HorizontalDivider()
                    }
                }

                // ---- Courses section ----
                if (results.courses.isNotEmpty()) {
                    item { SectionHeader("Courses") }
                    items(results.courses, key = { it.id }) { course ->
                        ListItem(
                            headlineContent = { Text("${course.code} — ${course.title}") },
                            supportingContent = { Text(course.instructor ?: "") }
                        )
                        HorizontalDivider()
                    }
                }

                if (results.isEmpty && !isSearching) {
                    item {
                        Text(
                            "No results for \"$query\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}
