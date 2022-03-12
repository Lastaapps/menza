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

import a3TimesNestedTag
import it.skrape.core.htmlDocument
import it.skrape.selects.text
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class EmbeddedContentSelectorsKtTest {

    @Test
    fun `can parse applet-tag`() {
        htmlDocument(a3TimesNestedTag("applet")) {
            applet {
                findAll {
                    expectThat(text).isEqualTo("123 23 3")
                }
                findFirst {
                    expectThat(text).isEqualTo("123")
                }
                applet {
                    findAll {
                        expectThat(text).isEqualTo("23 3")
                    }
                    findFirst {
                        expectThat(text).isEqualTo("23")
                        applet {
                            findAll {
                                expectThat(text).isEqualTo("3")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `can parse embed-tag`() {
        htmlDocument("<div><embed src='helloworld.swf'></div>") {
            embed {
                findFirst {
                    expectThat(attribute("src")).isEqualTo("helloworld.swf")
                }
            }
            div {
                findFirst {
                    embed {
                        findFirst {
                            expectThat(attribute("src")).isEqualTo("helloworld.swf")
                        }
                    }
                }
                embed {
                    findFirst {
                        expectThat(attribute("src")).isEqualTo("helloworld.swf")
                    }
                }
            }
        }
    }

    @Test
    fun `can parse iframe-tag`() {
        htmlDocument("<div><iframe>hello</iframe></div>") {
            iframe {
                findAll {
                    expectThat(text).isEqualTo("hello")
                }
            }
            div {
                findFirst {
                    iframe {
                        findFirst {
                            expectThat(text).isEqualTo("hello")
                        }
                    }
                }
                iframe {
                    findFirst {
                        expectThat(text).isEqualTo("hello")
                    }
                }
            }
        }
    }

    @Test
    fun `can parse noembed-tag`() {
        htmlDocument("<div><noembed src='helloworld.swf'></div>") {
            noembed {
                findFirst {
                    expectThat(attribute("src")).isEqualTo("helloworld.swf")
                }
            }
            div {
                findFirst {
                    noembed {
                        findFirst {
                            expectThat(attribute("src")).isEqualTo("helloworld.swf")
                        }
                    }
                }
                noembed {
                    findFirst {
                        expectThat(attribute("src")).isEqualTo("helloworld.swf")
                    }
                }
            }
        }
    }

    @Test
    fun `can parse object-tag`() {
        htmlDocument(a3TimesNestedTag("object")) {
            `object` {
                findAll {
                    expectThat(text).isEqualTo("123 23 3")
                }
                findFirst {
                    expectThat(text).isEqualTo("123")
                }
                `object` {
                    findAll {
                        expectThat(text).isEqualTo("23 3")
                    }
                    findFirst {
                        expectThat(text).isEqualTo("23")
                        `object` {
                            findAll {
                                expectThat(text).isEqualTo("3")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `can parse param-tag`() {
        htmlDocument("<div><param name='autoplay' value='true'></div>") {
            param {
                findFirst {
                    expectThat(attribute("name")).isEqualTo("autoplay")
                }
            }
            div {
                findFirst {
                    param {
                        findFirst {
                            expectThat(attribute("name")).isEqualTo("autoplay")
                        }
                    }
                }
                param {
                    findFirst {
                        expectThat(attribute("name")).isEqualTo("autoplay")
                    }
                }
            }
        }
    }

    @Test
    fun `can parse picture-tag`() {
        htmlDocument(a3TimesNestedTag("picture")) {
            picture {
                findAll {
                    expectThat(text).isEqualTo("123 23 3")
                }
                findFirst {
                    expectThat(text).isEqualTo("123")
                }
                picture {
                    findAll {
                        expectThat(text).isEqualTo("23 3")
                    }
                    findFirst {
                        expectThat(text).isEqualTo("23")
                        picture {
                            findAll {
                                expectThat(text).isEqualTo("3")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `can parse source-tag`() {
        htmlDocument("<div><source src='horse.ogg' type='audio/ogg'></div>") {
            source {
                findFirst {
                    expectThat(attribute("src")).isEqualTo("horse.ogg")
                }
            }
            div {
                findFirst {
                    source {
                        findFirst {
                            expectThat(attribute("src")).isEqualTo("horse.ogg")
                        }
                    }
                }
                source {
                    findFirst {
                        expectThat(attribute("src")).isEqualTo("horse.ogg")
                    }
                }
            }
        }
    }
}