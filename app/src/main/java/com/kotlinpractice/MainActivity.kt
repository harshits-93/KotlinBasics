package com.kotlinpractice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// =========================================================
// TYPE ALIASES (must be top-level - Kotlin does not allow nested type aliases)
// =========================================================
// TYPE ALIASES (must be top-level - Kotlin does not allow nested type aliases)
// =========================================================

// typealias gives an ALTERNATE NAME to an existing type.
// It does NOT create a new type - it is just a nickname, fully interchangeable.
typealias UserId = String
typealias UserMap = Map<UserId, String>
typealias ClickAction = (String) -> Unit

class MainActivity : AppCompatActivity() {
    /*
    * lateinit keyword usage and rules.
    * For some variables, we don't want to initialize at the time of declaration, even with null or
    * empty values also. But if we try it in kotlin, it won't allow, hence for the rescue we use
    * lateinit keyword which tell compiler that we are not initializing it now, but will assign the
    * value at some later point of time.
    *
    * Note: 1.)But on condition that, this variable should not be accessed or used before initializing it,
    * otherwise compiler will throw uninitialized property exception.
    * 2.) lateinit can't be used with the variable whose datatypes are nullable, it works with non-nullable
    * and non-primitive types only
    *
    * 3.) lateinit can't be used with val, it should be used with var.
    *
    * lateinit only works with non-nullable reference types, not primitives like Int, Float, etc. Why?
    *
    * Kotlin needs a way to track “not initialized yet”
    * For objects → it can use null internally
    * For primitives → they cannot hold null, so Kotlin cannot track initialization
    *
    * */


    //Note here String is not-null type in kotlin, we cannot assign null value to string directly,
    //for that we use this --> var abc:String? = null, then it is possible.

    //So, w.r.t point 2.) if we add ? to String or any object type then, lateinit won't allow it.
    //But below one is valid
    lateinit var name: String

    // How to check if initialized without crashing:
    // if (::name.isInitialized) { println(name) }

    /*++++
    * lazy keyword usage and rules.
    *
    * In many scenarios we declare a variable and initialize it, but it is not used in the project,
    * but it still occupies memory which is waste. For this we use lazy keyword.
    *
    * If we use lazy for initializing any variable, then its memory is allocated/occupied only when it is
    * actually used, not just when we declare it. Memory for the property reference exists, but
    * the actual object/value is created only on first access. If the variable is used one time then it is
    * stored in cache and can be reused and it is thread safe only in default mode.
    *
    * lazy can be used with val but not var, and datatypes can be nullable or non-nullable.
    *
    * Thread Safety Modes:
    * - SYNCHRONIZED (default): thread-safe
    * - PUBLICATION: multiple threads may initialize, but only one value is used
    * - NONE: no thread safety (faster)
    *
    * * Use case:
    * - Expensive object creation
    * - Object may not be used always
    * */

    //normal variable initialization
    //var city:String = "abc"

    //lazy initialization
    val city1: String by lazy { "abc" }
    val city2: String? by lazy { null }


    //Thread mode definitions:
    val data1 by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { "abc" }
    val data2 by lazy(LazyThreadSafetyMode.PUBLICATION) { "abc" }
    val data3 by lazy(LazyThreadSafetyMode.NONE) { "abc" }

    //Detailed explanation on lazy syntax
    val city: String by lazy { "abc" }

    /*  Break it into 3 parts:
      🔹 (1) lazy { "abc" }
      This creates a Lazy object (wrapper)
      It does NOT execute "abc" immediately

      Think:
      val temp = lazy { "abc" }  // not executed yet

      🔹 (2) by
      by = delegation
      Means: “Don’t store value in city, delegate it to another object”
      👉 So city is NOT storing "abc" directly

      🔹 (3) Combined meaning
      val city: String by lazy { "abc" }

      👉 Means:
      “city will get its value from lazy object when needed”
      Internal Working (Clean Version)

      Equivalent logic:
      val cityDelegate = lazy { "abc" }

      val city: String
          get() = cityDelegate.value


      ⚡ Runtime flow
      First time:
      println(city)

      Steps:

      - city getter called
      - cityDelegate.value accessed
      - { "abc" } executes
      - value stored (cached)
      - "abc" returned

      Second time:
      println(city)

      Steps:
      - cityDelegate.value accessed
      - Already initialized ✅
      - Cached value returned (no execution)

       When we write:
        val city: String by lazy { "abc" }
        The `lazy {}` actually returns an object of type:
        Lazy<String>

       What is Lazy<String> (Lazy object / wrapper)?
        This Lazy object is a wrapper/controller that:
          - Stores the lambda → { "abc" }
          - Executes it only on first access
          - Caches (stores) the result
          - Returns cached value on next calls

      🎯 One-line understanding
      city doesn’t store value — it asks delegate every time, and delegate caches result after first call.
      */


    /**
     * 🔥 by lazy vs = lazy {} (VERY IMPORTANT)
     * ✅ Case 1: by lazy
     * val city: String by lazy { "abc" }
     *
     * ✔️ city type = String
     * ✔️ You directly use:
     *    println(city)
     *
     * ❌ Case 2: = lazy {}
     * val city = lazy { "abc" }
     *
     * 👉 Now type is:
     * Lazy<String>
     *
     * So you must use:
     * println(city.value)
     * */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //Most kotlin concepts covered.
        varValConst()
        nullSafety()
        controlFlow()
        inheritance()
        inheritanceWithPrimaryAndSecondaryConstructor()
        abstractAndInterface()
        dataClass()
        objectExplanation()
        companionObjects()
        getterAndSetter()
        backingProperty()
        higherOrderFunctionsAndLambdas()
        scopeFunctions()
        // arrays()
        arrayExamples()
        collections()
        filterAndMap()
        predicates()
        extensionFunction()
        kotlinPractice();
        sealedClassesExample()
        genericsExample()
        delegationExample()

        // Newly added concepts (senior interview revision).
        enumClasses()
        objectExpressions()
        nestedAndInnerClasses()
        errorHandling()
        defaultNamedVarargArgs()
        destructuring()
        sealedInterfaceExample()
        typeAliasExample()
        infixAndOperatorOverloading()
        nothingAndUnit()
        customPropertyDelegate()
        sequenceVsCollection()
        collectionTransformations()
        valueClassExample()

