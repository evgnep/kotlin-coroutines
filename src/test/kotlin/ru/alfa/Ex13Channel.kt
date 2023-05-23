package ru.alfa

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import mu.KotlinLogging
import org.junit.Test

class Ex13Channel {
    private val log = KotlinLogging.logger {}

    @Test
    fun simple(): Unit = runBlocking {
        val channel = Channel<Int>()
        launch {
            delay(100)
            log.info("send")
            channel.send(5)
        }

        launch {
            log.info("before receive")
            val v = channel.receive()
            log.info("{}", v)
        }
    }

    @Test
    fun block(): Unit = runBlocking {
        val channel = Channel<Int>()
        launch {
            channel.send(1)
            log.info("sent 1")
            channel.send(2)
            log.info("sent 2")
            channel.send(3)
            log.info("sent 3")
        }

        launch {
            log.info("{}", channel.receive())
        }

        delay(2000)
        log.info("cancel")
        cancel()
    }

    @Test
    fun close(): Unit = runBlocking {
        val channel = Channel<Int>()
        launch {
            channel.send(1)
            log.info("sent 1 and close")
            channel.close()
        }

        launch {
            log.info("1: {}", channel.receive())
            try {
                log.info("2: {}", channel.receive())
            } catch (e: Exception) {
                log.info("CAUGHT!", e)
            }
        }
    }

    @Test
    fun capacity(): Unit = runBlocking {
        val channel = Channel<Int>(2)
        //val channel = Channel<Int>(Channel.Factory.CONFLATED)
        launch {
            for (i in 1 .. 5) {
                log.info("sent $i")
                channel.send(i)
            }

            log.info("closed")
            channel.close()
        }

        launch {
            delay(100)
            for (v in channel) {
                log.info("{}", v)
                delay(10)
            }
        }
    }

    @Test
    fun cancel(): Unit = runBlocking {
        val channel = Channel<Int>()
        val job1 = launch {
            /* // 1 - раскомментировать
            coroutineContext[Job]?.invokeOnCompletion {
                channel.close()
                log.info("closed")
            }
             */
            for (i in 1 .. 5) {
                log.info("sent $i")
                channel.send(i)
                delay(100)
            }

            // 1 - закомментировать
            channel.close()
            log.info("closed")
        }

        launch {
            for (v in channel) {
                log.info("{}", v)
            }
        }

        delay(200)
        job1.cancel()
    }

    @Test
    fun helpers() : Unit = runBlocking {
        val channel = produce<Int>(capacity=2) {
            for (i in 1 .. 5) {
                log.info("sent $i")
                send(i)
                delay(100)
            }
        }
        launch {
            channel.consumeEach {
                log.info("{}", it)
            }
        }

        delay(200)
        cancel()
    }

}