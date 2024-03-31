package dev.nomadicprogrammer.spendly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(slices: List<Slice>, useCenter : Boolean = false, style : DrawStyle = Fill) {
    val totalAngle = 360f
    var startAngle = 0f
    val canvasSize = remember { mutableStateOf(Size.Zero) }

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        canvasSize.value = size

        slices.forEachIndexed { index, (value, _) ->
            val sweepAngle = (value / slices.sumOf { it.value.toInt() }) * totalAngle
            drawArc(
                color = slices[index].color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = useCenter,
                size = canvasSize.value,
                style = style,
            )
            startAngle += sweepAngle
        }
    }
}

data class Slice(val value: Float, val color: Color)