package com.kotlinpractice

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/*
* ============================================================
* FLOW - FROM ZERO TO EXPERT (A complete course in one file)
* ============================================================
*
* PART 0 - WHY FLOW EXISTS
*
* A suspend function returns ONE value, ONCE:
*   suspend fun loadUser(): User        // one answer, done
*
* But many real-world data sources produce MULTIPLE values over TIME:
*   - a database table that changes (Room emits on every insert)
*   - a location provider (new coordinates every second)
*   - a countdown timer, a progress bar, a chat stream
*   - UI state that changes as the user interacts
*
* A suspend function cannot represent "many values over time". You would need
* a callback - and we are back to callback hell. FLOW solves this:
*
*   Flow<T> = a stream of T values, produced asynchronously, over time.
*
* Flow is built ON COROUTINES (it is just a suspend-driven sequence):
*   - Collecting a flow is a suspend call (works with structured concurrency).
*   - Cancellation propagates through the stream automatically.
*   - Exceptions travel through the stream with normal try/catch semantics.
*
* THE ONE-LINER DEFINITION (interview):
*   "Flow is a cold asynchronous data stream that sequentially emits values
*    and completes normally or with an exception."
*
* THE THREE FLOW FAMILIES (memorize):
*   1. Cold Flow      - flow { }, each collector gets its own stream.
*   2. StateFlow      - hot, holds ONE current value (UI state).
*   3. SharedFlow     - hot, broadcasts events to many collectors.
*
* COLD vs HOT (the core distinction):
*   - COLD: the stream does nothing until someone collects it. Each collector
*     gets its OWN independent stream (like a function call per collector).
*     Example: flow { emit(fetchFromNetwork()) } - each collector triggers
*     its own network call.
*   - HOT: the stream runs independently of collectors. Values are emitted
*     whether or not anyone listens; late collectors may miss earlier values.
*     Example: a stock ticker, a chat stream.
*
* MENTAL MODEL:
*   Flow      = a function that returns many values over time (cold).
*   StateFlow = a state holder with a current value (hot, always has a value).
*   SharedFlow= an event broadcaster (hot, may have no value).
* */
class FlowActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FlowCourse"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)

        // The course runs top to bottom. Filter Logcat by "FlowCourse".
        whyFlowExists()
        flowBuilders()
        flowOperators()
        terminalOperators()
        contextAndFlowOn()
        flowCancellation()
        flowExceptions()
        bufferingAndConflation()
        combiningFlows()
        stateFlow()
        sharedFlow()
        androidIntegration()
        testingFlows()
    }

    // ============================================================
    // CHAPTER 1 - WHY FLOW: the problem it solves
    // ============================================================
    private fun whyFlowExists() {

        /*
        * THE EVOLUTION OF "MANY VALUES OVER TIME" IN ANDROID:
        *
        * 1. Callbacks:        onDataChanged(values) - nesting, leaks, no lifecycle.
        * 2. RxJava:           Observable<T> - powerful but a whole second language
        *                      (its own operators, its own threading, its own
        *                      error model) on top of Java. Steep learning curve.
        * 3. LiveData:         Android-only, lifecycle-aware, but limited operators,
        *                      needs main thread, Android-only (no pure Kotlin tests).
        * 4. Flow:             built on coroutines, pure Kotlin, full operator set,
        *                      structured concurrency built in.
        *
        * FLOW vs SEQUENCE (interview classic):
        *   - Sequence<T> emits values with BLOCKING code (forEach on a sequence
        *     blocks the thread between values). Cannot use delay() inside.
        *   - Flow<T> emits values with NON-BLOCKING suspension - the thread is
        *     free between emissions, and the stream can hop dispatchers.
        *
        *   sequence { yield(1); Thread.sleep(100) }   // blocks thread
        *   flow { emit(1); delay(100) }               // suspends, thread free
        *
        * FLOW vs LIST:
        *   - toList() waits for ALL values, then returns them at once.
        *   - Flow delivers each value AS IT IS PRODUCED - the collector can
        *     react to value 1 while value 2 is still being computed.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 1.1 A suspend function: one value, once
            // ---------------------------------------------------------
            Log.d(TAG, "Suspend fn: ${loadSingleUser()}")

            // ---------------------------------------------------------
            // 1.2 A Flow: many values over time
            // ---------------------------------------------------------
            countdown().collect { value ->
                Log.d(TAG, "Countdown emitted: $value")
            }
            Log.d(TAG, "Countdown flow completed")
        }
    }

    // One value, once.
    private suspend fun loadSingleUser(): String {
        delay(50)
        return "User(harshit)"
    }

    // A cold flow: does NOTHING until collected
    private fun countdown(): Flow<Int> = flow {
        for (value in 3 downTo 1) {
            emit(value)      // produce a value
            delay(100)       // ... over time
        }
    }

    // ============================================================
    // CHAPTER 2 - FLOW BUILDERS: creating streams
    // ============================================================
    private fun flowBuilders() {

        /*
        * THE BUILDERS:
        *
        * flow { }        - the fundamental builder. Call emit(value) inside.
        *                  The block runs PER COLLECTOR (cold).
        * flowOf(a, b, c) - emits fixed values then completes.
        * asFlow()        - converts collections/sequences to flows.
        * channelFlow { } - for concurrent producers inside one flow (advanced).
        * callbackFlow {} - wraps callback-based APIs into flows (see ch. 12).
        *
        * THE FLOW CONTRACT (interview gold - why the rules exist):
        * Context preservation: flow { } must emit from the SAME coroutine context
        * as the collector. You cannot change context inside flow { } with
        * withContext - it throws IllegalStateException at runtime. Why? Because
        * the flow builder is designed to be transparent: the producer should
        * not surprise the collector with a different thread. Use flowOn()
        * instead (chapter 5).
        *
        * emit() is not thread-safe by itself: do not emit from multiple
        * coroutines inside one flow block (use channelFlow for that).
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 2.1 flow { } - the fundamental builder
            // ---------------------------------------------------------
            val stream: Flow<Int> = flow {
                Log.d(TAG, "flow block STARTED (only now, on collect)")
                emit(1)
                delay(100)
                emit(2)
            }
            Log.d(TAG, "Flow created but NOT running yet (cold!)")

            delay(200)   // nothing was emitted - cold flow waits for a collector

            Log.d(TAG, "--- collector 1 ---")
            stream.collect { Log.d(TAG, "c1 got $it") }

            // Each collector runs the block AGAIN - independent streams.
            Log.d(TAG, "--- collector 2 ---")
            stream.collect { Log.d(TAG, "c2 got $it") }

            // ---------------------------------------------------------
            // 2.2 flowOf / asFlow - quick fixed streams
            // ---------------------------------------------------------
            flowOf(1, 2, 3).collect { Log.d(TAG, "flowOf: $it") }
            listOf("a", "b").asFlow().collect { Log.d(TAG, "asFlow: $it") }
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: Is Flow cold or hot?
        * A: The base Flow is COLD - nothing happens until collect(), and each
        *    collector gets its own stream. StateFlow/SharedFlow are hot.
        *
        * Q: What is the difference between Flow and Sequence?
        * A: Sequence is blocking (each value computed synchronously); Flow is
        *    suspend-based - it can wait asynchronously (delay, network) without
        *    blocking the thread.
        * */
    }

    // ============================================================
    // CHAPTER 3 - OPERATORS: transforming streams
    // ============================================================
    private fun flowOperators() {

        /*
        * Operators are applied BETWEEN the producer and the collector. They
        * return a NEW flow - the original is never modified (immutability).
        *
        *   flow -> map -> filter -> collect
        *
        * KEY INSIGHT: operators are LAZY. Nothing runs until a terminal
        * operator (collect, first, toList...) triggers execution. Building a
        * chain of operators builds a pipeline description, not a running stream.
        *
        * THE ESSENTIAL OPERATORS (Android daily use):
        *   map        - transform each value: T -> R
        *   filter     - keep values matching a predicate
        *   onEach     - side effect per value (logging, analytics)
        *   onStart    - action before collection starts (show loading)
        *   onCompletion - action when the flow completes (hide spinner)
        *   distinctUntilChanged - skip consecutive duplicate values
        *   debounce   - only emit after a quiet period (search-as-you-type!)
        *   take(n)    - first n values then cancel upstream
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 3.1 map + filter - the workhorses
            // ---------------------------------------------------------
            flowOf(1, 2, 3, 4, 5)
                .map { it * 10 }                    // 10, 20, 30
                .filter { it > 10 }                 // 20, 30
                .collect { Log.d(TAG, "map/filter: $it") }

            // ---------------------------------------------------------
            // 3.2 onEach / onStart / onCompletion - lifecycle hooks
            // ---------------------------------------------------------
            flowOf(1, 2)
                .onStart { Log.d(TAG, "onStart: about to collect") }
                .onEach { Log.d(TAG, "onEach: processing $it") }
                .onCompletion { Log.d(TAG, "onCompletion: stream done (cause=$it)") }
                .collect { Log.d(TAG, "collected: $it") }

            // ---------------------------------------------------------
            // 3.3 distinctUntilChanged - skip consecutive duplicates
            // ---------------------------------------------------------

            // Critical for UI state: don't re-render identical state.
            flowOf(1, 1, 2, 2, 2, 3)
                .distinctUntilChanged()
                .collect { Log.d(TAG, "distinct: $it") }   // 1, 2

            // ---------------------------------------------------------
            // 3.4 debounce - the search bar pattern
            // ---------------------------------------------------------

            // Emits only after 100ms of silence. Rapid bursts collapse into
            // one emission - this is how "search as you type" avoids firing
            // a request per keystroke.
            val searchQueries = flow {
                emit("k")            // user types fast
                delay(50)
                emit("ko")
                delay(50)
                emit("kot")
                delay(200)           // user pauses -> this one gets through
                emit("kotlin")
            }
            searchQueries
                .debounce(100)
                .collect { Log.d(TAG, "debounced search: $it") }

            // ---------------------------------------------------------
            // 3.5 take - early termination cancels the producer
            // ---------------------------------------------------------
            flow {
                emit(1); emit(2); emit(3); emit(4); emit(5)
            }.take(2).collect { Log.d(TAG, "take(2): $it") }
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: Are Flow operators lazy?
        * A: Yes. map/filter/etc. just build a new wrapped flow. Nothing runs
        *    until a terminal operator (collect/first/toList) is called.
        *
        * Q: What is debounce used for in Android?
        * A: Search-as-you-type: only emit after the user stops typing for N ms,
        *    so you don't fire a network request per keystroke.
        * */
    }

    // ============================================================
    // CHAPTER 4 - TERMINAL OPERATORS: triggering execution
    // ============================================================
    private fun terminalOperators() {

        /*
        * Terminal operators START the flow. Without one, nothing runs.
        *
        * collect { }   - process every value (the fundamental terminal op).
        * collectLatest - on a new value, CANCEL the block for the previous one
        *                 if it is still running. Perfect for UI: only the
        *                 LATEST state matters (skip stale renders).
        * first()       - take the first value then cancel the stream.
        * toList()      - gather all values into a list.
        * single()      - expect exactly one value (error otherwise).
        * fold/ reduce  - accumulate values into one result.
        *
        * collectLatest - THE Android operator for UI:
        *   flow.collectLatest { value ->
        *       render(value)   // if a new value arrives while rendering,
        *   }                   // the old render is CANCELLED mid-flight
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 4.1 collectLatest - cancel stale processing
            // ---------------------------------------------------------
            flow {
                emit(1)
                delay(50)
                emit(2)      // arrives while block(1) is still sleeping
                emit(3)
            }.collectLatest { value ->
                Log.d(TAG, "collectLatest: processing $value")
                delay(200)   // pretend slow work - gets cancelled by next emit
                Log.d(TAG, "collectLatest: finished $value (only last survives)")
            }

            // ---------------------------------------------------------
            // 4.2 first() - grab one value and stop
            // ---------------------------------------------------------
            val firstValue = countdown().first()
            Log.d(TAG, "first() returned early: $firstValue")

            // ---------------------------------------------------------
            // 4.3 toList() - collect everything into a list
            // ---------------------------------------------------------
            val all = flowOf(1, 2, 3).toList()
            Log.d(TAG, "toList: $all")
        }
    }

    // ============================================================
    // CHAPTER 5 - CONTEXT & flowOn: which thread emits, which collects
    // ============================================================
    private fun contextAndFlowOn() {

        /*
        * CONTEXT PRESERVATION (the rule that confuses everyone):
        * The flow { } block runs in the context of the COLLECTOR, not the
        * creator. A flow does not own threads - it borrows the collector's.
        *
        *   flow { emit(fetch()) }        // runs wherever collect() runs
        *   .collect { }                  // if collect is on Main, emit is on Main!
        *
        * This is why you MUST use flowOn() to move the PRODUCER off the main
        * thread. flowOn(Dispatchers.IO) changes the context of everything
        * UPSTREAM of it (the producer + operators above it), while everything
        * DOWNSTREAM (the collector) stays where it was.
        *
        *   flow { emit(blockingIo()) }   // would crash on Main
        *       .flowOn(Dispatchers.IO)   // producer now runs on IO
        *       .collect { }              // collector stays on Main
        *
        * flowOn is THE way to make a flow "main-safe": the UI collects on
        * Main, the heavy work happens upstream on IO.
        *
        * WHY the withContext ban inside flow { }? Because emit must happen in
        * the collector's context - if the producer silently switched threads,
        * emit would be called from a different coroutine than the collector
        * expects, breaking the sequential contract. flowOn is the sanctioned
        * escape: it wraps the upstream in its own coroutine.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 5.1 Default: producer runs on the collector's thread
            // ---------------------------------------------------------
            flow {
                Log.d(TAG, "emit thread: ${Thread.currentThread().name}")
                emit(1)
            }.collect {
                Log.d(TAG, "collect thread: ${Thread.currentThread().name}")
            }

            // ---------------------------------------------------------
            // 5.2 flowOn - move the producer to IO
            // ---------------------------------------------------------
            flow {
                Log.d(TAG, "Producer on: ${Thread.currentThread().name}")
                emit(heavyComputation())
            }
                .map { it.length }   // also upstream - also on IO
                .flowOn(Dispatchers.IO)   // everything ABOVE this runs on IO
                .collect { value ->
                    Log.d(TAG, "Collector on: ${Thread.currentThread().name}, got $value")
                }

            // ---------------------------------------------------------
            // 5.2 withContext inside flow { } - FORBIDDEN (runtime crash)
            // ---------------------------------------------------------

            /*
            * flow {
            *     withContext(Dispatchers.IO) { emit(1) }   // THROWS!
            *     // IllegalStateException: Flow invariant is violated
            * }
            * The invariant exists so a flow's emission order stays sequential
            * and predictable. Use flowOn instead.
            * */
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: What does flowOn do?
        * A: Changes the context of everything UPSTREAM (producer + operators
        *    above it). The collector's context is untouched. It is the only
        *    correct way to change threads inside a flow.
        *
        * Q: Why does withContext inside flow { } throw?
        * A: It violates context preservation - emissions must happen in the
        *    collector's context so the stream stays sequential and predictable.
        * */
    }

    private fun heavyComputation(): String {
        // pretend CPU work
        return "computed-${System.nanoTime()}"
    }

    // ============================================================
    // CHAPTER 6 - FLOW CANCELLATION: structured concurrency in streams
    // ============================================================
    private fun flowCancellation() {

        /*
        * Flow is built on coroutines, so cancellation is automatic:
        * - Collecting happens in a coroutine; cancel that coroutine and the
        *   collection stops at the next suspension point.
        * - Operators like take(n) cancel the PRODUCER when done.
        * - emit() checks cancellation: after cancel(), emit throws
        *   CancellationException at the next suspension point.
        *
        * THE SUBTLE RULE: flow producers only notice cancellation at
        * suspension points - same cooperative model as coroutines. A flow
        * block doing pure CPU work between emissions won't notice cancel()
        * until it suspends. Use currentCoroutineContext().ensureActive() or
        * yield() in long producer loops.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 6.1 Cancelling the collector cancels the stream
            // ---------------------------------------------------------
            val job = launch {
                flow {
                    repeat(1000) { value ->
                        emit(value)
                        delay(50)   // suspension point - cancellation lands here
                    }
                }.collect { Log.d(TAG, "got $it") }
            }
            delay(250)
            job.cancelAndJoin()
            Log.d(TAG, "Collector cancelled - producer stopped with it")

            // ---------------------------------------------------------
            // 6.2 take() cancels upstream automatically
            // ---------------------------------------------------------
            flow {
                repeat(1000) { emit(it) }   // would emit forever
            }.take(3).collect { Log.d(TAG, "take(3): $it") }
            Log.d(TAG, "Producer was cancelled after 3 values")
        }

        /*
        * INTERVIEW QUESTION:
        * Q: How does cancellation work with Flow?
        * A: Through the coroutine machinery - collecting a flow in a cancelled
        *    coroutine stops the producer at its next suspension point. take(n)
        *    and first() cancel the upstream automatically once satisfied.
        * */
    }

    // ============================================================
    // CHAPTER 7 - EXCEPTION HANDLING in flows
    // ============================================================
    private fun flowExceptions() {

        /*
        * Exceptions in a flow travel DOWNSTREAM to the collector - like normal
        * code. A try/catch around collect() catches producer AND operator
        * exceptions:
        *
        *   try {
        *       myFlow.collect { }
        *   } catch (e: Exception) { ... }
        *
        * THE IDIOMATIC WAY - catch operator:
        *   flow.catch { emit(ErrorState) }   // catches UPSTREAM exceptions only!
        *
        * CRITICAL RULE: catch only sees exceptions from ABOVE it in the chain
        * (the producer + operators above). It does NOT see exceptions from the
        * collector block or operators BELOW it. That's why the pattern is:
        * put .catch near the END, right before collect.
        *
        * RETRY: retryWhen { cause, attempt -> ... } re-collects the upstream
        * on failure (with backoff via delay).
        *
        * onCompletion { cause -> } - runs on completion AND on exception
        * (cause != null), like finally.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 7.1 catch - upstream errors become values
            // ---------------------------------------------------------
            flow {
                emit(1)
                error("producer broke")
                emit(2)   // never reached
            }
                .catch { e ->
                    Log.d(TAG, "catch: ${e.message}")
                    emit(-1)   // emit a fallback value
                }
                .collect { Log.d(TAG, "got $it") }

            // ---------------------------------------------------------
            // 7.2 catch does NOT catch downstream errors
            // ---------------------------------------------------------

            /*
            * flow { emit(1) }
            *     .catch { }                    // catches producer errors only
            *     .map { error("downstream!") } // NOT caught by the catch above
            *     .collect { }
            * Fix: put catch AFTER the operators that can throw, or wrap collect
            * in try/catch.
            * */

            // ---------------------------------------------------------
            // 7.3 retryWhen - retry with backoff
            // ---------------------------------------------------------
            var attempts = 0
            flow {
                emit("attempt ${++attempts}")
                if (attempts < 3) error("flaky network")
                emit("success")
            }
                .retryWhen { cause, attempt ->
                    Log.d(TAG, "Retry ${attempt + 1} after: ${cause.message}")
                    delay(100)   // backoff
                    attempt < 2   // give up after 2 retries
                }
                .collect { Log.d(TAG, "retry result: $it") }

            // ---------------------------------------------------------
            // 7.4 onCompletion - the "finally" of flows
            // ---------------------------------------------------------
            flowOf(1, 2)
                .onCompletion { cause -> Log.d(TAG, "completed, cause=$cause") }
                .collect { Log.d(TAG, "onCompletion demo: $it") }
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: How do you handle exceptions in a Flow?
        * A: .catch operator for upstream exceptions (place before collect),
        *    or try/catch around collect. catch can emit fallback values.
        *
        * Q: What does flow.catch NOT catch?
        * A: Exceptions thrown downstream of it (in collect or later operators).
        *    It only sees exceptions from the upstream producer.
        * */
    }

    // ============================================================
    // CHAPTER 8 - BUFFERING & CONFLATION: fast producer, slow collector
    // ============================================================
    private fun bufferingAndConflation() {

        /*
        * THE PROBLEM: by default a flow is a strict handshake - the producer
        * waits for the collector to process each value before emitting the
        * next (no buffer). If the collector is slow, the producer is throttled.
        *
        * THE THREE TOOLS:
        *
        * buffer(capacity) - producer runs AHEAD of the collector, values wait
        *                    in a buffer. Total time = max(producer, collector)
        *                    instead of sum.
        * conflate()       - buffer of 1: if the collector is busy, keep only
        *                    the LATEST value, drop intermediates. "I only care
        *                    about the most recent value" (progress bars!).
        * collectLatest    - drop stale PROCESSING (cancels the collector's
        *                    block, not the values).
        *
        * When to use which:
        * - buffer: producer and collector are independent, you want throughput.
        * - conflate: only the latest value matters (progress, position).
        * - collectLatest: only the latest PROCESSING matters (UI render).
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 8.1 Without buffer: total = sum (sequential handoff)
            // ---------------------------------------------------------
            var start = System.currentTimeMillis()
            flow {
                repeat(3) {
                    emit(it)
                    delay(100)   // producer takes 100ms per value
                }
            }.collect {
                delay(100)   // collector takes 100ms per value
            }
            Log.d(TAG, "No buffer: ${System.currentTimeMillis() - start} ms (~600 = sum)")

            // ---------------------------------------------------------
            // 8.2 With buffer: producer runs ahead, total = max
            // ---------------------------------------------------------
            start = System.currentTimeMillis()
            flow {
                repeat(3) {
                    emit(it)
                    delay(100)
                }
            }
                .buffer(capacity = 3)   // producer doesn't wait for collector
                .collect { delay(100) }
            Log.d(TAG, "With buffer: ${System.currentTimeMillis() - start} ms (~400)")

            // ---------------------------------------------------------
            // 8.3 conflate - keep only the latest
            // ---------------------------------------------------------
            flow {
                repeat(5) {
                    emit(it)
                    delay(20)   // fast producer
                }
            }
                .conflate()   // slow collector misses intermediate values
                .collect {
                    delay(50)   // slow collector
                    Log.d(TAG, "conflated: $it (some values dropped)")
                }
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: What is the difference between buffer, conflate, and collectLatest?
        * A: buffer lets the producer run ahead (queue values). conflate keeps
        *    only the newest value if the collector is slow. collectLatest
        *    cancels the collector's in-flight block when a new value arrives.
        *
        * Q: When would you use conflate?
        * A: When intermediate values don't matter - progress updates, sensor
        *    readings, UI state. The collector only needs the latest.
        * */
    }

    // ============================================================
    // CHAPTER 9 - COMBINING FLOWS: combine, zip, combineLatest-style
    // ============================================================
    private fun combiningFlows() {

        /*
        * combine  - whenever EITHER flow emits, emit a pair of the LATEST
        *            values from both. The workhorse for combining UI state:
        *            combine(userFlow, settingsFlow) { user, settings -> ... }
        *
        * zip      - pairs values BY POSITION: 1st with 1st, 2nd with 2nd.
        *            Completes when the shorter flow completes.
        *
        * combine vs zip (interview):
        *   combine: latest-of-each, fires on ANY new value (N*M emissions).
        *   zip:     strict pairing, completes at the shorter stream.
        *
        * flatMapLatest - switch to a NEW inner flow per outer value. THE
        *                 pattern for search: outer = query, inner = results.
        *                 Previous inner flow is cancelled (no stale results).
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 9.1 combine - latest values from both
            // ---------------------------------------------------------
            val numbers = flowOf(1, 2, 3)
            val letters = flowOf("a", "b")
            numbers.combine(letters) { number, letter -> "$number$letter" }
                .collect { Log.d(TAG, "combine: $it") }

            // ---------------------------------------------------------
            // 9.2 zip - strict pairing
            // ---------------------------------------------------------
            flowOf(1, 2, 3).zip(flowOf("a", "b")) { number, letter ->
                "$number$letter"
            }.collect { Log.d(TAG, "zip: $it") }   // 1a, 2b (3 unpaired - dropped)

            // ---------------------------------------------------------
            // 9.3 flatMapLatest - the search pattern
            // ---------------------------------------------------------
            fun searchApi(query: String): Flow<String> = flow {
                delay(100)   // pretend network latency
                emit("results for '$query'")
            }

            flowOf("k", "ko", "kotlin")
                .flatMapLatest { query ->
                    searchApi(query)   // new inner flow per query,
                    // previous one CANCELLED - no stale results
                }
                .collect { Log.d(TAG, "flatMapLatest: $it") }
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: combine vs zip?
        * A: combine uses the LATEST value of each stream and fires on any new
        *    value; zip pairs values by position and completes with the shorter
        *    stream.
        *
        * Q: What is flatMapLatest used for?
        * A: Cancel-previous patterns like search: each new query cancels the
        *    previous in-flight request and starts a new one. (flatMapConcat =
        *    sequential, flatMapMerge = all in parallel.)
        * */
    }

    // ============================================================
    // CHAPTER 10 - STATEFLOW: the state holder
    // ============================================================
    private fun stateFlow() {

        /*
        * StateFlow<T> = a HOT flow that always holds ONE current value.
        *
        *   val _state = MutableStateFlow(0)     // mutable, private
        *   val state: StateFlow<Int> = _state   // read-only, exposed
        *
        * KEY PROPERTIES:
        * - ALWAYS has a value (requires an initial value).
        * - .value can be read anytime (no collection needed).
        * - New collectors IMMEDIATELY get the current value.
        * - Conflated by nature: only the latest value is kept.
        * - distinctUntilChanged built in: setting the same value does NOT
        *   re-emit (uses equals).
        *
        * THE ANDROID PATTERN (ViewModel):
        *   private val _uiState = MutableStateFlow(UiState())
        *   val uiState: StateFlow<UiState> = _uiState.asStateFlow()
        *
        *   - MutableStateFlow: private, only the VM writes.
        *   - StateFlow: public, the UI only reads.
        *   This is encapsulation: the UI can observe but never mutate state.
        *
        * stateIn() - converts a cold flow into a StateFlow with a scope:
        *   val uiState: StateFlow<UiState> = repository.updates
        *       .map { UiState(it) }
        *       .stateIn(scope, SharingStarted.WhileSubscribed(5000), UiState())
        *   - SharingStarted.WhileSubscribed(5000): keep the upstream active
        *     for 5s after the last collector leaves (survives rotation!),
        *     then stop. THE standard config for Android.
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 10.1 Basic StateFlow behavior
            // ---------------------------------------------------------
            val state = MutableStateFlow(0)

            val collector = launch {
                state.collect { Log.d(TAG, "StateFlow collector: $it") }
            }

            delay(100)
            state.value = 1
            state.value = 2
            state.value = 2   // duplicate - NOT emitted (distinctUntilChanged)
            delay(100)
            collector.cancel()
            Log.d(TAG, "StateFlow final value: ${state.value}")
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: StateFlow vs LiveData?
        * A: StateFlow is pure Kotlin (testable without Android), has full
        *    operators, works on any dispatcher, and always has a value.
        *    LiveData is Android-only, main-thread-bound, fewer operators.
        *    StateFlow needs lifecycle-aware collection in the UI (repeatOnLifecycle).
        *
        * Q: Why does StateFlow need an initial value?
        * A: Because it represents STATE - there is always a current state,
        *    even before any emission (Loading, empty, default).
        * */
    }

    // ============================================================
    // CHAPTER 11 - SHARED FLOW: the event broadcaster
    // ============================================================
    private fun sharedFlow() {

        /*
        * SharedFlow<T> = a HOT flow that BROADCASTS values to all collectors.
        *
        *   val _events = MutableSharedFlow<Event>()   // no initial value needed
        *   val events = _events.asSharedFlow()
        *
        * KEY PROPERTIES:
        * - No value held: new collectors get only FUTURE emissions (by default).
        * - Many collectors each receive every emission (broadcast).
        * - No initial value needed - it's an EVENT stream, not state.
        *
        * CONFIGURATION (interview gold):
        *   MutableSharedFlow(
        *       replay = 2,              // last N values re-sent to NEW collectors
        *       extraBufferCapacity = 10, // buffer for slow collectors
        *       onBufferOverflow = BufferOverflow.DROP_OLDEST  // what to do when full
        *   )
        *
        *   - replay: how many past values a new collector receives first.
        *   - extraBufferCapacity: values buffered beyond replay.
        *   - BufferOverflow: SUSPEND (default), DROP_OLDEST, DROP_LATEST.
        *
        * STATEFLOW vs SHAREDFLOW (THE interview question):
        *   StateFlow  = STATE. Always has a value. Conflated. New collector
        *                gets current value. For UI state.
        *   SharedFlow = EVENTS. No current value. Can replay history. Many
        *                collectors. For one-shot events (navigation, toasts).
        *
        *   Rule: state -> StateFlow. Events -> SharedFlow.
        *   (A StateFlow with replay=1 is similar to SharedFlow(replay=1),
        *   but StateFlow dedupes equal values and always has a value.)
        * */

        runBlocking {
            // ---------------------------------------------------------
            // 11.1 SharedFlow broadcasts to multiple collectors
            // ---------------------------------------------------------
            val events = MutableSharedFlow<String>(replay = 1)

            val c1 = launch {
                events.collect { Log.d(TAG, "collector 1: $it") }
            }
            val c2 = launch {
                events.collect { Log.d(TAG, "collector 2: $it") }
            }
            delay(100)

            events.emit("event-1")   // both collectors receive it
            delay(100)

            // New collector gets the replayed last value first:
            val c3 = launch {
                events.collect { Log.d(TAG, "late collector: $it") }
            }
            delay(100)
            c1.cancel(); c2.cancel(); c3.cancel()
        }

        /*
        * INTERVIEW QUESTIONS:
        * Q: StateFlow vs SharedFlow - when to use which?
        * A: StateFlow for UI state (always has a value, conflated, deduped).
        *    SharedFlow for events (toasts, navigation) where each occurrence
        *    matters and there is no "current" event.
        *
        * Q: What is replay in SharedFlow?
        * A: How many past values a NEW collector receives on subscribe.
        *    replay=0: late collectors only see future emissions.
        * */
    }

    // ============================================================
    // CHAPTER 12 - ANDROID INTEGRATION: the real-world patterns
    // ============================================================
    private fun androidIntegration() {

        /*
        * THE COMPLETE ANDROID ARCHITECTURE WITH FLOW:
        *
        *   UI (Activity/Fragment)
        *     |  collects StateFlow, lifecycle-aware
        *   ViewModel
        *     |  exposes StateFlow, transforms flows, owns state
        *   Repository
        *     |  exposes cold Flows (Room, DataStore, network)
        *   DataSources
        *
        * PATTERN 1 - ViewModel with StateFlow:
        *
        *   class MyViewModel(private val repo: Repo) : ViewModel() {
        *       private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
        *       val uiState: StateFlow<UiState> = _uiState.asStateFlow()
        *
        *       init {
        *           viewModelScope.launch {
        *               repo.userUpdates()          // cold Flow from Room
        *                   .map { UiState.Loaded(it) }
        *                   .catch { emit(UiState.Error(it)) }
        *                   .collect { _uiState.value = it }
        *           }
        *       }
        *   }
        *
        * PATTERN 2 - lifecycle-aware collection in the UI (CRITICAL):
        *
        *   // WRONG - collects even when the app is in background:
        *   lifecycleScope.launch {
        *       viewModel.uiState.collect { render(it) }
        *   }
        *
        *   // RIGHT - collect only while STARTED, restart on STOP->START:
        *   lifecycleScope.launch {
        *       lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        *           viewModel.uiState.collect { render(it) }
        *       }
        *   }
        *   Why: without repeatOnLifecycle, collection continues in background,
        *   wasting resources and risking crashes on UI access.
        *
        * PATTERN 3 - callbackFlow: wrap callback APIs into flows
        *   fun locationUpdates(): Flow<Location> = callbackFlow {
        *       val callback = object : LocationCallback() {
        *           override fun onLocationResult(result: LocationResult) {
        *               trySend(result.lastLocation)   // non-suspending send
        *           }
        *       }
        *       client.requestLocationUpdates(request, callback, mainLooper)
        *       awaitClose { client.removeLocationUpdates(callback) }  // cleanup
        *   }
        * */

        // ---------------------------------------------------------
        // 12.1 The ViewModel pattern, demonstrated
        // ---------------------------------------------------------

        /*
        * INTERVIEW QUESTIONS:
        * Q: How should the UI collect a StateFlow?
        * A: Inside lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { ... }
        *    (or the collectAsStateWithLifecycle API in Compose) so collection
        *    only runs while the UI is visible and restarts automatically.
        *
        * Q: What is callbackFlow for?
        * A: Converting callback-based APIs (LocationManager, Firebase listeners,
        *    BroadcastReceiver) into Flow. awaitClose { } removes the listener
        *    when collection stops.
        * */
    }

    // ============================================================
    // CHAPTER 13 - TESTING FLOWS + RAPID-FIRE INTERVIEW SUMMARY
    // ============================================================
    private fun testingFlows() {

        /*
        * TESTING TOOLS (kotlinx-coroutines-test):
        *
        * runTest { }      - like runBlocking but skips delays (virtual time!).
        * advanceTimeBy()  - fast-forward virtual time.
        * Turbine library  - popular helper: flow.test { awaitItem() }.
        *
        *   @Test
        *   fun `emits squared values`() = runTest {
        *       val flow = flowOf(1, 2, 3).map { it * it }
        *       assertEquals(listOf(1, 4, 9), flow.toList())
        *   }
        *
        *   @Test
        *   fun `state flow dedupes`() = runTest {
        *       val state = MutableStateFlow(1)
        *       val emissions = mutableListOf<Int>()
        *       val job = launch { state.collect { emissions += it } }
        *       state.value = 1   // duplicate - not emitted
        *       state.value = 2
        *       advanceUntilIdle()
        *       assertEquals(listOf(1, 2), emissions)
        *       job.cancel()
        *   }
        *
        * KEY RULE: use runTest (not runBlocking) - it manages virtual time so
        * tests with delay() finish instantly.
        * */

        // ---------------------------------------------------------
        // RAPID-FIRE INTERVIEW SUMMARY (the whole course in 10 Q&As)
        // ---------------------------------------------------------

        /*
        * 1. Q: What is Flow?
        *    A: A cold asynchronous stream of values built on coroutines.
        *
        * 2. Q: Cold vs hot?
        *    A: Cold (flow {}): runs per collector. Hot (StateFlow/SharedFlow):
        *       runs independently, collectors share the stream.
        *
        * 3. Q: What does flowOn do?
        *    A: Changes the dispatcher of everything upstream; collector untouched.
        *
        * 4. Q: Why does withContext inside flow {} throw?
        *    A: Violates context preservation - emissions must stay in the
        *    collector's context for sequential, predictable streams.
        *
        * 5. Q: buffer vs conflate vs collectLatest?
        *    A: buffer = queue values; conflate = keep newest value;
        *       collectLatest = cancel stale processing.
        *
        * 6. Q: combine vs zip?
        *    A: combine = latest of each, fires on any emission. zip = positional
        *    pairing, completes with the shorter stream.
        *
        * 7. Q: StateFlow vs SharedFlow?
        *    A: StateFlow = state (always a value, deduped). SharedFlow = events
        *    (no current value, optional replay, broadcast).
        *
        * 8. Q: StateFlow vs LiveData?
        *    A: StateFlow is pure Kotlin, full operators, any dispatcher.
        *    LiveData is Android-bound. Collect StateFlow with repeatOnLifecycle.
        *
        * 9. Q: What does flowOn do?
        *    A: Moves upstream (producer + operators above it) to a dispatcher;
        *    collector context unchanged. The only legal thread switch in a flow.
        *
        * 10. Q: How do you test a Flow?
        *     A: runTest { } with virtual time; collect into toList() and assert;
        *     Turbine for event-style assertions.
        * */
    }
}
