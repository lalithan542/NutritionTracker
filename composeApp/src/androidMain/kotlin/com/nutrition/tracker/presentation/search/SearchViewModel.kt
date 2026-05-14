package com.nutrition.tracker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrition.tracker.domain.model.Food
import com.nutrition.tracker.domain.usecase.SearchFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val results: List<Food> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFoods: SearchFoodsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        viewModelScope.launch { loadAll() }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        viewModelScope.launch { search(query) }
    }

    private suspend fun loadAll() {
        _state.update { it.copy(isLoading = true) }
        val results = searchFoods("")
        _state.update { it.copy(results = results, isLoading = false) }
    }

    private suspend fun search(query: String) {
        _state.update { it.copy(isLoading = true) }
        val results = searchFoods(query)
        _state.update { it.copy(results = results, isLoading = false) }
    }
}
