package com.rywent.pixelhabit.presentation.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.R
import com.rywent.pixelhabit.presentation.components.customElements.SineWaveLine
import com.rywent.pixelhabit.presentation.screens.about.components.FeatureHeader
import com.rywent.pixelhabit.presentation.screens.about.components.InfoCardsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    versions: List<VersionUiState>
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )

    val scrollState = rememberScrollState()

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
            sheetGesturesEnabled = true,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 5.dp)
                    .verticalScroll(scrollState)
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

                versions.forEachIndexed { index, version ->
                    FeatureHeader(
                        version = version.version,
                        data = version.releaseDate
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoCardsList(sections = version.sections)

                    if (index < versions.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))

                    }
                }
            }
        }
    }
}