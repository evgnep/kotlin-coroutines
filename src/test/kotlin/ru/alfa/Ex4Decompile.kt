package ru.alfa

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.Test

class Ex4Decompile {
    private val log = KotlinLogging.logger {}

    suspend fun someMethod(): String {
        var a = 1
        var b = "x"
        log.info("1 {} {}", a, b)
        delay(100)

        a += otherMethod(5)
        b += "y"
        log.info("2 {} {}", a, b)

        delay(200)

        a += 3
        b = withCatch(b)
        log.info("3 {} {}", a, b)

        return "Some data $a $b"
    }

    suspend fun otherMethod(a: Int): Int {
        if (a == 0) return -1

        var b = a
        for (i in 1..10) {
            delay(50)
            b += 2
        }
        return b
    }

    suspend fun withCatch(a: String): String {
        try {
            delay(100)
        } catch(e: Exception) {
            return "ex"
        }
        return "$a ok"
    }

    @Test
    fun coro(): Unit = runBlocking {// ***
        someMethod()
    }
}