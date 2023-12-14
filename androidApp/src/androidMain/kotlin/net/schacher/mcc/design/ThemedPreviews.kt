package net.schacher.mcc.design

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Dark Mode", showBackground = true, backgroundColor = Color.DKGRAY.toLong(), uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, backgroundColor = Color.LTGRAY.toLong(), uiMode = UI_MODE_NIGHT_NO)
annotation class ThemedPreviews