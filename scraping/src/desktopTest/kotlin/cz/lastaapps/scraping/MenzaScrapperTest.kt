/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.scraping

import cz.lastaapps.entity.menza.Opened
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class MenzaScrapperTest {

    @Test
    fun menzaListOnline() = runTest {

        val result = ContactsScrapper.createRequest().scrape()
        val menzaList = MenzaScrapper.scrape(result)

        menzaList.shouldNotBeEmpty()
        menzaList.map { it.menzaId.id } shouldContain 1
        menzaList.map { it.menzaId.id } shouldContain 2
        menzaList.find { it.menzaId.id == 1 }?.name shouldBe "Menza Strahov"
        menzaList.find { it.menzaId.id == 2 }?.name shouldBe "Menza Studentský dům"
    }

    @Test
    fun menzaList() = runTest {
        val toTest = """<div class="subnav" id="divmenumenzy">
            <ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS15"><a id="podSh15" href="javascript:void(0)" onclick="SelectPodsys('15');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            ArchiCafé</a></li>
            <li id="podS5"><a id="podSh5" href="javascript:void(0)" onclick="SelectPodsys('5');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Masarykova kolej</a></li>
            <li id="podS12"><a id="podSh12" href="javascript:void(0)" onclick="SelectPodsys('12');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            MEGA BUF FAT</a></li>
            <li id="podS9"><a id="podSh9" href="javascript:void(0)" onclick="SelectPodsys('9');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Menza Kladno</a></li>
            <li id="podS4"><a id="podSh4" href="javascript:void(0)" onclick="SelectPodsys('4');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Menza Podolí</a></li>
            <li id="podS1"><a id="podSh1" href="javascript:void(0)" onclick="SelectPodsys('1');">
            <img src="img/Otevreno.png" alt="Provoz otevřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Menza Strahov</a></li>
            <li id="podS2"><a id="podSh2" href="javascript:void(0)" onclick="SelectPodsys('2');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Menza Studentský dům</a></li>
            <li id="podS14"><a id="podSh14" href="javascript:void(0)" onclick="SelectPodsys('14');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Oddělění správy IT GÚ</a></li>
            <li id="podS3"><a id="podSh3" href="javascript:void(0)" onclick="SelectPodsys('3');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Technická menza</a></li>
            <li id="podS6"><a id="podSh6" href="javascript:void(0)" onclick="SelectPodsys('6');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Výdejna Horská</a></li>
            <li id="podS8"><a id="podSh8" href="javascript:void(0)" onclick="SelectPodsys('8');">
            <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
            Výdejna Karlovo náměstí</a></li>
        </ul>
        <img src="img/Otevreno.png" alt="Provoz otevřen" width="15" height="15" class="d-inline-block align-text-top"> - Otevřeno <br>
        <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top"> - Zavřeno
      </div>"""

        val menzaList = MenzaScrapper.scrape(toTest)

        menzaList shouldHaveSize 11
        menzaList.map { it.menzaId.id } shouldContain 1
        menzaList.find { it.menzaId.id == 1 }?.name shouldBe "Menza Strahov"
        menzaList.find { it.menzaId.id == 1 }?.opened shouldBe Opened.OPENED
        menzaList.map { it.menzaId.id } shouldContain 2
        menzaList.find { it.menzaId.id == 2 }?.name shouldBe "Menza Studentský dům"
        menzaList.find { it.menzaId.id == 2 }?.opened shouldBe Opened.CLOSED
    }

    @Test
    fun malformed() {
        val emptyList = """<ul class="nav nav-pills nav-stacked" id="menzy">
         </ul>"""
        val noId = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a id="podSh" href="javascript:void(0)" onclick="SelectPodsys('1');">
               <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
               Menza Strahov</a>
            </li>
         </ul>"""
        val noIdField = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a href="javascript:void(0)" onclick="SelectPodsys('1');">
               <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
               Menza Strahov</a>
            </li>
         </ul>"""
        val malformedId = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a id="podShABC" href="javascript:void(0)" onclick="SelectPodsys('1');">
               <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
               Menza Strahov</a>
            </li>
         </ul>"""
        val wrongImage = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a id="podSh1" href="javascript:void(0)" onclick="SelectPodsys('1');">
               <img src="img/No_to_urcite_ty_kokos.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
               Menza Strahov</a>
            </li>
         </ul>"""
        val noImage = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a id="podSh1" href="javascript:void(0)" onclick="SelectPodsys('1');">
               Menza Strahov</a>
            </li>
         </ul>"""
        val noImageSrc = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a id="podSh1" href="javascript:void(0)" onclick="SelectPodsys('1');">
               <img alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
               Menza Strahov</a>
            </li>
         </ul>"""
        val noMenzaName = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1"><a id="podSh1" href="javascript:void(0)" onclick="SelectPodsys('1');">
               <img src="img/Zavreno.png" alt="Provoz uzavřen" width="15" height="15" class="d-inline-block align-text-top">                        
               </a>
            </li>
         </ul>"""
        val noLink = """<ul class="nav nav-pills nav-stacked" id="menzy">
            <li id="podS1">
            </li>
         </ul>"""

        MenzaScrapper.scrape(emptyList).shouldBeEmpty()
        shouldThrowAny { MenzaScrapper.scrape("") }
        shouldThrowAny { MenzaScrapper.scrape(noId) }
        shouldThrowAny { MenzaScrapper.scrape(noIdField) }
        shouldThrowAny { MenzaScrapper.scrape(malformedId) }
        shouldThrowAny { MenzaScrapper.scrape(wrongImage) }
        shouldThrowAny { MenzaScrapper.scrape(noImage) }
        shouldThrowAny { MenzaScrapper.scrape(noImageSrc) }
        shouldThrowAny { MenzaScrapper.scrape(noMenzaName) }
        MenzaScrapper.scrape(noLink).shouldBeEmpty()
    }
}