package com.saico.feature.gymwork.routineconfig

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.saico.core.model.WgerExercise
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.GradientColors
import com.saico.feature.gymwork.component.DaySelectorTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineConfigScreen(
    navController: NavHostController,
    viewModel: RoutineConfigViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            FitlogTopAppBar(
                title = "CONFIGURAR RUTINA",
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                navigationIcon = {
                    FitlogIcon(
                        modifier = Modifier.clickable { navController.popBackStack() },
                        imageVector = FitlogIcons.ArrowBack,
                        background = Color.Transparent,
                        contentDescription = null
                    )
                }
            )
        },
        bottomBar = {
            SaveBottomBar(
                selectedCount = uiState.selectedExercises.size,
                isSaving = uiState.isSaving,
                onSave = viewModel::saveRoutine
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(GradientColors))
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                DaySelectorTabs(
                    selectedDay = uiState.selectedDay,
                    onDaySelected = viewModel::onDaySelected,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                SearchAndFilterSection(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected
                )

                ExerciseSelectionList(
                    availableExercises = uiState.availableExercises,
                    selectedExercises = uiState.selectedExercises,
                    onToggle = viewModel::toggleExerciseSelection,
                    onLoadMore = viewModel::loadNextPage,
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Buscar ejercicio...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.05f)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            leadingIcon = { Icon(FitlogIcons.Search, contentDescription = null, tint = Color.Gray) }
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category, color = if (isSelected) Color.White else Color.Gray) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White.copy(alpha = 0.05f),
                        selectedContainerColor = Color(0xFF38BDF8)
                    ),
                    border = null // Evitar problemas de versión con el border
                )
            }
        }
    }
}

@Composable
fun ExerciseSelectionList(
    availableExercises: List<WgerExercise>,
    selectedExercises: List<WgerExercise>,
    onToggle: (WgerExercise) -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean
) {
    if (isLoading && availableExercises.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF38BDF8))
        }
    } else {
        val listState = rememberLazyListState()
        
        // Efecto para cargar más cuando llegamos al final
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { index: Int? ->
                    if (index != null && index >= availableExercises.size - 5) {
                        onLoadMore()
                    }
                }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableExercises, key = { it.id }) { exercise ->
                val isSelected = selectedExercises.any { it.id == exercise.id }
                ExerciseConfigCard(
                    exercise = exercise,
                    isSelected = isSelected,
                    onToggle = { onToggle(exercise) }
                )
            }
        }
    }
}

@Composable
fun ExerciseConfigCard(
    exercise: WgerExercise,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF38BDF8).copy(alpha = 0.1f) else Color(0xFF111827))
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = exercise.imageUrl ?: "https://wger.de/static/images/icons/exercise.png",
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = exercise.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF38BDF8)
            )
        }

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF38BDF8),
                uncheckedColor = Color.White.copy(alpha = 0.3f),
                checkmarkColor = Color.White
            )
        )
    }
}

@Composable
fun SaveBottomBar(
    selectedCount: Int,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Color(0xFF0D1424).copy(alpha = 0.9f)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("SELECCIONADOS", color = Color.Gray, fontSize = 10.sp)
                Text(
                    "$selectedCount EJERCICIOS",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = onSave,
                enabled = selectedCount > 0 && !isSaving,
                modifier = Modifier
                    .width(160.dp)
                    .height(56.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0xFF38BDF8)),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (selectedCount > 0) Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF216EE0)))
                            else SolidColor(Color.Gray)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "GUARDAR",
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
