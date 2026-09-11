package io.github.aiya000.copymenu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Shows the copy menu for whatever text the clipboard currently holds.
 *
 * The clipboard can only be read while the app has the window focus,
 * so the text is picked up in [onWindowFocusChanged] rather than in [onCreate].
 */
class MainActivity : ComponentActivity() {

    private var copiedText by mutableStateOf<String?>(null)
    private var clipboardRead = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val text = copiedText
                if (text != null) {
                    CopyMenuScreen(
                        text = text,
                        onTextCommitted = { edited ->
                            copiedText = edited
                            copyToClipboard(edited)
                        },
                        onShare = { shareText(text) },
                        onDismiss = { finish() },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        clipboardRead = false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus || clipboardRead) {
            return
        }
        clipboardRead = true

        val text = readClipboardText()
        if (text == null) {
            Toast.makeText(this, R.string.no_text_copied, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        copiedText = text
    }
}
