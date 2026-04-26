package com.kotlinpractice

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt
import kotlin.properties.Delegates

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
    //But below one is valid as
    lateinit var name: String

    /*++++
    * lazy keyword usage and rules.
    *
    * In many scenarios we declare an variable and initialize it, but it is not used in the project,
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
    val abc : Int by lazy{10}

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
    - Value stored (cached)
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
    }

    fun controlFlow() {
        // when with a subject, You check a single value against multiple options.
        val number = 2
        when (number) {
            1 -> println("One")
            2 -> println("Two")
            3, 4 -> println("Three or Four")
            else -> println("Other number")
        }

        //Above is used as statement, but below is where when is used as expression
        var a = 1
        val result = when (a) {
            1 -> "One"
            2 -> "Two"
            else -> "else"
        }
        Log.d(Companion.TAG, "controlFlow: $result")

        //Used when you want to check multiple conditions or ranges, not just one variable.
        val x = 15
        when {
            x < 0 -> println("Negative number")
            x in 1..10 -> println("Between 1 and 10")
            x % 2 == 0 -> println("Even number")
            else -> println("Other case")
        }

        do {
            println("start of do")
            a++
        } while (a < 1)


        var n = 12
        val limit = sqrt(n.toDouble()).toInt()

        for (i in 1..limit) {
            if (n % i == 0) {
                print("$i ")
                if (i != n / i) {
                    print("$(n/i) ")
                }
            }
        }
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

    private fun nullSafety() {
        // WAP to find out length of name
        val name: String? =/* null*/"Steve"     // change it to null and see the effect in output

        // 1. Safe Call ( ?. )
        // Returns the length if 'name' is not null else returns NULL
        // Use it if you don't mind getting NULL value
        println("The length of name is ${name?.length}")


        // 2. Safe Call with let ( ?.let )
        // It executes the block ONLY IF name is NOT NULL
        name?.let {
            println("The length of name is ${name.length}")
        }


        // 3. Elvis-operator ( ?: )
        // When we have nullable reference 'name', we can say "is name is not null", use it,
        // otherwise use some non-null value"
        val len = if (name != null)
            name.length
        else
            -1

        val length = name?.length ?: -1
        println("The length of name is ${length}")

        /** The expression name?.length ?: -1 works in two steps:
         * 1.Safe Call (?.): First, name?.length is evaluated. If name is not null, this expression returns the integer value of its length. If name is null, it returns null instead of throwing a NullPointerException.
         * 2.Elvis Operator (?:): Next, the Elvis operator checks the result of the safe call.
         * •If the result is not null (meaning name had a value), the Elvis operator returns that value (the length).
         * •If the result is null (meaning name was null), the Elvis operator returns the default value provided on its right side, which is -1.
         *
         * In short, the Elvis operator (?:) acts on the result of the expression to its left,
         * not on the original variable (name).
         * */

        // 4. Non-null assertion operator ( !! )
        // Use it when you are sure the value is NOT NULL
        // Throws NullPointerException if the value is found to be NULL

        println("The length of name is ${name!!.length}")
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

    private fun arrays() {
        // Elements :   32  0   0   54  0
        // Index    :   0   1   2   3   4

        //{0} will initialize all elements with 0
        val myArray = Array<Int>(5) { 0 }   // Mutable. Fixed Size.
        myArray[0] = 32
        myArray[3] = 54


        for (element in myArray) {              // Using individual elements (Objects)
            println(element)
        }

        println()

        for (index in 0..myArray.size - 1) {            //Iterating with indexes.
            println(myArray[index])
        }
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
        // ⚡ Use in algorithms or loops for better memory and speed
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

    class Demo() {
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

    private fun abstractAndInterface() {
        // For inheritance we use open keyword so that the class or method can be inherited.
        // but if want to make a variable,function or class as abstract, we don't need open keyword
        // alongside abstract, we can simply use abstract and open is added internally.
        //Rest all concepts are same with java.

        //interface
        // Interface can contain both normal and abstract methods but variable if declared, should
        // be abstract only. All concepts are same as java

        //Note: if we have two interface with same name methods and both are normal methods i.e.
        // with body defined and not abstract. Then if we implement both interfaces, and  if even
        //both methods are normal and its not compulsory to override them, compiler will show error
        // so acc. to rules we have in the above scenario, then its mandatory to override method, we
        //have choice to show among two.

    }

    private fun inheritanceWithPrimaryAndSecondaryConstructor() {

        //Imp-Note: Whenever we use inheritance then we must call primary constructor of super/parent class
        //if primary constructor of parent class has one or more parameters, then while creating primary
        //constructor of child class, make sure you get the required param from child class primary constructor
        //so that we can pass the same in parent class primary constructor.

        //for secondary constructors
        //for secondary constructor in child class, we must call super i.e parent class secondary constructor
        var dog = TheDog("Black", "Pug")

        //One more additional point related to access modifiers
        //in kotlin we have a modifier named internal which is same as default in java
        //rest all are same i.e. public, protected and private.

    }

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

    private fun inheritance() {
        var dog = Dog()
        dog.bread = "labra"
        dog.color = "black"
        dog.bark()
        dog.eat()

        var cat = Cat()
        cat.age = 7
        cat.color = "brown"
        cat.meow()
        cat.eat()

        var animal = Animal()
        animal.color = "white"
        animal.eat()
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
        // eat method not interface one by specifying name of class in <>

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

        //These two functions are used , they are same function but it can be used as inline function
        inlineFunc(5, 6);
        inlineFuncAlt(5, 6);


        //In kotlin we can assign the function itself to a var , means a variable can hold a function
        var ft = ::normalFunc;

        //now using ft call function
        val returnedValue = ft(4, 5);
        Log.d(TAG, "kotlinPractice: " + returnedValue)
    }


    private fun normalFunc(a: Int, b: Int): Int {
        return a + b;
    }

    //Here, till return type Int , this function is same as normal , but instead of writing
    // a+b in body and then returning it, we can directly assign the value to Int itself.
    private fun inlineFunc(a: Int, b: Int): Int = a + b

    //or this can also be used, both are same, here we know a+b will always be int, so, no need
    // for returntype
    private fun inlineFuncAlt(a: Int, b: Int) = a + b


    private fun sealedClassesExample() {
        Log.d(TAG, "--- Sealed Classes Example ---")

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
    }

    // Sealed classes are perfect for modeling states that can hold different data.
// For example, the result of a network request can be Success (with data),
// Error (with a message), or Loading.
// All direct subclasses MUST be inside this sealed class or in the same file.
    sealed class NetworkResult {
        // A 'data class' is great for states that hold data.
        data class Success(val data: String) : NetworkResult()

        // Another 'data class' for a different state with different data.
        data class Error(val code: Int, val message: String) : NetworkResult()

        // An 'object' is perfect for states that don't need to hold any data.
        // It's a singleton, so only one instance exists.
        object Loading : NetworkResult()

        // You can also have a regular class as a subclass.
        class Unauthorized(val reason: String) : NetworkResult()
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

    companion object {
        const val TAG = "log_D"
    }

    fun varValConst() {
        var counter: Int = 0
        //counter = 10 // This is valid because 'counter' is a var.

        // 2. val (short for value)
        /*
     * - It is immutable (read-only).
     * - It must be initialized at the time of declaration or in an init block.
     * - Once assigned, it CANNOT be reassigned. This helps prevent accidental changes.
     * - The object it refers to might still be mutable itself.
     * - 'val' is preferred over 'var' for safer, more predictable code.
     */
        val languageName: String = "Kotlin"
        // languageName = "Java" // ERROR: Val cannot be reassigned.

        /*
     * The object a 'val' refers to can still have its internal state changed.
     * This is a key concept: the reference is immutable, not necessarily the object.
     */
        val nameList = mutableListOf("John")
        // nameList = mutableListOf("Jane") // ERROR: The 'val' reference cannot be changed.
        nameList.add("Doe")                  // OK: The list object itself is mutable.

        /*
     * --- In-depth: val vs. const ---
     *
     * The key difference is WHEN the value is assigned.
     * 'val' is a runtime constant, while 'const' is a compile-time constant.
     */

        /**
         * 'val' - Runtime Constant
         *
         * The value is assigned at runtime when the code is executed.
         * This means it can be assigned the result of a function call or any other runtime logic.
         * It can be declared inside a class, a function, or at the top level of a file.
         */
        val runtimeValue: Double = Math.random() // Value is determined when this line runs.
        val currentTime: Long =
            System.currentTimeMillis() // Value depends on when the code executes.

        /*
 * --- An Important Note on Syntax: Why "const val" and not just "const"? ---
 *
 * In Kotlin's grammar, 'const' is a MODIFIER, not a declaration keyword like 'val' or 'var'.
 * A modifier changes the characteristics of a declaration.
 *
 * Think of it like a label you attach to a variable:
 *  - 'val' declares: "This is a read-only variable."
 *  - 'const' modifies that declaration: "And make it a compile-time constant."
 *
 * Therefore, the two must be used together to create a compile-time constant.
 *
 * // CORRECT: 'const' modifies a 'val' declaration.
 * const val MAX_SIZE = 100
 *
 * // INCORRECT: 'const' alone is not a declaration and results in a syntax error.
 * // const MAX_SIZE = 100 // ✗ This will not compile.
 *
 * Since all 'const' values must be known at compile time, they are inherently read-only,
 * which is why you can only use 'const' with 'val' and never with 'var'.
 */
    }

}