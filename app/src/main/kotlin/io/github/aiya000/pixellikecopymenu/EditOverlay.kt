package io.github.aiya000.pixellikecopymenu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
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
 * Tapping outside of the panel finishes the editing and hands the edited text
 * to [onCommit], the same as the check button.
 * The close button and the back gesture discard the edit.
 *
 * The panel keeps the same size whether the keyboard is shown or not. Instead of
 * shrinking the panel, the text area is padded at the bottom by the height the
 * keyboard hides, so that the last line can still be brought above the keyboard.
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

    val density = LocalDensity.current
    var windowHeight by remember { mutableIntStateOf(0) }
    var textAreaBottom by remember { mutableFloatStateOf(0f) }
    val keyboardTop = windowHeight - WindowInsets.ime.getBottom(density)
    val hiddenByKeyboard = with(density) {
        (textAreaBottom - keyboardTop).coerceAtLeast(0f).toDp()
    }

    BackHandler { onCancel() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { windowHeight = it.size.height }
            .background(CopyMenuColors.Scrim)
            .noRippleClickable { onCommit(value.text) }
            .systemBarsPadding()
            .padding(start = 22.dp, top = 44.dp, end = 22.dp, bottom = 32.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircleButton(
                    icon = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.done),
                    onClick = { onCommit(value.text) },
                    size = 40.dp,
                    iconSize = 20.dp,
                )

                CircleButton(
                    icon = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close),
                    onClick = onCancel,
                    size = 40.dp,
                    iconSize = 20.dp,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(PanelBrush)
                    .noRippleClickable { }
                    .padding(20.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(CopyMenuColors.PanelInner)
                    .onGloballyPositioned {
                        textAreaBottom = it.positionInWindow().y + it.size.height
                    }
                    .padding(14.dp),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = hiddenByKeyboard)
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
