/*
 * Common - The components shared between Xpdf APIs (https://xpdf.io)
 * Copyright © 2024 xpdf.io (info@xpdf.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.xpdf.api.common.exception

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class XpdfValidationExceptionTest {

    @Test
    fun `should initialize`() {
        // given
        val exception = XpdfValidationException("some message")

        // when then
        exception.message shouldBe "some message"
    }

}