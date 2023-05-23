package ru.alfa

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Ex6Job {
    private val log = KotlinLogging.logger {}

    fun CoroutineScope.createJob(name: String, start: CoroutineStart = CoroutineStart.DEFAULT) = launch(start = start) {
        delay(10)
        log.info { "start $name" }
        delay(1000)
        log.info { "complete $name" }
    }

    @Test
    fun join(): Unit = runBlocking {
        val job1 = createJob("1")
        job1.join()
        log.info { "After join" }
    }

    @Test
    fun manualStart(): Unit = runBlocking {
        val job1 = createJob("1", start = CoroutineStart.LAZY)
        delay(50)
        log.info { "Manual start" }
        job1.start()
    }

    @Test
    fun dontStart(): Unit = runBlocking {
        val job1 = createJob("1", start = CoroutineStart.LAZY)
        delay(50)
        //job1.join()
    }

    @Test
    fun cancel(): Unit = runBlocking {
        val job1 = createJob("1")
        delay(50)
        job1.cancel()
        log.info { "After cancel" }
    }

    suspend fun x() {
        Thread.sleep(10)
    }

    @Test
    fun cancelTrouble(): Unit = runBlocking {
        val job1 = launch(Dispatchers.IO) {
            for (i in 1 .. 1000) {
                x()
                //if (!isActive) break
            }
            log.info("Job complete")
        }
        delay(50)
        log.info { "Before cancel" }
        job1.cancel()
        log.info { "After cancel" }
    }

    @Test
    fun scope() {
        val scope = CoroutineScope(Job())
        scope.createJob("1")
        scope.createJob("2")

        Thread.sleep(500)
        scope.cancel()
    }

    @Test
    fun scopeHierarchy(): Unit = runBlocking {
        log.info("top $this")
        val job1 = launch {
            log.info("job1 block $this")

            val myJob = this.coroutineContext[Job]
            log.info("job1 myJob $myJob")


            val job2 = launch {
                log.info("job2 block $this")
            }
            log.info("job2 $job2")
        }
        log.info("job1 $job1")
    }
}