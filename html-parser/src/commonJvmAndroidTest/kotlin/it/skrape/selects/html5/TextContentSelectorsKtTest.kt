/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

import aValidDocument
import java.awt.SystemColor.text
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TextContentSelectorsKtTest {

    @Test
    fun `can parse blockquote-tag`() {
        val selector = aValidDocument().blockquote {
            findFirst {
                expectThat(text).isEqualTo("i'm a quote")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("blockquote")
    }

    @Test
    fun `can parse dd-tag`() {
        val selector = aValidDocument().dd {
            findAll {
                expectThat(this.size).isEqualTo(2)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("dd")
    }

    @Test
    fun `can parse dir-tag`() {
        val selector = aValidDocument().dir {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("dir")
    }

    @Test
    fun `can parse dl-tag`() {
        val selector = aValidDocument().dl {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("dl")
    }

    @Test
    fun `can parse dt-tag`() {
        val selector = aValidDocument().dt {
            findAll {
                expectThat(this.size).isEqualTo(2)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("dt")
    }

    @Test
    fun `can parse figcaption-tag`() {
        val selector = aValidDocument().figcaption {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("figcaption")
    }

    @Test
    fun `can parse figure-tag`() {
        val selector = aValidDocument().figure {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("figure")
    }

    @Test
    fun `can parse hr-tag`() {
        val selector = aValidDocument().hr {
            findAll {
                expectThat(this.size).isEqualTo(2)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("hr")
    }

    @Test
    fun `can parse li-tag`() {
        val selector = aValidDocument().li {
            findAll {
                expectThat(this.size).isEqualTo(11)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("li")
    }

    @Test
    fun `can parse ol-tag`() {
        val selector = aValidDocument().ol {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("ol")
    }

    @Test
    fun `can parse ul-tag`() {
        val selector = aValidDocument().ul {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("ul")
    }

    @Test
    fun `can parse p-tag`() {
        val selector = aValidDocument().p {
            findLast {
                expectThat(text).isEqualTo("i'm the last paragraph")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("p")
    }

    @Test
    fun `can cascade p-tag`() {
        val selector = aValidDocument().p {
            withClass = "first"
            p {
                withClass = "second"
                p {
                    withClass = "third"
                    toCssSelector
                }
            }
        }

        expectThat(selector).isEqualTo("p.first p.second p.third")
    }

    @Test
    fun `can parse pre-tag`() {
        val selector = aValidDocument().pre {
            findFirst {
                expectThat(text).isEqualTo("i'm a pre")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("pre")
    }
}
