package com.kotlinpractice

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    var TAG = "log_D"

    /*
    * lateinit keyword usage and rules.
    * For some variables, we don't want to initialize at the time of declaration, even with null or
    * empty values also. But if we try it in kotlin, it won't allow, hence for the rescue we use
    * lateinit keyword which tell compiler that we are not initializing it now, but will assign the
    * value at some later point of time.
    *
    * Note: 1.)But on condition that, this variable should not be accessed or used before initializing it,
    * otherwise compiler will throw uninitialized property exception.
    * 2.) lateinit can't be used with the variable whose datatypes are nullable, it works with non- nullable
    * types only
    *
    * 3.) lateinit can't be used with val, it should be used with var.
    *
    * */

    //Note here String is not-null type in kotlin, we cannot assign null value to string directly,
    //for that we use this --> var abc:String? = null, then it is possible.

    //So, w.r.t point 2.) if we add ? to String or any object type then, lateinit won't allow it.
    lateinit var name: String


    /*
    * lazy keyword usage and rules.
    *
    * In many scenarios we declare an variable and initialize it, but it is not used in the project,
    * but it still occupies memory which is waste. For this we use lazy keyword.
    *
    * If we used lazy for initializing any variable, then its memory is allocated/occupied only when it is
    * actually used, not just when we declare it. If the variable is used one time then it is
    * stored in cache for further use and it is thread safe too.
    *
    * lazy can be used with var or val, and datatypes can be nullable or non-nullable.
    *
    * */

    //normal variable initialization
    //var city:String = "abc"

    //lazy initialization
    val city: String by lazy { "abc" }


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
        nullSafety()
        inheritance()
        inheritanceWithPrimaryAndSecondaryConstructor()
        abstractAndInterface()
        dataClass()
        objectExplanation()
        companionObjects()
        getterAndSetter()
        backingProperty()
        higherOrderFunctions()
        scopeFunctions()
        arrays()
        collections()
        filterAndMap()
        predicates()
        extensionFunction()
        kotlinPractice();


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
        //Output -->  37 which incorrect.

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
        val users = arrayOf(
            User(id = 1, name = "Amit"),
            User(id = 2, name = "Ali"),
            User(id = 3, name = "Sumit"),
            User(id = 4, name = "Himanshu")
        )

        val userWithId3 = users.single { it.id == 3 }
        print(userWithId3) // User(id=3, name=Sumit)

        val userWithId1 = users.find { it.id == 1 }
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

    private fun higherOrderFunctions() {
        /*
        * In kotlin, we can pass function as argument to another function, or return function from
        * a function, or both. Those functions which satisfy any of these conditions below:
        * 1.) Function as argument to another.
        * 2.) Function returned from another function.
        * 3.) Both 1 and 2.
        *
        * are called Higher-order function
        * */

        //Normal function
        addNum(2, 3)

        //Normal function as expression
        maxValue(4, 7)

        //Here we are defining lambda function to pass this to addNumber Function.
        //Anything inside this {} is considered lambda function. In our example, first 2 values
        //are argument to function i.e a, b. Here we can also define types of a and b explicitly.
        // a+b represents the function body or implementation.

        //After we are assigning it to some variable name lambda, but question is what should be
        //the type of this variable? It is nothing but the types of arguments and return type. i.e.
        // (Int, Int) -> Int
        var lambda: (Int, Int) -> Int = { a, b -> a + b }
        // or this can written like this
        var lambdaAlt: (Int, Int) -> Int = { a: Int, b: Int -> a + b }

        addNumber(3, 6, lambda)
        Log.d(TAG, "higherOrderFunctions: ")

        //Note: we can directly pass the lambda function to addNumber, no need to assign it to
        //new variable and then passing that variable. Like this,
        addNumber(5, 7, { x: Int, y: Int -> x + y })  // Valid
        // Or we can write it like this also
        addNumber(5, 7) { x: Int, y: Int -> x + y }  // Valid


        //Closure
        var result = 0;
        //Here inside lambda function we are assigning sum of x+y to result. Meaning external
        //variable can be used inside Lambda function
        addTwoNumbers(3, 8) { x, y -> result = x + y }
        Log.d(TAG, "result : " + result)


        //it_keyword_lambda
        /*
        * it -->  is internal or implicit name of single parameter
        * */
        //Normal way
        reverseAndDisplay("Hello") { s -> s.reversed() }

        //If we only have single parameter then there is shortcut, now instead of writing complete
        //lambda, we can remove this part with "it" i.e. remove this s->s with it
        //Note: It only applies for single variable, if more than one, we have to write as usual.
        reverseAndDisplay("Hello") { it.reversed() }

    }

    private fun reverseAndDisplay(str: String, func: (String) -> String) {
        val reversed = func(str)
        Log.d(TAG, "reverseAndDisplay: " + reversed)
    }

    //Here x represent Int value meaning it accepts only Int values, similar for y.
    // But when we pass any function in addNumber function, we must also know the type
    // that addNumber will be accepting. For func variable, its type is (Int, Int) -> Int
    // means any function with 2 int args and return type as int is accepted.
    private fun addNumber(x: Int, y: Int, func: (Int, Int) -> Int) {
        func(x, y)

    }

    private fun maxValue(a: Int, b: Int): Int = if (a > b) a else b

    private fun addNum(a: Int, b: Int): Int {
        return a + b;
    }

    private fun addTwoNumbers(
        a: Int,
        b: Int,
        action: (Int, Int) -> Unit
    ) {  // High Level Function with Lambda as Parameter
        action(a, b)
    }

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

        var user1 = User("Sam", 10)

        var user2 = User("Sam", 10)

        println(user1.toString())

        if (user1 == user2)
            Log.d(TAG, "Equal")
        else
            Log.d(TAG, "Not equal")

        var newUser = user1.copy(name = "Harshit")
        println(newUser)

    }

    data class User(var name: String, val id: Int)

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
        * In Scope functions let, also the scope is not changed (remains the same as caller scope)
        * but your lambda will receive the context as it inside the lambda:
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

}