        // Launch the coroutine / flow course activities
        findViewById<Button>(R.id.btn_coroutines).setOnClickListener {
            startActivity(Intent(this, CoroutinesActivity::class.java))
        }
        findViewById<Button>(R.id.btn_flow).setOnClickListener {
            startActivity(Intent(this, FlowActivity::class.java))
        }
    }

    fun varValConst() {

        // =========================================================
        // 1. var (Mutable Variable)
        // =========================================================

        /*
         * - 'var' means mutable variable.
         * - Its value CAN be changed after declaration.
         * - Memory/reference can be reassigned multiple times.
         * - Use 'var' only when modification is actually needed.
         */

        var counter: Int = 0
        counter = 10 // ✅ Valid


        // =========================================================
        // 2. val (Read-Only Variable)
        // =========================================================

        /*
         * - 'val' means read-only variable.
         * - Once assigned, it CANNOT be reassigned.
         * - It must be initialized either:
         *      1. At declaration
         *      2. In init block
         *      3. Via constructor
         *
         * - 'val' makes code safer and more predictable.
         * - Preferred over 'var' whenever possible.
         */

        val languageName: String = "Kotlin"

        // languageName = "Java"
        // ❌ ERROR: Val cannot be reassigned.

        // ---------------------------------------------------------
        // 1. Initialization at Declaration
        // ---------------------------------------------------------

        val appName: String = "MyApp"

        // ---------------------------------------------------------
        // 2. Initialization inside init block
        // ---------------------------------------------------------


        class User {
            val country: String

            init {
                country = "India"
            }

        }

        /*
         * Once assigned inside init,
         * it cannot be reassigned again.
         */

        // ---------------------------------------------------------
        // 3. Initialization via Constructor
        // ---------------------------------------------------------

        class Employee(
            val id: Int,
            val name: String
        )

        /*
         * Constructor automatically initializes:
         *
         * id
         * name
         */

        // =========================================================
        // Important Concept:
        // val makes the REFERENCE immutable,
        // NOT necessarily the OBJECT itself.
        // =========================================================

        val nameList = mutableListOf("John")

        // nameList = mutableListOf("Jane")
        // ❌ ERROR: Cannot change the reference

        nameList.add("Doe")
        // ✅ Valid because the list object itself is mutable


        // =========================================================
        // 3. val at Runtime
        // =========================================================

        /*
         * A 'val' value can still be determined at runtime.
         * It simply cannot be reassigned afterward.
         */


        val runtimeValue: Double = Math.random()

        val currentTime: Long = System.currentTimeMillis()

        /*
         * The values above are assigned DURING execution,
         * not during compilation.
         */


        // =========================================================
        // 4. const val (Compile-Time Constant)
        // =========================================================

        /*
         * 'const' is a MODIFIER in Kotlin.
         *
         * It converts a 'val' into a compile-time constant.
         *
         * Meaning:
         * - Value MUST be known before app runs
         * - Value is fixed forever
         * - Compiler directly replaces usages with actual value
         *
         * Since compile-time constants can never change,
         * 'const' works ONLY with 'val'
         * and NEVER with 'var'.
         */

        // const var MAX = 10
        // ❌ ERROR: const cannot be used with var


        // =========================================================
        // Rules of const val
        // =========================================================

        /*
         * const val:
         *
         * ✅ Must be initialized immediately
         * ✅ Must be primitive or String
         * ✅ Must be known at compile time
         *
         * ❌ Cannot use runtime functions
         * ❌ Cannot create objects
         * ❌ Cannot be declared inside normal functions i.e. local variables
         */


        // const val random = Math.random()
        // ❌ Runtime value not allowed

        // const val list = mutableListOf(1, 2, 3)
        // ❌ Object creation not allowed


        // =========================================================
        // Valid const val Locations
        // =========================================================

        /*
         * const val can only be declared:
         *
         * 1. Top-level
         * 2. Inside object
         * 3. Inside companion object
         *
         * NOT inside normal functions/classes.
         */

        /*
         * Example:
         *
         * const val APP_NAME = "MyApp"
         *
         * object Constants {
         *     const val MAX_COUNT = 100
         * }
         */


        // =========================================================
        // Important Syntax Note
        // =========================================================

        /*
         * 'const' itself is NOT a variable declaration keyword.
         *
         * It is only a modifier.
         *
         * Therefore:
         *
         * val  -> declares read-only variable
         * const -> modifies it into compile-time constant
         */

        // const MAX_SIZE = 100
        // ❌ ERROR

        // Correct:
        // const val MAX_SIZE = 100


        // =========================================================
        // Quick Revision
        // =========================================================

        /*
         * var       -> mutable variable
         * val       -> read-only reference
         * const val -> compile-time constant
         *
         * var  -> can reassign
         * val  -> cannot reassign
         * const val -> cannot reassign + known at compile time
         */


        // =========================================================
        // Important Syntax + Analogy
        // =========================================================

        /*
         * In Kotlin's grammar:
         *
         * 'const' is a MODIFIER,
         * not a declaration keyword like 'val' or 'var'.
         *
         * A modifier changes the characteristics
         * of an existing declaration.
         *
         * Think of it like this:
         *
         *  - 'val' declares:
         *      "This is a read-only variable."
         *
         *  - 'const' modifies that declaration:
         *      "And make it a compile-time constant."
         *
         * So:
         *
         * val name = "Kotlin"
         * -> read-only variable
         *
         * const val APP_NAME = "MyApp"
         * -> read-only + compile-time constant
         *
         * Since compile-time constants can NEVER change,
         * 'const' only works with 'val'
         * and not with 'var'.
         */

        // =========================================================
        // Quick Difference: var vs val vs const val
        // =========================================================

        /*
         * var
         * ----
         * - Mutable
         * - Can reassign
         * - Runtime value allowed
         *
         *
         * val
         * ----
         * - Read-only reference
         * - Cannot reassign
         * - Runtime value allowed
         *
         *
         * const val
         * ----------
         * - Compile-time constant
         * - Cannot reassign
         * - Value must be known before app runs
         * - Only primitive/String values allowed
         */


    }

    private fun nullSafety() {

        // =========================================================
        // Kotlin Null Safety
        // =========================================================

        /*
         * Kotlin's type system helps prevent:
         *
         * NullPointerException (NPE)
         * which is one of the most common crashes in Java.
         *
         * By default:
         * - Variables CANNOT store null
         * - Nullable types must be explicitly marked using '?'
         */

        // =========================================================
        // 1. Non-Nullable Type
        // =========================================================
        /*
         * Normal types cannot hold null.
         */

        val username: String = "Steve"
        // username = null
        // ❌ ERROR


        // =========================================================
        // 2. Nullable Type
        // =========================================================

        /*
         * '?' makes a type nullable.
         *
         * String?
         * means:
         * - Can hold String
         * - Can also hold null
         */

        val name: String? = /* null */ "Steve"


        // =========================================================
        // 3. Safe Call Operator ( ?. )
        // =========================================================

        /*
         * Executes only if object is NOT null.
         *
         * Returns:
         * - actual value if object is not null
         * - null if object itself is null
         *
         * Prevents NullPointerException.
         */
        println("Length = ${name?.length}")

        // =========================================================
        // 4. Safe Call with let ( ?.let )
        // =========================================================

        /*
         * Executes block ONLY IF value is not null.
         * 'it' becomes non-null inside block.
         */
        name?.let {
            // Here 'it' is String (not String?)
            println("Length = ${it.length}")
        }


        // =========================================================
        // 5. Smart Cast
        // =========================================================

        /*
         * Kotlin automatically converts nullable type
         * into non-null type after null check.
         */

        if (name != null) {
            // Smart cast happens here
            println(name.length)
        }

        // =========================================================
        // 6. Elvis Operator ( ?: )
        // =========================================================

        /*
         * Provides default value if expression becomes null.
         * Read as:
         * "If left side is null,
         * use right side value."
         */
        val length = name?.length ?: -1
        println("Length = $length")


        // =========================================================
        // 7. Non-Null Assertion Operator ( !! )
        // =========================================================
        /*
         * Converts nullable type into non-null manually.
         * Use ONLY when you are absolutely sure
         * value is not null.
         *
         * Otherwise throws:
         * NullPointerException (NPE)
         */

        println(name!!.length)

        /*
         * Dangerous example:
         *
         * val name: String? = null
         * println(name!!.length)
         *
         * ❌ Crash -> NullPointerException
         */


        // =========================================================
        // 8. Safe Cast Operator ( as? )
        // =========================================================

        /*
         * Safely converts one type into another.
         *
         * Returns:
         * - converted value if successful
         * - null if conversion fails
         *
         * Prevents ClassCastException.
         */
        val data: Any = "Kotlin"

        val safeString: String? = data as? String
        println(safeString)

        val invalidCast: Int? = data as? Int
        println(invalidCast) // null

        //Note here, we are only casting here meaning "Is the value stored in data already an Int? If yes,
        // cast it. Otherwise, return null."
        //It does convert it, meaning from double to Int or so, for that we use toInt, toDouble etc.
        //method.


        // =========================================================
        // Unsafe Cast ( as )
        // =========================================================

        /*
         * 'as' performs unsafe cast.
         *
         * Throws:
         * ClassCastException
         * if conversion fails.
         */
       //  val number = data as Int
        // ❌ Crash -> ClassCastException


        // =========================================================
        // 9. Nullable Collections
        // =========================================================

        /*
         * There is a BIG difference between:
         *
         * List<String?>
         * and
         * List<String>?
         */


        // List itself NOT null
        // Elements CAN be null

        val list1: List<String?> =
            listOf("A", null, "B")


        // List itself can be null
        // Elements cannot be null

        val list2: List<String>? = null

        /*
         * Read carefully:
         *
         * List<String?>
         * -> nullable ITEMS
         *
         * List<String>?
         * -> nullable LIST
         */


        // =========================================================
        // 10. Filtering Null Values
        // =========================================================

        val numbers: List<Int?> = listOf(1, 2, null, 4)

        /*
         * filterNotNull()
         * removes null values
         * and converts type to non-nullable.
         */

        val nonNullNumbers: List<Int> =
            numbers.filterNotNull()


        println(nonNullNumbers)


        // =========================================================
        // 11. Platform Types (Java Interoperability)
        // =========================================================

        /*
         * Kotlin interacts with Java using:
         *
         * Platform Types
         * Represented internally as:
         * String!
         */


        /*
         * Problem:
         *
         * Kotlin cannot always know whether
         * Java value is nullable or not.
         * So compiler trusts developer.
         */


        /*
         * Example:
         *
         * Java:
         *
         * String getName()
         *
         * Kotlin sees:
         *
         * String!
         */


        /*
         * Platform types are dangerous because:
         * They may throw NPE at runtime.
         */


        // =========================================================
        // 12. Nullable Generic Types
        // =========================================================
        /*
         * Generic types are nullable by default.
         */

        fun <T> printValue(value: T) {
            println(value)
        }

        /*
         * Here T can be nullable.
         */

        /*
         * Restrict generic to non-null type:
         */
        fun <T : Any> printNonNull(value: T) {
            println(value)
        }

        /*
         * T : Any
         * means:
         *
         * T cannot be null
         */


        // =========================================================
        // 13. Chained Safe Calls
        // =========================================================
        data class Address(
            val city: String?
        )

        data class User(
            val address: Address?
        )

        val user: User = User(Address("Mumbai"))

        /*
         * Chain safely through multiple nullable objects.
         */

        val city = user?.address?.city?.length
        println(city)


        // =========================================================
        // 14. let + Elvis Combination
        // =========================================================

        /*
         * Common real-world pattern.
         */
        name?.let {
            println("Name exists: $it")
        } ?: run {
            println("Name is null")
        }


        // =========================================================
        // Quick Revision
        // =========================================================

        /*
         * ?     -> nullable type
         * ?.    -> safe call
         * ?:    -> Elvis operator
         * !!    -> non-null assertion
         * as?   -> safe cast
         * let   -> execute only if non-null
         *
         *
         * String?
         * -> nullable variable
         *
         * lateinit
         * -> initialize later
         *
         * Platform type (String!)
         * -> comes from Java
         */
    }

    private fun controlFlow() {

        // =========================================================
        // Kotlin Control Flow
        // =========================================================

        /*
         * Control Flow determines:
         *
         * - which code executes
         * - when it executes
         * - how many times it executes
         *
         * Main control flow structures:
         *
         * 1. if / else
         * 2. when
         * 3. for loop
         * 4. while loop
         * 5. do-while loop
         * 6. break / continue
         */

        // =========================================================
        // 1. if / else Statement
        // =========================================================

        val age = 20
        if (age >= 18) {
            println("Adult")
        } else {
            println("Minor")
        }

        // =========================================================
        // 2. if as Expression
        // =========================================================

        /*
         * In Kotlin:
         *
         * if can return a value.
         *
         * Unlike Java,
         * Kotlin does not always require ternary operator.
         */

        val result = if (age >= 18) {
            "Eligible"
        } else {
            "Not Eligible"
        }
        println(result)


        // =========================================================
        // Short-Circuit Operators: && and ||
        // =========================================================

        // && (AND): If the first condition is false,
        // the second condition is NOT evaluated.
        val age1 = 20

        if (age1 >= 18 && age1 <= 60) {
            println("Age is between 18 and 60")
        }

        // || (OR): If the first condition is true,
        // the second condition is NOT evaluated.
        val isWeekend = true
        val isHoliday = false

        if (isWeekend || isHoliday) {
            println("Day off")
        }

        // Practical example: Null safety using short-circuit evaluation.
        val name: String? = "Kotlin"

        if (name != null && name.isNotEmpty()) {
            println("Name is not empty: $name")
        }

        // Here, name.isNotEmpty() is evaluated only if name != null.
        // Therefore, name can be safely accessed in the second condition.

        // Remember:
        // A && B -> B runs only when A is true.
        // A || B -> B runs only when A is false.


        // =========================================================
        // 3. when Statement
        // =========================================================

        /*
         * Used when checking:
         *
         * - multiple values
         * - multiple conditions
         * - types
         * - ranges
         *
         * Kotlin alternative to Java switch.
         */

        val number = 2

        when (number) {
            1 -> println("One")
            2 -> println("Two")
            3, 4 -> println("Three or Four")
            else -> println("Other number")
        }

        // =========================================================
        // 4. when as Expression
        // =========================================================

        /*
         * 'when' can also return value.
         */
        val value = when (number) {
            1 -> "One"
            2 -> "Two"
            else -> "Unknown"
        }
        println(value)


        // =========================================================
        // 5. when without Argument
        // =========================================================

        /*
         * Useful for:
         * - multiple conditions
         * - ranges
         * - complex logic
         */

        val x = 15

        when {
            x < 0 ->
                println("Negative")

            x in 1..10 ->
                println("Between 1 and 10")

            x % 2 == 0 ->
                println("Even Number")

            else ->
                println("Other case")
        }


        // =========================================================
        // 6. Type Checking using 'is'
        // =========================================================

        /*
         * 'is' checks object type.
         *
         * Similar to:
         *
         * instanceof (Java)
         */

        val data: Any = "Kotlin"

        when (data) {
            is String ->
                println("String of length ${data.length}")

            is Int ->
                println("Integer")

            else ->
                println("Unknown type")
        }


        // =========================================================
        // when with Ranges and Membership Checks
        // =========================================================

        val number1 = 15

        when (number1) {
            in 1..10 -> println("Number is between 1 and 10")
            in 11..20 -> println("Number is between 11 and 20")
            !in 1..20 -> println("Number is outside 1 to 20")
            else -> println("Number is in an unhandled range")
        }

        // `in` checks whether a value belongs to a range or collection.
        // `!in` checks whether a value does NOT belong to it.

        val fruits1 = listOf("Apple", "Mango", "Orange")

        when ("Mango") {
            in fruits1 -> println("Fruit exists in the list")
            else -> println("Fruit does not exist in the list")
        }

        // =========================================================
        // 7. Advanced Looping & Iteration
        // =========================================================

        /*
         * Kotlin loops are built heavily around:
         *
         * - ranges
         * - iterators
         * - collections
         * - higher-order functions
         *
         * Unlike Java:
         * Kotlin does NOT have:
         *
         * for(initialization; condition; update)
         *
         * syntax.
         */

        // =========================================================
        // 7.1 Range Iteration
        // =========================================================

        /*
         * '..'
         * creates inclusive range.
         *
         * 1..5
         * -> 1,2,3,4,5
         */

        for (i in 1..5) {
            print("$i ")
        }

        println()

        // =========================================================
        // 7.2 until
        // =========================================================

        /*
         * 'until'
         * excludes upper bound.
         *
         * 1 until 5
         * -> 1,2,3,4
         */

        for (i in 1 until 5) {
            print("$i ")
        }
        println()


        // =========================================================
        // 7.3 downTo
        // =========================================================

        /*
         * Reverse iteration.
         */
        for (i in 5 downTo 1) {
            print("$i ")
        }
        println()


        // =========================================================
        // 7.4 step
        // =========================================================

        /*
         * Skips values using interval.
         */
        for (i in 1..10 step 2) {
            print("$i ")
        }
        println()


        // =========================================================
        // 7.5 reversed()
        // =========================================================

        /*
         * Creates reversed progression.
         */

        for (i in (1..5).reversed()) {
            print("$i ")
        }
        println()

        // =========================================================
        // 7.6 Iterating Collections
        // =========================================================

        val fruits = listOf("Apple", "Banana", "Mango")
        /*
         * Direct item iteration.
         */
        for (fruit in fruits) {
            println(fruit)
        }


        // =========================================================
        // 7.7 indices
        // =========================================================

        /*
         * Returns valid index range.
         */

        for (index in fruits.indices) {
            println(
                "Index = $index, Value = ${fruits[index]}"
            )
        }


        /*
         * Internally:
         *
         * fruits.indices
         *
         * roughly becomes:
         *
         * 0 until fruits.size
         */


        // =========================================================
        // 7.8 withIndex()
        // =========================================================

        /*
         * Cleaner way to get:
         * - index
         * - value
         */
        for ((index, value) in fruits.withIndex()) {
            println("$index -> $value")
        }

        /*
         * This uses:
         * destructuring declaration
         */


        // =========================================================
        // Reverse Iteration: Different Approaches
        // =========================================================

        val numbers = listOf(1, 2, 4, 7, 2, 4)

        // 1. Index-based reverse iteration.
        // No reversed-list copy is created.
        // Useful when both the index and value are required.
        for (index in numbers.lastIndex downTo 0) {
            println("Index: $index, Item: ${numbers[index]}")
        }


        // 2. asReversed()
        // Provides a reversed view of the list rather than
        // creating a separate reversed list.
        for (item in numbers.asReversed()) {
            print("$item ")
        }
        println()

        // 3. indices.reversed()
        // Iterates through the valid indexes in reverse order.
        // Useful when you need indexes as well as values.
        for (index in numbers.indices.reversed()) {
            println("Index: $index, Item: ${numbers[index]}")
        }

        // 4. reversed()
        // Creates a new list containing the elements in reverse order.
        val reversedNumbers = numbers.reversed()
        println(reversedNumbers) // [4, 2, 7, 4, 2, 1]


        // =========================================================
        // 7.9 forEach
        // =========================================================

        /*
         * Functional iteration style.
         *
         * Uses lambda internally.
         */

        fruits.forEach {
            println(it)
        }

        /*
         * Equivalent:
         */

        fruits.forEach { fruit ->
            println(fruit)
        }


        /*
         * Important:
         *
         * forEach is NOT exactly same as for loop.
         *
         * Since it uses lambda:
         * - control flow behaves differently
         * - return behavior differs
         */


        // =========================================================
        // 7.10 forEachIndexed
        // =========================================================

        fruits.forEachIndexed { index, value ->
            println("$index -> $value")
        }

        /*
         * Cleaner alternative to:
         *
         * indices + collection[index]
         */

        // =========================================================
        // 7.11 repeat()
        // =========================================================

        /*
         * Executes block fixed number of times.
         */

        repeat(3) {
            println("Executed: $it")
        }

        /*
         * 'it' starts from 0.
         */


        // =========================================================
        // 7.12 Iterator
        // =========================================================

        /*
         * Collections internally use iterators.
         */
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            println(iterator.next())
        }

        /*
         * for loop internally also uses iterator.
         */

        // =========================================================
        // 7.13 Mutation During Iteration
        // =========================================================

        val mutableList = mutableListOf(1, 2, 3)

        /*
         * Dangerous:
         *
         * Modifying collection during iteration
         * may throw:
         *
         * ConcurrentModificationException
         */

        // for (item in mutableList) {
        //
        //     mutableList.remove(item)
        // }


        // =========================================================
        // 7.14 Safe Removal using Iterator
        // =========================================================
        val itr = mutableList.iterator()
        while (itr.hasNext()) {
            val item = itr.next()
            if (item == 2) {
                itr.remove()
            }
        }
        /*
         * Iterator safely updates collection state.
         */


        // =========================================================
        // 7.15 Nested Loops
        // =========================================================

        for (i in 1..3) {
            for (j in 1..3) {
                println("$i $j")
            }
        }


        // =========================================================
        // 7.16 Labels
        // =========================================================

        /*
         * Labels help control nested loop flow.
         */

        outer@
        for (i in 1..3) {
            for (j in 1..3) {
                if (i == 2 && j == 2) {
                    break@outer
                }
                println("$i $j")
            }
        }

        // =========================================================
        // Labeled continue
        // =========================================================

        // Skips the remaining statements in the current iteration
        // of the specified outer loop and continues its next iteration.

        outer@ for (i in 1..3) {
            for (j in 1..3) {
                if (j == 2) {
                    continue@outer
                }

                println("i = $i, j = $j")
            }
        }

        // Output:
        // i = 1, j = 1
        // i = 2, j = 1
        // i = 3, j = 1

        // Without @outer, `continue` would continue only the inner loop.
        // With continue@outer, execution jumps to the next iteration
        // of the outer loop.


        // =========================================================
        // 7.17 Non-local Return in forEach
        // =========================================================

        /*
         * Important senior-level concept.
         *
         * Since forEach is inline,
         * 'return' inside lambda may return
         * from OUTER function itself.
         */

        fun testReturn() {
            listOf(1, 2, 3).forEach {
                if (it == 2) {
                    return
                }
            }

            /*
             * Never reached.
             */
        }

        /*
         * To return only from lambda:
         */
        fun testLocalReturn() {
            listOf(1, 2, 3).forEach label@{
                if (it == 2) {
                    return@label
                }

                println(it)
            }
        }


        // =========================================================
        // 7.18 Progression Internals
        // =========================================================

        /*
         * Kotlin ranges use Progression internally.
         *
         * Example:
         *
         * 1..10 step 2
         *
         * becomes IntProgression.
         */


        /*
         * Types:
         *
         * IntProgression
         * LongProgression
         * CharProgression
         */


        // =========================================================
        // 7.19 Char Iteration
        // =========================================================

        for (c in 'a'..'f') {
            print("$c ")
        }
        println()


        // =========================================================
        // 7.20 Collection vs Sequence Iteration
        // =========================================================

        /*
         * Collections:
         * eager evaluation
         *
         * Sequences:
         * lazy evaluation
         *
         * Important for performance optimization.
         */

        // =========================================================
        // 10. while Loop
        // =========================================================

        /*
         * Executes while condition is true.
         */

        var count = 1

        while (count <= 3) {
            println(count)
            count++
        }


        // =========================================================
        // 11. do-while Loop
        // =========================================================

        /*
         * Executes at least ONCE,
         * even if condition is false.
         */

        var value2 = 5

        do {
            println("Executed once")
            value2++
        } while (value2 < 5)





        // =========================================================
        // 12. break
        // =========================================================

        /*
         * Immediately exits loop.
         */

        for (i in 1..10) {
            if (i == 5) {
                break
            }
            print("$i ")
        }
        println()


        // =========================================================
        // 13. continue
        // =========================================================

        /*
         * Skips current iteration.
         */

        for (i in 1..5) {
            if (i == 3) {
                continue
            }
            print("$i ")
        }
        println()


        // =========================================================
        // 14. Labeled break
        // =========================================================

        /*
         * Used in nested loops.
         */

        outerLoop@
        for (i in 1..3) {
            for (j in 1..3) {
                if (i == 2 && j == 2) {
                    break@outerLoop
                }
                println("$i $j")
            }
        }


        // =========================================================
        // 15. Infinite Loop
        // =========================================================

        /*
         * Loop with condition always true.
         */

        // while (true) {
        //
        // }

        // =========================================================
        // return: Exiting a Function Early
        // =========================================================

        fun printPositiveNumber(number: Int) {
            if (number <= 0) {
                println("Number must be positive")
                return
            }

            println("Positive number: $number")
        }

        printPositiveNumber(10)
        printPositiveNumber(-5)

        // `return` exits the current function immediately.
        // Statements after return are not executed on that path.

        // This is useful for guard clauses: handle invalid or
        // exceptional conditions early, then continue with normal logic.


        // =========================================================
        // Quick Revision
        // =========================================================

        /*
         * if
         * -> conditional execution
         *
         * when
         * -> Kotlin switch replacement
         *
         * for
         * -> iterate fixed/range values
         *
         * while
         * -> executes while condition true
         *
         * do-while
         * -> executes at least once
         *
         * break
         * -> exit loop
         *
         * continue
         * -> skip current iteration
         *
         * ..
         * -> inclusive range
         *
         * until
         * -> excludes upper bound
         *
         * downTo
         * -> reverse iteration
         *
         * step
         * -> jump interval
         */
    }


    // =========================================================
    // Inheritance
    // =========================================================

    private fun inheritance() {

        /*
         * In Kotlin:
         *
         * - classes are final by default
         * - methods are final by default
         * - properties are final by default
         *
         * Meaning:
         *
         * - classes cannot be inherited
         * - methods/properties cannot be overridden
         *
         * To allow inheritance:
         * use 'open'.
         */


        // =========================================================
        // 1. Basic Inheritance
        // =========================================================

        open class Animal {
            var color: String = ""

            open fun eat() {
                println("Animal is eating")
            }
        }

        class Dog : Animal() {
            var breed: String = ""

            fun bark() {
                println("Bark")
            }

            override fun eat() {
                /*
                 * super:
                 * calls parent implementation.
                 */
                super.eat()
                println("Dog is eating")
            }
        }


        // =========================================================
        // 2. Property Overriding
        // =========================================================

        /*
         * Properties can also be overridden.
         */

        open class Vehicle {
            open val type = "Vehicle"
        }

        class Car : Vehicle() {
            override val type = "Car"
        }

        // =========================================================
        // Property Overriding Rules
        // =========================================================

        /*
         * Properties can be overridden
         * similar to functions.
         *
         * Allowed:
         *
         * val -> val
         * val -> var
         * var -> var
         *
         * Not Allowed:
         * var -> val
         *
         * Reason:
         *
         * var provides:
         * - getter
         * - setter
         *
         * val provides:
         * - getter only
         *
         * Child class cannot remove
         * functionality promised by parent.
         */

        // val → var
        open class Parent1 {
            open val name = "Parent"
        }

        class Child1 : Parent1() {
            override var name = "Child"
        }

        // var → val (Invalid)
        /*
            open class Parent {
                open var name = "Parent"
            }

            class Child : Parent() {
                override val name = "Child"
            }
        */


        // =========================================================
        // 3. Qualified super Call
        // =========================================================

        /*
         * Multiple interface inheritance
         * may create method conflicts.
         *
         * In such cases:
         * overriding becomes mandatory.
         */

        /* interface A {
             fun show() {
                 println("A")
             }
         }

         interface B {
             fun show() {
                 println("B")
             }
         }

         class Test : A, B {

             override fun show() {

                 *//*
                 * Qualified super call.
                 *
                 * NOT generics.
                 *//*

                super<A>.show()
                super<B>.show()
            }
        }*/


        // =========================================================
        // 4. Inheritance using Primary Constructor
        // =========================================================

        /*
         * If parent class has constructor params,
         * child class must initialize parent.
         */

        open class Person(
            val name: String
        )

        class Employee(
            name: String,
            val role: String
        ) : Person(name)

        // =========================================================
        // 5. Initialization Order
        // =========================================================

        /*
         * During object creation,
         * parent class is always initialized first.
         *
         * Initialization order:
         *
         * 1. Parent property initialization
         * 2. Parent init block
         * 3. Child property initialization
         * 4. Child init block
         * 5. Child secondary constructor body
         */

        open class Parent {

            private val parentProperty =  println("Parent Property")

            init {
                println("Parent Init")
            }
        }

        class Child() : Parent() {

            private val childProperty = println("Child Property")

            init {
                println("Child Init")
            }

            constructor(value: String) : this() {
                println("Child Constructor Body")
            }
        }

        Child("Test")

        /*
         * Output:
         *
         * Parent Property
         * Parent Init
         * Child Property
         * Child Init
         * Child Constructor Body
         *
         * Parent initialization always
         * completes before child initialization.
         */

        // =========================================================
        // Why Parent Initializes First
        // =========================================================

        /*
         * Child object contains
         * parent object internally.
         *
         * Therefore parent must be fully
         * initialized before child
         * initialization begins.
         *
         * Conceptually:
         *
         * Child
         *  └── Parent
         *
         * Parent creation always happens first.
         */


        // =========================================================
        // 6. Calling Open Members in Constructor
        // =========================================================

        /*
         * Dangerous practice.
         *
         * During parent initialization,
         * child object may not be fully initialized.
         */

        open class Base {

            init {
                // Avoid calling open members here.
                test()
            }

            open fun test() {
                println("Base test")
            }
        }

        class Derived : Base() {

            private val value = "Initialized"

            override fun test() {
                /*
                 * May execute before:
                 * value initialization.
                 */
                println(value)
            }
        }


        // =========================================================
        // 7. Access Modifiers (Visibility Modifiers)
        // =========================================================

        /*
         * Access modifiers control WHO can see and use your code.
         * Think of them as privacy settings.
         *
         * The 4 Modifiers in Kotlin:
         * 1. public    -> Open to EVERYONE (default in Kotlin)
         * 2. protected -> Open to FAMILY ONLY (Parent class + Child classes)
         * 3. internal  -> Open to MODULE ONLY (Everyone inside the same Gradle module)
         * 4. private   -> Open to OWNER ONLY (Only inside the class or file where written)
         *
         * =========================================================
         * Parent & Child Behavior Matrix
         * =========================================================
         * Member Type | Can Parent access? | Can Child access? | Can Outside World access?
         * -----------------------------------------------------------------------------
         * public      | YES                | YES               | YES
         * protected   | YES                | YES               | NO
         * internal    | YES                | YES (same module) | YES (if same module)
         * private     | YES                | NO                | NO
         */

        // ---------------------------------------------------------
        // 1. PUBLIC (Default)
        // ---------------------------------------------------------
        open class PublicParent {           // Class Level: Anyone can access/create
            val publicName = "John"        // Field Level: Anyone can read
            fun publicGreet() {            // Method Level: Anyone can call
                println("Hello from Public")
            }
        }

        class PublicChild : PublicParent() {
            fun test() {
                println(publicName)  // Child CAN access property
                publicGreet()        // Child CAN access method
            }
        }

        // ---------------------------------------------------------
        // 2. PROTECTED
        // ---------------------------------------------------------
        open class ProtectedParent {
            protected val protectedSecret = "Parent's family secret" // Field Level
            protected fun protectedMethod() {                        // Method Level
                println("Protected method inside parent")
            }
        }

        class ProtectedChild : ProtectedParent() {
            fun test() {
                println(protectedSecret) // Child CAN access parent's protected field
                protectedMethod()        // Child CAN call parent's protected method
            }
        }

        // Outside code CANNOT access protected members:
        // val p = ProtectedParent()
        // p.protectedSecret // COMPILER ERROR: Cannot access 'protectedSecret'

        // ---------------------------------------------------------
        // 3. INTERNAL (Same Gradle Module)
        // ---------------------------------------------------------
        open class InternalParent {         // Note: At top-level, can be marked 'internal class'
            internal val moduleData = 100  // Field Level: Visible anywhere in same module
            internal fun moduleTask() {    // Method Level: Visible anywhere in same module
                println("Internal task")
            }
        }

        class InternalChild : InternalParent() {
            fun test() {
                println(moduleData) // Child CAN access if in the same module
                moduleTask()       // Child CAN call if in the same module
            }
        }

        // ---------------------------------------------------------
        // 4. PRIVATE
        // ---------------------------------------------------------
        open class PrivateParent {
            private val pinCode = 1234      // Field Level: Visible ONLY inside PrivateParent
            private fun privateVault() {    // Method Level: Visible ONLY inside PrivateParent
                println("Private vault: $pinCode")
            }

            fun accessOwnPrivate() {
                println(pinCode)    // Parent CAN access its own private field
                privateVault()     // Parent CAN call its own private method
            }
        }

        class PrivateChild : PrivateParent() {
            fun test() {
                // println(pinCode)   // COMPILER ERROR: Child CANNOT access parent's private field!
                // privateVault()     // COMPILER ERROR: Child CANNOT call parent's private method!
            }
        }


        // =========================================================
        // 8. Visibility Restriction Rule
        // =========================================================

        /*
         * Overridden member visibility
         * cannot be reduced.
         */

        open class Shape {
            open fun draw() {}
        }

        class Circle : Shape() {
            /*
             * Invalid:
             * private override fun draw()
             */

            override fun draw() {}
        }


        // =========================================================
        // 9. Final Override
        // =========================================================

        /*
         * override members remain open
         * unless explicitly marked final.
         */

        open class Bird {
            open fun fly() {}
        }

        open class Eagle : Bird() {
            final override fun fly() {
                println("Eagle flying")
            }
        }

        /*
         * Cannot override fly() further.
         */


        // =========================================================
        // Quick Revision
        // =========================================================

        /*
         * open
         * -> allows inheritance/override
         *
         * override
         * -> overrides parent member
         *
         * super
         * -> access parent implementation
         *
         * super<Type>
         * -> resolve multiple inheritance conflict
         *
         * classes are final by default
         *
         * properties can also be overridden
         *
         * constructor initialization order matters
         *
         * avoid calling open members
         * inside constructors/init blocks
         */
    }


    // =========================================================
    // Inheritance with Primary & Secondary Constructor
    // =========================================================
    private fun inheritanceWithPrimaryAndSecondaryConstructor() {

        // =========================================================
        // 1. Secondary Constructor Inheritance
        // =========================================================

        open class Animal {
            var color: String = ""

            constructor(color: String) {
                this.color = color
                println("Animal color: $color")
            }
        }

        class Dog : Animal {

            var breed: String = ""

            constructor(
                color: String,
                breed: String
            ) : super(color) {

                this.breed = breed

                println("Dog breed: $breed")
            }
        }

        val dog =
            Dog("Black", "Pug")


        // =========================================================
        // 2. Parent Constructor Rules
        // =========================================================

        /*
         * If parent has primary constructor:
         * child must initialize it.
         *
         * If child uses secondary constructor:
         * it must delegate to:
         *
         * - this(...)
         * OR
         * - super(...)
         */


        // =========================================================
        // 3. Primary Constructor Inheritance
        // =========================================================

        open class Person(
            val name: String,
            val age: Int
        )

        class Developer(
            name: String,
            age: Int,
            val tech: String
        ) : Person(name, age)


        // =========================================================
        // 4. this Constructor Delegation
        // =========================================================

        /*
         * 'this(...)' calls another
         * constructor of the SAME class.
         *
         * Used to reuse initialization logic.
         *
         * Every secondary constructor
         * must delegate to:
         *
         * - this(...)
         * OR
         * - super(...)
         */

        class Student(
            val name: String
        ) {

            var age: Int = 0

            init {
                println("Init Block")
            }

            constructor(
                name: String,
                age: Int
            ) : this(name) {

                println("Secondary Constructor")

                this.age = age
            }
        }

        Student("Rahul", 25)

        /*
         * Execution Flow:
         *
         * Student("Rahul", 25)
         *
         * ->
         * Secondary Constructor called
         *
         * ->
         * this(name)
         *
         * ->
         * Primary Constructor executes
         *
         * ->
         * init block executes
         *
         * ->
         * Control returns to
         * Secondary Constructor
         *
         * ->
         * Remaining constructor code runs
         */


        // =========================================================
        // 5. init Block
        // =========================================================

        /*
         * init block executes
         * during object creation.
         *
         * Runs before
         * secondary constructor body.
         */

        class User(
            val username: String
        ) {

            init {
                println("Init block: $username")
            }

            constructor(
                username: String,
                age: Int
            ) : this(username) {

                println("Secondary constructor")
            }
        }


        // =========================================================
        // 6. Constructor Execution Flow
        // =========================================================

        /*
         * For inherited classes:
         *
         * Parent constructor
         * ->
         * Parent init
         * ->
         * Child init
         * ->
         * Child constructor body
         */


        // =========================================================
        // Quick Revision
        // =========================================================

        /*
         * child constructor must initialize parent
         *
         * secondary constructor:
         * -> this(...)
         * -> super(...)
         *
         * init block:
         * executes during object creation
         *
         * parent initialization
         * always happens first
         */
    }


    // =========================================================
    // Abstract Class & Interface
    // =========================================================
    private fun abstractAndInterface() {

        // =========================================================
        // 1. Abstract Class
        // =========================================================

        /*
         * Abstract classes:
         *
         * - cannot be instantiated
         * - can contain abstract members
         * - can contain concrete members
         * - can contain state
         * - can have constructors
         *
         * abstract members are implicitly open.
         */

        abstract class Animal {

            abstract val type: String

            abstract fun sound()

            fun sleep() {
                println("Sleeping")
            }
        }

        class Dog : Animal() {

            override val type = "Dog"

            override fun sound() {
                println("Bark")
            }
        }


        // =========================================================
        // 2. Abstract Property
        // =========================================================

        /*
         * Abstract properties:
         *
         * - cannot have initialization
         * - must be overridden
         */

        abstract class Shape {
            abstract val area: Double
        }


        // =========================================================
        // 3. Interface Basics
        // =========================================================

        /*
         * Interfaces define behavior contract.
         *
         * Interfaces can contain:
         *
         * - abstract functions
         * - concrete functions
         * - abstract properties
         *
         * Interfaces cannot store state.
         */

        /* interface Drivable {

             fun drive()

             fun stop() {
                 println("Stopped")
             }
         }

         class Car : Drivable {
             override fun drive() {
                 println("Driving")
             }
         }*/


        // =========================================================
        // 4. Interface Property Rules
        // =========================================================

        /*    interface Engine {

                *//*
             * No backing field allowed.
             *//*

            val fuelType: String
                get() = "Petrol"
        }
*/

        // =========================================================
        // 5. Multiple Interface Inheritance
        // =========================================================

        /*  interface A {

              fun show() {
                  println("A")
              }
          }

          interface B {

              fun show() {
                  println("B")
              }
          }

          class Test : A, B {

              *//*
             * Mandatory override:
             * because method conflict exists.
             *//*

            override fun show() {

                super<A>.show()
                super<B>.show()
            }
        }
*/

        // =========================================================
        // 6. Abstract Class vs Interface
        // =========================================================

        /*
         * Abstract class:
         *
         * - shared state
         * - base implementation
         * - common hierarchy
         *
         * Interface:
         *
         * - capability
         * - contract
         * - multiple inheritance
         */


        // =========================================================
        // 7. Interface Delegation
        // =========================================================
        // 7. Class Delegation (`by` clause)
        // =========================================================

        /*
         * Class Delegation reduces boilerplate delegation code.
         * "Favor composition over inheritance."
         */

        val manager = Manager(FilePrinter())
        manager.print() // Delegates directly to FilePrinter's print()


        // =========================================================
        // 8. Functional Interface (SAM - Single Abstract Method)
        // =========================================================

        /*
         * 'fun interface' contains exactly ONE abstract method.
         * Enables clean SAM conversion (lambda instantiation).
         */

        val listener = ClickListener {
            println("Button Clicked via SAM conversion!")
        }
        listener.onClick()


        // =========================================================
        // 9. Sealed Class (Restricted Hierarchy)
        // =========================================================

        /*
         * Restricted class hierarchy for compile-time safety in 'when'.
         * (See sealedClassesExample() for full network state demo).
         */

        fun handleResult(state: ResultState) {
            when (state) {
                is ResultState.Success -> println("Success")
                is ResultState.Error -> println("Error: ${state.message}")
            } // Exhaustive: No 'else' required!
        }

        handleResult(ResultState.Success)
        handleResult(ResultState.Error("Network Timeout"))


        // =========================================================
        // 10. open vs abstract
        // =========================================================

        /*
         * open
         * -> can override
         *
         * abstract
         * -> must override
         */


        // =========================================================
        // Quick Revision
        // =========================================================

        /*
         * abstract class
         * -> shared state + behavior
         *
         * interface
         * -> contract/capability
         *
         * interfaces support:
         * multiple inheritance
         *
         * interfaces cannot store state
         *
         * abstract members are implicitly open
         *
         * super<Type>
         * resolves method conflicts
         *
         * sealed class
         * -> restricted hierarchy
         *
         * fun interface
         * -> SAM support
         */
    }

    // Support interfaces & classes for abstractAndInterface()
    interface Printer {
        fun print()
    }

    class FilePrinter : Printer {
        override fun print() {
            println("Printing from file")
        }
    }

    class Manager(private val printer: Printer) : Printer by printer

    fun interface ClickListener {
        fun onClick()
    }

    sealed class ResultState {
        object Success : ResultState()
        data class Error(val message: String) : ResultState()
    }


    private fun extensionFunction() {
        var student = Studentt()
        println("Pass status: " + student.hasPassed(57))

        println("Scholarship Status: " + student.isScholar(57))

    }

    fun Studentt.isScholar(marks: Int): Boolean {
        return marks > 95
    }

    class Studentt {         // OUR OWN CLASS

        fun hasPassed(marks: Int): Boolean {
            return marks > 40
        }
    }


    private fun predicates() {
        val myNumbers = listOf(2, 3, 4, 6, 23, 90)
        // We have defined this common predicate because it is used multiple places as it > 10
        // for below functions, but note here that we don't use it > 10 because at declaration
        // compiler doesn't know what it keyword represents, but if we use it like
        // myNumbers.all( {it > 10} ) it knows it represent each element of list.

        //val myPredicate = { it > 10 }

        val myPredicate = { num: Int -> num > 10 }


        val check1 = myNumbers.all(myPredicate)       // Are all elements greater than 10?
        println(check1)

        val check2 =
            myNumbers.any(myPredicate)         // Does any of these elements satisfy the predicate?
        println(check2)

        val totalCount: Int =
            myNumbers.count(myPredicate) // Number of elements that satisfy the predicate.
        println(totalCount)

        val num =
            myNumbers.find(myPredicate)     // Returns the first number that matches the predicate
        println(num)

        val last = myNumbers.last(myPredicate) // Returns the last number that matches the predicate
        println(last)

    }

    private fun filterAndMap() {
        /** FILTER
         * Returns a list containing only elements matching the given [predicate]/ condition
         * */

        /** MAP
         * Returns a list containing the results of applying the given [transform]/condition function
         * to each element in the original collection
         * */

        val myNumbers: List<Int> = listOf(2, 3, 4, 6, 23, 90)
        //So here, it will check each element in list and those satisfying below condition
        // will be returned, in this case 2,3,4,6 as they are less than 10
        val mySmallNums = myNumbers.filter { it < 10 }    // OR { num -> num < 10 }
        for (num in mySmallNums) {
            println(num)
        }


        Log.d(TAG, "filterAndMap: " + mySmallNums)

        //Map has the power to transform each element from our list based on our given condition.
        //Here we are squaring each element hence, it return complete list but each element
        //transformed as 4, 9, 16 ans so on.
        val mySquaredNums = myNumbers.map { it * it }     // OR { num -> num * num }
        for (num in mySquaredNums) {
            println(num)
        }

        // Here filter and map can be used in combinations as well, example, here firstly filter
        // will return list containing elements whose name property starts with s, so its returned
        // result will be list of these elements ->  Pperson(10, "Steve"), Pperson(17, "Sam")
        // Now on this map is applied which transform this list to return name property only.
        // Hence result after map will be a list containing names only i.e. ("Steve", "Sam")
        var people = listOf<Pperson>(Pperson(10, "Steve"), Pperson(23, "Annie"), Pperson(17, "Sam"))
        var names = people.filter { person -> person.name.startsWith("S") }.map { it.name }

        for (name in names) {
            println(name)
        }

    }

    class Pperson(var age: Int, var name: String) {
        // Some other code..
    }

    private fun collections() {
        /*----List------*/
        //Note here, all 3 --> mutableListOf, arrayListOf and ArrayList are same, we can use anyone.

        //    var list = mutableListOf<String>()  // Mutable, No Fixed Size, Can Add or Remove Elements
        //    var list = arrayListOf<String>()    // Mutable, No Fixed Size, Can Add or Remove Elements
        var list = ArrayList<String>()      // Mutable, No Fixed Size, Can Add or Remove Elements
        list.add("Yogi")        // 0
        list.add("Manmohan")    // 1
        list.add("Vajpayee")    // 2


        list.asSequence()

        val generateSequence = generateSequence(1) { it * 2 }
        val take = generateSequence.take(5)
        val words = "The quick brown fox jumps over the lazy dog".split(" ")

        //    list.remove("Manmohan")
        //    list.add("Vajpayee")

        list[1] = "Modi"

        for (element in list) {             // Using individual elements (Objects)
            println(element)
        }


        /*----Map------*/
        var map = mapOf<Int, String>(4 to "abc", 1 to "xyz") // Immutable, READ only,Fixed Size
        //Note here key sequence order doesn't matter, it can be written in any order.

        // Map Tutorial: Key-Value pair
        //    var myMap = HashMap<Int, String>()      // Mutable, READ and WRITE both, No Fixed Size
        //    var myMap = mutableMapOf<Int, String>() // Mutable, READ and WRITE both, No Fixed Size
        var myMap = hashMapOf<Int, String>()      // Mutable, READ and WRITE both, No Fixed Size

        myMap.put(4, "Yogi")
        myMap[43] = "Manmohan"
        myMap.put(7, "Vajpayee")

        myMap.put(43, "Modi")

        for (key in myMap.keys) {
            println("Element at $key = ${myMap[key]}")  // myMap.get(key)
        }

        /*----Set------*/

        // "Set" contains unique elements
        // "HashSet" also contains unique elements but sequence is not guaranteed in output

        var mySet =
            mutableSetOf<Int>(2, 54, 3, 1, 0, 9, 9, 9, 8)  // Mutable Set, READ and WRITE both
//    var mySet = hashSetOf<Int>( 2, 54, 3, 1, 0, 9, 9, 9, 8)     // Mutable Set, READ and WRITE both

        mySet.remove(54)
        mySet.add(100)

        for (element in mySet) {
            println(element)
        }

        //Few utility functions to be applied on collections


        var mutableListOf = mutableListOf<Int>(1, 2, 3, 87, 8, 4, 2, 1, 9)

        //distinct() remove all duplicate elements from the list.
        val distinctList = mutableListOf.distinct()
        Log.d(TAG, "distinct list : $distinctList")


        //To convert an array or list into String, we use joinToString()

        //Example 1
        val someKotlinCollectionFunctions = listOf(
            "distinct", "map", "isEmpty", "contains", "filter", "first", "last", "reduce",
            "single", "joinToString"
        )

        val joinToString = someKotlinCollectionFunctions.joinToString(
            separator = ", ",
            prefix = "Kotlin has many collection functions like: ",
            postfix = "and they are awesome.",
            limit = 3,
            truncated = "etc "
        )

        Log.d(TAG, "joinToString Example 1: " + joinToString)

        //Example 2
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        println(numbers.joinToString()) // 1, 2, 3, 4, 5, 6
        println(numbers.joinToString(prefix = "[", postfix = "]")) // [1, 2, 3, 4, 5, 6]
        println(numbers.joinToString(prefix = "<", postfix = ">", separator = "•")) // <1•2•3•4•5•6>

        val chars = charArrayOf(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q'
        )
        println(chars.joinToString(limit = 5, truncated = "...!") {
            it.uppercaseChar().toString()
        }) // A, B, C, D, E, ...!


        // fold and reduce
        /**
         * reduce() and fold() applies the provided operation to the collection elements
         * sequentially and return the accumulated result. The operation takes two arguments: the
         * previously accumulated value and the collection element.
         * The difference between the two functions is that fold() takes an initial value and uses
         * it as the accumulated value on the first step, whereas the first step of reduce() uses
         * the first and the second elements as operation arguments on the first step.
         * */

        val numbers1 = listOf(5, 2, 10, 4)

        //Here at first step, the sum is taken as 5 and element as 2 hence this will be
        // 5,2 -> 5+2*2, for next step, now sum becomes 9, so it will be 9, 10 -> 9+10*2 and so on.
        numbers1.reduce({ sum, element -> sum + element * 2 })


        //or this, both are same thing
        numbers1.reduce { sum, element -> sum + (element * 2) }
        //Output -->  37 which is correct as per reduce function but as per our needs this
        // is not the expected result, fold is correct candidate to use.

        //Here for fold, at first step, the sum is taken as 0 which is already initialized, it is
        // necessary to initialize it with whatever values we want to initialize with, so it is
        // 0,5 -> 0+5*2, for next step, now sum becomes 10, so it will be 10, 2 -> 10+2*2 and so on.
        numbers1.fold(0) { sum, element -> sum + (element * 2) }
        //Output -->  42 which is correct.

        //If we want to perform operations in list from right or reverse order then use,
        //foldRight and reduceRight rest all syntax same.

        // You can also apply operations that take element indices as parameters. For this purpose,
        // use functions reduceIndexed() and foldIndexed() passing element index as the first
        // argument of the operation.
        val numbers2 = listOf(5, 2, 10, 4)
        val sumEven =
            numbers.foldIndexed(0) { idx, sum, element -> if (idx % 2 == 0) sum + element else sum }
        println(sumEven)

        val sumEvenRight =
            numbers.foldRightIndexed(0) { idx, element, sum -> if (idx % 2 == 0) sum + element else sum }
        println(sumEvenRight)

        //Important
        /**
         * All reduce operations throw an exception on empty collections. To receive null instead,
         * use their *OrNull() counterparts
         * reduceOrNull(), reduceRightIndexedOrNull() and so on.
         * */


        //find and single
        /**
         * You can find a particular element from a list of elements that is satisfying a particular
         * condition by using find and single in Kotlin. For example, out of a list of students, you
         * can find the student having roll number 5.The find returns the first element matching the
         * given condition or null if no such element was found.While single returns the single
         * element matching the given condition or it will throw an exception if there are more than
         * one matching element or no matching element in the list.
         * */

        //Example 1
        val simpleUsers = arrayOf(
            SimpleUser(id = 1, name = "Amit"),
            SimpleUser(id = 2, name = "Ali"),
            SimpleUser(id = 3, name = "Sumit"),
            SimpleUser(id = 4, name = "Himanshu")
        )


        val userWithId3 = simpleUsers.single { it.id == 3 }
        print(userWithId3) // User(id=3, name=Sumit)

        val userWithId1 = simpleUsers.find { it.id == 1 }
        print(userWithId1) // User(id=1, name=Amit)


        //Example 2
        val numbers4 = listOf(1, 2, 3, 4, 5, 6, 7)
        val firstOdd = numbers4.find { it % 2 != 0 } // 1
        val lastEven = numbers4.findLast { it % 2 == 0 }   // 6


        /*union and intersection*/
        val listOne = listOf(1, 2, 3, 3, 4, 5, 6)
        val listTwo = listOf(2, 2, 4, 5, 6, 7, 8)

        val union = listOne.union(listTwo) // [1, 2, 3, 4, 5, 6, 7, 8]
        Log.d(TAG, "union: $union")

        val intersect = listOne.intersect(listTwo)
        Log.d(TAG, "intersect: $intersect")


        //Break your list into multiple sublists of smaller size
        val numList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val chunkedLists = numList.chunked(3)
        print(chunkedLists) // [[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]


        val colors = listOf("red", "brown", "grey")
        val animals = listOf("fox", "bear", "wolf")

        colors.zip(animals)
    }

    fun arrayExamples() {

        // ============================
        // 🌟 ARRAYS IN KOTLIN - FULL REFERENCE
        // ============================
        //
        // Arrays are fixed-size, index-based containers that store multiple values of the same type.
        // ✅ Common in: DSA (for algorithms, matrix, searching, sorting)
        // ✅ Common in: Android (static resources like constants, days, etc.)
        //
        // Key Points:
        // - Zero-based indexing
        // - Fixed size (cannot be resized)
        // - Mutable elements (you can change element values)
        // - Supports both object and primitive storage (via special types)

        // ----------------------------------------------------
        // 1️⃣ DIFFERENT WAYS TO DECLARE ARRAYS
        // ----------------------------------------------------
        // 1. arrayOf() → Most commonly used way
        val arr1 = arrayOf(1, 2, 3, 4, 5)
        // Type automatically inferred as Array<Int>
        // Stores boxed Integers (not primitives)

        // 2. arrayOf<Type>() → Explicitly specify the type
        val arr2 = arrayOf<String>("Kotlin", "Java", "Python")
        // Type-safe declaration — useful when type clarity matters

        // 3. Array(size) { init } → Dynamic initialization using lambda
        val arr3 = Array(5) { i -> i * i }
        // Creates array [0, 1, 4, 9, 16]
        // - 'i' represents the index
        // - The expression 'i * i' defines the initial value at each index
        // 🔸 Very useful for DSA when pre-filling array with computed values (like squares, Fibonacci seeds, etc.)

        // ➕ Extra Example:
        val arr4 = Array(5) { i -> "Hello $i" }
        // Here, input is index (i) → output can be *anything* (String, Boolean, Object)
        // This shows the lambda can produce any type result, not just numeric.
        // Very flexible and often used when generating test data, dummy objects, etc.

        // 4. Empty array using emptyArray<Type>()
        val emptyArr = emptyArray<String>()
        // Creates an empty array with size = 0
        // Use when array will be filled later dynamically
        //
        // 🔸 But since arrays are fixed-size, you *cannot add elements* later directly.
        // To "add" items, you’d need to create a new array:
        //     val newArr = emptyArr + "Kotlin"
        // Each `+` operation creates a *new* array with the added element.
        //
        // 🧠 Why empty arrays exist:
        // - Useful for safe initialization (avoid using null arrays)
        // - Often used for function parameters or placeholders when no data yet
        // - Avoids NullPointerExceptions
        // Example: fun loadData(data: Array<String> = emptyArray())

        // 5. Primitive arrays (Recommended for performance & DSA)
        val intArr = intArrayOf(10, 20, 30, 40)
        val doubleArr = doubleArrayOf(1.1, 2.2, 3.3)
        val floatArr = floatArrayOf(1.5f, 2.5f, 3.5f)
        val charArr = charArrayOf('A', 'B', 'C')
        val booleanArr = booleanArrayOf(true, false, true)
        // ⚡ These map directly to Java primitives → int[], double[], etc.
        // ⚡ Use in algorithms or loops for better memory and  speed
        // ⚠️ Cannot store nulls since primitives can’t be null
        //
        // 🧩 Note:
        // Kotlin uses wrapper classes (Int, Double, etc.) instead of raw primitives in `Array<T>`
        // But internally, for optimization, `IntArray`, `DoubleArray` etc. map to primitive types.
        // So:
        //     Array<Int> → boxed integers (slower, flexible)
        //     IntArray   → unboxed primitives (faster, fixed type)
        //
        // Hence Kotlin provides both versions — one for generic use, one for performance.


        // 💡 BONUS: Dynamic Primitive Array Initialization
        // ----------------------------------------------------
        // While intArrayOf() is for pre-filled known data,
        // IntArray(size) { index -> value } lets you generate elements dynamically using logic.
        // Similar to Array(size) { init } but optimized for primitives (no boxing).

        // 1️⃣ Pre-filled
        val fixed = intArrayOf(1, 2, 3)

        // 2️⃣ Generated dynamically
        val generated = IntArray(5) { i -> i + 1 }  // [1, 2, 3, 4, 5]
        // 'i' is the index → You can generate values based on it.
        // Useful for DSA or initializing arrays with patterns or computed values.

        // 3️⃣ Boxed version (Array<Int>)
        val boxed = Array(5) { i -> i + 1 }  // [1, 2, 3, 4, 5] but as Array<Int>
        // Same logic, but stored as objects (slightly slower than IntArray).

        // ✅ Use IntArray when working with large numeric datasets or DSA
        // ✅ Use Array<Int> when nullability, generics, or interoperability are required

        // 6. Mixed-type array using arrayOf<Any>()
        val mixedArr = arrayOf<Any>(1, "Kotlin", 3.14, true)
        // Used when storing heterogeneous data (less common in DSA, more in dev scenarios)

        // ----------------------------------------------------
        // 2️⃣ ACCESS & MODIFY ELEMENTS
        // ----------------------------------------------------
        println("First element: ${arr1[0]}")
        arr1[1] = 99
        println("Updated element: ${arr1[1]}")

        // Loop through elements
        for (value in arr1) println(value)

        // Loop using indices
        for (i in arr1.indices) println("Index $i = ${arr1[i]}")

        // Loop with index + value
        for ((index, value) in arr1.withIndex()) {
            println("arr1[$index] = $value")
        }

        // ----------------------------------------------------
        // 3️⃣ COMMON OPERATIONS
        // ----------------------------------------------------
        println("Array size: ${arr1.size}")
        println("Contains 99: ${arr1.contains(99)}")
        println("Index of 99: ${arr1.indexOf(99)}")

        // Convert to List and back
        val list = arr1.toList()
        val newArray = list.toTypedArray()

        // Sorting and Reversing
        val sorted = arr1.sortedArray()
        val reversed = arr1.reversedArray()
        println("Sorted: ${sorted.joinToString()}")
        println("Reversed: ${reversed.joinToString()}")

        // ----------------------------------------------------
        // 4️⃣ NULLABLE ARRAYS (Very Important)
        // ----------------------------------------------------
        val nullableArray: Array<Int?> = arrayOf(1, null, 3, null, 5)
        // Array elements can hold null values (boxed type only)
        for (item in nullableArray) {
            // Use Elvis operator to safely handle nulls
            println(item ?: "Null Found")
        }

        // Example: Replace nulls with default value
        val nonNullArray = nullableArray.map { it ?: 0 }.toTypedArray()
        println("After replacing nulls: ${nonNullArray.joinToString()}")

        // ----------------------------------------------------
        // 5️⃣ DSA USAGE EXAMPLES
        // ----------------------------------------------------
        // Arrays form the foundation for matrices, sorting, searching, etc.

        // 2D Array (Matrix)
        val matrix = Array(3) { IntArray(3) } // 3x3 zero matrix
        matrix[1][2] = 7
        println("Matrix[1][2] = ${matrix[1][2]}")

        // Example: Sum of all elements
        var sum = 0
        for (num in arr1) sum += num
        println("Sum of arr1 = $sum")

        // ----------------------------------------------------
        // 6️⃣ KOTLIN ARRAY vs JAVA ARRAY
        // ----------------------------------------------------
        //
        // Kotlin Array  → Generic class: Array<T>
        // Java Array    → Built-in type: int[], String[]
        //
        // ⚠️ Kotlin arrays are invariant: Array<Int> ≠ Array<Number>
        // ✅ Java arrays are covariant: Integer[] is a subtype of Object[]
        //
        // Kotlin’s array types:
        // - Array<T> (boxed types)
        // - IntArray, DoubleArray (primitives)
        //
        // Example:
        // val arr = arrayOf(1, 2, 3)      // Kotlin Array<Int>
        // int[] arr = {1, 2, 3};          // Java int[]
        //
        // Interoperability:
        // Kotlin auto-converts to Java array when calling Java methods.

        // ----------------------------------------------------
        // 7️⃣ WHEN TO USE ARRAYS
        // ----------------------------------------------------
        //
        // ✅ When the size is fixed
        // ✅ When working on performance-focused tasks or DSA
        // ✅ When calling Java libraries expecting arrays
        //
        // 🚫 Avoid when data size changes frequently (use MutableList)
        //
        // Android Use Cases:
        // - Static resource lists (e.g., weekDays array)
        // - Predefined dropdown values
        //

        // ----------------------------------------------------
        // 8️⃣ USEFUL EXTENSION FUNCTIONS
        // ----------------------------------------------------
        val arrExample = intArrayOf(1, 2, 3, 4, 5)

        println("Sum = ${arrExample.sum()}")
        println("Average = ${arrExample.average()}")
        println("Max = ${arrExample.maxOrNull()}")
        println("Min = ${arrExample.minOrNull()}")
        println("All even = ${arrExample.all { it % 2 == 0 }}")
        println("Any odd = ${arrExample.any { it % 2 != 0 }}")
        println("Filter > 2 = ${arrExample.filter { it > 2 }}")

        // ----------------------------------------------------
        // 9️⃣ SUMMARY RECAP
        // ----------------------------------------------------
        //
        // ✅ Array<T> → General-purpose array (boxed)
        // ✅ IntArray, DoubleArray, etc. → Primitive arrays (fast for DSA)
        // ✅ Use arrayOf() for known elements
        // ✅ Use Array(size) { init } for generated/computed values
        // ✅ Use emptyArray() when you need a placeholder
        // ✅ Use Array<Int?> for nullable data
        // ✅ Prefer Lists when data size is dynamic
        //
        // 🔁 Arrays are building blocks for DSA — mastering them is essential for
        //     memory understanding, indexing, and optimizing algorithm logic.
        //
    }

    private fun higherOrderFunctionsAndLambdas() {

        /*
        * ================================
        *   HIGHER-ORDER FUNCTIONS & LAMBDAS
        * ================================
        *
        * A Higher-Order Function = A function that:
        * 1. Accepts another function as parameter
        * 2. Returns a function
        * 3. OR both
        *
        * This method contains ALL concepts:
        * - Lambda basics
        * - Lambda type notation
        * - Trailing lambda rule
        * - Closures
        * - Function references (::)
        * - Lambda with receiver (concept only, no scope functions demo)
        * - Returning a function
        * - Inline HOF (performance)
        * - Anonymous function (fun() {})
        * - Lambda vs Anonymous return behavior
        */


        // ---------------------------------------------------------
        // 1️⃣ BASIC LAMBDA — ASSIGNED TO A VARIABLE
        // ---------------------------------------------------------
        //
        // A lambda in Kotlin is defined using { }.
        // Inside it:
        //   - Before `->` : parameters (inputs)
        //   - After  `->` : body (output / logic)
        //
        // When assigning lambda to a variable, the variable type must describe type:
        //        (parameter types) -> return type
        //
        // Example:
        //   (Int, Int) -> Int
        //   → takes two Int values and returns an Int.
        //
        // You may let Kotlin infer parameter types OR specify them explicitly.
        // ---------------------------------------------------------
        val lambda: (Int, Int) -> Int = { a, b -> a + b }
        val lambdaTyped: (Int, Int) -> Int = { a: Int, b: Int -> a + b }

        addNumber(3, 6, lambda)


        // ---------------------------------------------------------
        // 2️⃣ DIRECT INLINE LAMBDA PASSING
        // ---------------------------------------------------------
        addNumber(5, 7, { x, y -> x + y })

        // Trailing lambda (Kotlin shortcut)
        addNumber(10, 20) { x, y -> x + y }


        // ---------------------------------------------------------
        // 3️⃣ CLOSURE — Lambda captures external variable
        // ---------------------------------------------------------
        var external = 0
        addTwoNumbers(4, 6) { x, y ->
            external = x + y // modifies outer variable
        }
        Log.d(TAG, "Closure result = $external")


        // ---------------------------------------------------------
        // 4️⃣ SINGLE PARAM LAMBDA → "it"
        // ---------------------------------------------------------
        reverseAndDisplay("Hello") { it.reversed() }


        // ---------------------------------------------------------
        // 5️⃣ FUNCTION REFERENCE (::)
        // Passing a normal function without lambda syntax
        // ---------------------------------------------------------
        addNumber(3, 4, ::sumTwoNumbers)

        // ---------------------------------------------------------
        // 6️⃣ LAMBDA WITH RECEIVER (IMPORTANT CONCEPT)
        // (StringBuilder.() -> Unit)
        //
        // Used heavily in:
        // - apply{}, run{}, with{}, also{}, let{}
        // - Jetpack Compose
        // - Kotlin DSLs
        //
        // Receiver lambda means: "this" refers to the object
        // ---------------------------------------------------------
        val message = buildMessage {
            append("Hello ")
            append("Kotlin")
        }
        Log.d(TAG, "LambdaWithReceiver: $message")


        // ---------------------------------------------------------
        // 7️⃣ TRAILING LAMBDA — SPECIAL CASES
        // ---------------------------------------------------------

        // Case 1:
        // If the LAST parameter of a function is a lambda,
        // Kotlin allows the lambda to be placed *outside* the parentheses.
        // This improves readability, especially when lambda is long.
        doWork(10, 20) { it * 2 }  // cleaner, recommended

        // Case 2:
        // If a function has ONLY ONE parameter and that parameter is a lambda,
        // the parentheses can be removed entirely.
        // This is why many Kotlin library functions look very clean.
        runSimple { Log.d(TAG, "Only lambda parameter") }


        // ---------------------------------------------------------
        // 8️⃣ RETURNING A FUNCTION (HOF)
        // Returns a lambda (Int) -> Int
        // ---------------------------------------------------------
        val multiplier = getMultiplier(5)
        Log.d(TAG, "Multiplier = ${multiplier(4)}") // 20


        // ---------------------------------------------------------
        // 9️⃣ INLINE FUNCTIONS (Performance)
        //
        // Inline avoids creating lambda objects → faster & no memory overhead
        //
        // Used widely in Kotlin stdlib
        // ---------------------------------------------------------
        val inlineResult = operateInline(3, 5) { x, y -> x + y }
        Log.d(TAG, "Inline result = $inlineResult")


        // ---------------------------------------------------------
        // 🔟 ANONYMOUS FUNCTION (fun keyword)
        // Allows explicit return — unlike lambda
        // ---------------------------------------------------------
        val anonResult = addNumber(2, 3, fun(a, b): Int {
            return a + b   // local return allowed
        })
        Log.d(TAG, "AnonymousFunc: $anonResult")


        // ---------------------------------------------------------
        // 1️⃣1️⃣ LAMBDA vs ANONYMOUS — RETURN BEHAVIOR DIFFERENCE
        // ---------------------------------------------------------

        // Lambda → "return" CANNOT be used normally (non-local return error)
        testLambda()

        // Anonymous function → return allowed (local)
        testAnonymous()
    }


    /* ============================================================
       SUPPORT FUNCTIONS
       ============================================================ */

    //Here x represent Int value meaning it accepts only Int values, similar for y.
    // But when we pass any function in addNumber function, we must also know the type
    // that addNumber will be accepting. For func variable, its type is (Int, Int) -> Int
    // means any function with 2 int args and return type as int is accepted.
    private fun addNumber(x: Int, y: Int, func: (Int, Int) -> Int): Int {
        return func(x, y)
    }

    private fun reverseAndDisplay(str: String, func: (String) -> String) {
        val reversed = func(str)
        Log.d(TAG, "reverseAndDisplay: " + reversed)
    }

    // For function reference example (::sumTwoNumbers)
    private fun sumTwoNumbers(a: Int, b: Int): Int = a + b

    // Higher-order: passes lambda unit-return
    private fun addTwoNumbers(a: Int, b: Int, action: (Int, Int) -> Unit) {
        // High Level Function with Lambda as Parameter
        action(a, b)
    }

    // Lambda with receiver example
    private fun buildMessage(builderAction: StringBuilder.() -> Unit): String {
        val sb = StringBuilder()
        sb.builderAction() // calling receiver lambda
        return sb.toString()
    }


    // Trailing lambda helper
    private fun doWork(a: Int, b: Int, operation: (Int) -> Int): Int {
        return operation(a + b)
    }

    // Only lambda parameter
    private fun runSimple(action: () -> Unit) = action()


    // Returning a function
    private fun getMultiplier(factor: Int): (Int) -> Int {
        return { num -> num * factor }
    }


    // Inline HOF example
    private inline fun operateInline(a: Int, b: Int, op: (Int, Int) -> Int): Int {
        return op(a, b)
    }


    // Lambda return behavior demonstration
    private fun testLambda() {
        addNumber(2, 3) { x, y ->
            // return x + y ❌ NOT ALLOWED (non-local return)
            x + y
        }
    }

    private fun testAnonymous() {
        addNumber(2, 3, fun(x, y): Int {
            return x + y  // allowed
        })
    }

    private fun maxValue(a: Int, b: Int): Int = if (a > b) a else b

    private fun addNum(a: Int, b: Int): Int {
        return a + b;
    }

    // =====================================================================================
