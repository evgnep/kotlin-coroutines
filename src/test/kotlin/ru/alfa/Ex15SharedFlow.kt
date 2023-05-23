package ru.alfa

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.Test

class Ex15SharedFlow {
    private val log = KotlinLogging.logger {}

    @Test
    fun test(): Unit = runBlocking {
        val bus = MutableSharedFlow<Int>()
        val reader = bus.asSharedFlow()

        val sub1 = launch {
            reader.collect { // не завершится!
                log.info("sub1 $it")
                delay(200)
            }
        }

        val sub2 = launch {
            reader.collect { // не завершится!
                log.info("sub2 $it")
                delay(200)
            }
        }

        launch {
            launch {
                delay(100)
                for (i in 1..5) {
                    log.info("Send1 $i")
                    bus.emit(i)
                }
            }

            launch {
                delay(100)
                for (i in 11..15) {
                    log.info("Send2 $i")
                    bus.emit(i)
                }
            }
        }.join()

        sub1.cancel()
        sub2.cancel()
    }

}