package io.xpdftools.common.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class ReadInputStreamTaskTest {

    @Test
    fun `should call and return lines`() {
        // given
        val inputStream = ByteArrayInputStream("line1\r\nline2\r\nline3".toByteArray())
        val readInputStreamTask = ReadInputStreamTask(inputStream)

        // when then
        readInputStreamTask.call() shouldBe "line1\nline2\nline3"
    }

    @Test
    fun `should call and return null`() {
        // given
        val inputStream = ByteArrayInputStream("".toByteArray())
        val readInputStreamTask = ReadInputStreamTask(inputStream)

        // when then
        readInputStreamTask.call() shouldBe null
    }

}