// 🔥 INLINE FUNCTIONS — COMPLETE GUIDE (From Beginner → Advanced)
// =====================================================================================
// Inline is used ONLY for higher-order functions (functions that take lambdas).
// It removes the runtime overhead of creating lambda objects by copying code at call-site.
// =====================================================================================


// ---------------------------------------------------------
// 1️⃣ WHY INLINE IS NEEDED — BASIC DEMO
// ---------------------------------------------------------

    // Normal higher-order function (NOT inline)
// Creates a lambda object → extra memory & slower
    private fun calculateNormal(a: Int, b: Int, op: (Int, Int) -> Int): Int {
        return op(a, b)
    }

    // Inline version of SAME function
// The function body AND the lambda body are copied directly at the call-site.
    private inline fun calculateInline(a: Int, b: Int, op: (Int, Int) -> Int): Int {
        return op(a, b)
    }

    private fun testInlineBasic() {

        // Case 1 → Normal HOF → allocates lambda object
        val normalResult = calculateNormal(5, 3) { x, y ->
            x + y
        }

        // Case 2 → Inline HOF → lambda is inlined → NO object, faster
        val inlineResult = calculateInline(5, 3) { x, y ->
            x * y
        }

        Log.d(TAG, "Normal = $normalResult")
        Log.d(TAG, "Inline  = $inlineResult")
    }


