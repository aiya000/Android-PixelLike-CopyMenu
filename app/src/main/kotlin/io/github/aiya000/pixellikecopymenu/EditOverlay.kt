package io.github.aiya000.pixellikecopymenu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The editing panel shown when the copied text is tapped.
 *
 * Tapping outside of the panel (or going back) finishes the editing and
 * hands the edited text to [onCommit]. The close button discards the edit.
 */
@Composable
fun EditOverlay(
    initialText: String,
    onCommit: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var value by remember {
        mutableStateOf(TextFieldValue(initialText, TextRange(initialText.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    BackHandler { onCommit(value.text) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CopyMenuColors.Scrim)
            .noRippleClickable { onCommit(value.text) }
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 22.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .clip(RoundedCornerShape(30.dp))
                .background(PanelBrush)
                .noRippleClickable { }
                .padding(16.dp),
        ) {
            CircleButton(
                icon = Icons.Filled.Close,
                contentDescription = stringResource(R.string.close),
                onClick = onCancel,
                size = 40.dp,
                iconSize = 20.dp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(CopyMenuColors.PanelInner)
                    .padding(14.dp),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = CopyMenuColors.OnSurface,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                    ),
                    cursorBrush = SolidColor(CopyMenuColors.Cursor),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
}
