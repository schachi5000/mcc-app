package net.schacher.mcc

import MainView
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainView()
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(
            "MainActivity",
            "onNewIntent: ${intent.data}"
        )
    }
}

@Preview
@Composable
fun Preview() {
    MainView()
}