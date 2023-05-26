package ru.alfa

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import mu.KotlinLogging
import org.junit.Test

class Ex12xScope {
    private val log = KotlinLogging.logger {}

    @Test
    fun coroScope1() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            coroutineScope {
                launch {
                    delay(100)
                    log.info("1")
                }
                launch {
                    delay(200)
                    log.info("2")
                }
            }
            log.info("3")

        }
        Thread.sleep(2000)
        log.info("COMPLETED!")
    }

    @Test
    fun coroScope2() {
        val scope = CoroutineScope(
            Dispatchers.Default + CoroutineExceptionHandler { ctx, ex -> log.info("Handler") }
        )
        scope.launch {
            launch {
                delay(100)
                log.info("I am alive")
            }
            try {
                coroutineScope {
                    launch {
                        Integer.parseInt("a")
                    }
                }
            } catch (e: Exception) {
                log.info("CAUGHT")
            }

        }
        Thread.sleep(2000)
        log.info("COMPLETED!")
    }

    @Test
    fun supervisorScope() {
        val scope = CoroutineScope(
            Dispatchers.Default + CoroutineExceptionHandler { ctx, ex -> log.info("Handler") }
        )
        scope.launch {
            launch {
                delay(100)
                log.info("I am alive")
            }
            try {
                supervisorScope {
                    launch {
                        Integer.parseInt("a")
                    }
                }
            } catch (e: Exception) {
                log.info("CAUGHT")
            }

        }
        Thread.sleep(2000)
        log.info("COMPLETED!")
    }

    @Test
    fun withContext() {
        val scope = CoroutineScope(
            Dispatchers.Default + CoroutineExceptionHandler { ctx, ex -> log.info("Handler") }
        )
        scope.launch {
            launch {
                delay(100)
                log.info("I am alive")
            }
            try {
                withContext(Dispatchers.IO) {
                    launch {
                        Integer.parseInt("a")
                    }
                }
            } catch (e: Exception) {
                log.info("CAUGHT")
            }

        }
        Thread.sleep(2000)
        log.info("COMPLETED!")
    }

    @Test
    fun nonCancellable(): Unit = runBlocking {
        val job = launch {
            withContext(NonCancellable) {
                delay(100)
                log.info("child")
            }
            delay(100)
            log.info("parent")
        }

        delay(50) // а если закомментировать
        job.cancel()

        log.info { "After cancel" }
    }

    @Test
    fun runT() = runTest {
        launch {
            delay(10000)
            log.info("world")
        }

        launch(Dispatchers.Default) {
            log.info("1")
            delay(1000)
            log.info("2")
        }

        log.info("hello")

        1 // если бы был runBlocking - были бы проблемы
    }

    @Test
    fun global() {
        //val scope = CoroutineScope(Job())
        val scope = GlobalScope

        scope.launch {
            Integer.parseInt("a")
        }

        Thread.sleep(500)

        scope.launch {
            log.info("hello")
        }

        Thread.sleep(500)
    }

}