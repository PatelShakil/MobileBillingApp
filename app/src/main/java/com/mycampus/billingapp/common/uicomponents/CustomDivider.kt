package com.mycampus.billingapp.common.uicomponents

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DottedDivider(
    color: Color = Color.Gray,
    strokeWidth: Dp = 1.dp,
    dotRadius: Dp = 2.dp,
    gap: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val y = size.height / 2
        var x = 0f
        while (x < size.width) {
            drawCircle(color, dotRadius.toPx(), Offset(x, y))
            x += (dotRadius + gap).toPx()
        }
    }
}