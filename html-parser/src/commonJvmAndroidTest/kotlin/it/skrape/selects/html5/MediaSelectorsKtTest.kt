/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.skrape.selects.html5

import aSelfClosingTag
import aStandardTag
import aValidDocument
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MediaSelectorsKtTest {

    @Test
    fun `can parse area-tag`() {
        val selector = aValidDocument(aStandardTag("area")).area {
            findFirst {
                expectThat(className).isEqualTo("area-class")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("area")
    }

    @Test
    fun `can parse audio-tag`() {
        val selector = aValidDocument(aStandardTag("audio")).audio {
            findFirst {
                expectThat(text).isEqualTo("i'm a audio")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("audio")
    }

    @Test
    fun `can parse img-tag`() {
        val selector = aValidDocument(aSelfClosingTag("img")).img {
            findFirst {
                expectThat(attribute("custom-attr")).isEqualTo("img-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("img")
    }

    @Test
    fun `can parse map-tag`() {
        val selector = aValidDocument(aStandardTag("map")).map {
            findFirst {
                expectThat(text).isEqualTo("i'm a map")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("map")
    }

    @Test
    fun `can parse track-tag`() {
        val selector = aValidDocument(aSelfClosingTag("track")).track {
            findFirst {
                expectThat(attribute("custom-attr")).isEqualTo("track-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("track")
    }

    @Test
    fun `can parse video-tag`() {
        val selector = aValidDocument(aStandardTag("video")).video {
            findFirst {
                expectThat(text).isEqualTo("i'm a video")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("video")
    }
}