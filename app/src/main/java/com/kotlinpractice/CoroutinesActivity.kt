package com.kotlinpractice

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Deferred
import kotlin.coroutines.ContinuationInterceptor
import java.util.concurrent.Executors

/*
* ============================================================
* COROUTINES - FROM ZERO TO EXPERT (A complete course in one file)
* ============================================================
*
* PART 0 - WHY COROUTINES EXIST AT ALL
*
* THE PROBLEM BEFORE COROUTINES: THREADS
*
* A thread is the smallest unit of execution the OS schedules on a CPU core.
* Every app starts with one main thread. On Android, the main thread is where
* ALL UI work happens. If you block it for more than ~5 seconds, the system
* shows an ANR (Application Not Responding) dialog and can kill your app.
*
* So network calls, database reads, file IO must NOT run on the main thread.
* The classic solution was: create a background THREAD, do the work there, then
* post the result back to the main thread. That caused real problems:
*
* 1. Threads are EXPENSIVE.
*    Each thread reserves ~1MB of stack memory and creating/switching between
*    them involves the OS kernel. Spawning hundreds of threads wastes memory
*    and burns CPU on context switching, not on your actual work.
*
* 2. Threads are BLOCKING.
*    Thread.sleep(5000) holds the whole thread hostage for 5 seconds. The OS
*    thread sits idle doing nothing. With few threads this wastes resources;
*    with many threads it exhausts them.
*
* 3. Callbacks made code unreadable (Callback Hell).
*    To avoid blocking, you "don't wait" - you say "call me back when done":
*
*        api.getUser(userId) { user ->            // callback 1
*            db.save(user) { saved ->             // callback 2
*                api.getFriends(user.id) { friends ->   // callback 3
*                    runOnUiThread { updateUi(friends) }  // nesting grows forever
*                }
*            }
*        }
*
*    This is "Callback Hell": deeply nested, hard to read, impossible to use
*    normal control flow (loops, try/catch) across the nesting, and errors get
*    swallowed because each callback needs its own error handling.
*
* 4. Manual thread management is error-prone.
*    You must handle synchronization yourself (synchronized, locks, volatile),
*    cancel threads manually (there is no safe way to stop a thread in Java!),
*    and track lifecycle yourself (a thread outliving the Activity = leak).
*
* THE KEY INSIGHT: the problem was never "I need another thread".
* The problem was: "I need to WAIT for something without blocking the thread
* I am on". Waiting should be FREE, not cost a thread.
*
* COROUTINES = WAITING WITHOUT BLOCKING
*
* A coroutine is a piece of code that can PAUSE (suspend) without blocking the
* thread, and RESUME later - possibly on a different thread. While the coroutine
* "waits", the thread is released to do other work.
*
* - You can run 100,000 coroutines on a single thread. Try that with threads -
*   the JVM would run out of memory long before.
* - Code reads sequentially (no nesting), exceptions work with normal try/catch,
*   cancellation is cooperative and built-in.
*
* The magic that makes this possible is SUSPENSION: the compiler transforms your
* function into a state machine that can save its state (local variables, where
* it was) and restore it later. This is why suspend functions can "pause" -
* they don't pause the thread, they pause THEMSELVES and return control of the
* thread back to the dispatcher.
*
* MENTAL MODEL (memorize this):
* - Coroutine = a block of code that can pause and resume (like a pauseable function).
* - Dispatcher = WHICH thread(s) the coroutine runs on.
* - Scope = the LIFETIME owner of coroutines (who cancels them and when).
* - Job = the HANDLE to a coroutine (cancel it, wait for it, track children).
* */
class CoroutinesActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CoroutinesCourse"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines)

        // The course runs top to bottom. Each method is one chapter.
        // Output goes to Logcat - filter by TAG "CoroutinesCourse".
        whyThreadsWereNotEnough()
        firstCoroutine()
        suspendFunctions()
        dispatchers()
        coroutineContext()
        scopes()
        jobsAndHierarchy()
        cancellation()
        exceptionHandling()
        sequentialVsParallel()
        structuredConcurrency()
        sharedStateAndChannels()
    }

    // ============================================================
    // CHAPTER 1 - THE THREAD PROBLEM, DEMONSTRATED
    // ============================================================
    private fun whyThreadsWereNotEnough() {

        /*
        * This chapter shows WHY we needed something better than raw threads.
        * */

        // ---------------------------------------------------------
        // 1.1 Threads are expensive - measure memory per thread
        // ---------------------------------------------------------

        // Each JVM thread reserves ~1MB of stack. 1000 threads = ~1GB wasted.
        // Coroutines: each is just an object on the heap - a few bytes to KBs.
        Log.d(TAG, "Default thread stack size: ${getThreadStackSize()}")

        // ---------------------------------------------------------
        // 1.2 Threads BLOCK - the thread does nothing while waiting
        // ---------------------------------------------------------

        // Thread.sleep BLOCKS the current thread. On the main thread this
        // would freeze the UI. The thread is alive but doing NOTHING.
        val start = System.currentTimeMillis()
        Thread.sleep(100)   // main thread is frozen for 100ms
        Log.d(TAG, "Thread blocked for ${System.currentTimeMillis() - start} ms")

        // ---------------------------------------------------------
        // 1.3 Callback hell - the "solution" that made things worse
        // ---------------------------------------------------------

        // This is what async code looked like before coroutines.
        // Imagine 5 levels of nesting, error handling at every level...
        fakeNetworkCall("user_1") { user ->
            fakeNetworkCall(user) { profile ->
                Log.d(TAG, "Callback hell result: $profile")
                // try/catch? loops? Not across callbacks. This is the pain.
            }
        }

        // ---------------------------------------------------------
        // 1.4 The same flow with a coroutine - reads top to bottom
        // ---------------------------------------------------------

        // runBlocking = a bridge: it BLOCKS the current thread and starts a
        // coroutine on it. Used here ONLY because onCreate is not a suspend
        // function. In real Android code you use lifecycleScope/viewModelScope
        // instead - never runBlocking in production UI code.
        runBlocking {
            val user = fetchUser("user_1")       // suspends - does NOT block the thread
            val profile = fetchProfile(user)     // reads like normal sequential code
            Log.d(TAG, "Coroutine result: $profile")
            // Normal try/catch works across suspension points!
        }
    }

    // Fake callback-style API (what Java/old Android code looked like)
    private fun fakeNetworkCall(input: String, onDone: (String) -> Unit) {
        Thread {
            Thread.sleep(50)
            onDone("$input -> processed")
        }.start()
    }

    // Fake suspend-style API
    private suspend fun fetchUser(id: String): String {
        delay(50)   // suspends, does NOT block the thread
        return "user_$id"
    }

    private suspend fun fetchProfile(user: String): String {
        delay(50)
        return "profile for $user"
    }

    private fun getThreadStackSize(): Long {
        // Default stack size per thread (usually 1MB = 1048576 bytes)
        return Thread.currentThread().let {
            try {
                val field = Thread::class.java.getDeclaredField("stackSize")
                field.isAccessible = true
                field.getLong(this) 
            } catch (e: Exception) {
                1048576L // typical default
            }
        }
    }

    // ============================================================
    // CHAPTER 2 - YOUR FIRST COROUTINE: launch, delay, runBlocking
    // ============================================================
    private fun firstCoroutine() {

        /*
        * THE TWO BUILDERS YOU MUST KNOW:
        *
        * launch = "fire and forget". Starts a coroutine that does work and
        *          returns a Job. Does NOT return a result to the caller.
        *
        *   val job: Job = scope.launch { doWork() }
        *
        * async = "fire and get result later". Returns Deferred<T> -
        *         call .await() on it to GET the value when ready.
        *
        *   val deferred: Deferred<String> = scope.async { compute() }
        *   val result = deferred.await()
        *
        * delay() vs Thread.sleep() - THE core difference:
        * - Thread.sleep(1000): the THREAD is blocked 1s. Nothing else can use it.
        * - delay(1000): the COROUTINE pauses 1s. The THREAD is immediately free
        *   to run other coroutines. delay is a suspend function - it suspends
        *   the coroutine, not the thread.
        * */

        // ---------------------------------------------------------
        // 2.1 launch - start work, don't wait for a result
        // ---------------------------------------------------------
        runBlocking {
            Log.d(TAG, "Before launch: thread = ${Thread.currentThread().name}")

            val job: Job = launch {
                delay(100)
                Log.d(TAG, "Inside coroutine after delay")
            }

            // launch returns IMMEDIATELY - the code below runs BEFORE
            // the coroutine body (which is waiting on delay).
            Log.d(TAG, "After launch (coroutine may not have run yet)")

            job.join()   // suspend until the coroutine completes
            Log.d(TAG, "After join - coroutine finished")
        }

        // ---------------------------------------------------------
        // 2.2 delay does NOT block the thread - proof
        // ---------------------------------------------------------

        // We launch 3 coroutines that each "wait" 100ms.
        // If delay blocked the thread, this would take 300ms.
        // It takes ~100ms because all 3 wait in PARALLEL on the same thread.
        val start = System.currentTimeMillis()
        runBlocking {
            repeat(3) { index ->
                launch {
                    delay(100)
                    Log.d(TAG, "Coroutine $index done")
                }
            }
            // (runBlocking's scope waits for all children automatically)
        }
        Log.d(TAG, "3 x 100ms waits took ${System.currentTimeMillis() - start} ms (not 300!)")

        // ---------------------------------------------------------
        // 2.3 runBlocking - what it really is, and when to use it
        // ---------------------------------------------------------

        /*
        * runBlocking { } :
        * - Starts a coroutine and BLOCKS the current thread until it completes.
        * - It BRIDGES the normal blocking world and the coroutine world.
        * - Legit uses: main() functions in demos, unit tests, playgrounds.
        * - NEVER in real Android production code - blocking the main thread
        *   is exactly what coroutines exist to avoid. On Android use
        *   lifecycleScope, viewModelScope, or your own scope instead.
        * */
    }

    // ============================================================
    // CHAPTER 3 - SUSPEND FUNCTIONS: the heart of coroutines
    // ============================================================
    private fun suspendFunctions() {

        /*
        * A 'suspend' function = a function that can PAUSE without blocking the thread.
        *
        * Rules:
        * - suspend functions can only be called from other suspend functions
        *   or inside coroutines. Why? Because suspension needs a "continuation" -
        *   someone must own the paused state. A normal function has no such machinery.
        * - suspend is a HINT TO THE COMPILER: it transforms the function into a
        *   state machine with a Continuation parameter (see below).
        * - delay(), withContext(), join(), await() are all just suspend functions.
        *   There is no special "pause" instruction - pausing IS calling a suspend
        *   function that returns a special value telling the caller "I'm not done,
        *   resume me later".
        *
        * HOW IT WORKS UNDER THE HOOD (interview gold):
        * Every suspend function secretly takes a Continuation<T> parameter:
        *
        *   suspend fun fetchUser(id: String): String
        *   // compiles to:
        *   fun fetchUser(id: String, continuation: Continuation<String>): Any
        *
        * Continuation = "what to do next when this resumes". The compiler splits
        * your function at every suspension point into states of a state machine.
        * When the function suspends, it returns CoroutineSingletons.SUSPENDED and
        * the thread moves on. When the awaited result arrives, the continuation's
        * resume() is called and the state machine jumps to the saved state with
        * all local variables restored.
        *
        * SUSPENSION POINT = a call to a suspend function. Between suspension
        * points your code runs like normal, sequential code.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 3.1 Calling suspend functions - sequential, no nesting
            // ---------------------------------------------------------
            val user = fetchUser("42")
            val profile = fetchProfile(user)
            Log.d(TAG, "Sequential suspend calls: $profile")

            // ---------------------------------------------------------
            // 3.2 suspend functions work with normal control flow
            // ---------------------------------------------------------
            try {
                repeat(3) { attempt ->
                    delay(50)
                    Log.d(TAG, "Attempt $attempt")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Caught: ${e.message}")
            }

            // ---------------------------------------------------------
            // 3.3 A suspend function can call another suspend function -
            //     the "suspension" propagates up to the coroutine builder
            // ---------------------------------------------------------
            logWithDelay("This message waited 50ms without blocking any thread")
        }

        /*
        * INTERVIEW QUESTION:
        * Q: Does a suspend function run on a background thread automatically?
        * A: NO! suspend does NOT switch threads. A suspend function runs on the
        *    SAME dispatcher/thread as its caller. To switch threads you must use
        *    withContext(Dispatchers.IO) { } explicitly. This is a very common
        *    misconception - "suspend = background" is FALSE.
        * */
    }

    private suspend fun logWithDelay(message: String) {
        delay(50)
        Log.d(TAG, message)
    }

    // ============================================================
    // CHAPTER 4 - DISPATCHERS: which thread(s) run your coroutine
    // ============================================================
    private fun dispatchers() {

        /*
        * A DISPATCHER decides which thread(s) a coroutine runs on.
        * It is one piece of CoroutineContext (see next chapter).
        *
        * THE 4 STANDARD DISPATCHERS:
        *
        * Dispatchers.Main
        *   - The Android main/UI thread. ONLY thread allowed to touch Views.
        *   - On Android requires kotlinx-coroutines-android artifact.
        *   - Use for: UI updates, small quick work.
        *
        * Dispatchers.IO
        *   - A shared pool of 64 threads (elastic - grows as needed).
        *   - Designed for BLOCKING work: network, database, file IO.
        *   - It is safe to BLOCK these threads (that's their job).
        *
        * Dispatchers.Default
        *   - Pool of threads = number of CPU cores (min 2).
        *   - Designed for CPU-HEAVY work: parsing big JSON, sorting, image
        *     processing, encryption. Blocking these starves computation.
        *
        * Dispatchers.Main.immediate
        *   - Like Main but WITHOUT the post-to-queue hop if already on main.
        *   - launch on Main from the main thread: with .immediate the body runs
        *     immediately; without it, it is queued and runs after onCreate ends.
        *   - This is why lifecycleScope.launch(Main.immediate) is the default
        *     pattern in Android - avoids one unnecessary thread hop.
        *
        * Dispatchers.Unconfined (rarely used, know it for interviews)
        *   - Runs on the caller thread UNTIL the first suspension point, then
        *     resumes on whatever thread the resuming code used. "Unconfined" =
        *     no fixed thread. Avoid in production - makes execution order
        *     unpredictable.
        *
        * RULE OF THUMB (Android):
        * - Network/DB/File -> IO
        * - Heavy computation -> Default
        * - UI updates -> Main
        * */

        // ---------------------------------------------------------
        // 4.1 See which thread each dispatcher uses
        // ---------------------------------------------------------
        runBlocking {
            Log.d(TAG, "runBlocking thread: ${Thread.currentThread().name}")

            launch(Dispatchers.Main) {
                Log.d(TAG, "Main: ${Thread.currentThread().name}")   // main
            }
            launch(Dispatchers.IO) {
                Log.d(TAG, "IO: ${Thread.currentThread().name}")     // DefaultDispatcher-worker-N
            }
            launch(Dispatchers.Default) {
                Log.d(TAG, "Default: ${Thread.currentThread().name}") // DefaultDispatcher-worker-N
            }
        }

        // ---------------------------------------------------------
        // 4.2 withContext - switch dispatcher INSIDE a coroutine
        // ---------------------------------------------------------

        /*
        * withContext(Dispatcher) { } :
        * - A SUSPEND function that switches the current coroutine to the given
        *   dispatcher, runs the block, and returns its result.
        * - When the block finishes, you are BACK on the original dispatcher
        *   automatically. This is the idiomatic way to move work between threads.
        * - It suspends (does not block) while switching.
        * */

        runBlocking {
            Log.d(TAG, "Start on: ${Thread.currentThread().name}")

            val result = withContext(Dispatchers.IO) {
                Log.d(TAG, "Inside IO: ${Thread.currentThread().name}")
                delay(100)   // pretend network call
                "network result"
            }
            // We are BACK on the original dispatcher here - automatically.
            Log.d(TAG, "Back on: ${Thread.currentThread().name}, got: $result")
        }

        // ---------------------------------------------------------
        // 4.3 The Android pattern: suspend function + withContext inside
        // ---------------------------------------------------------

        /*
        * BEST PRACTICE - "suspend functions should be main-safe":
        * The CALLER stays on Main; the function itself jumps to IO internally.
        * The UI layer never worries about threads:
        *
        *   suspend fun loadUser(): User = withContext(Dispatchers.IO) {
        *       api.getUser()   // blocking call, safe here
        *   }
        *   // caller: val user = loadUser()  // safe to call from Main
        *
        * This is "separation of concerns": the function OWNS its threading,
        * the caller owns the LIFETIME (scope). Neither knows the other's job.
        * */

        runBlocking {
            val data = loadUserData()
            Log.d(TAG, "Loaded on main thread, got: $data")
        }

        // ---------------------------------------------------------
        // 4.4 Custom dispatcher - your own thread pool
        // ---------------------------------------------------------

        // Sometimes you need your own pool (e.g. a single-threaded dispatcher
        // to serialize access to something, like a DB transaction queue).
        val singleThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        runBlocking {
            withContext(singleThread) {
                Log.d(TAG, "Custom dispatcher thread: ${Thread.currentThread().name}")
            }
        }
        singleThread.close()   // always shut down executors you create

        /*
        * INTERVIEW QUESTIONS:
        * Q: Difference between Dispatchers.IO and Default?
        * A: IO = 64 elastic threads for BLOCKING work. Default = #cores threads
        *    for CPU work. Mixing them up doesn't crash, but blocks the wrong pool.
        *
        * Q: withContext(Dispatchers.Main) from the main thread - does it switch?
        * A: No. If you are already on that dispatcher, withContext does not
        *    redispatch (except Unconfined semantics) - it just runs the block.
        *
        * Q: Is withContext the same as async+await?
        * A: withContext is SEQUENTIAL (caller waits), async is for PARALLEL work.
        *    withContext(Dispatchers.IO) { api.call() } runs the call and returns
        *    its result - it does not run anything in parallel.
        * */
    }

    // Example of the "main-safe" suspend function pattern
    private suspend fun loadUserData(): String = withContext(Dispatchers.IO) {
        delay(100)   // pretend network call (blocking-style code is fine on IO)
        "User(harshit)"
    }

    // ============================================================
    // CHAPTER 5 - COROUTINE CONTEXT: the data behind every coroutine
    // ============================================================
    private fun coroutineContext() {

        /*
        * CoroutineContext = the SET OF DATA attached to every coroutine:
        *   - Job        (its lifecycle/cancellation handle)
        *   - Dispatcher (which threads it runs on)
        *   - CoroutineName (debugging name)
        *   - CoroutineExceptionHandler (uncaught error handler)
        *
        * Think of it as an indexed set: each element type appears at most once.
        * Combining with '+' REPLACES the element of the same type:
        *
        *   Dispatchers.IO + CoroutineName("network") + Job()
        *
        * CONTEXT INHERITANCE (crucial):
        * A child coroutine inherits the parent's context, EXCEPT:
        * - Its Job is a NEW child Job (parent-child link).
        * - Anything you explicitly pass to the builder overrides the inherited value.
        *
        *   scope.launch(Dispatchers.IO, CoroutineName("child")) { }
        *   // child context = parent context + IO + "child" name + new child Job
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 5.1 Reading the current context
            // ---------------------------------------------------------
            launch(CoroutineName("myNamedCoroutine")) {
                // coroutineContext is available inside every coroutine
                Log.d(TAG, "Name: ${coroutineContext[CoroutineName]}")
                Log.d(TAG, "Dispatcher: ${coroutineContext[ContinuationInterceptor]}")
                Log.d(TAG, "Job active: ${coroutineContext[Job]?.isActive}")
            }

            // ---------------------------------------------------------
            // 5.2 Combining context elements - later wins
            // ---------------------------------------------------------
            launch(Dispatchers.IO + CoroutineName("combined")) {
                Log.d(TAG, "Running: ${coroutineContext[CoroutineName]} on ${Thread.currentThread().name}")
            }

            // ---------------------------------------------------------
            // 5.3 CoroutineName is for debugging - shows in stack traces
            // ---------------------------------------------------------
            launch(CoroutineName("DebugMe")) {
                delay(50)
            }
        }

        /*
        * INTERVIEW QUESTION:
        * Q: What is inside a coroutine's CoroutineContext?
        * A: Job (lifecycle), ContinuationInterceptor (the dispatcher),
        *    CoroutineName, CoroutineExceptionHandler, and any custom elements
        *    you add. Elements combine with '+' and later elements override
        *    earlier ones of the same type.
        * */
    }

    // ============================================================
    // CHAPTER 6 - SCOPES: who owns the coroutine's lifetime
    // ============================================================
    private fun scopes() {

        /*
        * A CoroutineScope = an object that carries a CoroutineContext and can
        * CREATE coroutines (launch/async are extensions on CoroutineScope).
        *
        * WHY SCOPES EXIST - LIFETIME MANAGEMENT:
        * Coroutines must be CANCELLED when their owner dies, otherwise they leak:
        * - Activity destroyed but its coroutine still updating UI -> crash/leak.
        * - ViewModel cleared but its coroutine still doing network -> waste/leak.
        *
        * A scope is the "lifetime owner": when the scope is cancelled, ALL its
        * coroutines are cancelled automatically. This is the separation of
        * concerns in Android:
        *
        *   - lifecycleScope  -> lives as long as the Activity (androidx.lifecycle)
        *   - viewModelScope  -> lives until ViewModel.onCleared()
        *   - custom scope    -> you own it, you cancel it
        *
        * THE GOLDEN RULE: never use GlobalScope in Android apps.
        * GlobalScope.launch lives for the whole process - it ignores Activity/
        * ViewModel lifetimes, leaks memory, and cannot be tied to lifecycle.
        * It exists for global singletons that truly live app-long.
        *
        * BUILDING YOUR OWN SCOPE:
        *   val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        * The Job is the scope's "root". Cancelling the root cancels everything.
        * */

        // ---------------------------------------------------------
        // 6.1 Creating and cancelling a custom scope
        // ---------------------------------------------------------
        val scope = CoroutineScope(Job() + Dispatchers.Default + CoroutineName("MyScope"))

        scope.launch {
            repeat(5) { i ->
                delay(100)
                Log.d(TAG, "Scope coroutine tick $i")
            }
        }

        Thread.sleep(250)   // let it run a bit
        scope.cancel()      // cancels the root Job -> cancels ALL children
        Log.d(TAG, "Scope cancelled - no more ticks")

        // ---------------------------------------------------------
        // 6.2 Why scope cancellation matters on Android
        // ---------------------------------------------------------

        /*
        * WITHOUT scopes (the leak):
        *   class MyActivity {
        *       override fun onCreate(...) {
        *           GlobalScope.launch { updateUiForever() }  // runs FOREVER,
        *           // even after Activity is destroyed -> leak + crash on UI access
        *       }
        *   }
        *
        * WITH lifecycleScope (the Android way):
        *   lifecycleScope.launch { updateUi() }  // auto-cancelled when Activity
        *   // is destroyed. No leaks. This is why scopes exist.
        * */

        /*
        * INTERVIEW QUESTIONS:
        * Q: What is the difference between a scope and a dispatcher?
        * A: A dispatcher decides WHERE coroutines run. A scope decides HOW LONG
        *    they live. A scope = context (usually Job + dispatcher).
        *
        * Q: Why is GlobalScope bad?
        * A: Its coroutines live for the whole process - they outlive screens,
        *    leak memory, keep running after the user leaves, and can't be
        *    cancelled per-screen. Use lifecycle-aware scopes instead.
        * */
    }

    // ============================================================
    // CHAPTER 7 - JOB: the handle to a coroutine + parent-child hierarchy
    // ============================================================
    private fun jobsAndHierarchy() {

        /*
        * EVERY coroutine has a Job. The Job is its handle:
        *   - job.cancel()      -> cancel it
        *   - job.join()        -> wait for it to finish (suspend)
        *   - job.isActive      -> is it still running?
        *   - job.children      -> its child jobs
        *
        * THE PARENT-CHILD HIERARCHY (the most important concept):
        * Every coroutine launched inside another coroutine (or a scope) becomes
        * a CHILD of the parent's Job. This creates a TREE:
        *
        *   Parent Job
        *   ├── child coroutine 1
        *   │   └── grandchild coroutine
        *   └── child coroutine 2
        *
        * THE THREE RULES OF THE HIERARCHY:
        *
        * 1. Cancellation flows DOWN: cancelling a parent cancels all children.
        * 2. Exceptions propagate UP: a child's failure cancels the parent (and
        *    thus all siblings) - unless you use SupervisorJob (next chapter).
        * 3. The parent WAITS for children: a parent does not complete until all
        *    its children complete. (This is why runBlocking waited for the
        *    3 launches in chapter 2 without any join.)
        *
        * WHY THIS DESIGN?
        * Because "cancel everything under this scope" is what real apps need:
        * user leaves screen -> cancel screen's whole coroutine tree in one call.
        * Manual thread management could never do this safely.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 7.1 The parent waits for children automatically
            // ---------------------------------------------------------
            launch {
                delay(100)
                Log.d(TAG, "Child 1 done")
            }
            launch {
                delay(200)
                Log.d(TAG, "Child 2 done")
            }
            // runBlocking will NOT return until both children finish.
            // This is rule 3: parent waits for children.
        }
        Log.d(TAG, "runBlocking finished - all children completed")

        // ---------------------------------------------------------
        // 7.2 Cancellation flows down - cancel parent, children die
        // ---------------------------------------------------------
        runBlocking {
            val parent = launch {
                launch {
                    repeat(10) { i ->
                        delay(100)
                        Log.d(TAG, "Grandchild still alive: $i")
                    }
                }
                launch {
                    delay(100)
                    Log.d(TAG, "Child 2 done")
                }
            }
            delay(250)
            Log.d(TAG, "Cancelling parent...")
            parent.cancel()   // flows down: child + grandchild are cancelled too
            Log.d(TAG, "Parent cancelled - grandchild stopped mid-repeat")
        }

        // ---------------------------------------------------------
        // 7.3 Job states (interview favorite)
        // ---------------------------------------------------------

        /*
        * Job lifecycle states:
        *   New (created, not started yet - lazy start)
        *   -> Active      (running; isActive = true)
        *   -> Cancelling  (cancel() called; finishing current suspension)
        *   -> Cancelled   (fully cancelled; isCancelled = true)
        *   -> Completed   (finished normally)
        *
        *   val job = launch { }        // Active immediately
        *   val lazy = launch(start = CoroutineStart.LAZY) { }  // New until .start()
        *   job.cancel()                // Cancelling -> Cancelled
        *   job.join()                  // wait for terminal state
        *
        * A cancelled Job can NEVER be restarted. Cancel = terminal.
        * To "restart", create a new coroutine.
        * */

        runBlocking {
            val job = launch(start = CoroutineStart.LAZY) {
                Log.d(TAG, "Lazy coroutine ran")
            }
            Log.d(TAG, "Before start: isActive=${job.isActive}")   // false (still New)
            job.start()
            job.join()
            Log.d(TAG, "After join: isActive=${job.isActive}")     // false (completed)
        }

        // ---------------------------------------------------------
        // 7.4 The 4 CoroutineStart options (interview favorite)
        // ---------------------------------------------------------

        /*
        * CoroutineStart controls HOW a coroutine starts:
        *
        * CoroutineStart.DEFAULT   - start immediately (queued to dispatcher)
        * CoroutineStart.LAZY      - don't start until start()/join()/await()
        * CoroutineStart.ATOMIC    - start even if cancelled, cannot be cancelled
        *                            before first suspension point
        * CoroutineStart.UNDISPATCHED - start immediately on current thread,
        *                            but suspend normally afterwards
        * */

        runBlocking {
            val lazyJob = launch(start = CoroutineStart.LAZY) {
                Log.d(TAG, "Lazy started")
            }
            lazyJob.start()   // must start it manually
            lazyJob.join()
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: What happens when you cancel a parent coroutine?
        * A: All children are cancelled too (cancellation flows down the tree).
        *    The parent cannot complete while children are running.
        *
        * Q: Can you restart a cancelled Job?
        * A: No. Cancellation is terminal. Create a new coroutine instead.
        *
        * Q: What is the difference between cancel() and join()?
        * A: cancel() requests cancellation (cooperative - see next chapter).
        *    join() suspends until the job reaches a terminal state.
        *    cancelAndJoin() = both: cancel, then wait for it to actually stop.
        * */
    }

    // ============================================================
    // CHAPTER 8 - CANCELLATION: cooperative, and why it must be
    // ============================================================
    private fun cancellation() {

        /*
        * CANCELLATION IS COOPERATIVE - the single most misunderstood topic.
        *
        * job.cancel() does NOT kill the coroutine. Nothing in Kotlin can kill
        * running code (same as threads - Thread.stop() is deprecated/unsafe).
        * Instead, cancellation SETS A FLAG, and the coroutine must CHECK that
        * flag and exit itself. This is "cooperative cancellation".
        *
        * WHERE does the coroutine check the flag?
        * - Every SUSPEND function from the library (delay, withContext, yield,
        *   join, await...) checks cancellation on entry and throws
        *   CancellationException if the job was cancelled.
        * - Your own code can check explicitly: isActive, ensureActive(),
        *   or yield().
        *
        * WHY cooperative? Because killing a thread mid-work is DANGEROUS:
        * locks stay held, files stay open, data is half-written. Only the code
        * itself knows where a safe stopping point is. So the library asks,
        * the code decides.
        *
        * THE #1 REAL-WORLD BUG: a coroutine that never suspends never notices
        * cancellation. A tight CPU loop with no suspend calls runs forever
        * even after cancel(). Fix: check isActive or call ensureActive() inside
        * the loop.
        * */

        // ---------------------------------------------------------
        // 8.1 Cancellation works at suspension points
        // ---------------------------------------------------------
        runBlocking {
            val job = launch {
                repeat(1000) { i ->
                    delay(50)   // <- suspension point: checks cancellation here
                    Log.d(TAG, "tick $i")
                }
            }
            delay(200)
            job.cancel()          // sets the flag
            job.join()            // wait until it actually stops
            Log.d(TAG, "Cancelled cleanly at a suspension point")
        }

        // ---------------------------------------------------------
        // 8.2 THE BUG: CPU loop never suspends -> never cancels
        // ---------------------------------------------------------
        runBlocking {
            val job = launch(Dispatchers.Default) {
                var nextPrint = 0
                // BUGGY version (commented out) - would run forever:
                // repeat(1_000_000_000) { heavyComputation() }   // no suspend calls!

                // FIXED version - check the flag inside the loop:
                while (isActive) {   // isActive checks the cancellation flag
                    // heavyComputation()
                    if (++nextPrint % 100_000 == 0) {
                        Log.d(TAG, "Working... $nextPrint (cancellable loop)")
                        yield()      // also gives other coroutines a chance to run
                    }
                    if (nextPrint > 500) break   // keep the demo short
                }
            }
            delay(100)
            job.cancelAndJoin()
            Log.d(TAG, "CPU loop cancelled because it checked isActive")
        }

        // ---------------------------------------------------------
        // 8.3 ensureActive() vs yield() vs isActive
        // ---------------------------------------------------------

        /*
        * Three ways to make code cooperative:
        *
        * isActive          - read the flag, decide yourself what to do.
        * ensureActive()    - throws CancellationException IMMEDIATELY if cancelled.
        *                     Use at the top of loop iterations.
        * yield()           - checks cancellation AND gives other coroutines a
        *                     chance to run on this thread. Use in long loops.
        *
        * All three are cheap. Rule: any loop longer than a few ms should have
        * one of these inside.
        * */

        // ---------------------------------------------------------
        // 8.4 try/finally runs on cancellation - cleanup pattern
        // ---------------------------------------------------------

        /*
        * When a coroutine is cancelled, CancellationException is thrown at the
        * next suspension point, and normal finally blocks run. Use this to
        * release resources.
        *
        * WARNING: calling suspend functions inside finally is tricky - the
        * coroutine is already cancelled, so delay() etc. would throw again.
        * Use withContext(NonCancellable) for suspend cleanup:
        *
        *   try {
        *       doWork()
        *   } finally {
        *       withContext(NonCancellable) { cleanup() }  // suspend-safe cleanup
        *   }
        * */

        runBlocking {
            val job = launch {
                try {
                    repeat(1000) { i ->
                        delay(50)
                        Log.d(TAG, "tick $i")
                    }
                } finally {
                    Log.d(TAG, "Cleanup ran (finally executes on cancellation)")
                }
            }
            delay(150)
            job.cancelAndJoin()
        }

        // ---------------------------------------------------------
        // 8.5 withTimeout - auto-cancel slow work
        // ---------------------------------------------------------

        // withTimeout cancels the block if it exceeds the duration.
        // It throws TimeoutCancellationException (a CancellationException).
        runBlocking {
            try {
                withContext(Dispatchers.Default) {
                    withTimeout(100) {
                        delay(500)   // too slow!
                        Log.d(TAG, "Never printed")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.d(TAG, "Timed out: ${e.message}")
            }

            // withTimeoutOrNull returns null instead of throwing:
            val result = withTimeoutOrNull(100) {
                delay(500)
                "too late"
            }
            Log.d(TAG, "withTimeoutOrNull returned: $result")   // null
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: Why is cancellation cooperative?
        * A: Because killing code mid-execution is unsafe (locks, half-written
        *    data). The coroutine must reach a suspension point or check
        *    isActive/ensureActive() to notice cancellation.
        *
        * Q: What happens if you call a suspend function after cancellation?
        * A: Library suspend functions throw CancellationException immediately.
        *
        * Q: How do you run cleanup when a coroutine is cancelled?
        * A: try/finally. If cleanup itself must suspend, wrap it in
        *    withContext(NonCancellable).
        *
        * Q: What is the difference between CancellationException and other exceptions?
        * A: CancellationException is how cancellation travels - it is NOT treated
        *    as an error by the machinery (no parent cancellation, no crash).
        *    Never swallow it in catch blocks without rethrowing!
        * */
    }

    // ============================================================
    // CHAPTER 9 - EXCEPTION HANDLING & SUPERVISION
    // ============================================================
    private fun exceptionHandling() {

        /*
        * EXCEPTION PROPAGATION - what happens when a coroutine fails?
        *
        * DEFAULT RULE (regular Job): a failed child CANCELS its parent, which
        * cancels all siblings. The exception travels up to the scope root.
        * Why? Because if one part of a task failed, the whole task is usually
        * invalid - fail fast, cancel everything.
        *
        * WHERE the exception "lands" depends on the builder:
        * - launch: uncaught exceptions go to the scope's
        *   CoroutineExceptionHandler (or crash the app on Android).
        * - async: the exception is HELD in the Deferred and rethrown at
        *   .await(). If you never await, it is silently stored (and reported
        *   to the handler when the scope ends).
        *
        * THE THREE ERROR-HANDLING TOOLS:
        *
        * 1. try/catch inside the coroutine - for EXPECTED failures (network
        *    down, bad input). Catch and handle locally.
        *
        * 2. CoroutineExceptionHandler - a context element that catches
        *    UNCAUGHT exceptions from launch-style coroutines. Like
        *    Thread.uncaughtExceptionHandler. It does NOT stop cancellation -
        *    the coroutine still dies; it just lets you log/report.
        *
        * 3. SupervisorJob - stops exception propagation between SIBLINGS.
        *    With a SupervisorJob, one child failing does NOT cancel the
        *    parent or siblings. Use when children are independent tasks
        *    (e.g. a list of independent downloads).
        * */

        // ---------------------------------------------------------
        // 9.1 try/catch - the normal way (works across suspension!)
        // ---------------------------------------------------------
        runBlocking {
            launch {
                try {
                    delay(50)
                    error("Something broke")
                } catch (e: Exception) {
                    Log.d(TAG, "Caught locally: ${e.message}")
                }
            }
            // The parent survives - the exception was handled inside the child.
        }

        // ---------------------------------------------------------
        // 9.2 Uncaught exception in launch - kills parent + siblings
        // ---------------------------------------------------------
        runBlocking {
            val handler = CoroutineExceptionHandler { context, exception ->
                Log.d(TAG, "Handler caught: ${exception.message}")
            }
            val job = launch(CoroutineName("failingChild") + handler) {
                delay(50)
                error("boom")   // uncaught -> propagates to parent
            }
            launch {
                delay(100)
                Log.d(TAG, "Sibling - may never run if sibling failed first")
            }
            job.join()
        }

        // ---------------------------------------------------------
        // 9.3 async holds the exception until await()
        // ---------------------------------------------------------
        runBlocking {
            val deferred = async {
                delay(50)
                error("async failure")
            }
            try {
                deferred.await()   // exception surfaces HERE, not before
            } catch (e: Exception) {
                Log.d(TAG, "Caught from await: ${e.message}")
            }
        }

        // ---------------------------------------------------------
        // 9.4 SupervisorJob - children fail independently
        // ---------------------------------------------------------

        /*
        * SupervisorJob: cancellation/exceptions do NOT flow UP from a failing
        * child to the parent. Siblings keep running.
        *
        *   val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        *
        * RULE: SupervisorJob only works when it is the scope's DIRECT root.
        * If you write supervisorScope { } or use a scope with SupervisorJob,
        * children launched DIRECTLY in it are supervised. But a child of a
        * child (launch inside launch) still forms a normal parent-child link.
        *
        * ANDROID USE CASE: viewModelScope uses SupervisorJob internally -
        * one failed request should not cancel all other requests in the VM.
        * */

        runBlocking {
            val supervisor = SupervisorJob()
            val scope = CoroutineScope(supervisor + Dispatchers.Default)

            val failing = scope.launch {
                delay(50)
                error("child 1 failed")
            }
            val healthy = scope.launch {
                delay(100)
                Log.d(TAG, "Sibling survived thanks to SupervisorJob")
            }
            failing.join()
            healthy.join()
            supervisor.cancel()
        }

        // ---------------------------------------------------------
        // 9.5 supervisorScope - one-off supervised block
        // ---------------------------------------------------------
        runBlocking {
            supervisorScope {
                launch {
                    delay(50)
                    error("fails alone")
                }
                launch {
                    delay(100)
                    Log.d(TAG, "Sibling in supervisorScope survived")
                }
            }
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: A child coroutine throws - what happens to its siblings?
        * A: With a normal Job they are all cancelled (exception propagates up,
        *    cancellation flows down). With SupervisorJob, siblings survive.
        *
        * Q: How does async handle exceptions?
        * A: They are captured in the Deferred and thrown at await(). If never
        *    awaited, they surface when the parent completes.
        *
        * Q: What is CoroutineExceptionHandler and where does it work?
        * A: A context element that handles UNCAUGHT exceptions from launch
        *    coroutines. It must be in the SCOPE or the ROOT coroutine's context
        *    - putting it on a child launch does nothing (the exception already
        *    propagated past it). It does not prevent cancellation.
        *
        * Q: When do you use SupervisorJob?
        * A: When children are INDEPENDENT tasks (UI requests, per-item work).
        *    viewModelScope and custom app scopes should use it so one failure
        *    doesn't nuke unrelated work.
        * */
    }

    // ============================================================
    // CHAPTER 10 - SEQUENTIAL vs PARALLEL: async/await, concurrency
    // ============================================================
    private fun sequentialVsParallel() {

        /*
        * SEQUENTIAL vs CONCURRENT - a critical distinction.
        *
        * SEQUENTIAL (default): suspend calls run one after another.
        *   val user = api.getUser()      // waits
        *   val friends = api.getFriends() // starts AFTER user finishes
        *   Total time = sum of both calls.
        *
        * PARALLEL: start both with async, then await both.
        *   val userD = async { api.getUser() }
        *   val friendsD = async { api.getFriends() }
        *   Total time = the SLOWER of the two.
        *
        * THE CLASSIC MISTAKE:
        *   async { api.getUser() }.await()      // starts, WAITS immediately
        *   async { api.getFriends() }.await()   // starts after first done
        *   This is SEQUENTIAL despite using async! You must launch ALL asyncs
        *   FIRST, then await them.
        *
        * CONCURRENCY vs PARALLELISM (interview classic):
        * - Concurrency = many tasks IN FLIGHT at once (interleaved, may share
        *   one thread). Coroutines are concurrent even on a single thread.
        * - Parallelism = tasks running SIMULTANEOUSLY on different cores.
        *   Coroutines on Dispatchers.Default run in parallel.
        *   delay() gives concurrency without parallelism - one thread can
        *   "wait" for thousands of coroutines at once.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 10.1 Sequential - total = sum
            // ---------------------------------------------------------
            var start = System.currentTimeMillis()
            val a = slowTask("A", 100)
            val b = slowTask("B", 100)
            Log.d(TAG, "Sequential: $a, $b in ${System.currentTimeMillis() - start} ms (~200)")

            // ---------------------------------------------------------
            // 10.2 Parallel with async - total = max
            // ---------------------------------------------------------
            start = System.currentTimeMillis()
            val deferredA = async { slowTask("A", 100) }   // start BOTH first
            val deferredB = async { slowTask("B", 100) }
            val results = listOf(deferredA, deferredB).awaitAll()
            Log.d(TAG, "Parallel: $results in ${System.currentTimeMillis() - start} ms (~100)")

            // ---------------------------------------------------------
            // 10.3 awaitAll - parallel over a list
            // ---------------------------------------------------------
            start = System.currentTimeMillis()
            val ids = listOf(1, 2, 3, 4)
            val all: List<String> = ids.map { id ->
                async { slowTask("task$ids", 100) }
            }.awaitAll()
            Log.d(TAG, "4 parallel tasks in ${System.currentTimeMillis() - start} ms: $all")

            // ---------------------------------------------------------
            // 10.4 coroutineScope - parallel decomposition INSIDE a suspend fn
            // ---------------------------------------------------------

            /*
            * coroutineScope { } : creates a child scope that inherits the
            * caller's context. The block suspends until ALL children complete.
            * Use it to run parallel sub-tasks inside a suspend function while
            * keeping structured concurrency (fail together, cancel together).
            *
            *   suspend fun loadDashboard() = coroutineScope {
            *       val user = async { api.getUser() }
            *       val posts = async { api.getPosts() }
            *       Dashboard(user.await(), posts.await())
            *   }
            * */
            val dashboard = loadDashboard()
            Log.d(TAG, "Dashboard: $dashboard")
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: launch vs async - when to use which?
        * A: launch when you don't need a result (side effects, UI updates).
        *    async when you need a value back (parallel decomposition).
        *
        * Q: What is the difference between coroutineScope and supervisorScope?
        * A: coroutineScope fails if ANY child fails (fail-fast). supervisorScope
        *    lets children fail independently. Both wait for all children.
        *
        * Q: How do you run 2 network calls in parallel and combine results?
        * A: async both, then await both (or awaitAll). Never await inside the
        *    async block of the other - that serializes them.
        * */
    }

    private suspend fun slowTask(name: String, ms: Long): String {
        delay(ms)
        return "$name-done"
    }

    // Parallel decomposition inside a suspend function
    private suspend fun loadDashboard(): String = coroutineScope {
        val user = async { slowTask("user", 100) }
        val posts = async { slowTask("posts", 150) }
        "${user.await()} + ${posts.await()}"
    }

    // ============================================================
    // CHAPTER 11 - STRUCTURED CONCURRENCY: the philosophy that ties
    // everything together
    // ============================================================
    private fun structuredConcurrency() {

        /*
        * STRUCTURED CONCURRENCY = coroutines have a PARENT, and their lifetime
        * is BOUNDED by their parent's lifetime. It is the answer to:
        * "who is responsible for this coroutine?"
        *
        * THE OLD WAY (unstructured - threads):
        *   Thread(work).start()   // fire and pray
        *   - Nobody tracks it. Nobody cancels it. It leaks.
        *   - If it fails, nobody knows. Errors vanish.
        *   - When the screen closes, it keeps running.
        *
        * STRUCTURED CONCURRENCY enforces three guarantees:
        *
        * 1. Every coroutine lives INSIDE a scope (a parent).
        * 2. A parent cannot complete while children are running.
        * 3. Cancelling/failing the parent cancels/fails all children.
        *
        * RESULT: no orphaned work, no leaks, deterministic cancellation,
        * and errors always have a place to go. It turns "concurrent code"
        * back into something you can reason about like sequential code.
        *
        * THE RULES IN PRACTICE:
        * - launch/async inside a coroutine = child of that coroutine.
        * - launch/async on a scope object = child of the scope's Job.
        * - A suspend function that launches children should do it inside
        *   coroutineScope { } so the caller waits for them (structured).
        * - The ONLY way to escape the tree is GlobalScope (don't) or your
        *   own long-lived scope (use deliberately, cancel deliberately).
        * */

        // ---------------------------------------------------------
        // 11.1 Structured: caller waits for everything launched inside
        // ---------------------------------------------------------
        runBlocking {
            launch {   // child 1
                launch { delay(100); Log.d(TAG, "grandchild done") }
                Log.d(TAG, "child done")
            }
            // runBlocking waits for child AND grandchild. Nothing escapes.
        }

        // ---------------------------------------------------------
        // 11.2 Unstructured escape hatch: GlobalScope (for contrast)
        // ---------------------------------------------------------

        /*
        * GlobalScope.launch { } creates a coroutine with NO parent - it is
        * "unstructured". It outlives the block that created it. This breaks
        * all three guarantees above. In Android apps, avoid it.
        * */

        // ---------------------------------------------------------
        // 11.3 The Android lifetime mapping (memorize this table)
        // ---------------------------------------------------------

        /*
        *   Owner          Scope              Cancelled when
        *   ---------------------------------------------------------
        *   Activity       lifecycleScope     onDestroy()
        *   Fragment       viewLifecycleOwner.lifecycleScope  view destroyed
        *   ViewModel      viewModelScope     onCleared()
        *   Application    custom scope       app process dies
        *
        *   // Activity:
        *   lifecycleScope.launch {
        *       val user = repository.loadUser()   // main-safe suspend fn
        *       textView.text = user.name          // back on Main automatically
        *   }
        *
        *   // ViewModel:
        *   fun load() {
        *       viewModelScope.launch {
        *           _state.value = State.Loading
        *           _state.value = try { State.Data(repo.load()) }
        *                          catch (e: Exception) { State.Error(e) }
        *       }
        *   }
        * */

        /*
        * SEPARATION OF CONCERNS - the architecture insight:
        *
        * - UI layer (Activity/Fragment): owns the SCOPE (lifetime).
        *   It decides WHEN work starts and when it must stop.
        * - Repository/DataSource: owns the DISPATCHER (withContext inside).
        *   It decides WHERE work runs. Exposes suspend functions.
        * - Neither knows about the other. The repository never touches
        *   lifecycle; the Activity never picks threads.
        *
        * This is why modern Android architecture is "suspend functions all
        * the way down" - threading is an implementation detail of each layer.
        * */

        /*
        * INTERVIEW QUESTIONS:
        * Q: What is structured concurrency?
        * A: The principle that coroutines form a parent-child tree: every
        *    coroutine is scoped to a parent, parents wait for children,
        *    cancellation flows down, and failures propagate up. No orphaned work.
        *
        * Q: Why is structured concurrency better than GlobalScope?
        * A: It ties coroutine lifetime to an owner (screen/VM), so everything
        *    is cancelled automatically - no leaks, no orphaned work, and errors
        *    always have somewhere to go.
        *
        * Q: Where should launch/async be called from in Android?
        * A: From lifecycle-aware scopes (lifecycleScope, viewModelScope) or
        *    inside suspend functions via coroutineScope/supervisorScope.
        *    Never GlobalScope in feature code.
        * */
    }

    // ============================================================
    // CHAPTER 12 - SHARED STATE & CHANNELS: coroutines talking
    // ============================================================
    private fun sharedStateAndChannels() {

        /*
        * COROUTINES SHARE THREADS - so shared mutable state is dangerous.
        * Two coroutines on different threads incrementing the same counter
        * lose updates (race condition), same as threads.
        *
        * FOUR WAYS TO HANDLE SHARED STATE (from simplest to most advanced):
        *
        * 1. Confine to ONE thread/single-threaded dispatcher.
        * 2. Confine to one coroutine (actor-style) - others send messages.
        * 3. Mutex - a coroutine-friendly lock (lock() is a suspend fn, it
        *    does NOT block the thread while waiting).
        * 4. Atomic types / concurrent collections.
        *
        * CHANNEL = a pipe between coroutines. One side sends, other receives.
        *   - Channel<T> = queue: send() suspends when full, receive() when empty.
        *   - Channels are HOT: values are delivered even with no receiver ready
        *     (buffered), and lost if nobody ever receives (unbuffered).
        *   - Use for: producer-consumer flows, work queues, events.
        *
        * Channel types (interview):
        *   RENDEZVOUS (default, capacity 0) - send suspends until receiver ready
        *   BUFFERED   - fixed buffer, send suspends when full
        *   CONFLATED  - buffer of 1, new value replaces old
        *   UNLIMITED  - unbounded buffer (memory risk)
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 12.1 Race condition demo (why shared state needs care)
            // ---------------------------------------------------------
            var counter = 0
            val jobs = mutableListOf<Job>()
            repeat(100) {
                jobs += launch(Dispatchers.Default) {
                    repeat(100) { counter++ }   // RACE: lost updates
                }
            }
            jobs.joinAll()
            Log.d(TAG, "Expected 10000, got $counter (race condition!)")

            // ---------------------------------------------------------
            // 12.2 Mutex - coroutine-safe mutual exclusion
            // ---------------------------------------------------------
            val mutex = Mutex()
            var safeCounter = 0
            repeat(100) {
                launch(Dispatchers.Default) {
                    repeat(100) {
                        mutex.withLock { safeCounter++ }   // one at a time
                    }
                }
            }
            delay(300)
            Log.d(TAG, "With Mutex: $safeCounter (correct)")

            // ---------------------------------------------------------
            // 12.3 Channel - producer/consumer
            // ---------------------------------------------------------
            val channel = Channel<Int>(capacity = 2)   // buffered

            val producer = launch {
                for (value in 1..5) {
                    channel.send(value)   // suspends if buffer full
                    Log.d(TAG, "Sent $value")
                }
                channel.close()   // signals "no more values"
            }
            val consumer = launch {
                for (value in channel) {   // suspends when empty, ends on close
                    Log.d(TAG, "Received $value")
                }
                Log.d(TAG, "Channel closed - consumer done")
            }
            producer.join(); consumer.join()
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: Channel vs Flow?
        * A: Channel = 1 value to 1 consumer (hot, point-to-point). Flow =
        *    cold stream, each collector gets its own values. Channel for work
        *    distribution; Flow for reactive data streams.
        *
        * Q: What is a race condition and how do you avoid it with coroutines?
        * A: Two coroutines mutate shared state concurrently and lose updates.
        *    Fix with Mutex (suspends, doesn't block), single-threaded
        *    confinement, atomics, or immutable data.
        * */
    }
}
