package com.example.pants.ui.components

import android.graphics.Bitmap
import android.graphics.Color.HSVToColor
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import com.example.pants.R

private const val PICKER_WIDTH = 300

@Composable
fun HuePicker(
    modifier: Modifier = Modifier,
    hue: Float,
    animatedColor: Color,
    onHueChange: (Float) -> Unit,
) {
    var cursorWidth by remember { mutableIntStateOf(0) }

    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }

    val bitmap = remember(width, height) {
        if (width > 0 && height > 0) {
            createHueBitmap(width, height)
        } else {
            null
        }
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .width(PICKER_WIDTH.dp)
            .onSizeChanged { size ->
                width = size.width
                height = size.height
                cursorWidth = size.width
            }
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newHue = ((offset.x / size.width) * 360f).coerceIn(0f, 360f)
                        onHueChange(newHue)
                    }
                }
        ) {
            bitmap?.let {
                drawHueBitmap(it)
            }
        }
        var cursorWidth by remember { mutableIntStateOf(0) }
        Image(
            painter = painterResource(id = R.drawable.rectangle_cursor),
            contentDescription = null,
            modifier = Modifier
                .height(40.dp)
                .onSizeChanged { cursorWidth = it.width }
                .then(
                    with(LocalDensity.current) {
                        Modifier.offset(x = ((hue / 360f) * (PICKER_WIDTH - cursorWidth.toDp().value)).dp)
                    }
                )
        )
    }
}

private fun createHueBitmap(width: Int, height: Int): Bitmap {
    require(width > 0 && height > 0) { "Width and height must be greater than 0" }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        val hueCanvas = android.graphics.Canvas(this)
        val huePanel = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val hueColors = IntArray(width) { i ->
            val hue = (i / width.toFloat()) * 360f
            HSVToColor(floatArrayOf(hue, 1f, 1f))
        }
        val linePaint = Paint().apply { strokeWidth = 0f }
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            hueCanvas.drawLine(i.toFloat(), 0f, i.toFloat(), huePanel.bottom, linePaint)
        }
    }
}


private fun DrawScope.drawHueBitmap(bitmap: Bitmap) {
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawBitmap(bitmap, null, RectF(0f, 0f, size.width, size.height).toRect(), null)
    }
}

