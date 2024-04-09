package net.schacher.mcc

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.schacher.mcc.shared.auth.AuthHandler
import net.schacher.mcc.shared.auth.AuthHandler.APP_SCHEME

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainView()
        }
    }

    override fun onResume() {
        super.onResume()

        if (intent.data.toString().startsWith(APP_SCHEME)) {
            AuthHandler.handleCallbackUrl(intent.data.toString())
            intent.data = null
        }
    }
}

@Preview
@Composable
fun Preview() {
    MainView()
}