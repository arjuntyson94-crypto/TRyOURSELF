package com.example.data.sample

import com.example.data.model.ProblemDoubt
import com.example.data.model.SolutionStep
import com.example.data.model.Subject

object SampleDoubts {
    val list: List<ProblemDoubt> = listOf(
        ProblemDoubt(
            id = "physics_1",
            subject = Subject.PHYSICS,
            question = "A toy car accelerates from rest at 2 m/s² for 5 seconds. What is its final velocity and how far did it travel?",
            givenValues = listOf("Initial velocity (u) = 0 m/s", "Acceleration (a) = 2 m/s²", "Time (t) = 5 s"),
            steps = listOf(
                SolutionStep(
                    stepNumber = 1,
                    title = "Identify Given Quantities and Unknowns",
                    formula = null, // No formula required for listing givens
                    formulaName = null,
                    explanation = "Write down what information the problem gives you and what you need to solve for.",
                    calculation = "u = 0 m/s (starts from rest)\na = 2 m/s²\nt = 5 s\nFind: Final velocity (v) and Distance (s)"
                ),
                SolutionStep(
                    stepNumber = 2,
                    title = "Calculate the Final Velocity (v)",
                    formula = "v = u + at",
                    formulaName = "First Equation of Motion",
                    explanation = "Substitute the initial velocity, acceleration, and time into the first kinematic equation.",
                    calculation = "v = 0 + (2 m/s² × 5 s)\nv = 10 m/s"
                ),
                SolutionStep(
                    stepNumber = 3,
                    title = "Calculate the Total Distance Traveled (s)",
                    formula = "s = ut + ½at²",
                    formulaName = "Second Equation of Motion",
                    explanation = "Use the distance-acceleration formula to calculate the distance covered during these 5 seconds.",
                    calculation = "s = (0 × 5) + ½(2)(5²)\ns = 0 + ½(2)(25)\ns = 25 meters"
                ),
                SolutionStep(
                    stepNumber = 4,
                    title = "Verification and Sanity Check",
                    formula = null, // Verification step has no formula needed
                    formulaName = null,
                    explanation = "Double check units and confirm that positive acceleration from rest resulted in a positive displacement.",
                    calculation = "Average velocity = (0 + 10)/2 = 5 m/s.\nDistance = Average velocity × time = 5 m/s × 5 s = 25 m. Matches!"
                )
            ),
            finalAnswer = "Final Velocity = 10 m/s, Total Distance Traveled = 25 meters",
            isSample = true
        ),
        ProblemDoubt(
            id = "maths_1",
            subject = Subject.MATHS,
            question = "Find the roots of the quadratic equation: 2x² - 4x - 6 = 0",
            givenValues = listOf("Equation: 2x² - 4x - 6 = 0", "Standard form: ax² + bx + c = 0"),
            steps = listOf(
                SolutionStep(
                    stepNumber = 1,
                    title = "Identify Coefficients a, b, and c",
                    formula = null, // No formula for identifying coefficients
                    formulaName = null,
                    explanation = "Compare the given equation 2x² - 4x - 6 = 0 with the standard quadratic equation form ax² + bx + c = 0.",
                    calculation = "a = 2\nb = -4\nc = -6"
                ),
                SolutionStep(
                    stepNumber = 2,
                    title = "Compute the Discriminant (D)",
                    formula = "D = b² - 4ac",
                    formulaName = "Discriminant Formula",
                    explanation = "Calculate the discriminant to determine if real roots exist (D > 0 means two distinct real roots).",
                    calculation = "D = (-4)² - 4(2)(-6)\nD = 16 - (-48)\nD = 16 + 48 = 64 (√64 = 8)"
                ),
                SolutionStep(
                    stepNumber = 3,
                    title = "Apply the Quadratic Formula for Roots",
                    formula = "x = (-b ± √D) / (2a)",
                    formulaName = "Quadratic Formula",
                    explanation = "Substitute the coefficients and the square root of the discriminant into the quadratic formula.",
                    calculation = "x = (-(-4) ± 8) / (2 × 2)\nx = (4 ± 8) / 4\nx₁ = (4 + 8) / 4 = 12 / 4 = 3\nx₂ = (4 - 8) / 4 = -4 / 4 = -1"
                ),
                SolutionStep(
                    stepNumber = 4,
                    title = "Confirm the Solutions",
                    formula = null,
                    formulaName = null,
                    explanation = "Substitute x = 3 back into 2x² - 4x - 6: 2(9) - 4(3) - 6 = 18 - 12 - 6 = 0. It works!",
                    calculation = "Roots are x = 3 and x = -1"
                )
            ),
            finalAnswer = "The solutions to 2x² - 4x - 6 = 0 are x = 3 and x = -1",
            isSample = true
        ),
        ProblemDoubt(
            id = "chemistry_1",
            subject = Subject.CHEMISTRY,
            question = "A sample of neon gas occupies 4.0 liters at a pressure of 1.5 atm. If the temperature remains constant, what is the new pressure when compressed to 2.0 liters?",
            givenValues = listOf("Initial volume (V₁) = 4.0 L", "Initial pressure (P₁) = 1.5 atm", "Final volume (V₂) = 2.0 L", "Temperature = Constant"),
            steps = listOf(
                SolutionStep(
                    stepNumber = 1,
                    title = "Identify Given States and Constant Variable",
                    formula = null,
                    formulaName = null,
                    explanation = "Since the temperature is constant, we are looking at an isothermal gas transformation.",
                    calculation = "P₁ = 1.5 atm\nV₁ = 4.0 L\nV₂ = 2.0 L\nP₂ = ? (to be calculated)"
                ),
                SolutionStep(
                    stepNumber = 2,
                    title = "Apply Boyle's Law for Constant Temperature",
                    formula = "P₁V₁ = P₂V₂",
                    formulaName = "Boyle's Law",
                    explanation = "At constant temperature, the pressure of a given mass of gas is inversely proportional to its volume.",
                    calculation = "(1.5 atm) × (4.0 L) = P₂ × (2.0 L)\n6.0 atm·L = 2.0 L × P₂\nP₂ = 6.0 / 2.0 = 3.0 atm"
                ),
                SolutionStep(
                    stepNumber = 3,
                    title = "Conceptual Reflection on Gas Behavior",
                    formula = null,
                    formulaName = null,
                    explanation = "Halving the volume doubles the frequency of molecular collisions with the container walls, so pressure should double.",
                    calculation = "Volume halved (4.0 L → 2.0 L) ⇒ Pressure doubled (1.5 atm → 3.0 atm). Logically consistent!"
                )
            ),
            finalAnswer = "The new pressure of the neon gas is 3.0 atm",
            isSample = true
        ),
        ProblemDoubt(
            id = "physics_2",
            subject = Subject.PHYSICS,
            question = "A 12V battery is connected across an electrical circuit with a resistor of 4 Ohms. What current flows through the circuit?",
            givenValues = listOf("Voltage (V) = 12 Volts", "Resistance (R) = 4 Ohms (Ω)"),
            steps = listOf(
                SolutionStep(
                    stepNumber = 1,
                    title = "Organize Circuit Components",
                    formula = null,
                    formulaName = null,
                    explanation = "Note the source voltage and the total circuit resistance.",
                    calculation = "V = 12 V\nR = 4 Ω\nFind: Current (I) in Amperes"
                ),
                SolutionStep(
                    stepNumber = 2,
                    title = "Apply Ohm's Law",
                    formula = "I = V / R",
                    formulaName = "Ohm's Law",
                    explanation = "Current flowing through a conductor is directly proportional to voltage and inversely proportional to resistance.",
                    calculation = "I = 12 V / 4 Ω\nI = 3 Amperes (A)"
                ),
                SolutionStep(
                    stepNumber = 3,
                    title = "Optional Check: Power Dissipated",
                    formula = "P = V × I",
                    formulaName = "Electric Power Formula",
                    explanation = "Notice how much energy is converted to heat or light per second.",
                    calculation = "P = 12 V × 3 A = 36 Watts"
                )
            ),
            finalAnswer = "The current flowing through the circuit is 3 A (Amperes)",
            isSample = true
        )
    )
}
