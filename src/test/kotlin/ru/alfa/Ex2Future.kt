package ru.alfa

import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.concurrent.thread

private val log = KotlinLogging.logger {}

class Ex2Future {
    fun someMethod(): CompletableFuture<String> = CompletableFuture.supplyAsync {
        log.info { "Some method" }
        Thread.sleep(1000);
        //throw RuntimeException("Error")
        "Some data"
    }

    fun otherMethod(a: Int, throwPlace: Int = 0): CompletableFuture<Int> {
        if (throwPlace == 1) throw RuntimeException("1")
        return CompletableFuture.supplyAsync {
            log.info { "Other method" }
            if (throwPlace == 2) throw RuntimeException("2")
            Thread.sleep(1000);
            a * 2
        }
    }

    @Test
    fun future() {
        someMethod()
            .thenApply {
                log.info { "Apply" }
                it.length
            }
            .thenCompose {
                otherMethod(it)
            }
            .handle { num, ex ->
                if (ex != null)
                    log.info("Exception", ex)
                else
                    log.info { "Complete $num" }
            }
            .get()

        log.info { "Complete" }

        //Thread.sleep(3000)
    }

    @Test
    fun exception() {
        CompletableFuture.completedFuture(42)
            .thenCompose {
                try {
                    otherMethod(it, 1)
                } catch (e: Exception) {
                    log.info("ThenCompose", e)
                    CompletableFuture.completedFuture(42)
                }
            }
            .get()

        log.info { "Complete" }
    }
}