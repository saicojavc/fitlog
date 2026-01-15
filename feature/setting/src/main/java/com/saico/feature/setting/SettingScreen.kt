package com.saico.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.model.DarkThemeConfig
import com.saico.core.model.LanguageConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.setting.state.SettingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(LightPrimary, LightSuccess)
    } else {
        listOf(LightPrimary, LightSuccess, LightBackground)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = stringResource(id = R.string.settings),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surface
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(PaddingDim.MEDIUM)
        ) {
            when (val state = uiState) {
                is SettingUiState.Loading -> { /* Loading indicator */ }
                is SettingUiState.Success -> {
                    SettingsContent(
                        settings = state.settings,
                        onThemeChange = viewModel::updateDarkThemeConfig,
                        onLanguageChange = viewModel::updateLanguageConfig,
                        onDynamicColorChange = viewModel::updateDynamicColorPreference
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    settings: com.saico.core.model.UserData,
    onThemeChange: (DarkThemeConfig) -> Unit,
    onLanguageChange: (LanguageConfig) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit
) {
    // Modo Oscuro
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FitlogText(
            text = stringResource(id = R.string.theme),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Switch(
            checked = settings.darkThemeConfig == DarkThemeConfig.DARK,
            onCheckedChange = { isDark ->
                onThemeChange(if (isDark) DarkThemeConfig.DARK else DarkThemeConfig.LIGHT)
            },
            thumbContent = {
                Icon(
                    imageVector = if (settings.darkThemeConfig == DarkThemeConfig.DARK) FitlogIcons.Moon else FitlogIcons.Sun,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        )
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = PaddingDim.MEDIUM))

    // Idioma
    SettingSectionTitle(title = stringResource(id = R.string.language))
    Column {
        LanguageOption(
            label = stringResource(id = R.string.follow_system),
            selected = settings.languageConfig == LanguageConfig.FOLLOW_SYSTEM,
            onClick = { onLanguageChange(LanguageConfig.FOLLOW_SYSTEM) }
        )
        LanguageOption(
            label = stringResource(id = R.string.english),
            selected = settings.languageConfig == LanguageConfig.ENGLISH,
            onClick = { onLanguageChange(LanguageConfig.ENGLISH) }
        )
        LanguageOption(
            label = stringResource(id = R.string.spanish),
            selected = settings.languageConfig == LanguageConfig.SPANISH,
            onClick = { onLanguageChange(LanguageConfig.SPANISH) }
        )
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = PaddingDim.MEDIUM))

    // Color Dinámico
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            FitlogText(
                text = stringResource(id = R.string.dynamic_color),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FitlogText(
                text = stringResource(id = R.string.dynamic_color_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = settings.useDynamicColor,
            onCheckedChange = onDynamicColorChange
        )
    }
}

@Composable
fun SettingSectionTitle(title: String) {
    FitlogText(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = PaddingDim.SMALL)
    )
}

@Composable
fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = PaddingDim.EXTRA_SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        FitlogText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = PaddingDim.SMALL)
        )
    }
}