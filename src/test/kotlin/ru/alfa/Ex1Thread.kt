package ru.alfa

import mu.KotlinLogging
import org.junit.Test
import kotlin.concurrent.thread

private val log = KotlinLogging.logger {}

class Ex1Thread {
    @Test
    fun thr() {
        thread {
            log.info { "Hello, thread started" }
            try {
                for (i in 1..10) {
                    log.info { "i = $i" }
                    Thread.sleep(100)
                    if (i == 6) throw RuntimeException("Some error")
                }
            } catch (e: Exception) {
                log.info { "Exception" }
            }
        }.join()
        log.info { "Thread complete" }
    }
}