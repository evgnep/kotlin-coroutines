package ru.alfa

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import org.junit.Test

class Ex17Actor {
    private val log = KotlinLogging.logger {}

    @Test
    fun test(): Unit = runBlocking {
        val c1: SendChannel<String> = actor<String> {
            channel.consumeEach { log.info(it) }
        }

        c1.send("Hello")
        c1.send("world")
        c1.close()
    }

}