// ---------------------------------------------------------
// 2️⃣ WHAT INLINE ACTUALLY DOES — "CALL-SITE COPY"
// ---------------------------------------------------------

// This inline call:
//    calculateInline(10, 20) { x, y -> x + y }
//
// Is converted BY COMPILER to something like:
//    val result = 10 + 20
//
// No lambda object. No extra function calls.


// ---------------------------------------------------------
// 3️⃣ INLINE + NON-LOCAL RETURN (VERY IMPORTANT)
// ---------------------------------------------------------
// Lambdas inside inline functions can use "return" to exit the OUTER function.

    private inline fun operateWithInlineReturn(block: () -> Unit) {
        block()    // because 'block' is inlined, return inside block returns from caller
    }

    private fun testNonLocalReturn() {
        operateWithInlineReturn {
            Log.d(TAG, "Before return")
            return        // ⬅ exits testNonLocalReturn(), not just lambda
        }
        Log.d(TAG, "This will NOT run")  // unreachable
    }


// ---------------------------------------------------------
// 4️⃣ ADVANCED → noinline (when some lambdas SHOULD NOT be inlined)
// ---------------------------------------------------------
// Reason: a lambda CAN'T be inlined if you:
//  - store it in a variable
//  - return it
//  - pass it to another function

    private inline fun mixedInline(
        a: Int,
        b: Int,
        inlineOp: (Int, Int) -> Int,          // will be inlined
        noinline normalOp: (Int, Int) -> Int  // WILL NOT be inlined
    ): Int {
        val v1 = inlineOp(a, b)     // allowed (inlined)
        val v2 = normalOp(a, b)     // allowed ONLY because of "noinline"
        return v1 + v2
    }

    private fun testNoInline() {
        mixedInline(
            3, 4,
            inlineOp = { x, y -> x * y },
            normalOp = { x, y -> x + y }   // stored as lambda object
        )
    }


