package com.cycletracker.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Catppuccin Macchiato (dark) ────────────────────────────────────────────
object Macchiato {
    val Rosewater = Color(0xFFF4DBD6)
    val Flamingo  = Color(0xFFF0C6C6)
    val Pink      = Color(0xFFF5BDE6)
    val Mauve     = Color(0xFFC6A0F6)
    val Red       = Color(0xFFED8796)
    val Maroon    = Color(0xFFEE99A0)
    val Peach     = Color(0xFFF5A97F)
    val Yellow    = Color(0xFFEED49F)
    val Green     = Color(0xFFA6DA95)
    val Teal      = Color(0xFF8BD5CA)
    val Sky       = Color(0xFF91D7E3)
    val Sapphire  = Color(0xFF7DC4E4)
    val Blue      = Color(0xFF8AADF4)
    val Lavender  = Color(0xFFB7BDF8)
    val Text      = Color(0xFFCAD3F5)
    val Subtext1  = Color(0xFFB8C0E0)
    val Subtext0  = Color(0xFFA5ADCB)
    val Overlay2  = Color(0xFF939AB7)
    val Overlay1  = Color(0xFF8087A2)
    val Overlay0  = Color(0xFF6E738D)
    val Surface2  = Color(0xFF5B6078)
    val Surface1  = Color(0xFF494D64)
    val Surface0  = Color(0xFF363A4F)
    val Base      = Color(0xFF24273A)
    val Mantle    = Color(0xFF1E2030)
    val Crust     = Color(0xFF181926)
}

// ── Catppuccin Latte (light) ───────────────────────────────────────────────
object Latte {
    val Rosewater = Color(0xFFDC8A78)
    val Flamingo  = Color(0xFFDD7878)
    val Pink      = Color(0xFFEA76CB)
    val Mauve     = Color(0xFF8839EF)
    val Red       = Color(0xFFD20F39)
    val Maroon    = Color(0xFFE64553)
    val Peach     = Color(0xFFFE640B)
    val Yellow    = Color(0xFFDF8E1D)
    val Green     = Color(0xFF40A02B)
    val Teal      = Color(0xFF179299)
    val Sky       = Color(0xFF04A5E5)
    val Sapphire  = Color(0xFF209FB5)
    val Blue      = Color(0xFF1E66F5)
    val Lavender  = Color(0xFF7287FD)
    val Text      = Color(0xFF4C4F69)
    val Subtext1  = Color(0xFF5C5F77)
    val Subtext0  = Color(0xFF6C6F85)
    val Overlay2  = Color(0xFF7C7F93)
    val Overlay1  = Color(0xFF8C8FA1)
    val Overlay0  = Color(0xFF9CA0B0)
    val Surface2  = Color(0xFFACB0BE)
    val Surface1  = Color(0xFFBCC0CC)
    val Surface0  = Color(0xFFCCD0DA)
    val Base      = Color(0xFFEFF1F5)
    val Mantle    = Color(0xFFE6E9EF)
    val Crust     = Color(0xFFDCE0E8)
}

// ── Semantic calendar colours (used in CycleCalendar) ─────────────────────
// Dark (Macchiato)
val PeriodColor          = Macchiato.Maroon
val PredictedPeriodColor = Color(0xFF5B3F44)   // dark maroon tint on Surface0
val FertileColor         = Color(0xFF2E4D4A)   // teal tint on Surface0
val OvulationColor       = Macchiato.Mauve
val TodayRingColor       = Macchiato.Peach

// Light (Latte) — referenced via LocalCalendarColors in Theme if needed
val PeriodColorLight          = Latte.Maroon
val PredictedPeriodColorLight = Color(0xFFFFD6DA)
val FertileColorLight         = Color(0xFFCCF2F0)
val OvulationColorLight       = Latte.Lavender
val TodayRingColorLight       = Latte.Peach
