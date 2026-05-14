package com.nutrition.tracker.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nutrition.tracker.domain.model.MealType
import com.nutrition.tracker.presentation.imagenutrition.ImageNutritionViewModel
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageNutritionScreen(
    onLogged: () -> Unit,
    viewModel: ImageNutritionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraImageUri?.let { uri ->
                val bitmap = uri.toBitmap(context)
                bitmap?.let(viewModel::onImageCaptured)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = it.toBitmap(context)
            bitmap?.let(viewModel::onImageCaptured)
        }
    }

    LaunchedEffect(state.isLogged) {
        if (state.isLogged) onLogged()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Scan Food") })
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image preview or placeholder
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (state.capturedBitmap != null) {
                    Image(
                        bitmap = state.capturedBitmap!!.asImageBitmap(),
                        contentDescription = "Captured food",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("Take or upload a photo of your food", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (state.isAnalyzing) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            Text("Analyzing with AI...", color = Color.White)
                        }
                    }
                }
            }

            // Camera / Gallery buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        if (cameraPermission.status.isGranted) {
                            val uri = createImageUri(context)
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermission.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Camera")
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Gallery")
                }
            }

            if (!cameraPermission.status.isGranted && cameraPermission.status.shouldShowRationale) {
                Text(
                    "Camera permission is needed to take food photos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Analysis result
            state.result?.let { result ->
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(result.detectedFoodName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            AssistChip(
                                onClick = {},
                                label = { Text("${(result.confidence * 100).toInt()}% confident") }
                            )
                        }
                        Text("Serving: ${result.estimatedServing}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HorizontalDivider()
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            NutrientChip("Calories", "${result.nutrients.calories.toInt()} kcal", MaterialTheme.colorScheme.primary)
                            NutrientChip("Protein", "${result.nutrients.protein.toInt()}g", Color(0xFF2196F3))
                            NutrientChip("Carbs", "${result.nutrients.carbohydrates.toInt()}g", Color(0xFFFF9800))
                            NutrientChip("Fat", "${result.nutrients.fat.toInt()}g", Color(0xFFE91E63))
                        }
                    }
                }

                // Meal selector
                Text("Log to meal:", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.entries.forEach { meal ->
                        FilterChip(
                            selected = state.selectedMeal == meal,
                            onClick = { viewModel.onMealChange(meal) },
                            label = { Text(meal.label) }
                        )
                    }
                }

                Button(onClick = viewModel::logDetectedFood, modifier = Modifier.fillMaxWidth()) {
                    Text("Add to Log")
                }
            }

            if (state.capturedBitmap != null) {
                OutlinedButton(onClick = viewModel::reset, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Scan Another")
                }
            }
        }
    }
}

@Composable
private fun NutrientChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "food_photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

private fun Uri.toBitmap(context: Context): Bitmap? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
} catch (e: Exception) { null }
