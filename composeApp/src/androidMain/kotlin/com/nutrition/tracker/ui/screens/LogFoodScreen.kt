package com.nutrition.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrition.tracker.domain.model.MealType
import com.nutrition.tracker.presentation.logfood.LogFoodViewModel

@Composable
fun LogFoodScreen(
    onBack: () -> Unit,
    onLogged: () -> Unit,
    viewModel: LogFoodViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onLogged()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Food") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.food?.let { food ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(food.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        if (food.brand.isNotBlank()) Text(food.brand, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        NutrientRow("Calories", "${food.calories.toInt()} kcal per ${food.servingSize.toInt()}${food.servingUnit}")
                        NutrientRow("Protein", "${food.protein}g")
                        NutrientRow("Carbs", "${food.carbohydrates}g")
                        NutrientRow("Fat", "${food.fat}g")
                    }
                }

                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text("Amount (${food.servingUnit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.error != null
                )

                Text("Meal", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.entries.forEach { meal ->
                        FilterChip(
                            selected = state.selectedMeal == meal,
                            onClick = { viewModel.onMealChange(meal) },
                            label = { Text(meal.label) }
                        )
                    }
                }

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

                Button(onClick = viewModel::logFood, modifier = Modifier.fillMaxWidth()) {
                    Text("Log Food")
                }
            } ?: Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun NutrientRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