// ---------------------------------------------------------
// 5️⃣ ADVANCED → crossinline (block IS inline but cannot use non-local return)
// ---------------------------------------------------------
// Needed when the lambda is executed in another scope (thread, callback).
// Non-local return is impossible → so compiler forces "crossinline".

    private inline fun delayedExecution(crossinline block: () -> Unit) {
        Thread {
            block()     // cannot use "return" inside block (would break)
        }.start()
    }

    private fun testCrossInline() {
        delayedExecution {
            Log.d(TAG, "Executed in background thread")
            // return ❌ NOT allowed (crossinline)
        }
    }


// ---------------------------------------------------------
// 6️⃣ EDGE CASE — Inline increases code size
// ---------------------------------------------------------
// Inline copies code at call-site.
// If the function body is large → code duplication → APK size increases.
// So avoid inline for large logic methods.


// ---------------------------------------------------------
// 7️⃣ INLINE + REIFIED (MOST IMPORTANT FOR GENERICS)
// ---------------------------------------------------------
// Reified only works with inline functions.
// It allows you to use type T directly (T::class, T::class.java).

    private inline fun <reified T> printTypeInfo() {
        Log.d(TAG, "Type is = ${T::class.java.name}")
    }

    private fun testReified() {
        printTypeInfo<String>()  // prints java.lang.String
        printTypeInfo<Int>()     // prints java.lang.Integer
    }


// ---------------------------------------------------------
// 8️⃣ INLINE USE-CASE IN ANDROID — PERFORMANCE
// ---------------------------------------------------------
// Kotlin uses inline in:
//    apply, run, also, let, with
// Jetpack Compose uses inline to avoid allocations inside UI recomposition.


// ---------------------------------------------------------
// 9️⃣ INLINE SUMMARY (Put in your notes)
// ---------------------------------------------------------
    /*
    INLINE DONE =

    1. Removes lambda object → faster, lower memory
    2. Copies lambda at call-site
    3. Allows non-local returns
    4. Use noinline when lambda must be stored/returned/passed
    5. Use crossinline when lambda runs in different scope (threads/callbacks)
    6. Inline can increase code size → don't inline large methods
    7. Reified types ONLY work with inline

    Use inline:
    ✔ small utility HOFs
    ✔ DSL builders
    ✔ map/filter-style utilities
    ✔ Compose performance
    */


    private fun backingProperty() {
        val human = Human()
        human.age = 30
        Log.d(TAG, "Human age is : " + human.age)

        //Same as above just internal implementation changed which is safe
        val human1 = Human1()
        human1.age = 30
        Log.d(TAG, "Human1 age is : " + human1.age)

    }

    class Human {
        //By default every property inside class is public unless declared private.
        var age: Int = 0 // Hence this is exposed outside of class and anyone can modify it.
    }

    //Alternative and safe code for same implementation using backing property
    class Human1 {
        private var _age: Int = 0  // Backing field: holds actual 'age' property data
        var age: Int               // Backing property exposed to outside world
            get() {
                return _age
            }
            set(value) {
                _age = value
            }
        //Note: We should always use _age inside this Human1 class, age is just used for outside
        //access
    }

    private fun getterAndSetter() {
        //Now whenever we declare any instance variable in a class, by default its getter and setter
        //are written internally and are available for use, even though we haven't provided them
        //explicitly,

        val demo = Demo()
        demo.firstName = "Harshit"
        demo.lastName = "sharma"

        Log.d(TAG, "getterAndSetter: " + demo.firstName + " " + demo.lastName)
        //Note, we only write setter and getter explicitly when we don't just want to set
        //or get value, but instead we need to add few other things or do some computations.
    }

    class Demo {
        var firstName: String? = null
        //Here even if we don't write this, by default this is implemented internally.
        //Note: in kotlin we don't write firstName = value, because if we do so, we will get
        //stack overflow exception, here this firstName is represented as field, now field
        //keyword can only be used inside setter and getter.

            //Important : This field keyword is also know as backing field.It just stores value of
            // its own property.
            //Backing field != Backing property
            set(name) {
                field = name
            }
            //here we can declared getter in two ways
            //1st --> get() = field
            //2nd approach, when we have to write more complex code or more no. of lines then
            get() {
                return field
            }

        var lastName: String? = null


        //Related to backing field
        var name: String = "" // No backing field is generated internally

        var age = 10
            get() {
                return field
            }

        //Here backing field is not generated because even though we are using a custom getter but
        // it is referencing age, not the isOld or say field inside the getter body.
        var isOld: Boolean = false
            get() = age >= 25  // No backing field is generated internally

        //Remember backing field is not dependent on custom getter and setter, its only generated
        //when we are trying to use the same property inside its setter or getter using field.
        //example is firstName property
    }

    private fun companionObjects() {

        //companion objects are same as object but declared within class
        MyClass.count           // You can print it and check result
        MyClass.typeOfCustomers()

        Log.d(TAG, "companionObjects: " + MyClass.count + "  " + MyClass.typeOfCustomers())
    }

    class MyClass {
        //Here if we look at byte code of this, we will find that they are actually converted to static
        //methods and fields.
        companion object {

            var count: Int = -1             // Behaves like STATIC variable

            //Here to make this method compatible with java and to access this method from java file
            // we use @JvmStatic
            @JvmStatic
            fun typeOfCustomers(): String { // Behaves like STATIC method
                return "Indian"
            }
        }
    }

    private fun objectExplanation() {

        // When we use object keyword, it behave just like a singleton of java
        // It is not exactly a class, but kotlin internally creates a class and an object/instance
        // This object is created only once .
        //This object can have
        // properties/variables, methods and init blocks
        // It doesn't have constructor
        //It also supports inheritance, meaning they can have a super class.

        //the variables and methods written inside object behaves as static.

        CustomersData.count = 98
        CustomersData.typeOfCustomers()

        println(CustomersData.typeOfCustomers())

        CustomersData.count = 109
        println(CustomersData.count)

        CustomersData.myMethod("hello")

    }

    open class MySuperClass {

        open fun myMethod(str: String) {
            println("MySuperClass")
        }
    }

    object CustomersData : MySuperClass() {      // Object Declaration

        var count: Int = -1             // Behaves like a STATIC variable

        fun typeOfCustomers(): String { // Behaves like a STATIC method
            return "Indian"
        }

        override fun myMethod(str: String) {    // Currently, behaving like a STATIC method
            super.myMethod(str)
            println("object Customer Data: $str")
        }
    }

    private fun dataClass() {
        // In kotlin, == means value comparison and === means reference comparison,
        // so, for == comparison, if it was not a data class, then its result would be not equal,
        // and if we declare that as data class, then its equals because data has overridden the
        //tostring method for content matching. In kotlin == behaves as equals method of java
        //while for reference we use ===.
        //Here var, val are mandatory to write in parameter, we can't just pass the name :type.

        var simpleUser1 = SimpleUser("Sam", 10)

        var simpleUser2 = SimpleUser("Sam", 10)

        println(simpleUser1.toString())

        if (simpleUser1 == simpleUser2)
            Log.d(TAG, "Equal")
        else
            Log.d(TAG, "Not equal")

        var newUser = simpleUser1.copy(name = "Harshit")
        println(newUser)

    }

    data class SimpleUser(var name: String, val id: Int)


    open class TheAnimal {      // Super class / Parent class /  Base class

        var color: String = ""

        constructor(color: String) {
            this.color = color
            println("From Animal: $color")
        }
    }

    class TheDog : TheAnimal {    // Sub class / Child class / Derived class

        var bread: String = ""

        constructor(color: String, breed: String) : super(color) {
            this.bread = breed
            println("From Dog: $color and $breed")
        }
    }


    class Person {
        var name: String = "harshit"
        var city: String = "jodhpur"
    }

    private fun scopeFunctions() {
        /*
        * Before knowing scope we need to know difference b/w this and  it
        *
        * In Scope functions run, apply and with, the scope is (temporarily) changed to the scope
        * of the object you are calling this function on:
        *
        *  val str = "Hello"
        *  str.run {
        *  //Here this refers to str
        *  }
        *
        * In Scope function let, the scope is also not changed (remains the same as caller scope)
        * but your lambda will receive the context as it is inside the lambda:
        *
        * val str = "Hello"
        * str.let {
        * //Here it refers to str
        * }
        *
        * it is the default name for a single parameter and is a shorthand that allows you to omit
        *  naming the single parameter.
        * */


        /* There are 5 scope functions namely with, apply, let, run , also.
        * How do we differentiate them as which one to use. These are differentiate based on
        *  two things:
        *  1.) The way to refer to the context object. Either 'this'  or  'it'.
        * 2.) The return value. Either 'context object' or 'lambda result'.
        *
        * we will explore each one.
        * */

        /* apply scope function
        *
        * return : context object
        * Context object : this
        * */

        //apply is generally used to initialize properties of object and it returns
        // the same object
        val person = Person().apply {
            //note here this keyword is optional, its used by default even if not written.
            this.name = "harshit"
            city = "Jodhpur"
        }


        /* with scope function
        *
        * return : lambda result
        * Context object : this
        *
        * if we want to operate on non- null obj then this should be used.
        * */

        var returnedValue = with(person) {
            Log.d(TAG, "with scopeFunction: $name $city")
            name = "hjshdjsh"
            city = "hjdfjd"
            //here whatever is the type of last statement, that value will be returned. ex

            "last is string"
        }
        Log.d(TAG, "returned value is: $returnedValue")


        val abc: Person? = null

        with(abc) {
            this?.name
        }

        /* also scope functionn
        *
        * return :context object
        * Context object : it
        *
        * if we want to do some additional operation or configuration on the same obj, then
        * also is used
        * */

        //say we want to change the name in the same person obj
        //Note generally we don't use reference for it, now if we check the person
        //obj, then its name is already changed.
        person.also {
            it.name = "Kotlin"
        }


        /* let scope function
        *
        * return : lambda result
        * Context object : it
        *
        * if we want to just execute lambda expression on a nullable object and avoid null
        * pointer exception. it is generally used with safe call operator ?.
        * */

        var name: String? = null
        name?.let {
            it.uppercase()
            it.reversed()
        }


        /* run scope function
        *
        * return : lambda result
        * Context object : this
        *
        * if we want to operate on a nullable object,execute lambda expression and avoid null
        * pointer exception. It is a combination of let and with
        * */

        val person1: Person? = null
        person1?.run {
            Log.d(TAG, "run function : " + person1.name)
        }

    }

    //In kotlin we don't have to write constructor explicitly in the class like java,
    //like below we add () in front of class name, and this is our constructor, actually we have to
    //write MyTest contructor(), but if we don't have any @annotation like @inject etc we can ignore
    //this as it is written internally.
    class MyTest() {

    }

    //Here we are using a constructor with one parameter, just like we do the initialization of our
    //field variables in java, here we are doing the same thing. init{} function is the first thing
    //which get called when new obj is created. i.e. MyTest()
    class MyTest1(name: String) {
        var name: String  //field variable

        init {
            this.name = name
        }
    }

    // What if I say all that is done above can be done in much easier way in kotlin.Check below.
    // Here if we add var/val then internally it is conveyed to compiler that name is the field
    //variable name and whatever value we pass in constructor is assigned to it automatically.
    //Note: All constructors we use till now are called primary constructors in kotlin.
    //Primary constructors doesn't have its own body, we can treat init block as the body of primary
    // constructor to initialize anything.
    class MyTest2(var name: String) {
        init {
            Log.d("TAG", "name already initialized with : $name")
        }
    }

    //Secondary constructor example
    class Student(var name: String) {
        var id: Int = -1;

        init {
            Log.d("TAG", "Student got his name as : $name")
        }

        //This is secondary constructor, very important here is that whenever we declare secondary
        //constructor, we compulsorily have to make an explicit call to the primary constructor, otherwise
        //compiler won't allow to make to call to this secondary constructor without first
        // declaring primary constructor.
        //Unlike primary, secondary constructor have its own body.
        //Note: We cannot declare field variable in secondary constructor just like we did in
        //primary constructor as it is not allowed. We have to perform initialization just as
        // MyTest1 class,  here one last thing is name variable need not be same as our primary
        //constructor variable name.

        constructor(name: String, id: Int) : this(name) {
            this.id = id
        }
    }

    //Inheritance
    //By default all classes in kotlin are public final, so any subclass can't use it
    //directly, for that we need to use open keyword for the base class from whom the
    // child classes are deriving, Similarly the field variables and functions are also
    //by default public final, so to override them we have to use open keyword there also.
    // In java for inheriting a class we use extend keyword, but in kotlin we can : for that.

    // For overriding any variable or function we use open, then also use override keyword
    //on the derived variable or function.

    open class Animal {         // Super class / Parent class /  Base class
        var color: String = ""
        open fun eat() {
            println("Eat")
        }
    }

    class Dog : Animal() {      // Sub class / Child class / Derived class
        var bread: String = ""
        fun bark() {
            println("Bark")
        }

        //Here we have overridden the eat method from animal class. Say we also want to
        //print the statement in eat function of animal class along with this overridden one
        // we use super keyword
        //Imp Note : Suppose the Animal class also implement an interface which also have same
        //eat method, then which one will be called for super call?
        //To avoid such confusions we use generics i.e. <>, and specify that we need Animal class
        //eat method not interface one by specifying name of class in <>

        override fun eat() {
            super<Animal>.eat()
            println("Dog is eating")
        }
    }

    // This interface also has an 'eat()' method with a default implementation.
