package com.cycletracker.app.domain

import com.cycletracker.app.domain.model.CyclePhase

data class Tip(val title: String, val body: String, val category: TipCategory, val url: String = "")

enum class TipCategory(val label: String) {
    NUTRITION("Nutrition"),
    WELLNESS("Wellness"),
    EXERCISE("Exercise"),
    HEALTH("Health"),
    MINDFULNESS("Mindfulness")
}

object TipsContent {

    fun tipsFor(phase: CyclePhase): List<Tip> = when (phase) {
        CyclePhase.MENSTRUAL -> menstrualTips
        CyclePhase.FOLLICULAR -> follicularTips
        CyclePhase.OVULATION -> ovulationTips
        CyclePhase.LUTEAL -> lutealTips
        CyclePhase.LATE -> lateTips
        CyclePhase.UNKNOWN -> generalTips
    }

    private val menstrualTips = listOf(
        Tip("Stay Hydrated", "Drinking plenty of water reduces bloating and can ease cramps. Aim for 8–10 glasses a day.", TipCategory.NUTRITION,
            "https://www.healthline.com/health/womens-health/period-bloating"),
        Tip("Heat Therapy", "A heating pad or warm water bottle on your lower abdomen relaxes uterine muscles and reduces cramp intensity.", TipCategory.WELLNESS,
            "https://www.mayoclinic.org/diseases-conditions/menstrual-cramps/diagnosis-treatment/drc-20374944"),
        Tip("Iron-Rich Foods", "Your body loses iron during menstruation. Eat spinach, lentils, tofu, and lean meat to replenish stores.", TipCategory.NUTRITION,
            "https://www.nhs.uk/conditions/iron-deficiency-anaemia/"),
        Tip("Gentle Movement", "Light yoga, walking, or stretching releases endorphins that naturally ease period pain — skip high-intensity workouts today.", TipCategory.EXERCISE,
            "https://my.clevelandclinic.org/health/diseases/4148-dysmenorrhea"),
        Tip("Anti-Inflammatory Foods", "Ginger tea, turmeric, and omega-3 rich foods like salmon can help reduce inflammation and cramping.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/anti-inflammatory-diet-101"),
        Tip("Rest is Productive", "Fatigue during your period is real. Prioritize sleep and don't overbook yourself. Rest supports healing.", TipCategory.WELLNESS,
            "https://www.healthline.com/health/womens-health/fatigue-before-period"),
        Tip("Limit Caffeine", "Caffeine constricts blood vessels and can worsen cramps. Try chamomile or raspberry leaf tea instead.", TipCategory.HEALTH,
            "https://my.clevelandclinic.org/health/diseases/4148-dysmenorrhea"),
        Tip("Track Your Flow", "Noting flow intensity each day helps detect patterns. Sudden changes in flow can be worth discussing with a doctor.", TipCategory.HEALTH,
            "https://www.nhs.uk/conditions/heavy-periods/")
    )

    private val follicularTips = listOf(
        Tip("Energy is Rising", "Estrogen is climbing and so is your energy. This is an ideal time to tackle demanding tasks or start new projects.", TipCategory.WELLNESS,
            "https://www.healthline.com/health/womens-health/follicular-phase"),
        Tip("Strength Training", "Your body responds especially well to strength and endurance exercise in this phase. Try adding resistance to your workouts.", TipCategory.EXERCISE,
            "https://www.healthline.com/health/womens-health/follicular-phase"),
        Tip("Focus on Protein", "Protein supports muscle recovery and keeps energy steady. Include eggs, legumes, or Greek yogurt in your meals.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/how-much-protein-per-day"),
        Tip("Social Energy", "Many people feel more outgoing and communicative in the follicular phase. A great time for meetings or creative collaboration.", TipCategory.WELLNESS,
            "https://www.healthline.com/health/womens-health/follicular-phase"),
        Tip("Seed Cycling: Flax & Pumpkin", "Some evidence suggests flaxseed and pumpkin seeds in the follicular phase support estrogen balance. Try adding them to smoothies.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/seed-cycling"),
        Tip("Gut Health", "A healthy gut microbiome helps metabolize estrogen. Include fermented foods like kefir, yogurt, or kimchi.", TipCategory.HEALTH,
            "https://www.healthline.com/health/womens-health/follicular-phase")
    )

