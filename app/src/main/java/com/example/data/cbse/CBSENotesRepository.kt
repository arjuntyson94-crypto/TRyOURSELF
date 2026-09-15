package com.example.data.cbse

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ChapterCacheHelper
import com.example.data.model.CBSEBoardQuestion
import com.example.data.model.CBSEClass
import com.example.data.model.CBSEFormula
import com.example.data.model.CBSEFormulaHandbookItem
import com.example.data.model.CBSENoteChapter
import com.example.data.model.CacheStatus
import com.example.data.model.StudentNotebookEntry
import com.example.data.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object CBSENotesRepository {

    // =========================================================================
    // CBSE CLASS 11 CHAPTER NOTES
    // =========================================================================
    private val class11Notes: List<CBSENoteChapter> = listOf(
        // --- CLASS 11 MATHEMATICS ---
        CBSENoteChapter(
            id = "c11_math_ch1",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.MATHS,
            chapterNumber = 1,
            title = "Sets and Functions",
            ncertReference = "NCERT Class 11 Chapter 1 & 2",
            weightageMarks = "8 - 10 Marks (Unit I)",
            summary = "Foundational unit covering subsets, power sets, Venn diagrams, Cartesian products, relations, domain, range, and types of real functions.",
            keyPoints = listOf(
                "A set is a well-defined collection of distinct objects.",
                "Number of subsets of a set with n elements is [ 2ⁿ ]; number of proper subsets is [ 2ⁿ - 1 ].",
                "De Morgan's Laws: [ (A ∪ B)' = A' ∩ B' ] and [ (A ∩ B)' = A' ∪ B' ].",
                "A relation R from set A to B is a subset of A × B.",
                "A function f: A → B assigns every element in A exactly one image in B."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Union Cardinality Formula",
                    formulaText = "[ n(A ∪ B) = n(A) + n(B) - n(A ∩ B) ]",
                    description = "Fundamental formula for two finite sets in set theory."
                ),
                CBSEFormula(
                    name = "Three Sets Inclusion-Exclusion",
                    formulaText = "[ n(A ∪ B ∪ C) = n(A) + n(B) + n(C) - n(A∩B) - n(B∩C) - n(C∩A) + n(A∩B∩C) ]",
                    description = "Used to solve word problems involving three survey groups."
                ),
                CBSEFormula(
                    name = "Number of Subsets",
                    formulaText = "[ |P(A)| = 2ⁿ ]",
                    description = "Total elements in power set where n = |A|."
                )
            ),
            derivationsOrReactions = listOf(
                "Proof of De Morgan's First Law using element membership: Let x ∈ (A ∪ B)'. Then x ∉ (A ∪ B) ⇒ x ∉ A and x ∉ B ⇒ x ∈ A' and x ∈ B' ⇒ x ∈ (A' ∩ B'). Hence (A ∪ B)' ⊆ A' ∩ B' and vice-versa.",
                "Cartesian product cardinality: If n(A) = p and n(B) = q, then n(A × B) = pq. Number of relations = [ 2^(pq) ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "If X and Y are two sets such that n(X) = 17, n(Y) = 23 and n(X ∪ Y) = 38, find n(X ∩ Y).",
                    yearRepeated = "CBSE Class 11 Exam (2 Marks)",
                    answerSummary = "Apply [ n(X ∪ Y) = n(X) + n(Y) - n(X ∩ Y) ] ⇒ 38 = 17 + 23 - n(X ∩ Y) ⇒ n(X ∩ Y) = 40 - 38 = 2."
                ),
                CBSEBoardQuestion(
                    question = "Find the domain and range of the real function f(x) = √(9 - x²).",
                    yearRepeated = "CBSE Annual Exam & NCERT Exemplar (3 Marks)",
                    answerSummary = "For f(x) to be real, 9 - x² ≥ 0 ⇒ x² ≤ 9 ⇒ -3 ≤ x ≤ 3. Domain = [-3, 3]. Range = [0, 3]."
                )
            ),
            pdfEstimatedPages = 5
        ),
        CBSENoteChapter(
            id = "c11_math_ch3",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.MATHS,
            chapterNumber = 3,
            title = "Trigonometric Functions",
            ncertReference = "NCERT Class 11 Chapter 3",
            weightageMarks = "12 - 14 Marks",
            summary = "Radian measure, trigonometric ratios of compound angles, transformation formulas, and trigonometric identities essential for Class 12 Calculus.",
            keyPoints = listOf(
                "Radian-degree conversion: [ 180° = π radians ] ⇒ [ 1 rad ≈ 57° 16' 22\" ].",
                "Arc length formula: [ l = r · θ ] where θ must strictly be in radians.",
                "ASTC rule: All positive in Q1, Sin/Csc in Q2, Tan/Cot in Q3, Cos/Sec in Q4.",
                "Cos(-x) = cos x, Sin(-x) = -sin x, Tan(-x) = -tan x (even & odd functions)."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Compound Angle Addition (Cosine)",
                    formulaText = "[ cos(A + B) = cos A cos B - sin A sin B ]",
                    description = "Core identity from which all other angle additions are derived."
                ),
                CBSEFormula(
                    name = "Compound Angle Addition (Sine)",
                    formulaText = "[ sin(A + B) = sin A cos B + cos A sin B ]",
                    description = "Fundamental addition formula for sine."
                ),
                CBSEFormula(
                    name = "Double Angle Formulas",
                    formulaText = "[ cos 2A = cos² A - sin² A = 2cos² A - 1 = 1 - 2sin² A = (1 - tan² A) / (1 + tan² A) ]",
                    description = "High-frequency formulas in calculus and integration."
                ),
                CBSEFormula(
                    name = "Sine Double Angle",
                    formulaText = "[ sin 2A = 2 sin A cos A = (2 tan A) / (1 + tan² A) ]",
                    description = "Converts double angle into single angles or tangent."
                ),
                CBSEFormula(
                    name = "Transformation (Sum to Product)",
                    formulaText = "[ sin C + sin D = 2 sin((C+D)/2) cos((C-D)/2) ]",
                    description = "Converts trigonometric sums into products."
                )
            ),
            derivationsOrReactions = listOf(
                "Derivation of cos(x + y) via unit circle distance formula: Construct unit circle with points P0(1,0), P1(cos y, sin y), P2(cos(x+y), sin(x+y)), P3(cos(-x), sin(-x)). Since triangle rotations preserve chords, P0P2 = P1P3. Expanding distances yields [ cos(x+y) = cos x cos y - sin x sin y ].",
                "Half-angle substitution: [ 1 - cos 2θ = 2 sin² θ ] and [ 1 + cos 2θ = 2 cos² θ ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Prove that: (sin 5x - 2 sin 3x + sin x) / (cos 5x - cos x) = tan x.",
                    yearRepeated = "CBSE Class 11 Annual Exam (4 Marks)",
                    answerSummary = "Combine numerator: (sin 5x + sin x) - 2 sin 3x = 2 sin 3x cos 2x - 2 sin 3x = 2 sin 3x (cos 2x - 1). Denominator: -2 sin 3x sin 2x. Quotient simplifies directly to (1 - cos 2x) / sin 2x = 2 sin² x / (2 sin x cos x) = tan x."
                )
            ),
            pdfEstimatedPages = 6
        ),
        CBSENoteChapter(
            id = "c11_math_ch13",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.MATHS,
            chapterNumber = 13,
            title = "Limits and Derivatives",
            ncertReference = "NCERT Class 11 Chapter 13",
            weightageMarks = "10 - 12 Marks",
            summary = "Introduction to Calculus: intuitive concept of limits, algebraic limits, trigonometric limits, first principle of differentiation, and product/quotient rules.",
            keyPoints = listOf(
                "Limit lim(x→a) f(x) exists iff Left Hand Limit (LHL) = Right Hand Limit (RHL) = finite value.",
                "Standard trigonometric limit: [ lim(x→0) (sin x / x) = 1 ] where x is in radians.",
                "Derivative definition: [ f'(x) = lim(h→0) (f(x+h) - f(x)) / h ] (First Principle).",
                "Leibniz Product Rule: [ (uv)' = u'v + uv' ]; Quotient Rule: [ (u/v)' = (u'v - uv') / v² ]."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Algebraic Standard Limit",
                    formulaText = "[ lim(x→a) (xⁿ - aⁿ) / (x - a) = n · aⁿ⁻¹ ]",
                    description = "Used to evaluate indeterminate forms of type 0/0."
                ),
                CBSEFormula(
                    name = "Trigonometric Standard Limit",
                    formulaText = "[ lim(x→0) (tan x / x) = 1 ] and [ lim(x→0) (1 - cos x) / x = 0 ]",
                    description = "Key limits for evaluating trigonometric indeterminate quotients."
                ),
                CBSEFormula(
                    name = "Power Rule for Derivatives",
                    formulaText = "[ d/dx (xⁿ) = n · xⁿ⁻¹ ]",
                    description = "Valid for all real exponents n."
                )
            ),
            derivationsOrReactions = listOf(
                "Derivation of d/dx (sin x) = cos x from First Principle:\nlim(h→0) [sin(x+h) - sin x] / h = lim(h→0) [2 cos(x + h/2) sin(h/2)] / h = lim(h→0) cos(x + h/2) × lim(h→0) [sin(h/2) / (h/2)] = cos x × 1 = cos x."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Find the derivative of f(x) = x² + 3x from first principles.",
                    yearRepeated = "CBSE Class 11 Annual Examination (4 Marks)",
                    answerSummary = "f'(x) = lim(h→0) [((x+h)² + 3(x+h)) - (x² + 3x)] / h = lim(h→0) [2xh + h² + 3h] / h = lim(h→0) (2x + h + 3) = 2x + 3."
                )
            ),
            pdfEstimatedPages = 5
        ),

        // --- CLASS 11 PHYSICS ---
        CBSENoteChapter(
            id = "c11_phys_ch2",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.PHYSICS,
            chapterNumber = 2,
            title = "Motion in a Straight Line & Plane (Kinematics)",
            ncertReference = "NCERT Class 11 Physics Chapters 2 & 3",
            weightageMarks = "10 Marks (Unit II)",
            summary = "Equations of uniformly accelerated rectilinear motion, relative velocity, scalar and vector components, projectile motion, and circular motion.",
            keyPoints = listOf(
                "Distance is total path length (scalar ≥ 0); Displacement is shortest directed vector between initial and final points.",
                "Slope of x-t graph represents instantaneous velocity [ v = dx/dt ]; slope of v-t graph represents acceleration [ a = dv/dt ].",
                "Area under v-t graph represents net displacement.",
                "In projectile motion, horizontal velocity component [ u_x = u cos θ ] remains strictly constant throughout flight."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "First Equation of Motion",
                    formulaText = "[ v = u + a · t ]",
                    description = "Connects initial velocity, acceleration, elapsed time, and final velocity."
                ),
                CBSEFormula(
                    name = "Second Equation of Motion",
                    formulaText = "[ s = u · t + ½ · a · t² ]",
                    description = "Displacement under constant uniform acceleration."
                ),
                CBSEFormula(
                    name = "Third Equation of Motion",
                    formulaText = "[ v² - u² = 2 · a · s ]",
                    description = "Independent of time; relates velocities and displacement."
                ),
                CBSEFormula(
                    name = "Projectile Time of Flight",
                    formulaText = "[ T = (2 · u · sin θ) / g ]",
                    description = "Total time a projectile remains in the air on level ground."
                ),
                CBSEFormula(
                    name = "Horizontal Range",
                    formulaText = "[ R = (u² · sin 2θ) / g ]",
                    description = "Maximum range is attained when angle θ = 45°, where [ R_max = u² / g ]."
                )
            ),
            derivationsOrReactions = listOf(
                "Calculus Derivation of s = ut + ½at²:\na = dv/dt ⇒ dv = a dt ⇒ Integrating from u to v: v - u = at ⇒ v = u + at.\nNow v = ds/dt ⇒ ds = (u + at) dt ⇒ Integrating from 0 to s and 0 to t: s = ut + ½at².",
                "Equation of Trajectory for Projectile:\nx = (u cos θ)t ⇒ t = x / (u cos θ).\ny = (u sin θ)t - ½gt² = x tan θ - [ g / (2u² cos² θ) ] x², which is an equation of a parabola."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Show that the trajectory of an oblique projectile is parabolic. Find expression for its maximum height.",
                    yearRepeated = "CBSE Class 11 Annual Exam (5 Marks)",
                    answerSummary = "Combine horizontal and vertical displacements to obtain y = ax - bx² (parabola). At peak, v_y = 0 ⇒ 0 = (u sin θ)² - 2gH ⇒ [ H = (u² sin² θ) / (2g) ]."
                )
            ),
            pdfEstimatedPages = 6
        ),
        CBSENoteChapter(
            id = "c11_phys_ch4",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.PHYSICS,
            chapterNumber = 4,
            title = "Laws of Motion & Work-Energy-Power",
            ncertReference = "NCERT Class 11 Physics Chapters 4 & 5",
            weightageMarks = "14 Marks (Unit III & IV)",
            summary = "Newton's 3 laws, momentum conservation, friction, banking of roads, work-energy theorem, conservative forces, and collisions in one and two dimensions.",
            keyPoints = listOf(
                "Newton's 1st Law: Law of inertia; frames of reference (inertial vs non-inertial).",
                "Newton's 2nd Law: [ F_net = dp/dt = m · a ]; second law is the real law of motion containing both 1st and 3rd laws.",
                "Friction: [ f_s ≤ μ_s · N ]; kinetic friction [ f_k = μ_k · N ] with μ_k < μ_s.",
                "Work done [ W = F · d = F d cos θ ]; conservative forces have zero work over closed path."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Newton's Second Law",
                    formulaText = "[ F = m · a = dp / dt ]",
                    description = "Rate of change of momentum is equal to external net force."
                ),
                CBSEFormula(
                    name = "Work-Energy Theorem",
                    formulaText = "[ W_net = ΔK = ½ · m · v² - ½ · m · u² ]",
                    description = "Work done by all forces equals change in kinetic energy."
                ),
                CBSEFormula(
                    name = "Maximum Safe Speed on Banked Road",
                    formulaText = "[ v_max = √[ r · g · (μ + tan θ) / (1 - μ tan θ) ] ]",
                    description = "Speed limit to avoid skidding on a banked circular curve."
                ),
                CBSEFormula(
                    name = "Elastic 1D Collision Velocity",
                    formulaText = "[ v₁ = [ (m₁ - m₂) / (m₁ + m₂) ] u₁ + [ 2m₂ / (m₁ + m₂) ] u₂ ]",
                    description = "Final velocity of body 1 after head-on elastic collision."
                )
            ),
            derivationsOrReactions = listOf(
                "Work-Energy Theorem for variable force:\nW = ∫ F dx = ∫ m (dv/dt) dx = ∫ m v dv = m [ v²/2 ] from u to v = ½mv² - ½mu² = ΔK.",
                "Optimum speed on banked road without friction (μ = 0): N sin θ = mv²/r, N cos θ = mg ⇒ tan θ = v²/(rg) ⇒ [ v_opt = √(rg tan θ) ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "State and prove Work-Energy Theorem for a variable force in one dimension.",
                    yearRepeated = "CBSE Class 11 Final Exam (3 Marks)",
                    answerSummary = "W = ∫ F dx = ∫ (m dv/dt) dx = ∫ m v dv = ½ m(v² - u²) = K_f - K_i = ΔK. Proved."
                )
            ),
            pdfEstimatedPages = 6
        ),

        // --- CLASS 11 CHEMISTRY ---
        CBSENoteChapter(
            id = "c11_chem_ch1",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.CHEMISTRY,
            chapterNumber = 1,
            title = "Some Basic Concepts of Chemistry (Mole Concept)",
            ncertReference = "NCERT Class 11 Chemistry Chapter 1",
            weightageMarks = "7 Marks",
            summary = "Laws of chemical combination, Dalton's atomic theory, Avogadro's number, mole concept, molar mass, empirical & molecular formula, stoichiometry, and limiting reagent.",
            keyPoints = listOf(
                "1 Mole = [ 6.022 × 10²³ ] elementary entities (Avogadro Constant N_A).",
                "1 mole of any ideal gas occupies [ 22.4 Liters ] at STP (0°C, 1 atm) or 22.7 L at standard temperature and pressure (0°C, 1 bar).",
                "Limiting Reagent is the reactant that is completely consumed first in a reaction, limiting the amount of product formed.",
                "Molarity (M) depends on temperature because liquid volume expands with heat; Molality (m) is independent of temperature."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Mole Calculation",
                    formulaText = "[ n = m / M = N / N_A = V_stp / 22.4 ]",
                    description = "Connects mass (m), molar mass (M), particle count (N), and gas volume at STP."
                ),
                CBSEFormula(
                    name = "Molarity",
                    formulaText = "[ M = (moles of solute) / (Volume of solution in L) = (w₂ × 1000) / (M₂ × V_ml) ]",
                    description = "Molar concentration of a liquid solution."
                ),
                CBSEFormula(
                    name = "Molality",
                    formulaText = "[ m = (moles of solute) / (Mass of solvent in kg) = (w₂ × 1000) / (M₂ × w₁_g) ]",
                    description = "Temperature-independent concentration unit."
                ),
                CBSEFormula(
                    name = "Empirical to Molecular Formula",
                    formulaText = "[ Molecular Formula = n × Empirical Formula, where n = (Molar Mass) / (Empirical Mass) ]",
                    description = "Multiplier relating empirical and molecular formula."
                )
            ),
            derivationsOrReactions = listOf(
                "Stoichiometry of Haber Process: N₂(g) + 3H₂(g) ⇌ 2NH₃(g). 1 mole N₂ reacts with 3 moles H₂ to produce 2 moles NH₃. If 10 g N₂ and 10 g H₂ are mixed, H₂ is in excess and N₂ is the limiting reagent.",
                "Dilution formula: [ M₁ · V₁ = M₂ · V₂ ] and mixing formula [ M₃ = (M₁V₁ + M₂V₂) / (V₁ + V₂) ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Calculate the molarity of NaOH in the solution prepared by dissolving its 4 g in enough water to form 250 mL of the solution.",
                    yearRepeated = "CBSE Class 11 Exam & NCERT In-Text (2 Marks)",
                    answerSummary = "Moles of NaOH = 4 / 40 = 0.1 mol. Volume = 250 mL = 0.25 L. Molarity = 0.1 / 0.25 = 0.4 mol/L (0.4 M)."
                )
            ),
            pdfEstimatedPages = 5
        ),
        CBSENoteChapter(
            id = "c11_chem_ch4",
            classLevel = CBSEClass.CLASS_11,
            subject = Subject.CHEMISTRY,
            chapterNumber = 4,
            title = "Chemical Bonding & Molecular Structure",
            ncertReference = "NCERT Class 11 Chemistry Chapter 4",
            weightageMarks = "7 - 8 Marks",
            summary = "Kössell-Lewis approach, Octet rule, ionic vs covalent bonds, VSEPR theory, hybridization (sp, sp², sp³, sp³d), Valence Bond Theory, and Molecular Orbital Theory (MOT).",
            keyPoints = listOf(
                "Lewis symbols and octet rule limitations (incomplete octet in BeCl₂, expanded octet in PCl₅, SF₆, odd-electron in NO).",
                "VSEPR Theory: Repulsion order: [ Lone Pair - Lone Pair > Lone Pair - Bond Pair > Bond Pair - Bond Pair ].",
                "Bond order formula from MOT: [ Bond Order = ½ (N_b - N_a) ].",
                "If Bond Order > 0, molecule is stable; if molecule has unpaired electrons in MO, it is paramagnetic (e.g. O₂ has BO = 2 and is paramagnetic)."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Molecular Orbital Bond Order",
                    formulaText = "[ Bond Order = ½ · (N_b - N_a) ]",
                    description = "N_b = electrons in bonding orbitals, N_a = electrons in antibonding orbitals."
                ),
                CBSEFormula(
                    name = "Dipole Moment",
                    formulaText = "[ μ = q · d ]",
                    description = "Measured in Debye (1 D = 3.33564 × 10⁻³⁰ C·m). Nonpolar molecules have vector sum μ = 0."
                ),
                CBSEFormula(
                    name = "Formal Charge",
                    formulaText = "[ Formal Charge = V - L - ½ · S ]",
                    description = "V = valence e⁻, L = lone pair e⁻, S = shared bonding e⁻."
                )
            ),
            derivationsOrReactions = listOf(
                "MO Configuration for O₂ (16 electrons):\nσ1s² σ*1s² σ2s² σ*2s² σ2p_z² (π2p_x² = π2p_y²) (π*2p_x¹ = π*2p_y¹).\nBond Order = ½ (10 - 6) = 2. Two unpaired electrons in π* antibonding orbitals prove O₂ is paramagnetic (which Lewis theory could not explain).",
                "Hybridization Formula: [ H = ½ [ V + M - C + A ] ] where V = valence e⁻ on central atom, M = monovalent atoms, C = cationic charge, A = anionic charge."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Why is NH₃ polar while BF₃ is nonpolar although both have polar bonds?",
                    yearRepeated = "CBSE Class 11 Annual Exam (3 Marks)",
                    answerSummary = "BF₃ has trigonal planar geometry (sp²) with bond angles of 120°. The vector sum of three equal B-F dipole moments is zero (μ = 0). NH₃ has trigonal pyramidal geometry (sp³ with 1 lone pair); individual N-H bond dipoles add constructively with the lone pair dipole, resulting in net μ = 1.47 D."
                )
            ),
            pdfEstimatedPages = 6
        )
    )

    // =========================================================================
    // CBSE CLASS 12 CHAPTER NOTES
    // =========================================================================
    private val class12Notes: List<CBSENoteChapter> = listOf(
        // --- CLASS 12 MATHEMATICS ---
        CBSENoteChapter(
            id = "c12_math_ch3",
            classLevel = CBSEClass.CLASS_12,
            subject = Subject.MATHS,
            chapterNumber = 3,
            title = "Matrices and Determinants",
            ncertReference = "NCERT Class 12 Chapter 3 & 4",
            weightageMarks = "10 Marks (Unit II - CBSE Board)",
            summary = "Matrix operations, transpose, symmetric/skew-symmetric matrices, properties of determinants, minors, cofactors, adjoint, inverse of a matrix, and solving linear systems via matrix method.",
            keyPoints = listOf(
                "Matrix multiplication is non-commutative in general: [ AB ≠ BA ].",
                "Transpose product rule: [ (AB)ᵀ = Bᵀ Aᵀ ].",
                "A square matrix A is invertible if and only if it is non-singular, i.e., [ |A| ≠ 0 ].",
                "Inverse formula: [ A⁻¹ = (1 / |A|) · adj(A) ].",
                "System of equations AX = B has a unique solution [ X = A⁻¹ B ] when |A| ≠ 0."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Matrix Inverse",
                    formulaText = "[ A⁻¹ = (1 / |A|) · adj(A) ]",
                    description = "Valid for non-singular square matrices (|A| ≠ 0)."
                ),
                CBSEFormula(
                    name = "Adjoint Determinant Property",
                    formulaText = "[ |adj(A)| = |A|ⁿ⁻¹ ] and [ A · adj(A) = |A| · Iₙ ]",
                    description = "n is order of the square matrix. Very common 1-mark & 2-mark CBSE question."
                ),
                CBSEFormula(
                    name = "Area of Triangle via Determinant",
                    formulaText = "[ Area = ½ · | x₁(y₂ - y₃) + x₂(y₃ - y₁) + x₃(y₁ - y₂) | ]",
                    description = "Determinant formula for 3 coplanar vertices."
                )
            ),
            derivationsOrReactions = listOf(
                "Matrix Method for Linear System: Given a₁x + b₁y + c₁z = d₁, a₂x + b₂y + c₂z = d₂, a₃x + b₃y + c₃z = d₃.\nLet A = [[a₁,b₁,c₁],[a₂,b₂,c₂],[a₃,b₃,c₃]], X = [[x],[y],[z]], B = [[d₁],[d₂],[d₃]].\nIf |A| ≠ 0, premultiply by A⁻¹: A⁻¹(AX) = A⁻¹B ⇒ IX = A⁻¹B ⇒ [ X = A⁻¹B ].",
                "Proof that A · adj(A) = |A| · I: Sum of products of elements of any row with their corresponding cofactors equals |A|; with elements of any other row equals 0."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Solve the system of equations using matrix method:\n2x + 3y + 3z = 5\nx - 2y + z = -4\n3x - y - 2z = 3",
                    yearRepeated = "CBSE Board 2023, 2020, 2018 (5 Marks)",
                    answerSummary = "|A| = 2(4+1) - 3(-2-3) + 3(-1+6) = 10 + 15 + 15 = 40 ≠ 0. Compute cofactor matrix, take transpose to obtain adj(A), then multiply X = A⁻¹ B. Solution yields x = 1, y = 2, z = -1."
                )
            ),
            pdfEstimatedPages = 6
        ),
        CBSENoteChapter(
            id = "c12_math_ch7",
            classLevel = CBSEClass.CLASS_12,
            subject = Subject.MATHS,
            chapterNumber = 7,
            title = "Integrals (Calculus)",
            ncertReference = "NCERT Class 12 Chapter 7",
            weightageMarks = "14 - 16 Marks (Highest Weightage in CBSE)",
            summary = "Indefinite integrals, integration by substitution, partial fractions, integration by parts, definite integrals, and properties of definite integrals (King's Property).",
            keyPoints = listOf(
                "Integration is the reverse process of differentiation (anti-derivative).",
                "Integration by parts rule (ILATE): [ ∫ u · v dx = u ∫ v dx - ∫ (u' ∫ v dx) dx ].",
                "Special exponential integral: [ ∫ eˣ [ f(x) + f'(x) ] dx = eˣ · f(x) + C ].",
                "King's Property of Definite Integrals: [ ∫ₐᵇ f(x) dx = ∫ₐᵇ f(a + b - x) dx ]."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Integration by Parts (ILATE)",
                    formulaText = "[ ∫ u · v dx = u · ∫ v dx - ∫ [ du/dx · ∫ v dx ] dx ]",
                    description = "Choose first function u based on ILATE: Inverse, Log, Algebraic, Trig, Exponential."
                ),
                CBSEFormula(
                    name = "King's Definite Integral Property",
                    formulaText = "[ ∫₀ᵃ f(x) dx = ∫₀ᵃ f(a - x) dx ]",
                    description = "Crucial property used to solve 90% of CBSE 4-mark and 6-mark definite integral questions."
                ),
                CBSEFormula(
                    name = "Standard Integral: dx / (x² - a²)",
                    formulaText = "[ ∫ dx / (x² - a²) = (1 / 2a) · ln |(x - a) / (x + a)| + C ]",
                    description = "Standard quadratic denominator formula."
                ),
                CBSEFormula(
                    name = "Standard Integral: dx / √(a² - x²)",
                    formulaText = "[ ∫ dx / √(a² - x²) = sin⁻¹(x / a) + C ]",
                    description = "Inverse trigonometric form."
                )
            ),
            derivationsOrReactions = listOf(
                "Derivation of King's Property: Let I = ∫₀ᵃ f(x) dx. Substitute t = a - x ⇒ dt = -dx. When x = 0, t = a; when x = a, t = 0.\nI = ∫ₐ⁰ f(a - t)(-dt) = ∫₀ᵃ f(a - t) dt = ∫₀ᵃ f(a - x) dx. Proved.",
                "Classic CBSE Evaluation: I = ∫₀^(π/2) [ √sin x / (√sin x + √cos x) ] dx.\nApply property: I = ∫₀^(π/2) [ √cos x / (√cos x + √sin x) ] dx.\nAdd both: 2I = ∫₀^(π/2) 1 dx = π/2 ⇒ [ I = π / 4 ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Evaluate: ∫₀^π [ x sin x / (1 + cos² x) ] dx.",
                    yearRepeated = "CBSE Board 2024, 2022, 2019, 2016 (4 Marks)",
                    answerSummary = "Apply King's property I = ∫₀^π [ (π - x) sin x / (1 + cos² x) ] dx. Adding 2I = π ∫₀^π [ sin x / (1 + cos² x) ] dx. Substitute cos x = t, dt = -sin x dx. Limits from 1 to -1. 2I = π [ -tan⁻¹(t) ]₁⁻¹ = π [ π/4 - (-π/4) ] = π²/2 ⇒ I = π² / 4."
                )
            ),
            pdfEstimatedPages = 7
        ),

        // --- CLASS 12 PHYSICS ---
        CBSENoteChapter(
            id = "c12_phys_ch1",
            classLevel = CBSEClass.CLASS_12,
            subject = Subject.PHYSICS,
            chapterNumber = 1,
            title = "Electric Charges and Fields & Gauss's Law",
            ncertReference = "NCERT Class 12 Physics Chapter 1",
            weightageMarks = "8 Marks (Unit I)",
            summary = "Coulomb's Law, electric field lines, electric dipole, torque on dipole, Gauss's theorem and its three major applications (infinitely long wire, infinite plane sheet, and thin spherical shell).",
            keyPoints = listOf(
                "Electric charge is quantized: [ q = ± n · e ] where e = 1.6 × 10⁻¹⁹ C.",
                "Coulomb's Law: [ F = (1 / 4πε₀) · (q₁q₂ / r²) ] where 1/4πε₀ = 9 × 10⁹ N·m²/C².",
                "Electric field due to dipole at axial point: [ E_axial = (1 / 4πε₀) · (2p / r³) ]; equatorial point: [ E_equatorial = (1 / 4πε₀) · (p / r³) ]. Axial field is exactly twice the equatorial field.",
                "Gauss's Law: Net electric flux through any closed Gaussian surface equals [ Φ = ∮ E · dA = q_enclosed / ε₀ ]."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Coulomb's Law",
                    formulaText = "[ F = (1 / 4πε₀) · (q₁ · q₂) / r² ]",
                    description = "Force between two static point charges in vacuum."
                ),
                CBSEFormula(
                    name = "Gauss's Theorem",
                    formulaText = "[ Φ = ∮ E · dA = q_in / ε₀ ]",
                    description = "Connects surface flux to total charge enclosed."
                ),
                CBSEFormula(
                    name = "Electric Field of Infinite Straight Wire",
                    formulaText = "[ E = λ / (2πε₀ · r) ]",
                    description = "λ is linear charge density; r is radial distance from wire."
                ),
                CBSEFormula(
                    name = "Electric Field of Infinite Plane Sheet",
                    formulaText = "[ E = σ / (2ε₀) ]",
                    description = "σ is surface charge density; field is uniform and independent of distance r."
                ),
                CBSEFormula(
                    name = "Torque on Dipole in Uniform Field",
                    formulaText = "[ τ = p × E = p · E · sin θ ]",
                    description = "p = q · 2a is electric dipole moment."
                )
            ),
            derivationsOrReactions = listOf(
                "Derivation of E due to Infinitely Long Straight Wire via Gauss's Law:\nConstruct cylindrical Gaussian surface of radius r and length l coaxial with wire. Flux through two flat circular ends = 0 (E ⊥ dA). Flux through curved surface = E(2πrl). Total enclosed charge q_in = λl.\nFrom Gauss's Law: E(2πrl) = λl / ε₀ ⇒ [ E = λ / (2πε₀ r) ].",
                "Derivation of E due to Uniform Thin Spherical Shell:\nInside shell (r < R): q_enclosed = 0 ⇒ E = 0.\nOutside shell (r ≥ R): E(4πr²) = q / ε₀ ⇒ [ E = q / (4πε₀ r²) ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "State Gauss's theorem in electrostatics. Use it to derive an expression for the electric field due to an infinitely long thin wire of uniform linear charge density λ.",
                    yearRepeated = "CBSE Board 2023, 2021, 2019, 2017 (5 Marks)",
                    answerSummary = "Statement: Φ = ∮ E·dA = q/ε₀. Choose cylinder of radius r and length l. Curve surface area = 2πrl. E(2πrl) = λl/ε₀ ⇒ E = λ / (2πε₀r). Direction is radially outwards if λ > 0."
                )
            ),
            pdfEstimatedPages = 6
        ),
        CBSENoteChapter(
            id = "c12_phys_ch3",
            classLevel = CBSEClass.CLASS_12,
            subject = Subject.PHYSICS,
            chapterNumber = 3,
            title = "Current Electricity",
            ncertReference = "NCERT Class 12 Physics Chapter 3",
            weightageMarks = "8 - 10 Marks",
            summary = "Drift velocity and relaxation time, microscopic form of Ohm's law, temperature dependence of resistance, internal resistance, cells in series and parallel, Kirchhoff's laws, and Wheatstone bridge principle.",
            keyPoints = listOf(
                "Drift velocity expression: [ v_d = - (e · E · τ) / m ] where τ is average relaxation time.",
                "Current density relation: [ I = n · e · A · v_d ] and microscopic Ohm's law [ J = σ · E ].",
                "Kirchhoff's Junction Rule (KCL) is based on conservation of charge: [ Σ I = 0 ].",
                "Kirchhoff's Loop Rule (KVL) is based on conservation of energy: [ Σ ΔV = 0 ].",
                "Balanced Wheatstone Bridge condition: [ P / Q = R / S ] (no current passes through galvanometer)."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Drift Velocity Formula",
                    formulaText = "[ v_d = (e · E · τ) / m = (e · V · τ) / (m · l) ]",
                    description = "Relates applied potential difference V, length l, and drift speed."
                ),
                CBSEFormula(
                    name = "Current & Drift Velocity Relation",
                    formulaText = "[ I = n · e · A · v_d ]",
                    description = "n is number density of free electrons (e⁻/m³)."
                ),
                CBSEFormula(
                    name = "Resistivity in terms of Electron Parameters",
                    formulaText = "[ ρ = m / (n · e² · τ) ]",
                    description = "Shows why resistance increases with temperature in conductors (τ decreases)."
                ),
                CBSEFormula(
                    name = "Internal Resistance of a Cell",
                    formulaText = "[ r = [ (E / V) - 1 ] · R ]",
                    description = "E is EMF, V is terminal potential difference, R is load resistance."
                ),
                CBSEFormula(
                    name = "Wheatstone Bridge Balance",
                    formulaText = "[ P / Q = R / S ]",
                    description = "Condition for null deflection in galvanometer."
                )
            ),
            derivationsOrReactions = listOf(
                "Derivation of Ohm's Law from Drude Model:\nv_d = eEτ/m = eVτ/(ml). Since I = neAv_d = neA(eVτ/ml) = [ (ne²Aτ) / (ml) ] V ⇒ V = [ (m / ne²τ) · (l / A) ] I = R I where R = ρ l / A and ρ = m / (ne²τ). Proved.",
                "Wheatstone Bridge proof using Kirchhoff's rules: At balance I_g = 0. Loop 1: I₁P - I₂R = 0 ⇒ I₁P = I₂R. Loop 2: I₁Q - I₂S = 0 ⇒ I₁Q = I₂S. Dividing yields P/Q = R/S."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "Derive an expression for the drift velocity of free electrons in a conductor in terms of relaxation time. Hence deduce Ohm's law.",
                    yearRepeated = "CBSE Board 2024, 2020, 2018 (5 Marks)",
                    answerSummary = "F = -eE = ma ⇒ a = -eE/m. v_d = 0 + aτ = -eEτ/m. Current I = neAv_d = (ne²Aτ/ml)V. Rearranging yields V/I = ml / (ne²Aτ) = constant R at constant temperature."
                )
            ),
            pdfEstimatedPages = 6
        ),

        // --- CLASS 12 CHEMISTRY ---
        CBSENoteChapter(
            id = "c12_chem_ch2",
            classLevel = CBSEClass.CLASS_12,
            subject = Subject.CHEMISTRY,
            chapterNumber = 2,
            title = "Solutions & Colligative Properties",
            ncertReference = "NCERT Class 12 Chemistry Chapter 1 (NCERT revised)",
            weightageMarks = "7 Marks (Unit I)",
            summary = "Raoult's law for volatile and non-volatile solutes, ideal and non-ideal solutions, azeotropes, four colligative properties, van 't Hoff factor (i), association and dissociation of electrolytes.",
            keyPoints = listOf(
                "Raoult's Law: For a solution of volatile liquids, partial vapour pressure of each component [ p₁ = x₁ · p₁° ].",
                "Colligative properties depend only on the number of solute particles, not on their nature.",
                "Four colligative properties: Relative lowering of vapour pressure, elevation of boiling point, depression of freezing point, and osmotic pressure.",
                "Osmotic pressure [ Π = i · C · R · T ] is the best method to determine molar masses of polymers and biomolecules because measurements are at room temperature."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Relative Lowering of Vapour Pressure",
                    formulaText = "[ (p₁° - p₁) / p₁° = i · x₂ ≈ i · (w₂ · M₁) / (M₂ · w₁) ]",
                    description = "x₂ is mole fraction of non-volatile solute."
                ),
                CBSEFormula(
                    name = "Elevation in Boiling Point",
                    formulaText = "[ ΔT_b = i · K_b · m = i · (1000 · K_b · w₂) / (M₂ · w₁) ]",
                    description = "K_b is ebullioscopic constant (for water = 0.52 K·kg/mol)."
                ),
                CBSEFormula(
                    name = "Depression in Freezing Point",
                    formulaText = "[ ΔT_f = i · K_f · m = i · (1000 · K_f · w₂) / (M₂ · w₁) ]",
                    description = "K_f is cryoscopic constant (for water = 1.86 K·kg/mol)."
                ),
                CBSEFormula(
                    name = "Osmotic Pressure",
                    formulaText = "[ Π = i · C · R · T = i · (w₂ · R · T) / (M₂ · V) ]",
                    description = "R = 0.0821 L·atm/(mol·K) or 8.314 J/(mol·K)."
                ),
                CBSEFormula(
                    name = "Van 't Hoff Factor & Degree of Dissociation (α)",
                    formulaText = "[ i = 1 + (n - 1) · α ] and [ α = (i - 1) / (n - 1) ]",
                    description = "n is number of ions formed upon complete dissociation (e.g. for NaCl n=2, CaCl₂ n=3)."
                )
            ),
            derivationsOrReactions = listOf(
                "Degree of Association Formula: If n molecules of solute associate to form A_n: 2CH₃COOH ⇌ (CH₃COOH)₂ in benzene. i = 1 - α + α/n ⇒ [ α = (1 - i) / (1 - 1/n) ].",
                "Ideal vs Non-Ideal Solutions: Positive deviation (ΔH_mix > 0, ΔV_mix > 0, Ethanol + Acetone); Negative deviation (ΔH_mix < 0, ΔV_mix < 0, Chloroform + Acetone with H-bonding)."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "A 5% solution (by mass) of cane sugar (M = 342 g/mol) in water has freezing point of 271 K. Calculate the freezing point of 5% glucose (M = 180 g/mol) in water. (Freezing point of pure water = 273.15 K).",
                    yearRepeated = "CBSE Board 2023, 2020 (3 Marks)",
                    answerSummary = "For cane sugar: ΔT_f = 273.15 - 271 = 2.15 K. Molality m₁ = (5 / 342) / (95 / 1000) = 0.154 mol/kg. K_f = ΔT_f / m₁ = 2.15 / 0.154 = 13.97 K·kg/mol. For glucose: m₂ = (5 / 180) / (95 / 1000) = 0.292 mol/kg. ΔT_f(glucose) = 13.97 × 0.292 = 4.08 K. Freezing point = 273.15 - 4.08 = 269.07 K."
                )
            ),
            pdfEstimatedPages = 6
        ),
        CBSENoteChapter(
            id = "c12_chem_ch3",
            classLevel = CBSEClass.CLASS_12,
            subject = Subject.CHEMISTRY,
            chapterNumber = 3,
            title = "Electrochemistry & Chemical Kinetics",
            ncertReference = "NCERT Class 12 Chemistry Chapter 2 & 3",
            weightageMarks = "14 Marks (High Board Weightage)",
            summary = "Galvanic cells, Nernst equation, Kohlrausch's law, Faraday's laws of electrolysis, rate laws, order vs molecularity, integrated rate equations for zero and first order, and Arrhenius activation energy.",
            keyPoints = listOf(
                "Standard EMF of cell: [ E°_cell = E°_cathode - E°_anode ] (both taken as standard reduction potentials).",
                "Nernst equation at 298 K: [ E_cell = E°_cell - (0.0591 / n) · log₁₀ Q ].",
                "Kohlrausch's Law of Independent Migration of Ions: [ Λ°_m = ν₊ λ°₊ + ν₋ λ°₋ ].",
                "First order reaction half-life [ t½ = 0.693 / k ] is completely independent of initial reactant concentration [A]₀."
            ),
            formulas = listOf(
                CBSEFormula(
                    name = "Nernst Equation at 298 K",
                    formulaText = "[ E_cell = E°_cell - (0.0591 / n) · log₁₀ [ (Products) / (Reactants) ] ]",
                    description = "n is number of moles of electrons transferred in balanced cell reaction."
                ),
                CBSEFormula(
                    name = "Gibbs Energy and Cell EMF",
                    formulaText = "[ ΔG° = - n · F · E°_cell ] and [ ΔG° = - 2.303 · R · T · log₁₀ K_c ]",
                    description = "F is Faraday's constant = 96487 C/mol ≈ 96500 C/mol."
                ),
                CBSEFormula(
                    name = "First Order Integrated Rate Equation",
                    formulaText = "[ k = (2.303 / t) · log₁₀ ( [A]₀ / [A] ) ]",
                    description = "[A]₀ is initial concentration; [A] is remaining concentration at time t."
                ),
                CBSEFormula(
                    name = "First Order Half-Life",
                    formulaText = "[ t½ = 0.693 / k ]",
                    description = "Constant half-life unique to first-order reactions."
                ),
                CBSEFormula(
                    name = "Arrhenius Equation",
                    formulaText = "[ log₁₀ (k₂ / k₁) = (E_a / 2.303 R) · [ (T₂ - T₁) / (T₁ · T₂) ] ]",
                    description = "Calculates activation energy E_a from rate constants at two temperatures."
                )
            ),
            derivationsOrReactions = listOf(
                "Derivation of First Order Integrated Rate Law:\n- d[A]/dt = k[A] ⇒ d[A]/[A] = - k dt ⇒ Integrating: ln [A] = - kt + ln [A]₀ ⇒ ln ([A]₀ / [A]) = kt ⇒ [ k = (2.303 / t) log₁₀ ([A]₀ / [A]) ].\nAt t = t½, [A] = [A]₀/2 ⇒ k = (2.303 / t½) log₁₀ 2 = 2.303 × 0.3010 / t½ = [ 0.693 / t½ ].",
                "Kohlrausch's Law calculation of weak acid dissociation constant: α = Λ_m / Λ°_m. K_a = c α² / (1 - α) = c (Λ_m / Λ°_m)² / [ 1 - (Λ_m / Λ°_m) ]."
            ),
            boardQuestions = listOf(
                CBSEBoardQuestion(
                    question = "A first order reaction takes 20 minutes for 25% decomposition. Calculate the time when 75% of the reaction will be completed.",
                    yearRepeated = "CBSE Board 2024, 2020, 2018 (3 Marks)",
                    answerSummary = "For 25% decomposition: [A] = 0.75 [A]₀. k = (2.303 / 20) log(100 / 75) = (2.303 / 20) log(4/3) = 0.11515 × 0.1249 = 0.01438 min⁻¹. For 75% completion: [A] = 0.25 [A]₀. t = (2.303 / 0.01438) log(100 / 25) = (2.303 / 0.01438) log 4 = 160.15 × 0.6020 = 96.4 minutes."
                )
            ),
            pdfEstimatedPages = 7
        )
    )

    fun getChapters(classLevel: CBSEClass, subject: Subject = Subject.ALL): List<CBSENoteChapter> {
        val allForClass = if (classLevel == CBSEClass.CLASS_11) class11Notes else class12Notes
        return if (subject == Subject.ALL) {
            allForClass
        } else {
            allForClass.filter { it.subject == subject }
        }
    }

    fun getChapterById(chapterId: String): CBSENoteChapter? {
        return (class11Notes + class12Notes).find { it.id == chapterId }
    }

    fun getAllCuratedFormulas(classLevel: CBSEClass? = null, subject: Subject? = null): List<CBSEFormulaHandbookItem> {
        val chapters = when (classLevel) {
            CBSEClass.CLASS_11 -> class11Notes
            CBSEClass.CLASS_12 -> class12Notes
            null -> class11Notes + class12Notes
        }.filter { subject == null || subject == Subject.ALL || it.subject == subject }

        return chapters.flatMap { ch ->
            ch.formulas.mapIndexed { idx, f ->
                CBSEFormulaHandbookItem(
                    id = "${ch.id}_f$idx",
                    chapterId = ch.id,
                    chapterTitle = ch.title,
                    classLevel = ch.classLevel,
                    subject = ch.subject,
                    formula = f,
                    isFavorite = false
                )
            }
        }
    }

    // =========================================================================
    // ROOM LOCAL OFFLINE CACHE INTEGRATION
    // =========================================================================
    suspend fun ensureCachePopulated(context: Context): CacheStatus = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        val chapterCount = db.chapterDao().getChapterCount()
        val allCurated = class11Notes + class12Notes

        if (chapterCount < allCurated.size) {
            val now = System.currentTimeMillis()
            val chapterEntities = allCurated.map { ChapterCacheHelper.chapterToEntity(it, now) }
            db.chapterDao().insertChapters(chapterEntities)

            val formulaEntities = allCurated.flatMap { ChapterCacheHelper.extractFormulasFromChapter(it, now) }
            db.formulaDao().insertFormulas(formulaEntities)
        }

        // Migrate legacy notebook notes if needed
        val notesCount = db.notebookDao().getNotesCount()
        if (notesCount == 0) {
            val legacy = loadStudentNotes(context)
            if (legacy.isNotEmpty()) {
                db.notebookDao().insertNotes(legacy.map { ChapterCacheHelper.notebookToEntity(it) })
            }
        }

        getCacheStatus(context)
    }

    suspend fun forceRefreshCache(context: Context): CacheStatus = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        val allCurated = class11Notes + class12Notes
        val now = System.currentTimeMillis()

        val chapterEntities = allCurated.map { ChapterCacheHelper.chapterToEntity(it, now) }
        db.chapterDao().insertChapters(chapterEntities)

        val formulaEntities = allCurated.flatMap { ChapterCacheHelper.extractFormulasFromChapter(it, now) }
        db.formulaDao().insertFormulas(formulaEntities)

        getCacheStatus(context)
    }

    suspend fun getCacheStatus(context: Context): CacheStatus = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        val chapterCount = db.chapterDao().getChapterCount()
        val formulaCount = db.formulaDao().getFormulaCount()
        val lastTimestamp = db.chapterDao().getLastCachedTimestamp() ?: System.currentTimeMillis()
        CacheStatus(
            totalChapters = if (chapterCount > 0) chapterCount else (class11Notes.size + class12Notes.size),
            totalFormulas = if (formulaCount > 0) formulaCount else getAllCuratedFormulas().size,
            lastCachedTimestamp = lastTimestamp,
            isOfflineReady = true,
            storageType = "Room SQLite Database"
        )
    }

    fun getChaptersFlow(context: Context, classLevel: CBSEClass, subject: Subject = Subject.ALL): Flow<List<CBSENoteChapter>> {
        val db = AppDatabase.getInstance(context)
        val rawFlow = if (subject == Subject.ALL) {
            db.chapterDao().getChaptersByClassFlow(classLevel.name)
        } else {
            db.chapterDao().getChaptersByClassAndSubjectFlow(classLevel.name, subject.name)
        }
        return rawFlow.map { list ->
            if (list.isEmpty()) {
                getChapters(classLevel, subject)
            } else {
                list.map { ChapterCacheHelper.entityToChapter(it) }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getFormulasFlow(context: Context, classLevel: CBSEClass? = null, subject: Subject? = null): Flow<List<CBSEFormulaHandbookItem>> {
        val db = AppDatabase.getInstance(context)
        val rawFlow = when {
            classLevel != null && subject != null && subject != Subject.ALL ->
                db.formulaDao().getFormulasByClassAndSubjectFlow(classLevel.name, subject.name)
            classLevel != null ->
                db.formulaDao().getFormulasByClassFlow(classLevel.name)
            else ->
                db.formulaDao().getAllFormulasFlow()
        }
        return rawFlow.map { list ->
            if (list.isEmpty()) {
                getAllCuratedFormulas(classLevel, subject)
            } else {
                list.map { ChapterCacheHelper.entityToFormulaHandbookItem(it) }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun searchFormulasFlow(context: Context, query: String): Flow<List<CBSEFormulaHandbookItem>> {
        val db = AppDatabase.getInstance(context)
        return db.formulaDao().searchFormulas(query).map { list ->
            list.map { ChapterCacheHelper.entityToFormulaHandbookItem(it) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun toggleChapterBookmark(context: Context, chapterId: String, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        AppDatabase.getInstance(context).chapterDao().updateBookmark(chapterId, isBookmarked)
    }

    suspend fun toggleFormulaFavorite(context: Context, formulaId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        AppDatabase.getInstance(context).formulaDao().updateFavorite(formulaId, isFavorite)
    }

    fun getStudentNotesFlow(context: Context): Flow<List<StudentNotebookEntry>> {
        val db = AppDatabase.getInstance(context)
        return db.notebookDao().getAllNotesFlow().map { list ->
            if (list.isEmpty()) {
                loadStudentNotes(context)
            } else {
                list.map { ChapterCacheHelper.entityToNotebook(it) }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun insertStudentNoteRoom(context: Context, note: StudentNotebookEntry) = withContext(Dispatchers.IO) {
        AppDatabase.getInstance(context).notebookDao().insertNote(ChapterCacheHelper.notebookToEntity(note))
        saveStudentNote(context, note)
    }

    suspend fun deleteStudentNoteRoom(context: Context, noteId: String) = withContext(Dispatchers.IO) {
        AppDatabase.getInstance(context).notebookDao().deleteNoteById(noteId)
        deleteStudentNote(context, noteId)
    }

    // =========================================================================
    // STUDENT NOTEBOOK PERSISTENCE (Local JSON in SharedPreferences)
    // =========================================================================
    private const val PREFS_NAME = "tryourself_cbse_notebook"
    private const val KEY_NOTEBOOK_ENTRIES = "notebook_entries"

    fun loadStudentNotes(context: Context): List<StudentNotebookEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_NOTEBOOK_ENTRIES, null) ?: return getSampleStudentNotes()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<StudentNotebookEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val formulasArr = obj.optJSONArray("formulasJotted")
                val formulas = mutableListOf<String>()
                if (formulasArr != null) {
                    for (j in 0 until formulasArr.length()) {
                        formulas.add(formulasArr.getString(j))
                    }
                }
                list.add(
                    StudentNotebookEntry(
                        id = obj.getString("id"),
                        classLevel = CBSEClass.valueOf(obj.optString("classLevel", CBSEClass.CLASS_12.name)),
                        subject = Subject.valueOf(obj.optString("subject", Subject.MATHS.name)),
                        chapterTitle = obj.getString("chapterTitle"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        formulasJotted = formulas,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isStarred = obj.optBoolean("isStarred", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            getSampleStudentNotes()
        }
    }

    fun saveStudentNote(context: Context, note: StudentNotebookEntry) {
        val currentNotes = loadStudentNotes(context).toMutableList()
        val index = currentNotes.indexOfFirst { it.id == note.id }
        if (index >= 0) {
            currentNotes[index] = note
        } else {
            currentNotes.add(0, note)
        }
        persistNotes(context, currentNotes)
    }

    fun deleteStudentNote(context: Context, noteId: String) {
        val currentNotes = loadStudentNotes(context).toMutableList()
        currentNotes.removeAll { it.id == noteId }
        persistNotes(context, currentNotes)
    }

    private fun persistNotes(context: Context, notes: List<StudentNotebookEntry>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val array = JSONArray()
        for (n in notes) {
            val obj = JSONObject().apply {
                put("id", n.id)
                put("classLevel", n.classLevel.name)
                put("subject", n.subject.name)
                put("chapterTitle", n.chapterTitle)
                put("title", n.title)
                put("content", n.content)
                put("timestamp", n.timestamp)
                put("isStarred", n.isStarred)
                val fArr = JSONArray()
                n.formulasJotted.forEach { fArr.put(it) }
                put("formulasJotted", fArr)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_NOTEBOOK_ENTRIES, array.toString()).apply()
    }

    private fun getSampleStudentNotes(): List<StudentNotebookEntry> {
        return listOf(
            StudentNotebookEntry(
                id = "sample_note_1",
                classLevel = CBSEClass.CLASS_12,
                subject = Subject.MATHS,
                chapterTitle = "Integrals (Calculus)",
                title = "Definite Integrals - King's Property Cheat Sheet",
                content = "Whenever limits are 0 to a or a to b with symmetric trig functions like sin and cos, immediately use [ ∫₀ᵃ f(x) dx = ∫₀ᵃ f(a - x) dx ]. Add equation (1) and (2). Almost always the integrand simplifies to 1 or cancels out, giving I = (b - a)/2 or π/4!",
                formulasJotted = listOf("[ ∫₀ᵃ f(x) dx = ∫₀ᵃ f(a-x) dx ]", "[ ∫ u v dx = u ∫ v dx - ∫ (u' ∫ v dx) dx ]"),
                timestamp = System.currentTimeMillis() - 86400000L,
                isStarred = true
            ),
            StudentNotebookEntry(
                id = "sample_note_2",
                classLevel = CBSEClass.CLASS_12,
                subject = Subject.PHYSICS,
                chapterTitle = "Current Electricity",
                title = "Kirchhoff's Laws & Sign Conventions for Boards",
                content = "Going in direction of current: -IR drop.\nGoing against current: +IR gain.\nGoing from -ve to +ve terminal of cell: +EMF.\nGoing from +ve to -ve terminal of cell: -EMF.\nWheatstone bridge: null point condition P/Q = R/S.",
                formulasJotted = listOf("[ Σ I = 0 ]", "[ Σ ΔV = 0 ]", "[ v_d = (e E τ) / m ]"),
                timestamp = System.currentTimeMillis() - 172800000L,
                isStarred = false
            ),
            StudentNotebookEntry(
                id = "sample_note_3",
                classLevel = CBSEClass.CLASS_11,
                subject = Subject.PHYSICS,
                chapterTitle = "Laws of Motion & Kinematics",
                title = "Newton's 2nd Law & Banked Road Notes",
                content = "Always draw Free Body Diagram (FBD) first! Resolve normal force N into N cos θ (balances mg) and N sin θ (provides centripetal force mv²/r).",
                formulasJotted = listOf("[ F = m a ]", "[ v_max = √(r g (μ + tan θ) / (1 - μ tan θ)) ]"),
                timestamp = System.currentTimeMillis() - 259200000L,
                isStarred = true
            )
        )
    }
}
