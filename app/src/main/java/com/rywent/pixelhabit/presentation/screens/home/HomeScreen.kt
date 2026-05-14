package com.rywent.pixelhabit.presentation.screens.home


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rywent.pixelhabit.R
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(55.dp))
        WelcomeHeader("Rywent")



    }
}


@OptIn(ExperimentalTextApi::class)
@Composable
fun WelcomeHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = "Hi,",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily(
                    Font(
                        resId = R.font.gflex_variable,
                        variationSettings = FontVariation.Settings(
                            FontVariation.weight(750),
                            FontVariation.width(252f)
                        )
                    )
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 55.sp,
                lineHeight = 10.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "$name!",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily(
                    Font(
                        resId = R.font.gflex_variable,
                        variationSettings = FontVariation.Settings(
                            FontVariation.weight(750),
                            FontVariation.width(155f)
                        )
                    )
                ),
                fontWeight = FontWeight.Black,
                fontSize = 55.sp,
                lineHeight = 10.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}