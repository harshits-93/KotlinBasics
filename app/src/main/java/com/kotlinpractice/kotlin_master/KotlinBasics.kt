package com.kotlinpractice.kotlin_master

// ============================================================
// 1️⃣ VARIABLES (var / val / const) — COMPLETE GUIDE
// ============================================================

fun variablesDeep() {

    // -------------------------
    // var (mutable)
    // -------------------------
    var count: Int = 10
    count = 20   // ✅ allowed

    // -------------------------
    // val (read-only reference)
    // -------------------------
    val name: String = "Kotlin"
    // name = "Java" ❌ NOT allowed

    /*
     IMPORTANT:
     val = reference is immutable, NOT the object
     */

    val list = mutableListOf("A", "B")
    list.add("C")       // ✅ allowed
    // list = mutableListOf() ❌ NOT allowed

    // -------------------------
    // TYPE INFERENCE
    // -------------------------
    val age = 25        // Kotlin infers Int

    // -------------------------
    // RUNTIME vs COMPILE TIME
    // -------------------------

    // Runtime (decided when code runs)
    val runtimeValue = Math.random()

    // Compile-time constant
    // Must be:
    // - top-level OR inside object/companion
    // - primitive or String
}

// Compile-time constant
const val MAX_USERS = 100

/*
INTERVIEW POINT:

val → runtime constant
const val → compile-time constant

Difference:
- const is replaced directly in bytecode
- val is evaluated at runtime
*/

// ============================================================
// 2️⃣ NULL SAFETY — FULL UNDERSTANDING
// ============================================================

fun nullSafetyDeep() {

    // -------------------------
    // Nullable vs Non-nullable
    // -------------------------
    val name: String = "Kotlin"
    // name = null ❌ NOT allowed

    val nullableName: String? = null // ✅ allowed

    // -------------------------
    // 1. SAFE CALL (?.)
    // -------------------------
    val length1 = nullableName?.length
    // returns null if nullableName is null

    // -------------------------
    // 2. LET (safe execution)
    // -------------------------
    nullableName?.let {
        // executes ONLY if not null
        println(it.length)
    }

    /*
    WHY let?
    - avoids null check
    - scoped variable (it)
    */

    // -------------------------
    // 3. ELVIS OPERATOR (?:)
    // -------------------------
    val length2 = nullableName?.length ?: -1

    /*
    FLOW:
    - if left is NOT null → return it
    - else → return right value
    */

    // -------------------------
    // 4. NON-NULL ASSERTION (!!)
    // -------------------------
    val length3 = nullableName!!.length
    // ❗ CRASH if null

    /*
    RULE:
    - Avoid in production
    - Only use when 100% sure
    */

    // -------------------------
    // 5. SAFE CAST (as?)
    // -------------------------
    val obj: Any = "Hello"
    val str: String? = obj as? String

    /*
    as → crash if wrong type
    as? → returns null safely
    */

    // -------------------------
    // 6. LATEINIT vs NULLABLE
    // -------------------------

    // lateinit (non-null, assigned later)
    lateinit var userName: String

    userName = "Harshit"
    println(userName)

    /*
    RULES:
    - only var
    - non-null type
    - must initialize before use
    */

    // Nullable alternative
    var email: String? = null
}

/*
INTERVIEW TRAPS:

1. Difference:
   lateinit vs nullable

   lateinit:
   - non-null
   - crash if not initialized

   nullable:
   - safe but needs null handling

2. !! should be avoided → use ?. or ?: instead
*/

// ============================================================
// 3️⃣ CONTROL FLOW (SMART USAGE)
// ============================================================

fun controlFlowDeep(x: Int) {

    // -------------------------
    // WHEN as EXPRESSION
    // -------------------------
    val result = when (x) {
        1 -> "One"
        2 -> "Two"
        in 3..5 -> "Range"
        else -> "Other"
    }

    /*
    when can:
    - return value
    - replace switch
    */

    // -------------------------
    // WHEN without argument
    // -------------------------
    when {
        x < 0 -> println("Negative")
        x % 2 == 0 -> println("Even")
        else -> println("Odd")
    }

    // -------------------------
    // LOOPS
    // -------------------------

    // range
    for (i in 1..5) {}

    // until (exclusive)
    for (i in 1 until 5) {}

    // reverse
    for (i in 5 downTo 1) {}

    // step
    for (i in 1..10 step 2) {}

    // with index
    val list = listOf("A", "B", "C")
    for ((index, value) in list.withIndex()) {
        println("$index -> $value")
    }
}

/*
INTERVIEW POINTS:

1. when is more powerful than switch
2. ranges are inclusive (..)
3. until excludes upper bound
*/