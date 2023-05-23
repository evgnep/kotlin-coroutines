package ru.alfa

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Ex5Continuation {
    private val log = KotlinLogging.logger {}

    private fun future() = CompletableFuture.supplyAsync {
        Thread.sleep(1000)
        log.info("Future finished")
        42
    }

    @Test
    fun coro(): Unit = runBlocking {// ***
        val res = suspendCoroutine<Int> { continuation ->
            future().whenComplete { res, ex ->
                if (ex == null) continuation.resume(res)
                else continuation.resumeWithException(ex)
            }
        }
        log.info("Complete: $res")

    }

}