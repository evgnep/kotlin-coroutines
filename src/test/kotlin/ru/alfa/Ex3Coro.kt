package ru.alfa

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import kotlin.concurrent.thread

private val log = KotlinLogging.logger {}

class Ex3Coro {
    suspend fun someMethod(): String { // ***
        log.info("Some method")
        delay(1000) // ***
        return "Some data"
    }

    suspend fun otherMethod(a: Int, throwPlace: Int = 0): Int {
        if (throwPlace == 1) throw RuntimeException("1")
        log.info("Other method")
        if (throwPlace == 2) throw RuntimeException("2")
        delay(1000);
        return a * 2
    }

    fun x() {
        //someMethod()
    }

    @Test
    fun coro(): Unit = runBlocking {// ***
        launch {// ***
            try {
                val str = someMethod()

                log.info("Apply")
                val len = str.length

                val num = otherMethod(len)
                log.info("Complete $num")
            } catch (ex: Exception) {
                log.info("Exception", ex)
            }
        }.join()

        log.info("Complete")
    }

    fun doSomething(block: suspend (Int) -> Unit) {
    }

    fun simpleMethod(i: Int) {
    }

    @Test
    fun signature(): Unit = runBlocking {
        val list = listOf(1, 2, 3)

        list.forEach { otherMethod(it) } // это работает, потому что inline
        //list.forEach (Consumer { otherMethod(it) }) // это не работает, потому что ждут обычную функцию

        doSomething(::otherMethod) // ждут suspend и передаем его
        doSomething(::simpleMethod) // ждут suspend, передаем обычный метод - это ок, котлин вставит преобразование
    }

    @Test
    fun launch2(): Unit = runBlocking {// ***
        launch {// ***
            for (i in 1 .. 10) {
                log.info { "coro1 $i" }
                delay(100)
            }
        }

        launch {// ***
            for (i in 1 .. 10) {
                log.info { "coro2 $i" }
                delay(110)
            }
        }
    }

    @Test
    fun launchMany(): Unit = runBlocking {// ***
        val counter = AtomicInteger()

        log.info { "START" }
        launch {
            for (i in 0..1_000_000) {
                launch {
                    delay(100)
                    counter.incrementAndGet()
                }
            }
        }.join()

        log.info ("COMPLETE ${counter.get()}")
    }

}