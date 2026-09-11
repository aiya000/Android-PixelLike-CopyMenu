package io.github.aiya000.pixellikecopymenu

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

/**
 * Returns the plain text of the current clipboard content,
 * or null when the clipboard is empty or does not hold text.
 */
fun Context.readClipboardText(): String? {
    val manager = getSystemService(ClipboardManager::class.java) ?: return null
    if (!manager.hasPrimaryClip()) {
        return null
    }

    val description = manager.primaryClipDescription ?: return null
    val holdsText = description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
        description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
    if (!holdsText) {
        return null
    }

    val clip = manager.primaryClip ?: return null
    if (clip.itemCount == 0) {
        return null
    }

    return clip.getItemAt(0).text?.toString()?.takeIf { it.isNotEmpty() }
}

fun Context.copyToClipboard(text: String) {
    val manager = getSystemService(ClipboardManager::class.java) ?: return
    manager.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))
}

fun Context.shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, null))
}
