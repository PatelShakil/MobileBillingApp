package com.mycampus.billingapp.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.ScrollView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView


//        implementation "dev.shreyaspatil:capturable:1.0.3"



@Composable
fun ScrollableCapturable(
    modifier: Modifier = Modifier,
    controller: CaptureController,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    content: @Composable () -> Unit
) {
    AndroidView(
        factory = { context ->
            val scrollView = ScrollView(context)
            val composeView = ComposeView(context).apply {
                setContent {
                    content()
                }
            }
            scrollView.addView(composeView)
            scrollView
        },
        update = { scrollView ->
            if (controller.readyForCapture) {
                // Hide scrollbars for capture
                scrollView.isVerticalScrollBarEnabled = false
                scrollView.isHorizontalScrollBarEnabled = false
                try {
                    val bitmap = loadBitmapFromScrollView(scrollView)
                    onCaptured(bitmap, null)
                } catch (throwable: Throwable) {
                    onCaptured(null, throwable)
                }
                scrollView.isVerticalScrollBarEnabled = true
                scrollView.isHorizontalScrollBarEnabled = true
                controller.captured()
            }
        },
        modifier = modifier
    )
}

/**
 * Need to use view.getChildAt(0).height instead of just view.height,
 * so you can get all ScrollView content.
 */
private fun loadBitmapFromScrollView(scrollView: ScrollView): Bitmap {
    val bitmap = Bitmap.createBitmap(
        scrollView.width,
        scrollView.getChildAt(0).height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    scrollView.draw(canvas)
    return bitmap
}

class CaptureController {
    var readyForCapture by mutableStateOf(false)
        private set

    fun capture() {
        readyForCapture = true
    }

    internal fun captured() {
        readyForCapture = false
    }
}

@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}
