package com.nutrition.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrition.tracker.domain.model.ActivityLevel
import com.nutrition.tracker.domain.model.Gender
import com.nutrition.tracker.presentation.profile.ProfileViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val profile = state.profile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = viewModel::save) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    } else {
                        IconButton(onClick = viewModel::startEditing) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
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
            // Stats summary card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("BMI", String.format("%.1f", profile.bmi), profile.bmiCategory)
                        StatItem("TDEE", "${profile.tdee.toInt()} kcal", "Daily energy")
                        StatItem("Goal", "${profile.effectiveCalorieGoal.toInt()} kcal", "Target")
                    }
                }
            }

            SectionTitle("Personal Info")
            ProfileField("Name", profile.name, state.isEditing) { viewModel.updateName(it) }
            ProfileNumberField("Age", profile.age.toString(), state.isEditing) {
                it.toIntOrNull()?.let(viewModel::updateAge)
            }
            ProfileNumberField("Weight (kg)", profile.weightKg.toString(), state.isEditing) {
                it.toFloatOrNull()?.let(viewModel::updateWeight)
            }
            ProfileNumberField("Height (cm)", profile.heightCm.toString(), state.isEditing) {
                it.toFloatOrNull()?.let(viewModel::updateHeight)
            }

            SectionTitle("Gender")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gender.entries.forEach { g ->
                    FilterChip(
                        selected = profile.gender == g,
                        onClick = { if (state.isEditing) viewModel.updateGender(g) },
                        label = { Text(g.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            SectionTitle("Activity Level")
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ActivityLevel.entries.forEach { level ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = profile.activityLevel == level,
                            onClick = { if (state.isEditing) viewModel.updateActivity(level) }
                        )
                        Column {
                            Text(level.label, fontWeight = FontWeight.Medium)
                            Text("×${level.multiplier}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            SectionTitle("Custom Goals (0 = auto)")
            ProfileNumberField("Calorie Goal (kcal)", profile.goalCalories.toString(), state.isEditing) {
                it.toFloatOrNull()?.let(viewModel::updateCalorieGoal)
            }
            ProfileNumberField("Protein Goal (g)", profile.goalProtein.toString(), state.isEditing) {
                it.toFloatOrNull()?.let(viewModel::updateProteinGoal)
            }
            ProfileNumberField("Carb Goal (g)", profile.goalCarbs.toString(), state.isEditing) {
                it.toFloatOrNull()?.let(viewModel::updateCarbGoal)
            }
            ProfileNumberField("Fat Goal (g)", profile.goalFat.toString(), state.isEditing) {
                it.toFloatOrNull()?.let(viewModel::updateFatGoal)
            }

            if (state.isSaved) {
                Snackbar { Text("Profile saved!") }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun StatItem(label: String, value: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileField(label: String, value: String, editable: Boolean, onValueChange: (String) -> Unit) {
    if (editable) {
        OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
    } else {
        ReadOnlyField(label, value)
    }
}

@Composable
private fun ProfileNumberField(label: String, value: String, editable: Boolean, onValueChange: (String) -> Unit) {
    if (editable) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    } else {
        ReadOnlyField(label, value)
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(thickness = 0.5.dp)
}
