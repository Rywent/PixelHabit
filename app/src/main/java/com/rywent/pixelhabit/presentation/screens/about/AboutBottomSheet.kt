package com.rywent.pixelhabit.presentation.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rywent.pixelhabit.R
import com.rywent.pixelhabit.presentation.components.customElements.SineWaveLine
import com.rywent.pixelhabit.presentation.screens.about.components.FeatureHeader
import com.rywent.pixelhabit.presentation.screens.about.components.InfoCardsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Pixel Habit",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily(
                            Font(resId = R.font.gflex_variable)
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        lineHeight = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                SineWaveLine(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .padding(horizontal = 10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    amplitude = 5.dp,
                    waves = 6f,
                    animate = true,
                    animationDurationMillis = 2500
                )

                Spacer(modifier = Modifier.height(16.dp))

                FeatureHeader(
                    version = uiState.appVersion,
                    data = uiState.releaseDate
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoCardsList(sections = uiState.changelogSections)
            }
        }
    }
}
