package io.xpdftools.common.util

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class ReadInputStreamTaskTest {

    @Test
    fun `should call and return lines`() {
        // given
        mockkStatic(System::class)
        every { System.lineSeparator() } returns "\n"

        val inputStream = ByteArrayInputStream("line1\r\nline2\r\nline3".toByteArray())
        val readInputStreamTask = ReadInputStreamTask(inputStream)

        // when then
        readInputStreamTask.call() shouldBe "line1\nline2\nline3"

        unmockkStatic(System::class)
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