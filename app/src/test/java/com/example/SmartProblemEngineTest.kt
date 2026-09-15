package com.example

import com.example.data.math.SmartProblemEngine
import com.example.data.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartProblemEngineTest {

    @Test
    fun testArithmeticSolver() {
        val doubt = SmartProblemEngine.solve(
            questionText = "15 * 4 + 40 / 2",
            subject = Subject.MATHS
        )

        assertEquals(Subject.MATHS, doubt.subject)
        assertTrue("Final answer should contain evaluated 80", doubt.finalAnswer.contains("80"))
        assertTrue("Steps should be at least 3", doubt.steps.size >= 3)
    }

    @Test
    fun testPercentageSolver() {
        val doubt = SmartProblemEngine.solve(
            questionText = "What is 20% of 150?",
            subject = Subject.MATHS
        )

        assertEquals(Subject.MATHS, doubt.subject)
        assertTrue("Should calculate 30", doubt.finalAnswer.contains("30"))
        val formulaStep = doubt.steps.find { it.formula != null }
        assertNotNull("Should contain percentage formula", formulaStep)
        assertTrue("Formula should contain Amount = (Percentage / 100) × Base", formulaStep?.formula?.contains("Amount") == true)
    }

    @Test
    fun testLinearEquationSolver() {
        val doubt = SmartProblemEngine.solve(
            questionText = "2x + 6 = 18",
            subject = Subject.MATHS
        )

        assertEquals(Subject.MATHS, doubt.subject)
        assertTrue("Should solve x = 6", doubt.finalAnswer.contains("6"))
        val step1 = doubt.steps[0]
        assertNull("Step 1 (listing givens) should not have a formula", step1.formula)
    }

    @Test
    fun testQuadraticEquationSolver() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Find roots of 2x^2 - 4x - 6 = 0",
            subject = Subject.MATHS
        )

        assertEquals(Subject.MATHS, doubt.subject)
        assertTrue("Should contain root 3", doubt.finalAnswer.contains("3"))
        assertTrue("Should contain root -1", doubt.finalAnswer.contains("-1"))

        // Check formula step has clean formula without square brackets
        val discStep = doubt.steps.find { it.formulaName?.contains("Discriminant") == true }
        assertNotNull(discStep)
        assertEquals("D = b² - 4ac", discStep?.formula)

        val quadStep = doubt.steps.find { it.formulaName?.contains("Quadratic Formula") == true }
        assertNotNull(quadStep)
        assertEquals("x = (-b ± √D) / (2a)", quadStep?.formula)
    }

    @Test
    fun testCircleGeometry() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Find area and circumference of circle with radius 7",
            subject = Subject.MATHS
        )

        assertTrue(doubt.finalAnswer.contains("sq units") || doubt.finalAnswer.contains("Circle Area"))
        val areaStep = doubt.steps.find { it.formula?.contains("π · r²") == true }
        assertNotNull(areaStep)
    }

    @Test
    fun testPythagoreanTheorem() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Find hypotenuse of right triangle with legs 3 and 4",
            subject = Subject.MATHS
        )

        assertTrue("Hypotenuse should be 5", doubt.finalAnswer.contains("5"))
        val pythStep = doubt.steps.find { it.formula?.contains("√(a² + b²)") == true }
        assertNotNull(pythStep)
    }

    @Test
    fun testPhysicsKinematics() {
        val doubt = SmartProblemEngine.solve(
            questionText = "A car accelerates from rest at 2 m/s² for 5 seconds. Find final velocity and distance.",
            subject = Subject.PHYSICS
        )

        assertEquals(Subject.PHYSICS, doubt.subject)
        assertTrue("Final velocity should be 10", doubt.finalAnswer.contains("10"))
        assertTrue("Distance should be 25", doubt.finalAnswer.contains("25"))

        val vStep = doubt.steps.find { it.formula == "v = u + at" }
        assertNotNull("First equation of motion should be present", vStep)

        val sStep = doubt.steps.find { it.formula?.contains("s = ut + ½at²") == true }
        assertNotNull("Second equation of motion should be present", sStep)
    }

    @Test
    fun testPhysicsForceNewton() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Find force acting on mass 10 kg with acceleration 3 m/s²",
            subject = Subject.PHYSICS
        )

        assertTrue("Force should be 30 N", doubt.finalAnswer.contains("30"))
        val forceStep = doubt.steps.find { it.formula == "F = m · a" }
        assertNotNull(forceStep)
    }

    @Test
    fun testPhysicsOhmsLaw() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Find voltage if current is 3 A and resistance is 4 ohm",
            subject = Subject.PHYSICS
        )

        assertTrue("Voltage should be 12 V", doubt.finalAnswer.contains("12"))
        val ohmStep = doubt.steps.find { it.formula?.contains("V = I · R") == true }
        assertNotNull(ohmStep)
    }

    @Test
    fun testChemistryBoylesLaw() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Boyle's law: Gas at initial pressure 1.5 atm, volume 4 L compressed to 2 L",
            subject = Subject.CHEMISTRY
        )

        assertEquals(Subject.CHEMISTRY, doubt.subject)
        assertTrue("Pressure should be 3 atm", doubt.finalAnswer.contains("3"))
        val gasStep = doubt.steps.find { it.formula?.contains("P₁ · V₁ = P₂ · V₂") == true }
        assertNotNull(gasStep)
    }

    @Test
    fun testChemistryPhotosynthesis() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Explain photosynthesis and its chemical equation",
            subject = Subject.CHEMISTRY
        )

        assertTrue(doubt.finalAnswer.contains("Photosynthesis"))
        val eqnStep = doubt.steps.find { it.formula?.contains("6CO₂ + 6H₂O") == true }
        assertNotNull(eqnStep)
    }

    @Test
    fun testUniversalAnyQuestion() {
        val doubt = SmartProblemEngine.solve(
            questionText = "Why do magnets attract iron?",
            subject = Subject.PHYSICS
        )

        assertEquals(Subject.PHYSICS, doubt.subject)
        assertTrue(doubt.steps.size >= 3)
        assertEquals(1, doubt.steps[0].stepNumber)
    }
}