// We will use this to create an inheritance conflict.
    interface Carnivore {
        fun eat() {
            println("Carnivore is eating meat.")
        }
    }

    // Wolf now inherits from Animal AND implements the Carnivore interface.
    // Both Animal and Carnivore have an 'eat()' method. This creates a conflict.
    // The compiler forces us to override 'eat()' in Wolf to resolve this ambiguity.

    class Wolf : Animal(), Carnivore {

        // --- CORRECTED COMMENT AND IMPLEMENTATION FOR CONFLICT RESOLUTION ---

        // We MUST override 'eat()' because the compiler doesn't know which version to use:
        // the one from 'Animal' or the one from 'Carnivore'.
        override fun eat() {
            // Inside the override, we must choose which parent's (super) implementation to call.
            // If we just used 'super.eat()', it would still be ambiguous and would not compile.

            // To solve this, we use a "qualified super" call. We use angle brackets <>
            // to specify exactly which parent's 'eat()' method we want to call.
            // NOTE: This syntax is for specifying a supertype, it is NOT generics.

            // Explicitly call the 'eat()' method from the Animal class.
            super<Animal>.eat()

            // Explicitly call the 'eat()' method from the Carnivore interface.
            super<Carnivore>.eat()

            // Finally, we can add the Wolf's own specific behavior.
            println("Wolf is eating its prey.")
        }
    }


    class Cat : Animal() {      // Sub class / Child class / Derived class

        var age: Int = -1

        fun meow() {
            println("Meow")
        }
    }


    private fun kotlinPractice() {
        //Imp: when calling secondary constructor, in Student class the name "harshit" is first passed
        //to primary constructor, then init block will be called, then secondary constructor body will
        //get executed.
        Student("harshit", 12);


        var arr: ArrayList<String> = ArrayList();

        //this will print  1 to 10
        for (i in 1..10) {
            Log.d(TAG, "kotlinPractice: " + i)
        }

        //this will print  1 to 9 , until means one less than upper bound
        for (i in 1 until 10) {
            Log.d(TAG, "kotlinPractice Until: " + i)
        }

        // if we want to print in reverse order
        for (i in 10 downTo 1) {
            Log.d(TAG, "kotlinPractice Until: " + i)
        }

        //or this , both are same
        for (i in 10.downTo(1)) {
            Log.d(TAG, "kotlinPractice Downto: " + i)
        }

        //if want to increase frequency . i.e say print each 2 element then use this
        for (i in 1..10 step 2) {
            Log.d(TAG, "kotlinPractice Step: " + i)
        }

        val items = listOf("apple", "banana", "kiwifruit")

        for (index in items.indices) {
            println("item at $index is ${items[index]}")
        }

        for (items in 1..items.size) {
            println("item is ${items}")
        }

        //Normal Functions
        normalFunc(5, 4);

        // Single-Expression Functions (functions that consist of a single expression)
        singleExpressionFunc(5, 6);
        singleExpressionFuncAlt(5, 6);


        //In kotlin we can assign the function itself to a var , means a variable can hold a function
        var ft = ::normalFunc;

        //now using ft call function
        val returnedValue = ft(4, 5);
        Log.d(TAG, "kotlinPractice: " + returnedValue)
    }


    private fun normalFunc(a: Int, b: Int): Int {
        return a + b;
    }

    // Single-Expression Function with explicit return type Int:
    private fun singleExpressionFunc(a: Int, b: Int): Int = a + b

    // Single-Expression Function with inferred return type (Int):
    private fun singleExpressionFuncAlt(a: Int, b: Int) = a + b


    private fun sealedClassesExample() {
        Log.d(TAG, "--- Sealed Classes Example ---")

        /*
        * SEALED CLASS = a class with a RESTRICTED, known-in-advance set of subclasses.
        *
        * Why it exists:
        * - Normal inheritance is open: anyone, anywhere can extend your class, so a
        *   'when' over subtypes can never be exhaustive - you always need 'else'.
        * - Sealed class locks the hierarchy: all direct subclasses must be declared
        *   INSIDE the sealed class (or in the same package/module, Kotlin 1.5+).
        * - The compiler then KNOWS every possible subtype, so 'when' becomes
        *   EXHAUSTIVE: no 'else' needed, and adding a new subtype breaks compilation
        *   everywhere you forgot to handle it. This is compile-time safety.
        *
        * Key rules (interview edge cases):
        * - A sealed class is abstract by itself - you cannot instantiate it directly.
        * - Its constructor is PRIVATE by default - you cannot inherit from outside
        *   the allowed set (that's the whole point).
        * - Subclasses must be declared in the same package (Kotlin 1.5+).
        * - Subclasses can be data class, object, or regular class - mix freely.
        * - Sealed class CAN have state/properties (unlike sealed interface).
        * - 'when' over a sealed hierarchy as an EXPRESSION requires exhaustiveness
        *   (no else) - as a statement, the compiler only warns if a case is missing.
        * */

        // Create instances of the different states defined in our sealed class
        val successResult: NetworkResult = NetworkResult.Success("User data loaded successfully")
        val errorResult: NetworkResult = NetworkResult.Error(404, "User not found")
        val loadingResult: NetworkResult = NetworkResult.Loading
        val unauthorizedResult: NetworkResult = NetworkResult.Unauthorized("Session expired")

        // Use a 'when' expression to handle each state. The compiler will force you to handle
        // all possible types, which is a key advantage (compile-time safety).
        handleNetworkResult(successResult)
        handleNetworkResult(errorResult)
        handleNetworkResult(loadingResult)
        handleNetworkResult(unauthorizedResult)

        // ---------------------------------------------------------
        // Edge case 1: 'when' as an EXPRESSION must be exhaustive (no else needed)
        // ---------------------------------------------------------
        val label = when (errorResult) {
            is NetworkResult.Success -> "success"
            is NetworkResult.Error -> "error"
            is NetworkResult.Loading -> "loading"
            is NetworkResult.Unauthorized -> "unauthorized"
            is NetworkResult.Timeout -> "timeout"
        }
        println(label) // error

        // ---------------------------------------------------------
        // Edge case 2: smart-casting - after 'is', the compiler casts for you
        // ---------------------------------------------------------
        if (errorResult is NetworkResult.Error) {
            // No cast needed - errorResult is already NetworkResult.Error here
            println("Code = ${errorResult.code}")
        }

        // ---------------------------------------------------------
        // Edge case 3: comparing singletons - use === for object subtypes
        // ---------------------------------------------------------
        // NetworkResult.Loading is a SINGLETON - there is only one instance ever.
        // So '===' (reference equality) is safe and idiomatic for object states:
        println(loadingResult === NetworkResult.Loading) // true

        // ---------------------------------------------------------
        // Edge case 4: sealed class with STATE (shared property in the parent)
        // ---------------------------------------------------------
        // Unlike a sealed interface, a sealed class can hold common state:
        val timedOut = NetworkResult.Timeout(5000)
        println("Elapsed = ${timedOut.elapsedMs}, sealed parent state = ${timedOut.tag}")
    }

    // Sealed classes are perfect for modeling states that can hold different data.
// For example, the result of a network request can be Success (with data),
// Error (with a message), or Loading.
// All direct subclasses MUST be inside this sealed class or in the same file.
    sealed class NetworkResult {
        // Common state living in the sealed parent - shared by ALL subtypes.
        // (This is possible because a sealed class is a real class, unlike an interface.)
        open val tag: String = "NetworkResult"

        // A 'data class' is great for states that hold data.
        data class Success(val data: String) : NetworkResult()

        // Another 'data class' for a different state with different data.
        data class Error(val code: Int, val message: String) : NetworkResult()

        // An 'object' is perfect for states that don't need to hold any data.
        // It's a singleton, so only one instance exists.
        object Loading : NetworkResult()

        // You can also have a regular class as a subclass.
        class Unauthorized(val reason: String) : NetworkResult()

        // A subclass with its own extra state.
        class Timeout(val elapsedMs: Long) : NetworkResult()
    }

    fun handleNetworkResult(result: NetworkResult) {
        // The 'when' expression is exhaustive because NetworkResult is sealed.
        // If you add a new subclass to NetworkResult and forget to handle it here,
        // your code won't compile, preventing runtime errors.
        when (result) {
            is NetworkResult.Success -> {
                Log.d(TAG, "Success! Data: ${result.data}")
            }

            is NetworkResult.Error -> {
                Log.d(TAG, "Error Code ${result.code}: ${result.message}")
            }

            is NetworkResult.Loading -> {
                Log.d(TAG, "Status: Loading...")
            }

            is NetworkResult.Unauthorized -> {
                Log.d(TAG, "Unauthorized: ${result.reason}. Please log in again.")
            }

            is NetworkResult.Timeout -> {
                Log.d(TAG, "Timeout after ${result.elapsedMs} ms")
            }
            // No 'else' branch is needed because all cases are covered.
        }
    }

    private fun genericsExample() {
        Log.d(TAG, "--- Generics Example ---")

        // Create a Box instance for a String.
        // The type 'String' is inferred by the compiler.
        val stringBox = Box("Hello, Generics!")
        Log.d(TAG, "String box contains: ${stringBox.value}")

        // Create a Box instance for an Int.
        val intBox = Box(123)
        Log.d(TAG, "Int box contains: ${intBox.value}")

        // Edge Case: What about nullability?
        // The generic type T can also be nullable. To allow this, you must provide a nullable type.
        val nullableBox = Box<String?>(null)
        Log.d(TAG, "Nullable box contains: ${nullableBox.value}")

        // Using a generic function
        val lastString = getLastElement(listOf("Apple", "Banana", "Cherry"))
        val lastInt = getLastElement(listOf(10, 20, 30))
        Log.d(TAG, "Last element of string list: $lastString")
        Log.d(TAG, "Last element of int list: $lastInt")
    }

    // A generic class 'Box' that can hold an item of any type 'T'.
// This avoids creating separate Box classes for String, Int, etc.
    class Box<T>(item: T) {
        var value = item
    }

    // A generic function that works on a list of any type 'T'.
