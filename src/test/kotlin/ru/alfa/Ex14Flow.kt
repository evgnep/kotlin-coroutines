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

class Ex14Flow {
    private val log = KotlinLogging.logger {}

    @Test
    fun simple() {
        val x = flow<Int> {
            for (i in 1..5) {
                delay(100)
                log.info("Send $i")
                emit(i)
            }
        }

        runBlocking {
            x.collect {
                log.info("Receive $it")
            }

            x.collect { // cold
                log.info("Receive2 $it")
            }
        }
    }

    @Test
    fun ignore() {
        val x = flow<Int> {
            log.info("Hello")
        }
    }

    @Test
    fun map(): Unit = runBlocking {
        flowOf(1, 2, 3)
            .map { it * 2 }
            .collect {
                log.info("$it")
            }
    }

    // мы не ищем легких путей
    fun Flow<String>.toUpper(): Flow<String> = flow {
        val src = this@toUpper
        src.collect {
            emit(it.uppercase())
        }
    }

    @Test
    fun customOperator(): Unit = runBlocking {
        flowOf("a", "Bc")
            .toUpper()
            .collect {
                log.info(it)
            }
    }

    suspend fun Flow<String>.join(): String {
        val res = StringBuilder()
        val src = this@join
        src.collect {
            res.append(it)
        }
        return res.toString()
    }

    @Test
    fun customTerminalOperator() = runBlocking {
        val res = flowOf("a", "Bc")
            .toUpper()
            .join()
        log.info(res)
    }

    @Test
    fun parallel1() = runBlocking {
        flow<String> {
            coroutineScope {
                launch {
                    delay(100)
                    emit("a")
                }
                launch {
                    delay(200)
                    emit("b")
                }
            }
        }.collect {
            log.info(it)
        }
    }

    @Test
    fun parallel2() = runBlocking {
        flow<String> {
            coroutineScope {
                val channel = produce<String> {
                    launch {
                        delay(100)
                        send("a")
                    }
                    launch {
                        delay(200)
                        send("b")
                    }
                }
                channel.consumeEach { emit(it) }
            }
        }.collect {
            log.info(it)
        }
    }

    @Test
    fun channelFlow() = runBlocking {
        channelFlow<String> {
            launch {
                delay(100)
                send("a")
            }
            launch {
                delay(200)
                send("b")
            }
        }.collect {
            log.info(it)
        }
    }

    @Test
    fun exception() = runBlocking {
        val flow = flow<Int> {
            emit(1)
            emit(Integer.parseInt("a"))
        }
        try {
            flow.collect {
                log.info("$it")
            }
        } catch (e: Exception) {
            log.error("CAUGHT", e)
        }
    }

    @Test
    fun catch() = runBlocking {
        val flow = flow<Int> {
            emit(1)
            emit(Integer.parseInt("a"))
        }.catch {
            emit(42)
        }.collect {
            log.info("$it")
        }
    }

    @Test
    fun cancel() = runBlocking {
        val job = launch {
            flow<Int> {
                emit(1)
                delay(1000)
                emit(2)
            }.collect {
                log.info("$it")
            }
        }

        delay(50)
        job.cancel()
    }


}