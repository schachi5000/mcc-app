package net.schacher.mcc

import LoginBridge
import MainView
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import co.touchlab.kermit.Logger


class MainActivity : AppCompatActivity() {

    private var loginBridge: LoginBridge? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.d("MainActivity") { "onCreate $this" }
        setContent {
            MainView {
                loginBridge = it
                openWebView(it.url)
            }
        }
    }

    private fun openWebView(url: String) {
        CustomTabsIntent.Builder()
            .setUrlBarHidingEnabled(true)
            .build()
            .launchUrl(this, Uri.parse(url))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        this.loginBridge?.let {
            if (it.isCallbackUrl(intent.data.toString())) {
                it.handleCallbackUrl(intent.data.toString())
                intent.data = null
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    MainView {

    }
}