// The 'where' clause is a type constraint, ensuring 'T' is not nullable.
// Here, we ensure that the function can only be called on types that are non-nullable.
    fun <T : Any> getLastElement(list: List<T>): T? {
        return list.lastOrNull()
    }

    private fun delegationExample() {
        Log.d(TAG, "--- Delegation Example ---")

        // 1. Delegated Property: observable
        // This delegate runs a callback function whenever the property's value changes.
        // It's useful for triggering actions in response to state changes.
        Log.d(TAG, "Demonstrating 'Delegates.observable':")
        val user = User()
        user.name = "John Doe" // Triggers the observable: Initial Name -> John Doe
        user.name = "Jane Doe" // Triggers again: John Doe -> Jane Doe

        // 2. Delegated Property: vetoable
        // This is similar to observable but gives you the power to "veto" or reject a new value.
        // The change only occurs if the lambda returns true.
        Log.d(TAG, "Demonstrating 'Delegates.vetoable':")
        user.age = 25  // This change is allowed and will be printed.
        user.age = -5  // This change is rejected because of our condition (it < 0).
        user.age = 150 // This change is also rejected.
        Log.d(TAG, "Final user age: ${user.age}") // Will still be 25
    }

    // Example of Delegates.observable and Delegates.vetoable
    class User {
        // 'observable' takes an initial value and a lambda that is called AFTER the property
        // has been changed. It receives the property, the old value, and the new value.
        var name: String by Delegates.observable("Initial Name") { property, oldValue, newValue ->
            Log.d(TAG, "${property.name} changed from '$oldValue' to '$newValue'")
        }

        // 'vetoable' also takes an initial value and a lambda, but this lambda is called BEFORE
        // the property value is changed. If the lambda returns 'true', the change happens.
        // If it returns 'false', the change is blocked.
        var age: Int by Delegates.vetoable(0) { property, oldValue, newValue ->
            Log.d(TAG, "Attempting to change ${property.name} from $oldValue to $newValue")
            // We only allow positive ages under 130
            newValue > 0 && newValue < 130
        }
    }

    // =========================================================
    // 1. ENUM CLASSES
    // =========================================================
    private fun enumClasses() {

        /*
        * Enum class = a class that has a FIXED, known set of instances,
        * declared at compile time. Every value you write is a singleton object.
        *
        * Use enum when the set of values is small and closed, e.g. days of week,
        * order status, screen state. The compiler KNOWS all possible values.
        *
        * Key rules:
        * - Enum constants are separated by commas, the list ends with a semicolon.
        * - An enum class CANNOT be inherited (it is final by default).
        * - Enums CAN implement interfaces, and each constant can override methods.
        * - Every enum constant automatically has: name (String) and ordinal (Int position).
        * - Enums can have constructors, properties and methods like normal classes.
        * */

        // ---------------------------------------------------------
        // 1. Basic enum with constructor and method
        // ---------------------------------------------------------
        val today = Day.MONDAY

        // 'name' and 'ordinal' come free with every enum constant.
        println("${today.name} is position ${today.ordinal}") // MONDAY is position 0
        println(today.isWeekend()) // false

        // ---------------------------------------------------------
        // 2. values() and valueOf()
        // ---------------------------------------------------------

        // values() -> array of ALL constants, in declaration order.
        for (day in Day.values()) {
            print("${day.name} ")
        }
        println()

        // valueOf(name) -> converts a String back to the enum constant.
        // Throws IllegalArgumentException if the name does not match exactly.
        val parsed = Day.valueOf("FRIDAY")
        println(parsed) // FRIDAY

        // Safe alternative: entries / valueOfOrNull style lookup.
        val safeParsed = Day.entries.firstOrNull { it.name == "SUNDAY" }
        println(safeParsed) // SUNDAY

        // ---------------------------------------------------------
        // 3. Each constant can override methods (enum polymorphism)
        // ---------------------------------------------------------
        println(Day.SATURDAY.isWeekend()) // true
        println(Day.MONDAY.isWeekend())   // false

        // ---------------------------------------------------------
        // 4. when works perfectly with enum (exhaustive, no else needed)
        // ---------------------------------------------------------
        val message = when (today) {
            Day.MONDAY -> "Start of week"
            Day.FRIDAY -> "Almost weekend"
            Day.SATURDAY, Day.SUNDAY -> "Weekend!"
            else -> "Regular day"
        }
        println(message)

        /*
        * INTERVIEW: enum vs sealed class
        * - enum    -> each constant is a SINGLETON, all constants share the SAME type,
        *              cannot hold different data per constant (only fixed constructor params).
        * - sealed  -> each subtype is a DIFFERENT class, can hold DIFFERENT data per subtype,
        *              and subtypes can themselves be data classes/objects.
        * Rule of thumb: fixed set of simple constants -> enum.
        *                fixed set of STATES with different data -> sealed.
        * */
    }

    enum class Day(val isWeekendDefault: Boolean = false) {
        MONDAY,          // uses default constructor value
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY(true),  // passes custom value
        SUNDAY(true);

        // Enum classes can have methods and properties like normal classes.
        fun isWeekend(): Boolean = isWeekendDefault || this == Day.SUNDAY
    }

    // =========================================================
    // 2. OBJECT EXPRESSIONS (Anonymous Objects)
    // =========================================================
    private fun objectExpressions() {

        /*
        * object EXPRESSION = creating an object of an anonymous class on the spot.
        * "object : SomeType { ... }" means: give me a one-off object that implements
        * SomeType right here, without declaring a named class.
        *
        * This is Kotlin's replacement for Java's anonymous inner classes, e.g.
        * button.setOnClickListener(object : View.OnClickListener { ... })
        *
        * Rules:
        * - The object expression can implement one or more interfaces, and/or extend a class.
        * - It CANNOT have a constructor.
        * - It is created fresh EVERY time the expression runs (not a singleton!).
        *   This is the key difference from object DECLARATION (which is a singleton).
        * - Inside, you can access variables from the surrounding scope (closure).
        * */

        // ---------------------------------------------------------
        // 1. Implementing an interface anonymously
        // ---------------------------------------------------------
        val greeter = object : Greeter {
            override fun greet(name: String): String {
                return "Hello, $name!"
            }
        }
        println(greeter.greet("Kotlin")) // Hello, Kotlin!

        // ---------------------------------------------------------
        // 2. Object with NO supertype (just a one-off object)
        // ---------------------------------------------------------
        val counter = object {
            var count = 0
            fun increment() {
                count++
            }
        }
        counter.increment()
        counter.increment()
        println("Count = ${counter.count}") // Count = 2

        // ---------------------------------------------------------
        // 3. Accessing outer variables (closure) - like Java but without 'final' restriction
        // ---------------------------------------------------------
        var clicks = 0
        val clickHandler = object : Greeter {
            override fun greet(name: String): String {
                clicks++ // Kotlin allows modifying outer var (Java anonymous class cannot)
                return "Clicked $clicks times"
            }
        }
        clickHandler.greet("x")
        clickHandler.greet("y")
        println("Total clicks = $clicks") // Total clicks = 2

        /*
        * INTERVIEW: object DECLARATION vs object EXPRESSION
        * - object CustomersData { }   -> singleton, created once, lazy (on first access)
        * - object : Greeter { }       -> anonymous instance, new object every time
        * */
    }

    interface Greeter {
        fun greet(name: String): String
    }

    // =========================================================
    // 3. NESTED vs INNER CLASSES
    // =========================================================
    private fun nestedAndInnerClasses() {

        /*
        * NESTED class = class declared inside another class WITHOUT 'inner'.
        * - It does NOT hold any reference to the outer class.
        * - You cannot access outer class members from inside it.
        * - Behaves like Java's 'static' nested class.
        *
        * INNER class = class declared with 'inner' keyword.
        * - It HOLDS a reference to the outer class instance.
        * - It CAN access outer class members (even private ones).
        * - It can only be created via an outer instance: outer.Inner()
        *
        * Why does Kotlin need 'inner' keyword at all?
        * Because nested classes are static-like by default (unlike Java where
        * nested classes are inner by default). This avoids accidental memory leaks:
        * an inner class holds a hidden reference to the outer object, which can
        * keep an Activity alive if misused.
        * */

        // ---------------------------------------------------------
        // 1. Nested class - created independently of outer instance
        // ---------------------------------------------------------
        val nested = Outer.Nested()
        nested.show() // Nested: no access to outer

        // ---------------------------------------------------------
        // 2. Inner class - needs an outer instance to exist
        // ---------------------------------------------------------
        val outer = Outer()
        outer.name = "Outer A"
        val inner = outer.Inner()   // note: created FROM an outer instance
        inner.show()                // Inner: my outer is Outer A

        // ---------------------------------------------------------
        // 3. Inner class can access outer's private members too
        // ---------------------------------------------------------
        val secretReader = outer.SecretInner()
        secretReader.readSecret() // Inner: secret is 42

        /*
        * INTERVIEW: memory leak angle
        * - A nested class is safe to use in callbacks/threads (no outer reference).
        * - An inner class keeps the outer object alive as long as the inner lives.
        *   If outer is an Activity and inner is a long-lived Handler/Thread,
        *   the Activity leaks. Prefer nested (static-like) classes for callbacks.
        * */
    }

    class Outer {
        var name: String = ""
        private val secret = 42

        class Nested {   // no 'inner' -> no reference to Outer
            fun show() {
                println("Nested: no access to outer")
                // println(name) // ❌ ERROR: cannot access outer instance members
            }
        }

        inner class Inner {  // holds reference to outer instance
            fun show() {
                // 'this@Outer' refers to the outer instance explicitly
                println("Inner: my outer is ${this@Outer.name}")
            }
        }

        inner class SecretInner {
            fun readSecret() {
                // inner class can even access outer's private members
                println("Inner: secret is $secret")
            }
        }
    }

    // =========================================================
    // 4. ERROR HANDLING (try/catch/finally, throw, custom exceptions, runCatching)
    // =========================================================
    private fun errorHandling() {

        /*
        * Kotlin error handling is similar to Java but with important differences:
        *
        * - try/catch/finally works the same, but 'try' is an EXPRESSION in Kotlin,
        *   meaning it can return a value.
        * - Kotlin has NO checked exceptions (unlike Java 'throws'). Any function can
        *   throw anything; the compiler does not force you to catch.
        * - 'throw' is also an expression (its type is Nothing - see nothingAndUnit()).
        * - Idiomatic Kotlin prefers runCatching/Result over raw try/catch in APIs.
        * */

        // ---------------------------------------------------------
        // 1. Basic try/catch/finally
        // ---------------------------------------------------------
        try {
            val result = 10 / 0   // ArithmeticException
            println(result)
        } catch (e: ArithmeticException) {
            println("Caught: ${e.message}") // Caught: / by zero
        } finally {
            // finally ALWAYS runs - used for cleanup (closing resources etc.)
            println("finally always runs")
        }

        // ---------------------------------------------------------
        // 2. try as an EXPRESSION (returns a value)
        // ---------------------------------------------------------
        val number = try {
            "123".toInt()
        } catch (e: NumberFormatException) {
            0   // fallback value if parsing fails
        }
        println("Parsed = $number") // 123

        // ---------------------------------------------------------
        // 3. Multiple catch blocks - order matters (child before parent)
        // ---------------------------------------------------------
        try {
            val list = emptyList<Int>()
            list.first()   // NoSuchElementException
        } catch (e: NoSuchElementException) {
            println("Specific: ${e.javaClass.simpleName}")
        } catch (e: Exception) {
            println("Generic: ${e.javaClass.simpleName}")
        }
        // If you put catch(Exception) FIRST, the specific one becomes unreachable code.

        // ---------------------------------------------------------
        // 4. throw + custom exception
        // ---------------------------------------------------------
        // Custom exception = just extend Exception (or RuntimeException).
        class InsufficientBalanceException(message: String) : Exception(message)

        fun withdraw(balance: Int, amount: Int): Int {
            if (amount > balance) {
                throw InsufficientBalanceException("Need ${amount - balance} more")
            }
            return balance - amount
        }

        val remaining = withdraw(100, 30)
        println("Remaining = $remaining") // 70

        // ---------------------------------------------------------
        // 5. runCatching - idiomatic, returns Result (no crash, no try noise)
        // ---------------------------------------------------------

        // runCatching wraps the block: success -> Result.success(value),
        // any exception -> Result.failure(exception). Nothing crashes.
        val result: Result<Int> = runCatching { "abc".toInt() }
        println(result.isSuccess) // false
        println(result.exceptionOrNull()?.javaClass?.simpleName) // NumberFormatException

        // Typical usage with getOrDefault / getOrNull / onSuccess / onFailure:
        val safeValue = result.getOrDefault(-1)
        println("Safe value = $safeValue") // 0 fallback -> -1

        result
            .onSuccess { println("Got $it") }
            .onFailure { println("Failed with ${it.javaClass.simpleName}") }

        // ---------------------------------------------------------
        // 6. require / check / error - Kotlin's built-in guard idioms
        // ---------------------------------------------------------

        // require(): validates ARGUMENTS. Throws IllegalArgumentException.
        fun setAge(age: Int) {
            require(age in 0..130) { "Age must be 0..130, got $age" }
        }
        // check(): validates STATE. Throws IllegalStateException.
        fun save() {
            check(isSaved == false) { "Already saved" }
        }
        // error(): marks an impossible path. Throws IllegalStateException.
        fun getSomething(x: Int): String = when (x) {
            1 -> "one"
            else -> error("Unexpected value $x")
        }

        setAge(25)
        save()
        println(getSomething(1))

        /*
        * INTERVIEW: require vs check
        * - require -> "the CALLER passed a bad argument" (IllegalArgumentException)
        * - check   -> "MY object is in a wrong state"     (IllegalStateException)
        * */
    }

    private var isSaved = false

    // =========================================================
    // 5. DEFAULT ARGUMENTS, NAMED ARGUMENTS, VARARG
    // =========================================================
    private fun defaultNamedVarargArgs() {

        /*
        * DEFAULT ARGUMENTS: parameters can have default values, so callers can skip them.
        * This removes the need for Java-style method overloading (5 constructors doing
        * the same thing). Kotlin generates the overloads for you.
        *
        * NAMED ARGUMENTS: you can pass arguments as name = value in any order, and it
        * becomes self-documenting. Very common in Jetpack Compose and Android APIs.
        *
        * VARARG: a parameter marked 'vararg' accepts any number of values of that type.
        * Inside the function it is visible as an Array<out T>.
        * SPREAD OPERATOR (*): passes an existing array's elements as individual vararg
        * values. Only ONE spread is allowed per call, and it must not be mixed with
        * positional args of the same type before it.
        * */

        // ---------------------------------------------------------
        // 1. Default arguments - skip params that have defaults
        // ---------------------------------------------------------
        greet()                          // Hello, Guest from Unknown
        greet("Harshit")                 // Hello, Harshit from Unknown
        greet("Harshit", "Jodhpur")      // Hello, Harshit from Jodhpur

        // ---------------------------------------------------------
        // 2. Named arguments - pick which ones to pass, in any order
        // ---------------------------------------------------------
        greet(city = "Delhi")            // Hello, Guest from Delhi
        greet(prefix = "Hi", name = "Sam") // Hi, Sam from Unknown

        // ---------------------------------------------------------
        // 3. vararg - pass as many values as you want
        // ---------------------------------------------------------
        println(sum(1, 2, 3))            // 6
        println(sum())                   // 0
        println(sum(10, 20, 30, 40, 50)) // 150

        // ---------------------------------------------------------
        // 4. Spread operator - pass an array into a vararg parameter
        // ---------------------------------------------------------
        val numbers = intArrayOf(1, 2, 3)
        println(sum(*numbers))           // 6

        // You can even mix spread with extra values:
        println(sum(*numbers, 4, 5))     // 15

        // ---------------------------------------------------------
        // 5. vararg is NOT necessarily the last parameter (unlike Java)
        //    but then callers MUST use named arguments for the rest
        // ---------------------------------------------------------
        printInfo(*numbers, label = "My numbers") // label=My numbers, count=3

        /*
        * INTERVIEW: why named arguments matter in Android
        * - Compose functions have 20+ parameters; named args make calls readable.
        * - Default arguments kill constructor/telescoping overloads.
        * - @JvmOverloads: needed so JAVA callers also get the overloads
        *   (Kotlin defaults are not visible to Java without this annotation).
        * */
    }

    // Default params: name = "Guest", city = "Unknown", prefix = "Hello"
    private fun greet(name: String = "Guest", city: String = "Unknown", prefix: String = "Hello") {
        println("$prefix, $name from $city")
    }

    private fun sum(vararg numbers: Int): Int {
        var total = 0
        for (n in numbers) total += n
        return total
    }

    private fun printInfo(vararg values: Int, label: String) {
        println("label=$label, count=${values.size}")
    }

    // =========================================================
    // 6. DESTRUCTURING DECLARATIONS
    // =========================================================
    private fun destructuring() {

        /*
        * Destructuring = splitting an object into multiple variables in ONE statement.
        *
        * val (a, b) = somePair
        *
        * How it works internally: Kotlin calls component1(), component2(), ... on the
        * object. Data classes generate componentN() automatically for every primary
        * constructor property - that's why data classes destructuring works out of the box.
        *
        * Rules:
        * - Use '_' to skip a component you don't need.
        * - You can destructure in for loops, lambdas and map entries.
        * - For classes without componentN(), you must define them manually (or use data class).
        * */

        // ---------------------------------------------------------
        // 1. Data class destructuring
        // ---------------------------------------------------------
        val user = SimpleUser("Harshit", 10)
        val (name, id) = user   // calls user.component1() and user.component2()
        println("Name = $name, Id = $id")

        // Skip the second component with _
        val (onlyName, _) = user
        println("Only name = $onlyName")

        // ---------------------------------------------------------
        // 2. Destructuring in for loops (withIndex, map entries)
        // ---------------------------------------------------------
        val fruits = listOf("Apple", "Banana")
        for ((index, fruit) in fruits.withIndex()) {
            println("$index -> $fruit")
        }

        val map = mapOf("a" to 1, "b" to 2)
        for ((key, value) in map) {   // Map.Entry has component1()/component2()
            println("$key -> $value")
        }

        // ---------------------------------------------------------
        // 3. Destructuring in lambdas
        // ---------------------------------------------------------
        val pairs = listOf(1 to "one", 2 to "two")
        pairs.map { (num, word) -> "$num=$word" }.forEach { println(it) }

        // ---------------------------------------------------------
        // 4. Destructuring a Pair/Triple directly
        // ---------------------------------------------------------
        val (x, y) = 10 to 20
        println("x = $x, y = $y")

        // ---------------------------------------------------------
        // 5. Custom componentN functions (works without data class)
        // ---------------------------------------------------------
        val point = Point3D(1, 2, 3)
        val (px, py, pz) = point
        println("px=$px, py=$py, pz=$pz")

        /*
        * INTERVIEW: destructuring does NOT use position by name - it uses POSITION.
        * val (a, b) = user gives a = first property (name), b = second (id),
        * regardless of what you name a and b. Also: destructuring a non-data class
        * requires manual componentN() functions.
        * */
    }

    class Point3D(val x: Int, val y: Int, val z: Int) {
        // Manual operator conventions - data class would generate these automatically
        operator fun component1() = x
        operator fun component2() = y
        operator fun component3() = z
    }

    // =========================================================
    // 7. SEALED INTERFACE
    // =========================================================
    private fun sealedInterfaceExample() {

        /*
        * sealed INTERFACE = like a sealed class, but:
        * - A class can implement MANY sealed interfaces (single inheritance limit gone).
        * - No state/constructor - it is purely a contract.
        * - Perfect for modeling "capabilities" or union types.
        *
        * All implementations must be in the same package (Kotlin 1.5+) or same module.
        * The 'when' exhaustiveness works exactly like sealed class.
        *
        * INTERVIEW: sealed class vs sealed interface vs enum
        * - enum             -> fixed set of SINGLETON constants, one shared type.
        * - sealed class     -> fixed set of subtypes, each can hold state, single inheritance.
        * - sealed interface -> fixed set of implementors, no state, a class can implement many.
        * */

        // ---------------------------------------------------------
        // 1. Basic sealed interface with data class/object implementors
        // ---------------------------------------------------------
        val events: List<UiEvent> = listOf(
            UserClicked("btn_1"),
            ScreenShown,
            UserClicked("btn_2")
        )

        // Exhaustive when - compiler forces all cases, no 'else' needed.
        for (event in events) {
            when (event) {
                is UserClicked -> println("User clicked: ${event.id}")
                ScreenShown -> println("Screen was shown")
            }
        }

        // ---------------------------------------------------------
        // 2. One class implementing MULTIPLE sealed interfaces
        //    (impossible with sealed classes - single inheritance)
        // ---------------------------------------------------------
        val payment = UpiPayment("upi_123")
        describePayment(payment)
        // UpiPayment implements both Payable and Refundable sealed interfaces.

        // ---------------------------------------------------------
        // 3. Sealed interface + generic sealed hierarchy (common in MVI/Redux)
        // ---------------------------------------------------------
        val state: Loadable<String> = Loadable.Loaded("Kotlin")
        when (state) {
            is Loadable.Loading -> println("Loading...")
            is Loadable.Loaded -> println("Loaded: ${state.data}")
            is Loadable.Failed -> println("Failed: ${state.error}")
        }
    }

    // Sealed interface for UI events
    sealed interface UiEvent
    data class UserClicked(val id: String) : UiEvent
    object ScreenShown : UiEvent

    // Two sealed interfaces - a class can implement both
    sealed interface Payable {
        val amount: Int
    }

    sealed interface Refundable {
        val refundId: String
    }

    data class UpiPayment(val id: String) : Payable, Refundable {
        override val amount = 100
        override val refundId = "ref_$id"
    }

    // Generic sealed interface - union type for async results
    sealed interface Loadable<out T> {
        object Loading : Loadable<Nothing>
        data class Loaded<T>(val data: T) : Loadable<T>
        data class Failed(val error: String) : Loadable<Nothing>
    }

    // Accepts ANY Payable - this is why sealed interfaces shine:
    // UpiPayment works here without UpiPayment-specific code.
    fun describePayment(payment: Payable) {
        println("Amount = ${payment.amount}")
    }

    // =========================================================
    // 8. TYPE ALIASES
    // =========================================================

    // typealias gives an ALTERNATE NAME to an existing type.
    // It does NOT create a new type - it is just a nickname, fully interchangeable.
    // Used to shorten long generic types and make code self-documenting.
    // NOTE: typealias must be written at TOP-LEVEL (outside any class) -
    // Kotlin does not support nested/local type aliases.
    private fun typeAliasExample() {

        /*
        * typealias = a nickname for a type. Written at top-level (outside classes).
        *
        * Why it matters:
        * - Long generic types like Map<String, List<Pair<Int, String>>> become unreadable.
        * - A typealias makes intent clear: UserId is clearer than String.
        * - It does NOT create a new type - UserId and String are 100% interchangeable.
        *   (This is the difference from 'value class' which DOES create a new type.)
        *
        * Common in Android: typealias Callback = (Result) -> Unit
        * */

        // ---------------------------------------------------------
        // 1. Simple alias - interchangeable with the original type
        // ---------------------------------------------------------
        val id: UserId = "user_42"
        val same: String = id   // ✅ no conversion needed - it IS a String
        println("Id = $id, length = ${same.length}")

        // ---------------------------------------------------------
        // 2. Aliasing a function type - makes HOF signatures readable
        // ---------------------------------------------------------
        val onClick: ClickAction = { name -> println("Clicked: $name") }
        onClick("Save") // Clicked: Save

        // ---------------------------------------------------------
        // 3. Aliasing a generic collection type
        // ---------------------------------------------------------
        val users: UserMap = mapOf("u1" to "Harshit", "u2" to "Sam")
        println(users)

        /*
        * INTERVIEW: typealias vs value class
        * - typealias    -> compile-time nickname only, zero runtime cost, zero type safety gain.
        * - value class  -> real new type at compile time (prevents mixing up types), tiny
        *                   runtime wrapper (see valueClassExample()).
        * */
    }

    // =========================================================
    // 9. INFIX FUNCTIONS & OPERATOR OVERLOADING
    // =========================================================
    private fun infixAndOperatorOverloading() {

        /*
        * INFIX function = a member/extension function with ONE parameter that can be
        * called WITHOUT dot and parentheses:  a methodName b
        *
        * You already use one everywhere:  1 to "one"
        * 'to' is just an infix function in the standard library that creates a Pair!
        *
        * Rules:
        * - Must be a member or extension function.
        * - Must have exactly ONE parameter (no vararg, no default value).
        * - Marked with the 'infix' keyword.
        * */

        // ---------------------------------------------------------
        // 1. Custom infix function
        // ---------------------------------------------------------
        val student = Studentt()
        val result = student took "Mathematics"
        println(result) // Steve took Mathematics

        // The famous 'to' - it is infix too:
        val pair = 1 to "one"
        println(pair) // (1, one)

        // ---------------------------------------------------------
        // 2. OPERATOR OVERLOADING
        // ---------------------------------------------------------

        /*
        * Kotlin lets your classes define how operators (+, -, [], ==, etc.) behave,
        * by implementing functions marked 'operator' with FIXED names:
        *
        *   a + b    ->  a.plus(b)
        *   a - b    ->  a.minus(b)
        *   a[i]     ->  a.get(i)
        *   a[i] = v ->  a.set(i, v)
        *   a..b     ->  a.rangeTo(b)
        *   a in b   ->  b.contains(a)
        *   -a       ->  a.unaryMinus()
        *   a()      ->  a.invoke()
        *
        * The names are fixed by the compiler - you cannot invent new operators.
        * */

        val wallet1 = Wallet(100)
        val wallet2 = Wallet(50)

        // '+' works because Wallet defines plus()
        val total = wallet1 + wallet2
        println(total) // Wallet(150)

        // Comparison operators work because of compareTo()
        println(Wallet(50) < Wallet(80)) // true

        // 'in' works because of contains()
        println(50 in Wallet(50)) // true

        /*
        * INTERVIEW: operator overloading is NOT magic - it maps to named functions.
        * If a class defines plus(), you can write a + b. Kotlin does not allow
        * inventing NEW operators - only overriding the fixed set with their
        * predefined function names.
        * */
    }

    // Infix extension on our own class
    infix fun Studentt.took(subject: String): String = "Steve took $subject"

    class Wallet(val balance: Int) : Comparable<Wallet> {

        // a + b  ->  calls plus()
        operator fun plus(other: Wallet): Wallet = Wallet(balance + other.balance)

        // comparison -> calls compareTo (enables <, >, <=, >=)
        override fun compareTo(other: Wallet): Int = balance - other.balance

        // 'in' operator -> calls contains()
        operator fun contains(amount: Int): Boolean = amount == balance

        override fun toString(): String = "Wallet($balance)"
    }

    // =========================================================
    // 10. Nothing vs Unit
    // =========================================================
    private fun nothingAndUnit() {

        /*
        * Unit = "this function returns NOTHING useful" (like Java's void, but a real type).
        * Every function returns something in Kotlin - if there is no meaningful result,
        * the return type is Unit.
        *
        * Nothing = "this code NEVER completes normally". It has NO instance at all.
        * Used for functions that ALWAYS throw or loop forever.
        *
        * Why Nothing matters to the compiler:
        * - Nothing is a subtype of EVERY type in Kotlin.
        * - So a function returning Nothing can be used anywhere any type is expected.
        * - This is how the compiler knows code after 'throw' or 'error()' is unreachable.
        * */

        // ---------------------------------------------------------
        // 1. Unit - default return type when nothing meaningful is returned
        // ---------------------------------------------------------
        fun printSum(a: Int, b: Int): Unit {   // ': Unit' is optional
            println(a + b)
        }
        printSum(2, 3) // 5

        // Unit is an actual object, so it can be returned/stored:
        val unitValue: Unit = printSum(1, 2)
        println(unitValue) // kotlin.Unit

        // ---------------------------------------------------------
        // 2. Nothing - functions that NEVER return
        // ---------------------------------------------------------

        // fail() can be used anywhere a String is expected, because
        // Nothing is a subtype of every type - the compiler knows
        // execution will never continue past it.
        fun fail(message: String): Nothing {
            throw IllegalArgumentException(message)
        }

        val name: String = "Kotlin"
        val checked = if (name.isEmpty()) fail("Name was empty") else name
        println(checked) // Kotlin

        // ---------------------------------------------------------
        // 3. Nothing? - the type of 'null' literal itself
        // ---------------------------------------------------------

        // 'null' has type Nothing? - it can be assigned to any nullable type.
        val nothingOrNull: Nothing? = null

        // ---------------------------------------------------------
        // 4. Practical use: TODO() and error() return Nothing
        // ---------------------------------------------------------

        // TODO() returns Nothing - that's why the compiler doesn't complain
        // about the missing return below.
        fun notImplementedYet(x: Int): Int {
            TODO("implement later")
        }

        // It DOES throw at runtime - we catch it here so the demo doesn't
        // crash the app. The point stands: the compiler is fine with the
        // missing return because TODO() has type Nothing.
        val todoResult: Int = try {
            notImplementedYet(1)
        } catch (e: NotImplementedError) {
            -1 // sentinel: proves TODO() threw NotImplementedError
        }
        println("notImplementedYet threw NotImplementedError as expected: ${todoResult == -1}")

        /*
        * INTERVIEW: Nothing vs Unit
        * - Unit   -> function completes, but result is meaningless (one instance: Unit).
        * - Nothing-> function NEVER completes (throws or loops forever), no instance exists.
        * - Nothing is the BOTTOM type: subtype of all types. Nothing? is the type of null.
        * - 'return fail()' compiles because Nothing fits any expected return type.
        * */
    }

    // =========================================================
    // 11. CUSTOM PROPERTY DELEGATE
    // =========================================================
    private fun customPropertyDelegate() {

        /*
        * You already saw Delegates.observable/vetoable. But the senior-level skill is
        * writing your OWN delegate.
        *
        * A delegate is just a class that implements:
        * - ReadOnlyProperty  -> for val (needs getValue operator)
        * - ReadWriteProperty -> for var (needs getValue + setValue operators)
        *
        * The 'by' keyword connects the property to the delegate:
        *   var name: String by MyDelegate()
        *
        * Every read calls getValue(), every write calls setValue().
        * */

        // ---------------------------------------------------------
        // 1. Custom delegate that logs every read/write
        // ---------------------------------------------------------
        val settings = Settings()
        settings.theme = "dark"     // setValue logs: theme <- dark
        println(settings.theme)     // getValue logs, prints: dark

        // ---------------------------------------------------------
        // 2. Custom delegate with validation (like vetoable, but ours)
        // ---------------------------------------------------------
        val account = Account()
        account.balance = 500   // ✅ accepted
        account.balance = -50   // ❌ rejected, stays 500
        println("Balance = ${account.balance}") // 500

        /*
        * INTERVIEW: what does 'by' actually compile to?
        * var x: T by Delegate() roughly becomes:
        *   private val delegate = Delegate()
        *   var x: T
        *       get() = delegate.getValue(this, ::x)
        *       set(v) = delegate.setValue(this, ::x, v)
        * The delegate receives the owner object and the property metadata on every access.
        * */
    }

    // Simple read/write delegate that logs access
    private class LoggingDelegate<T> : ReadWriteProperty<Any?, T> {

        private var value: T? = null

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            println("Reading '${property.name}'")
            return value as T   // simple version - real code would handle null properly
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            println("Writing '${property.name}' = $value")
            this.value = value
        }
    }

    class Settings {
        var theme: String by LoggingDelegate()
    }

    // Delegate that can REJECT invalid values (vetoable-style)
    private class PositiveIntDelegate(initial: Int) : ReadWriteProperty<Any?, Int> {

        private var value = initial

        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            if (value >= 0) {
                this.value = value
            } else {
                println("Rejected negative value: $value")
            }
        }
    }

    class Account {
        var balance: Int by PositiveIntDelegate(0)
    }

    // =========================================================
    // 12. SEQUENCE vs COLLECTION
    // =========================================================
    private fun sequenceVsCollection() {

        /*
        * Collections are EAGER: every operation runs immediately and creates a NEW
        * intermediate list. list.filter{}.map{} creates 2 intermediate lists.
        *
        * Sequences are LAZY: nothing runs until a terminal operation (toList, first...).
        * Elements flow ONE BY ONE through the whole chain - no intermediate lists.
        *
        * Rule of thumb:
        * - Small collections -> plain collection functions (simpler, fast enough).
        * - Big collections / multiple chained operations -> asSequence() to save memory.
        * */

        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)

        // ---------------------------------------------------------
        // 1. EAGER collection - intermediate lists are created
        // ---------------------------------------------------------

        // Step 1: filter runs on ALL elements -> new list [2, 4, 6]
        // Step 2: map runs on that new list -> another new list [4, 8, 12]
        val eager = numbers.filter { it % 2 == 0 }.map { it * 10 }
        println(eager) // [4, 8, 12]

        // ---------------------------------------------------------
        // 2. LAZY sequence - one element at a time through the chain
        // ---------------------------------------------------------

        // For element 1: 1 -> filter(no) -> dropped.
        // For element 2: 2 -> filter(yes) -> map -> 4.
        // No intermediate lists are ever created.
        val lazy = numbers.asSequence()
            .filter { it % 2 == 0 }
            .map { it * 10 }
            .toList()   // terminal operation - nothing ran before this line
        println(lazy) // [20, 40, 60]

        // ---------------------------------------------------------
        // 3. Why sequences matter: short-circuit with terminal ops
        // ---------------------------------------------------------

        // With a collection, filter would process ALL 1M elements first.
        // With a sequence, it stops at the FIRST match - huge saving.
        val bigList = generateSequence(1) { it + 1 }   // infinite sequence!
        val firstSquareOver100 = bigList
            .map { it * it }
            .first { it > 100 }   // terminal - stops as soon as found
        println(firstSquareOver100) // 121

        // A collection CANNOT do this - an infinite list would never finish building.

        // ---------------------------------------------------------
        // 4. generateSequence - sequences from a seed
        // ---------------------------------------------------------
        val powers = generateSequence(1) { it * 2 }.take(5).toList()
        println(powers) // [1, 2, 4, 8, 16]

        /*
        * INTERVIEW: when is a sequence SLOWER?
        * - For tiny collections, sequence overhead (lambda calls per element)
        *   can be slower than simple eager operations.
        * - Also, some operations (like sorted()) are inherently eager even on sequences.
        * Rule: use sequences for LARGE data + CHAINED operations, otherwise keep it simple.
        * */
    }

    // =========================================================
    // 12b. COLLECTION TRANSFORMATIONS (senior interview favorites)
    // =========================================================
    private fun collectionTransformations() {

        /*
        * These functions appear in almost every Android interview:
        * groupBy, associateBy, associateWith, partition, sortedBy, take/drop.
        * They convert a list into maps, pairs, or filtered/sorted views.
        * */

        data class Employee(val name: String, val dept: String, val salary: Int)

        val employees = listOf(
            Employee("Amit", "Android", 50),
            Employee("Bina", "iOS", 45),
            Employee("Chetan", "Android", 60),
            Employee("Divya", "Backend", 55)
        )

        // ---------------------------------------------------------
        // 1. groupBy - groups elements into a Map<Key, List<T>>
        // ---------------------------------------------------------
        // ---------------------------------------------------------
        // 1. groupBy - groups elements into a Map<Key, List<T>>
        // ---------------------------------------------------------
        val byDept: Map<String, List<Employee>> = employees.groupBy { it.dept }
        println(byDept.keys) // [Android, iOS, Backend]
        println(byDept["Android"]?.size) // 2

        // ---------------------------------------------------------
        // 2. associateBy - Map<Key, T> (key from one property, value = whole element)
        //    If two elements have the same key, the LAST one wins.
        // ---------------------------------------------------------
        val byName: Map<String, Employee> = employees.associateBy { it.name }
        println(byName["Amit"]) // Employee(name=Amit, ...)

        // ---------------------------------------------------------
        // 3. associateWith - Map<T, V> (element itself as key)
        // ---------------------------------------------------------
        // ---------------------------------------------------------
        // 3. associateWith - Map<T, V> (element itself as key)
        // ---------------------------------------------------------
        val salaryByName: Map<String, Int> = employees.associate { it.name to it.salary }
        println(salaryByName) // {Amit=50, Bina=45, Chetan=60, Divya=55}

        // ---------------------------------------------------------
        // 4. partition - splits a list into (matching, non-matching) Pair
        // ---------------------------------------------------------
        val (android, others) = employees.partition { it.dept == "Android" }
        println("Android devs: ${android.size}, others: ${others.size}") // 2, 2

        // ---------------------------------------------------------
        // 5. sortedBy / sortedByDescending - sort by a property
        // ---------------------------------------------------------
        val bySalary = employees.sortedBy { it.salary }
        println(bySalary.first().name) // Bina (lowest salary)

        // ---------------------------------------------------------
        // 6. take / drop - first N / skip first N
        // ---------------------------------------------------------
        println(employees.take(2).size)  // 2
        println(employees.drop(2).size)  // 2

        // ---------------------------------------------------------
        // 7. sumOf / maxByOrNull / minByOrNull - quick aggregations
        // ---------------------------------------------------------
        println(employees.sumOf { it.salary })          // 210
        println(employees.maxByOrNull { it.salary }?.name) // Chetan
        println(employees.minByOrNull { it.salary }?.name) // Bina

        /*
        * INTERVIEW: groupBy vs associateBy
        * - groupBy     -> MANY values per key:  Map<Key, List<T>>
        * - associateBy -> ONE value per key:    Map<Key, T> (last wins on duplicates)
        * */
    }

    // =========================================================
    // 13. VALUE CLASS (inline class)
    // =========================================================

    // A value class wraps ONE value. It gives you a REAL new type at compile time
    // (type safety) with almost zero runtime cost - the wrapper usually disappears
    // after compilation and only the raw Int remains.
    @JvmInline
    value class Meters(val value: Int)

    @JvmInline
    value class Kilometers(val value: Int)

    private fun valueClassExample() {

        /*
        * value class = a performance-friendly wrapper around a SINGLE value.
        *
        * Problem it solves: passing raw Int/String everywhere is error-prone.
        * Which argument is the distance and which is the speed? Both are Int!
        *
        * A value class creates a distinct compile-time type, so you CANNOT
        * accidentally mix them up - but at runtime there is usually NO extra
        * object allocated (the compiler uses the underlying Int directly).
        *
        * Rules:
        * - Must have exactly ONE read-only ('val') property in the primary constructor.
        * - Must be marked @JvmInline (Kotlin/JVM requirement).
        * - Can have functions and implement interfaces.
        * - Boxing happens only when used as a generic type / nullable.
        * */

        // ---------------------------------------------------------
        // 1. Type-safe wrappers - cannot be mixed up
        // ---------------------------------------------------------
        val distance = Meters(500)
        println(distance.value) // 500

        // This would NOT compile - the compiler knows Meters and Int are different types:
        // val wrong: Int = distance   // ❌ type mismatch

        // ---------------------------------------------------------
        // 2. Functions on the value class
        // ---------------------------------------------------------
        fun Meters.toKilometers(): Double = value / 1000.0
        println(Meters(2500).toKilometers()) // 2.5

        // ---------------------------------------------------------
        // 3. Real-world use case: type-safe IDs
        // ---------------------------------------------------------
        fun sendNotification(userId: UserId, message: String) {
            println("Sent to $userId: $message")
        }
        sendNotification("user_1", "Hello") // Sent to user_1: Hello

        /*
        * INTERVIEW: value class vs typealias
        * - typealias  -> just a nickname, String and UserId are the SAME type, no safety.
        * - value class-> a NEW type; Meters(500) is NOT an Int, compiler blocks mixing.
        * - value class has near-zero cost: usually compiled to the raw primitive,
        *   only boxed when nullable/generic context forces it.
        * */
    }

    companion object {
        const val TAG = "log_D"
    }


}