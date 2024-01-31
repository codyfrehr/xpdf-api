package io.xpdf.api.common.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class ReadInputStreamTaskTest {

    private val originalLineSeparator = System.getProperty("line.separator")

    @AfterEach
    fun afterEach() {
        // reset system properties that test may have altered
        System.setProperty("line.separator", originalLineSeparator)
    }

    @Test
    fun `should call and return lines`() {
        // given
        System.setProperty("line.separator", " END ")

        val inputStream = ByteArrayInputStream("line1\r\nline2\r\nline3".toByteArray())
        val readInputStreamTask = ReadInputStreamTask(inputStream)

        // when then
        readInputStreamTask.call() shouldBe "line1 END line2 END line3"
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