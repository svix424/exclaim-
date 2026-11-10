package com.cycletracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import com.cycletracker.app.domain.model.DayInfo
import com.cycletracker.app.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle as JTextStyle
import java.util.Locale

@Composable
fun CycleCalendar(
    month: YearMonth,
    dayInfoMap: Map<LocalDate, DayInfo>,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Month header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }
            Text(
                text = "${month.month.getDisplayName(JTextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Day-of-week headers (Sun–Sat)
        Row(Modifier.fillMaxWidth()) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Calendar grid
        val firstDay = month.atDay(1)
        // ISO dayOfWeek: Mon=1..Sun=7 → convert to Sun=0..Sat=6
        val startOffset = firstDay.dayOfWeek.let {
            if (it == DayOfWeek.SUNDAY) 0 else it.value
        }
        val totalDays = month.lengthOfMonth()
        val rows = ((startOffset + totalDays) + 6) / 7

        var dayCounter = 1
        repeat(rows) { row ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    if (cellIndex < startOffset || dayCounter > totalDays) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.atDay(dayCounter)
                        val info = dayInfoMap[date] ?: DayInfo()
                        DayCell(
                            day = dayCounter,
                            info = info,
                            isSelected = date == selectedDate,
                            onClick = { onDayClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                        dayCounter++
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Legend
        CalendarLegend()
    }
}

@Composable
private fun DayCell(
    day: Int,
    info: DayInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = isSystemInDarkTheme()
    val bgColor = when {
        info.isPeriod          -> if (dark) PeriodColor          else PeriodColorLight
        info.isOvulation       -> if (dark) OvulationColor       else OvulationColorLight
        info.isFertileWindow   -> if (dark) FertileColor         else FertileColorLight
        info.isPredictedPeriod -> if (dark) PredictedPeriodColor else PredictedPeriodColorLight
        else -> Color.Transparent
    }
    val todayRing = if (dark) TodayRingColor else TodayRingColorLight

    val textColor = when {
        info.isPeriod || info.isOvulation -> MaterialTheme.colorScheme.background
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .then(
                if (info.isToday) Modifier.border(2.dp, todayRing, CircleShape)
                else Modifier
            )
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 13.sp,
                fontWeight = if (info.isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            val majorPhase = info.moonPhase?.takeIf {
                it == com.cycletracker.app.domain.model.MoonPhase.NEW ||
                it == com.cycletracker.app.domain.model.MoonPhase.FIRST_QUARTER ||
                it == com.cycletracker.app.domain.model.MoonPhase.FULL ||
                it == com.cycletracker.app.domain.model.MoonPhase.LAST_QUARTER
            }
            if (majorPhase != null) {
                Text(text = majorPhase.emoji, fontSize = 8.sp, lineHeight = 8.sp)
            } else if (info.hasLog) {
                Box(
                    Modifier
                        .size(4.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun CalendarLegend() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(PeriodColor, "Period")
            LegendItem(PredictedPeriodColor, "Predicted")
            LegendItem(FertileColor, "Fertile")
            LegendItem(OvulationColor, "Ovulation")
        }
        Text(
            "🌑 New  🌓 First Quarter  🌕 Full  🌗 Last Quarter",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
