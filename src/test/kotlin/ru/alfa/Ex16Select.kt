package ru.alfa

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import org.junit.Test

class Ex16Select {
    private val log = KotlinLogging.logger {}

    @Test
    fun test(): Unit = runBlocking {
        val res = select<String> {
            async {
                delay(300)
                "Hello"
            }.onAwait { it }

            async {
                delay(200)
                "World"
            }.onAwait  { it }

            onTimeout(1000) {
                "ops"
            }
        }
        log.info(res)
    }

}