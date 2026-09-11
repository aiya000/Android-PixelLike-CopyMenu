package io.github.aiya000.copymenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The launcher UI: the copied text on the left, the share button next to it.
 *
 * @param text the text currently held by the clipboard
 * @param onTextCommitted called with the edited text when editing finishes
 * @param onShare called when the share button is tapped
 * @param onDismiss called when the area outside of the menu is tapped
 */
@Composable
fun CopyMenuScreen(
    text: String,
    onTextCommitted: (String) -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
) {
    var editing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomStart,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            CopiedTextCard(
                text = text,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = 260.dp),
                onClick = { editing = true },
            )
            CircleButton(
                icon = Icons.Filled.Share,
                contentDescription = stringResource(R.string.share),
                onClick = onShare,
            )
        }
    }

    if (editing) {
        EditOverlay(
            initialText = text,
            onCommit = { edited ->
                editing = false
                onTextCommitted(edited)
            },
            onCancel = { editing = false },
        )
    }
}

@Composable
private fun CopiedTextCard(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AccentBrush)
            .padding(6.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(CopyMenuColors.CardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(
            text = text,
            color = CopyMenuColors.OnSurface,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(AccentBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = CopyMenuColors.OnSurface,
            modifier = Modifier.size(28.dp),
        )
    }
}

/** A [clickable] without the ripple, for backgrounds that only need to catch taps. */
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick,
)
