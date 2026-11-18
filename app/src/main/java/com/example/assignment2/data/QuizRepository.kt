package com.example.assignment2.data

import com.example.assignment2.model.QuizChoice
import com.example.assignment2.model.QuizScenario

object QuizRepository {
    private val askedQuizIds = mutableSetOf<Int>()

    private val scenarios = listOf(
        QuizScenario(
            id = 1,
            title = "Unexpected Medical Expense",
            category = "Health Emergency",
            description = "You suddenly developed a severe toothache and need dental treatment.",
            choices = listOf(
                QuizChoice(
                    text = "Use medical insurance & panel clinic (Pay RM 100)",
                    moneyDelta = -100,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Great job! Panel clinics save money and keep you within budget."
                ),
                QuizChoice(
                    text = "Go to private luxury specialist clinic without insurance (Pay RM 500)",
                    moneyDelta = -500,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Ouch! Paying premium prices out of pocket severely hurt your budget."
                ),
                QuizChoice(
                    text = "Use emergency savings fund (Pay RM 150)",
                    moneyDelta = 0,
                    savingsDelta = -150,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Wise choice! That's exactly why emergency savings exist."
                )
            )
        ),
        QuizScenario(
            id = 2,
            title = "Car Repair Emergency",
            category = "Transportation",
            description = "Your car engine breaks down on the highway. Repair cost estimated at RM 400.",
            choices = listOf(
                QuizChoice(
                    text = "Repair essential parts only at a trusted workshop (Pay RM 250)",
                    moneyDelta = -250,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Smart move! Fixing essential issues avoids overspending."
                ),
                QuizChoice(
                    text = "Upgrade to expensive racing modifications (Pay RM 900)",
                    moneyDelta = -900,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Unnecessary spending! Racing mods exceeded your budget."
                ),
                QuizChoice(
                    text = "Take public transport for 2 weeks & save for repair (Pay RM 80)",
                    moneyDelta = -80,
                    savingsDelta = 50,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Excellent discipline! Taking public transport saved money."
                )
            )
        ),
        QuizScenario(
            id = 3,
            title = "Mobile Phone Purchase",
            category = "Technology",
            description = "A flashy new flagship smartphone has just been released!",
            choices = listOf(
                QuizChoice(
                    text = "Keep current working phone & deposit RM 200 into savings",
                    moneyDelta = 0,
                    savingsDelta = 200,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Fantastic financial discipline! +10 points for saving."
                ),
                QuizChoice(
                    text = "Buy flagship phone on expensive installment plan (Pay RM 1,200)",
                    moneyDelta = -1200,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Impulsive purchase! Exceeding budget hurt your monthly finances."
                ),
                QuizChoice(
                    text = "Buy a practical mid-range phone within budget (Pay RM 450)",
                    moneyDelta = -450,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Balanced decision! You satisfied your needs without breaking the bank."
                )
            )
        ),
        QuizScenario(
            id = 4,
            title = "Monthly Electricity Bill",
            category = "Utilities",
            description = "Air-conditioning usage was high this month, raising your electricity bill.",
            choices = listOf(
                QuizChoice(
                    text = "Pay electricity bill on time & adjust thermostat (Pay RM 180)",
                    moneyDelta = -180,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Responsible utility management keeps you within budget."
                ),
                QuizChoice(
                    text = "Ignore bill & spend money on gaming skins (Pay RM 150 skins, late fee later)",
                    moneyDelta = -150,
                    savingsDelta = 0,
                    scoreDelta = -10,
                    isWiseChoice = false,
                    feedback = "Unnecessary spending on skins while ignoring essential bills!"
                )
            )
        ),
        QuizScenario(
            id = 5,
            title = "Emergency Savings Opportunity",
            category = "Savings",
            description = "You earned a performance bonus of RM 300 at work!",
            choices = listOf(
                QuizChoice(
                    text = "Transfer full RM 300 straight into High-Yield Emergency Savings",
                    moneyDelta = 0,
                    savingsDelta = 300,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Superb saving habit! Building an emergency cushion secures your future."
                ),
                QuizChoice(
                    text = "Blow the bonus on a luxury dinner party (Pay RM 300)",
                    moneyDelta = -300,
                    savingsDelta = 0,
                    scoreDelta = -10,
                    isWiseChoice = false,
                    feedback = "Unnecessary spending! You spent your entire bonus in one night."
                ),
                QuizChoice(
                    text = "Save RM 200 in Savings & treat yourself with RM 100",
                    moneyDelta = -100,
                    savingsDelta = 200,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Great balance! Saving the majority while enjoying a small reward."
                )
            )
        ),
        QuizScenario(
            id = 6,
            title = "Discount Shopping Sale",
            category = "Shopping",
            description = "Supermarket offers a 'Buy 2 Get 1 Free' sale on non-perishable groceries.",
            choices = listOf(
                QuizChoice(
                    text = "Stock up on essential groceries you actually use (Pay RM 120)",
                    moneyDelta = -120,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Smart shopping! Bulk buying essentials reduces overall monthly costs."
                ),
                QuizChoice(
                    text = "Buy useless clearance items just because they are discounted (Pay RM 280)",
                    moneyDelta = -280,
                    savingsDelta = 0,
                    scoreDelta = -10,
                    isWiseChoice = false,
                    feedback = "Unnecessary spending! A discount is not savings if you don't need it."
                )
            )
        ),
        QuizScenario(
            id = 7,
            title = "Online Shopping Flash Sale",
            category = "E-Commerce",
            description = "Midnight 11.11 Flash Sale countdown starts with pop-up coupons!",
            choices = listOf(
                QuizChoice(
                    text = "Close the shopping app and stick to your budget",
                    moneyDelta = 0,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Awesome impulse control! Staying within budget earned +20 points."
                ),
                QuizChoice(
                    text = "Impulse buy items in cart worth RM 500",
                    moneyDelta = -500,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Exceeding budget! Late-night impulse buying drained your cash."
                )
            )
        ),
        QuizScenario(
            id = 8,
            title = "Saving for Education",
            category = "Self Development",
            description = "An online certification course relevant to your career path is available for RM 200.",
            choices = listOf(
                QuizChoice(
                    text = "Enroll in course using RM 200 (Gain skills for future income)",
                    moneyDelta = -200,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Investing in education is one of the highest yield investments!"
                ),
                QuizChoice(
                    text = "Use education allowance to deposit into Education Savings Fund",
                    moneyDelta = 0,
                    savingsDelta = 200,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Good foresight! Saving for education builds future security."
                )
            )
        ),
        QuizScenario(
            id = 9,
            title = "Credit Card Statement Due",
            category = "Debt Management",
            description = "Your credit card bill of RM 400 is due. Interest rate is 18% p.a. if unpaid.",
            choices = listOf(
                QuizChoice(
                    text = "Pay off full RM 400 balance immediately to avoid interest charges",
                    moneyDelta = -400,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Smart debt management! Clearing credit card balance in full avoids high interest."
                ),
                QuizChoice(
                    text = "Pay minimum RM 50 only and roll over debt with high interest",
                    moneyDelta = -50,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Caution! Paying minimum interest adds expensive compound debt."
                ),
                QuizChoice(
                    text = "Use Emergency Savings to clear debt cleanly (Pay RM 400 from Savings)",
                    moneyDelta = 0,
                    savingsDelta = -400,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Good call! Clearing high-interest debt using savings is financially sound."
                )
            )
        ),
        QuizScenario(
            id = 10,
            title = "High Return Scheme Scam",
            category = "Financial Literacy",
            description = "An acquaintance promises a 'Guaranteed 50% Profit in 3 Days' investment scheme.",
            choices = listOf(
                QuizChoice(
                    text = "Reject the scheme & deposit RM 150 into legitimate Savings",
                    moneyDelta = -150,
                    savingsDelta = 150,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Brilliant wisdom! Guaranteed high return schemes are classic scams."
                ),
                QuizChoice(
                    text = "Hand over RM 600 savings hoping to get rich quick",
                    moneyDelta = -600,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Scam alert! You lost RM 600 to an unlicensed high-risk investment fraud."
                )
            )
        ),
        QuizScenario(
            id = 11,
            title = "Unused Subscriptions Audit",
            category = "Monthly Budget",
            description = "You notice multiple recurring streaming & app subscriptions costing RM 90 every month.",
            choices = listOf(
                QuizChoice(
                    text = "Cancel 3 unused subscriptions and transfer RM 60 monthly into Savings",
                    moneyDelta = 0,
                    savingsDelta = 60,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Great audit! Trimming hidden subscription leaks boosts your monthly savings."
                ),
                QuizChoice(
                    text = "Keep all subscriptions active even though you rarely watch them (Pay RM 90)",
                    moneyDelta = -90,
                    savingsDelta = 0,
                    scoreDelta = -10,
                    isWiseChoice = false,
                    feedback = "Unnecessary spending! Unused subscriptions drain your hard-earned money."
                )
            )
        ),
        QuizScenario(
            id = 12,
            title = "Annual Health Insurance Policy",
            category = "Risk Protection",
            description = "Your annual basic medical insurance premium of RM 150 is due for renewal.",
            choices = listOf(
                QuizChoice(
                    text = "Pay RM 150 premium on time to safeguard medical protection",
                    moneyDelta = -150,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Wise risk management! Insurance protects your finances from catastrophic costs."
                ),
                QuizChoice(
                    text = "Skip insurance renewal to buy luxury designer shoes (Pay RM 150)",
                    moneyDelta = -150,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "High risk! Skipping essential medical coverage leaves you vulnerable."
                )
            )
        ),
        QuizScenario(
            id = 13,
            title = "Freelance Side Hustle Bonus",
            category = "Income Generation",
            description = "You completed a weekend freelance gig and earned an extra RM 250 cash bonus!",
            choices = listOf(
                QuizChoice(
                    text = "Deposit 80% (RM 200) into Savings & treat yourself with RM 50",
                    moneyDelta = -50,
                    savingsDelta = 200,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Perfect balance! Building wealth while enjoying a small reward."
                ),
                QuizChoice(
                    text = "Blow the full RM 250 on expensive branded clothes (Pay RM 250)",
                    moneyDelta = -250,
                    savingsDelta = 0,
                    scoreDelta = -10,
                    isWiseChoice = false,
                    feedback = "Impulsive spending! You spent your entire side hustle income immediately."
                )
            )
        ),
        QuizScenario(
            id = 14,
            title = "Weekend Fine Dining Invitation",
            category = "Social Expenses",
            description = "Friends invite you to an upscale luxury fine-dining restaurant tonight.",
            choices = listOf(
                QuizChoice(
                    text = "Suggest a cozy affordable café within budget (Pay RM 40)",
                    moneyDelta = -40,
                    savingsDelta = 0,
                    scoreDelta = 20,
                    isWiseChoice = true,
                    feedback = "Smart social balance! You enjoyed socializing without overspending."
                ),
                QuizChoice(
                    text = "Go to luxury restaurant and split a huge bill (Pay RM 250)",
                    moneyDelta = -250,
                    savingsDelta = 0,
                    scoreDelta = -20,
                    isWiseChoice = false,
                    feedback = "Exceeding budget! Splurging on expensive dining strained your cash."
                ),
                QuizChoice(
                    text = "Eat at home first & join for drinks only (Pay RM 20, Save RM 100)",
                    moneyDelta = -20,
                    savingsDelta = 100,
                    scoreDelta = 10,
                    isWiseChoice = true,
                    feedback = "Clever financial move! You saved RM 100 while staying connected with friends."
                )
            )
        )
    )

    fun getRandomScenario(): QuizScenario {
        var available = scenarios.filter { it.id !in askedQuizIds }
        if (available.isEmpty()) {
            askedQuizIds.clear()
            available = scenarios
        }
        val chosen = available.random()
        askedQuizIds.add(chosen.id)
        return chosen
    }

    fun getScenarioForTile(tileId: Int): QuizScenario {
        return getRandomScenario()
    }

    fun resetSession() {
        askedQuizIds.clear()
    }
}