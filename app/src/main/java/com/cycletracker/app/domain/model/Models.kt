package com.cycletracker.app.domain.model

enum class FlowIntensity(val label: String) {
    NONE("None"),
    SPOTTING("Spotting"),
    LIGHT("Light"),
    MEDIUM("Medium"),
    HEAVY("Heavy")
}

enum class Mood(val label: String, val emoji: String) {
    HAPPY("Happy", "😊"),
    CALM("Calm", "😌"),
    ANXIOUS("Anxious", "😰"),
    IRRITABLE("Irritable", "😤"),
    SAD("Sad", "😢"),
    ENERGETIC("Energetic", "⚡"),
    TIRED("Tired", "😴")
}

enum class Symptom(val label: String) {
    CRAMPS("Cramps"),
    HEADACHE("Headache"),
    BACKACHE("Backache"),
    BLOATING("Bloating"),
    BREAST_TENDERNESS("Breast Tenderness"),
    NAUSEA("Nausea"),
    FATIGUE("Fatigue"),
    ACNE("Acne"),
    MOOD_SWINGS("Mood Swings"),
    ANXIETY("Anxiety"),
    IRRITABILITY("Irritability"),
    FOOD_CRAVINGS("Food Cravings"),
    INSOMNIA("Insomnia"),
    DIZZINESS("Dizziness"),
    SPOTTING("Spotting"),
    HOT_FLASHES("Hot Flashes")
}

enum class MoonPhase(val emoji: String, val label: String) {
    NEW("🌑", "New Moon"),
    WAXING_CRESCENT("🌒", "Waxing Crescent"),
    FIRST_QUARTER("🌓", "First Quarter"),
    WAXING_GIBBOUS("🌔", "Waxing Gibbous"),
    FULL("🌕", "Full Moon"),
    WANING_GIBBOUS("🌖", "Waning Gibbous"),
    LAST_QUARTER("🌗", "Last Quarter"),
    WANING_CRESCENT("🌘", "Waning Crescent")
}

data class Tip(val text: String, val url: String = "")

