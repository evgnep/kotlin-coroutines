package ru.alfa

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Ex7Launch {
    private val log = KotlinLogging.logger {}

    @Test
    fun launch1(): Unit = runBlocking {// ***
        log.info("started")
        launch {
            delay(1000)
            log.info("coro1")
        }
        launch {
            delay(1000)
            log.info("coro2")
        }
        log.info("completed")
    }

    @Test
    fun child(): Unit = runBlocking {// ***
        launch {
            launch {
                delay(100)
                log.info("child complete")
            }
            log.info("parent complete")
        }.join()
        log.info("all complete")
    }

}