    private val ovulationTips = listOf(
        Tip("Peak Cognitive Power", "Estrogen and testosterone are at their highest. You may notice sharper focus, better memory, and quicker thinking.", TipCategory.WELLNESS,
            "https://www.nhs.uk/conditions/getting-pregnant/understanding-your-cycle/"),
        Tip("High-Intensity Exercise", "Your body is primed for peak performance. HIIT, cycling, or sports you enjoy are all great choices today.", TipCategory.EXERCISE,
            "https://www.nhs.uk/conditions/getting-pregnant/understanding-your-cycle/"),
        Tip("Fertile Window Awareness", "If you are trying to conceive, the 5 days before and 1 day after ovulation are your most fertile. If avoiding pregnancy, use protection consistently.", TipCategory.HEALTH,
            "https://www.mayoclinic.org/healthy-lifestyle/getting-pregnant/in-depth/ovulation/art-20047098"),
        Tip("Ovulation Signs", "You may notice clear, stretchy cervical mucus (like egg whites), mild pelvic twinges, or a slight rise in basal body temperature.", TipCategory.HEALTH,
            "https://www.mayoclinic.org/healthy-lifestyle/getting-pregnant/in-depth/ovulation/art-20047098"),
        Tip("Stay Cool", "Body temperature rises slightly at ovulation. Stay hydrated and avoid overheating during exercise.", TipCategory.WELLNESS,
            "https://www.nhs.uk/conditions/getting-pregnant/understanding-your-cycle/"),
        Tip("Zinc-Rich Foods", "Zinc supports egg health and hormone balance. Sources include pumpkin seeds, cashews, chickpeas, and shellfish.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/best-foods-high-in-zinc")
    )

    private val lutealTips = listOf(
        Tip("Manage PMS Naturally", "Magnesium (found in dark chocolate, nuts, and leafy greens) can ease PMS symptoms including irritability and cramps.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/magnesium-benefits"),
        Tip("Reduce Sugar Spikes", "Blood sugar swings worsen mood and cravings in the luteal phase. Favor complex carbs like oats, sweet potato, and brown rice.", TipCategory.NUTRITION,
            "https://www.nhs.uk/conditions/premenstrual-syndrome/"),
        Tip("Mood & Progesterone", "Rising progesterone can cause fatigue and mood dips. Gentle exercise like yoga or walking helps stabilize mood-related neurotransmitters.", TipCategory.MINDFULNESS,
            "https://www.nhs.uk/conditions/premenstrual-syndrome/"),
        Tip("Limit Alcohol", "Alcohol disrupts sleep architecture and amplifies PMS mood symptoms — even one or two drinks noticeably impact sleep quality and irritability in the luteal phase.", TipCategory.HEALTH,
            "https://www.sleepfoundation.org/sleep-hygiene/alcohol-and-sleep"),
        Tip("Prioritize Sleep", "Progesterone affects sleep quality in the luteal phase. Aim for a consistent bedtime, reduce screen time before bed, and keep your room cool.", TipCategory.WELLNESS,
            "https://www.sleepfoundation.org/women-sleep/"),
        Tip("Vitamin B6", "B6 may help reduce PMS symptoms, especially mood-related ones. Found in bananas, poultry, potatoes, and sunflower seeds.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/vitamin-b6-benefits"),
        Tip("Self-Compassion", "Luteal phase is a natural time to slow down and reflect. Journaling, gentle walks, and saying no to non-essentials is healthy — not lazy.", TipCategory.MINDFULNESS,
            "https://www.nhs.uk/mental-health/self-help/tips-and-support/mindfulness/"),
        Tip("Seed Cycling: Sesame & Sunflower", "Some evidence suggests these seeds in the luteal phase support progesterone balance.", TipCategory.NUTRITION,
            "https://www.healthline.com/nutrition/seed-cycling")
    )

    private val lateTips = listOf(
        Tip("Cycles Vary Naturally", "A 'late' period is often caused by stress, illness, travel, or significant diet changes — not necessarily a medical issue.", TipCategory.HEALTH,
            "https://www.nhs.uk/conditions/irregular-periods/"),
        Tip("Stress and Cycles", "High cortisol delays ovulation, which pushes your period back. Mindfulness, adequate sleep, and gentle exercise can help regulate stress.", TipCategory.MINDFULNESS,
            "https://www.healthline.com/health/stress/effects-on-body"),
        Tip("Stay Hydrated", "Even without your period, good hydration supports hormone regulation and overall wellbeing.", TipCategory.NUTRITION),
        Tip("When to Seek Advice", "If your period is more than 7 days late and you are sexually active, consider a pregnancy test. If cycles are regularly irregular, speak with a healthcare provider.", TipCategory.HEALTH,
            "https://www.nhs.uk/conditions/stopped-or-missed-periods/")
    )

    private val generalTips = listOf(
        Tip("Start Tracking Today", "Log your period start date to unlock cycle predictions, phase tips, and fertile window estimates.", TipCategory.WELLNESS),
        Tip("Consistency Matters", "3 or more logged cycles give the app enough data for accurate predictions. Even basic logging helps.", TipCategory.HEALTH),
        Tip("Your Data, Your Device", "Everything you log stays encrypted on your device. No data is ever sent anywhere.", TipCategory.WELLNESS),
        Tip("Temperature Tracking", "Recording your basal body temperature each morning (before getting up) is one of the most reliable ways to confirm ovulation.", TipCategory.HEALTH,
            "https://www.mayoclinic.org/tests-procedures/basal-body-temperature/about/pac-20393026")
    )
}
