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

class DemarcatingEditsSelectorsKtTest {

    @Test
    fun `can parse del-tag`() {
        htmlDocument(a3TimesNestedTag("del")) {
            del {
                findAll {
                    expectThat(text).isEqualTo("1 2 3 2 3 3")
                }
                findFirst {
                    expectThat(text).isEqualTo("1 2 3")
                }
                del {
                    findAll {
                        expectThat(text).isEqualTo("2 3 3")
                    }
                    findFirst {
                        expectThat(text).isEqualTo("2 3")
                        del {
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
    fun `can parse ins-tag`() {
        htmlDocument(a3TimesNestedTag("ins")) {
            ins {
                findAll {
                    expectThat(text).isEqualTo("1 2 3 2 3 3")
                }
                findFirst {
                    expectThat(text).isEqualTo("1 2 3")
                }
                ins {
                    findAll {
                        expectThat(text).isEqualTo("2 3 3")
                    }
                    findFirst {
                        expectThat(text).isEqualTo("2 3")
                        ins {
                            findAll {
                                expectThat(text).isEqualTo("3")
                            }
                        }
                    }
                }
            }
        }
    }
}