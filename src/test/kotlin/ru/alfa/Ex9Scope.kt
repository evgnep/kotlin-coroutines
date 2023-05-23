package ru.alfa

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.*

class Ex9Scope {
    private val log = KotlinLogging.logger {}

    @Test
    fun create1() {
        val scope = CoroutineScope(Dispatchers.Main)

        log.info("scope: ${scope}")
    }

    @Test
    fun create2() {
        val scope = CoroutineScope(Dispatchers.Main + Job())

        log.info("scope: ${scope}")
    }

    @Test
    fun create3() {
        val scope = CoroutineScope(SomeData(10, 20))
        val data = scope.coroutineContext[SomeData]

        log.info("scope: $scope, $data")
    }

    private fun CoroutineScope.scopeToString(): String =
        "Job = ${coroutineContext[Job]}, Dispatcher = ${coroutineContext[ContinuationInterceptor]}, Data = ${coroutineContext[SomeData]}"

    @Test
    fun defaults() {
        val scope = CoroutineScope(SomeData(10, 20))
        scope.launch {
            log.info("Child1: ${scopeToString()}")
        }
        scope.launch(SomeData(1, 2)) {
            log.info("Child2: ${scopeToString()}")
        }

        log.info("This: ${scope.scopeToString()}")
    }

    data class SomeData(val x: Int, val y: Int) : AbstractCoroutineContextElement(SomeData) {
        companion object : CoroutineContext.Key<SomeData>
    }
}