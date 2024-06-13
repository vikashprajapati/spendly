package dev.nomadicprogrammer.spendly.ui.components

import android.util.Log
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
fun PieChart(slices: List<Slice>, useCenter : Boolean = false, style : DrawStyle = Stroke(width = 16f, cap = StrokeCap.Round)) {
    val totalAngle = 360f
    var startAngle = 0f
    val canvasSize = remember { mutableStateOf(Size.Zero) }

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        canvasSize.value = size
        val totalValue = slices.sumOf { it.value.toInt() }
        if (totalValue == 0) {
            drawArc(
                color = Color.LightGray,
                startAngle = startAngle,
                sweepAngle = totalAngle,
                useCenter = useCenter,
                size = canvasSize.value,
                style = style,
            )
        }else{
            slices.forEach{ (value, color) ->
                val sweepAngle = (value / totalValue) * totalAngle
                Log.d("PieChart", "startAngle: $startAngle, sweepAngle: $sweepAngle")
                drawArc(
                    color = color,
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
}

data class Slice(val value: Float, val color: Color)