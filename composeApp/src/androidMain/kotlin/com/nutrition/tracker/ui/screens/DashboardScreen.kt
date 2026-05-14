package com.nutrition.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.model.MealType
import com.nutrition.tracker.presentation.dashboard.DashboardViewModel
import com.nutrition.tracker.ui.components.CalorieRingCard
import com.nutrition.tracker.ui.components.MacroProgressCard

@Composable
fun DashboardScreen(
    onAddFood: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val nutrients = state.totalNutrients
    val profile = state.profile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Nutrition") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFood) {
                Icon(Icons.Default.Add, contentDescription = "Add food")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CalorieRingCard(
                    consumed = nutrients.calories,
                    goal = profile.effectiveCalorieGoal,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroProgressCard("Protein", nutrients.protein, profile.effectiveProteinGoal, color = Color(0xFF2196F3), modifier = Modifier.weight(1f))
                    MacroProgressCard("Carbs", nutrients.carbohydrates, profile.effectiveCarbGoal, color = Color(0xFFFF9800), modifier = Modifier.weight(1f))
                    MacroProgressCard("Fat", nutrients.fat, profile.effectiveFatGoal, color = Color(0xFFE91E63), modifier = Modifier.weight(1f))
                }
            }
            MealType.entries.forEach { mealType ->
                val mealEntries = state.entriesByMeal[mealType].orEmpty()
                item {
                    Text(
                        mealType.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (mealEntries.isEmpty()) {
                    item {
                        Text(
                            "No entries — tap + to add",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        )
                    }
                } else {
                    items(mealEntries, key = { it.id }) { entry ->
                        FoodEntryRow(entry = entry, onDelete = { viewModel.removeEntry(entry.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodEntryRow(entry: FoodEntry, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(entry.food.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(
                    "${entry.amount.toInt()} ${entry.food.servingUnit}  •  ${entry.nutrients.calories.toInt()} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