enum class CyclePhase(
    val label: String,
    val description: String,
    val bodyDetail: String,
    val quickWins: List<Tip>
) {
    MENSTRUAL(
        label = "Menstrual Phase",
        description = "Your period has arrived",
        bodyDetail = "Your uterine lining is shedding as estrogen and progesterone reach their lowest point of the cycle. Prostaglandins trigger uterine muscle contractions — the direct cause of cramping. Fatigue, low mood, and a natural urge to slow down and turn inward are completely normal physiological responses during this time.",
        quickWins = listOf(
            Tip(
                "Apply heat to your lower abdomen or lower back for cramp relief",
                "https://www.mayoclinic.org/diseases-conditions/menstrual-cramps/diagnosis-treatment/drc-20374944"
            ),
            Tip(
                "Eat iron-rich foods (spinach, lentils, red meat) to replenish what is lost",
                "https://www.healthline.com/nutrition/increase-iron-absorption"
            ),
            Tip(
                "Rest without guilt — your energy will naturally begin rising again in a few days",
                "https://www.healthline.com/health/womens-health/period-fatigue"
            )
        )
    ),
    FOLLICULAR(
        label = "Follicular Phase",
        description = "Energy is rising",
        bodyDetail = "Estrogen is climbing as follicles in your ovaries develop. Your uterine lining is rebuilding. You will likely notice increasing energy, sharper mental clarity, an elevated mood, and a stronger drive to socialise. Serotonin and dopamine are more available, making this your brain's most creative and optimistic phase of the cycle.",
        quickWins = listOf(
            Tip(
                "Channel rising energy into demanding tasks, new projects, or learning",
                "https://www.healthline.com/health/womens-health/follicular-phase"
            ),
            Tip(
                "Gradually increase workout intensity — your body handles more load right now",
                "https://www.healthline.com/health/fitness-exercise/cycle-syncing"
            ),
            Tip(
                "Eat protein and complex carbs to sustain your natural surge in energy",
                "https://www.healthline.com/nutrition/good-carbs-bad-carbs"
            )
        )
    ),
    OVULATION(
        label = "Ovulation",
        description = "Peak fertility window",
        bodyDetail = "A sharp surge in LH (luteinizing hormone) has triggered your dominant follicle to release a mature egg. Estrogen and testosterone peak together, boosting confidence, libido, verbal fluency, and emotional attunement. The egg is viable for 12–24 hours — this is your most fertile window of the entire cycle.",
        quickWins = listOf(
            Tip(
                "Watch for signs: clear stretchy discharge, mild pelvic twinges, a slight temperature rise",
                "https://www.mayoclinic.org/healthy-lifestyle/getting-pregnant/in-depth/ovulation/art-20047098"
            ),
            Tip(
                "Your communication and social skills are at their peak — great timing for important conversations",
                "https://www.healthline.com/health/ovulation-signs"
            ),
            Tip(
                "High intensity exercise feels excellent right now — your body is primed for peak performance",
                "https://www.healthline.com/health/fitness-exercise/cycle-syncing"
            )
        )
    ),
    LUTEAL(
        label = "Luteal Phase",
        description = "Progesterone is rising",
        bodyDetail = "Progesterone rises to thicken the uterine lining in case of pregnancy. If fertilisation does not occur, both estrogen and progesterone drop sharply in the final days, triggering your next period. PMS symptoms — bloating, breast tenderness, mood shifts, and food cravings — are most pronounced in the second half of this phase.",
        quickWins = listOf(
            Tip(
                "Magnesium from dark chocolate, nuts, or leafy greens reliably eases PMS symptoms",
                "https://www.healthline.com/nutrition/magnesium-and-pms"
            ),
            Tip(
                "Reduce alcohol and sugar, which worsen mood swings and disrupt sleep",
                "https://www.nhs.uk/conditions/premenstrual-syndrome/"
            ),
            Tip(
                "Gentle movement like yoga or walking stabilises mood better than rest alone",
                "https://www.healthline.com/health/womens-health/yoga-for-pms"
            )
        )
    ),
    LATE(
        label = "Period May Be Late",
        description = "Cycle running longer than usual",
        bodyDetail = "Your period is arriving later than predicted. This most commonly happens because ovulation was delayed — by stress, illness, poor sleep, significant weight change, or intense exercise. A delayed ovulation pushes the entire cycle back. Occasional longer cycles are completely normal; concern is typically only warranted beyond 35–40 days.",
        quickWins = listOf(
            Tip(
                "If sexually active, consider a pregnancy test to rule that out first",
                "https://www.mayoclinic.org/healthy-lifestyle/getting-pregnant/in-depth/pregnancy-test/art-20047390"
            ),
            Tip(
                "Reduce stress — elevated cortisol directly suppresses and delays ovulation",
                "https://www.healthline.com/health/womens-health/stress-and-menstrual-cycle"
            ),
            Tip(
                "If cycles are regularly irregular, speak with a healthcare provider",
                "https://www.nhs.uk/conditions/irregular-periods/"
            )
        )
    ),
    UNKNOWN(
        label = "Start Tracking",
        description = "Log your first period to get started",
        bodyDetail = "Log your first period start date to begin receiving cycle predictions, phase-based health tips, and fertile window estimates. The app learns your unique rhythm over time — most users see reliable predictions after just 2–3 logged cycles. All data stays encrypted on your device.",
        quickWins = listOf(
            Tip("Tap the 'Period today?' switch below to log your first day"),
            Tip("Log symptoms and mood daily for richer, more personalised insights"),
            Tip("After 2–3 cycles, predictions become highly accurate for your body")
        )
    )
}

/** Summary of a single calendar day, computed by CyclePredictionEngine. */
data class DayInfo(
    val isPeriod: Boolean = false,
    val isPredictedPeriod: Boolean = false,
    val isFertileWindow: Boolean = false,
    val isOvulation: Boolean = false,
    val isToday: Boolean = false,
    val hasLog: Boolean = false,
    val moonPhase: MoonPhase? = null
)

/** Top-level cycle summary shown on the Home screen. */
data class CycleSummary(
    val phase: CyclePhase = CyclePhase.UNKNOWN,
    val dayOfCycle: Int? = null,
    val daysUntilNextPeriod: Int? = null,
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val nextPeriodDate: java.time.LocalDate? = null,
    val ovulationDate: java.time.LocalDate? = null,
    val confidence: Confidence = Confidence.LOW
)

enum class Confidence { LOW, MEDIUM, HIGH }
