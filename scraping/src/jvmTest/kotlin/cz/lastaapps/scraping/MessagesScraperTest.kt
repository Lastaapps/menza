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

package cz.lastaapps.scraping

import cz.lastaapps.entity.menza.MenzaId
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class MessagesScraperTest {

    @Test
    fun testMessagesOnline() = runTest {

        val result = MessagesScraperImpl.createRequest().bodyAsText()
        val messages = MessagesScraperImpl.scrape(result)

        messages.shouldNotBeEmpty()
    }

    @Test
    fun testHoliday() = runTest {

        val toTest = """
<div id="aktuality" class="aktuality" style="display:block; max-width:800px; padding-left:10px;">
   <div class="row-fluid">
      <div class="span6">
         <div id="Info3" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="ArchiCafé" class="label label-important">Technická menza</span></p>
            <p>
               Vážení hosté, <br>
               provozní doba během vánočních svátků 2021<br>
               Pondělí  20. prosince		8:00 - 14:30<br>
               Úterý      21. prosince		8:00 - 14:30<br>
               Středa    22. prosince		zavřeno<br>
               Čtvrtek  23. prosince		zavřeno<br>
               27.prosince  - 31. prosince  ZAVŘENO<br>
               Kolektiv zaměstnanců Technické menzy<br>
               vám přeje krásné Vánoce a šťastný Nový rok 2022. <br>
               Budeme se na Vás těšit 3. ledna 2022<br>
            </p>
         </div>
      </div>
      <div class="span6">
         <div id="Info2" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Masarykova kolej" class="label label-important">Menza Studentský dům</span></p>
            <p>
               Vážení hosté,<br>
               během vánočních svátků 2021<br>
               od 20. prosince do 31. prosince ZAVŘENO.<br>
               Kolektiv zaměstnanců menzy vám přeje krásne Vánoce                                                                                                                                                                                                          a šťastný Nový rok 2022.<br>
               Těšíme se na Vás 3. ledna 2022.                                                                                                                                       <br>
               Vážení zákazníci,<br>
               od 22.11.2021<br>
               Zákaz vstupu osob bez Certifikátu o očkování nebo prodělaném onemocnění covid-19.<br>
               Možnost namátkové kontroly.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       Nevztahuje se na studenty a zaměstnance VŠ.<br>
            </p>
         </div>
      </div>
   </div>
   <br>
   <div class="row-fluid">
      <div class="span6">
         <div id="Info5" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="MEGA BUF FAT" class="label label-important">Masarykova kolej</span></p>
            <p>
               Vážení hosté,<br>
               20.12.-31.12.2021 bude Akademická restaurace zavřená.<br>
               Přejeme krásné svátky<br>
               Vážení zákazníci,<br>
               v souladu s nařízením Vlády ČR <br>
               je nutné při vstupu do restaurace se prokázat<br>
               dokladem o bezinfekčnosti (dokončené očkování, prodělání nemoci).<br>
               V případě nedoložení dokladu, nebude možné zákazníka obsloužit.<br>
               Děkujeme za pochopení<br>
            </p>
         </div>
      </div>
      <div class="span6">
         <div id="Info15" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Kladno" class="label label-important">ArchiCafé</span></p>
            <p>
               .Vážení zákazníci,<br>
               od 1.11.2021<br>
               Zákaz vstupu osob bez Certifikátu o očkování,prodělaném onemocnění<br>
               na covid-19.<br>
               Možnost namátkové kontroly.<br>
               Nevztahuje se na studenty a zaměstnance VŠ.<br>
               Během vánočních svátků 2021<br>
               od 20.12.2021 do 2.1.2022 ZAVŘENO<br>
            </p>
         </div>
      </div>
   </div>
   <br>
   <div class="row-fluid">
      <div class="span6">
         <div id="Info1" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Podolí" class="label label-important">Menza Strahov</span></p>
            <p>
               Vážení hosté,<br>
               provozní doba během vánočních svátků<br>
               20.12.-21.12.2021 - Snídaně- Zavřeno<br>
               Jídelna       11:00 - 14:30 <br>
               21.12.2021 RESTAURACE ZAVŘENO<br>
               22.12. - 31.12.2021 ZAVŘENO<br>
               Zákaz vstupu osob bez Certifikátu o očkování nebo prodělaném onemocnění covid - 19<br>
               Možnost namátkové kontroly. Nevztahuje se na studenty a zaměstnance ČVUT <br>
            </p>
         </div>
      </div>
      <div class="span6">
         <div id="Info8" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Strahov" class="label label-important">Výdejna Karlovo náměstí</span></p>
            <p>
               Vážení hosté,<br>
               výdejna bude od 20.12.2021 do 2.1.2022 uzavřena.<br>
               Kolektiv výdejny Vám přeje klidné a šťastné vánoční svátky<br>
               Zákaz vstupu bez Certifikátu o očkování, <br>
               prodělaném onemocnění  covid-19.<br>
               Možnost namátkové kontroly<br>
               Nevztahuje se na studenty a zaměstnance VŠ.<br>
            </p>
         </div>
      </div>
   </div>
   <br>
   <div class="row-fluid">
      <div class="span6">
         <div id="Info6" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Studentský dům" class="label label-important">Výdejna Horská</span></p>
            <p>
               Vážení hosté,<br>
               výdejna bude uzavřena od 20.12.2021 do 2.1.2022.<br>
               Kolektiv výdejny Horská Vám přeje klidné a šťastné vánoční svátky<br>
               ZÁKAZ VSTUPU OSOB BEZ CERTIFIKÁTU O OČKOVÁNÍ,PRODĚLANÉM ONEMOCNĚNÍ COVID-19.<br>
               Možnost namátkové kontroly.<br>
               Nevztahuje se na studenty a zaměstnace VŠ<br>
            </p>
         </div>
      </div>
      <div class="span6">
         <div id="Info4" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Oddělění správy IT GÚ" class="label label-important">Menza Podolí</span></p>
            <p>
               Vážení hosté,<br>
               menza Podolí bude od  20.12.2021 do 2.1.2022 uzavřena.<br>
               Kolektiv menzy Vám přeje klidné a šťastné vánoční svátky.<br>
               ZÁKAZ VSTUPU OSOB BEZ CERTIFIKÁTU O OČKOVÁNÍ,PRODĚLANÉM ONEMOCNĚNÍ  COVID-19.<br>
               Možnost namátkové kontroly.<br>
               Nevztahuje se na studenty a zaměstnance VŠ<br>
            </p>
         </div>
      </div>
   </div>
   <br>
   <div class="row-fluid">
      <div class="span6">
         <div id="Info12" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Technická menza" class="label label-important">MEGA BUF FAT</span></p>
            <p>
               Zákaz vstupu osob bez Certifikátu o očkování nebo prodělaném onemocnění covid-19.<br>
               Možnost namátkové kontroly.<br>
               Nevztahuje se na studenty a zaměstnance VŠ<br>
               Megabuffat  vánoční provoz :  20.12.2021 - 2.1.2022       ZAVŘENO<br>
            </p>
         </div>
      </div>
      <div class="span6">
         <div id="Info9" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Výdejna Horská" class="label label-important">Menza Kladno</span></p>
            <p>
               Od 20.12.20201 do 2.1.2022 bude Menza UZAVŘENA. Přejeme krásné prožití Vánočních svátků.<br>
            </p>
         </div>
      </div>
   </div>
   <br>
   <div class="row-fluid"></div>
</div>
"""
        val messages = MessagesScraperImpl.scrape(toTest)

        messages shouldHaveSize 10
        messages.map { it.id } shouldContain MenzaId(1)
        messages.find { it.id == MenzaId(1) }?.message shouldBe """Vážení hosté,
provozní doba během vánočních svátků
20.12.-21.12.2021 - Snídaně- Zavřeno
Jídelna 11:00 - 14:30
21.12.2021 RESTAURACE ZAVŘENO
22.12. - 31.12.2021 ZAVŘENO
Zákaz vstupu osob bez Certifikátu o očkování nebo prodělaném onemocnění covid - 19
Možnost namátkové kontroly. Nevztahuje se na studenty a zaměstnance ČVUT""".trimIndent()
    }

    @Test
    fun malformed() = runTest {
        val noItems =
            """<div id="aktuality" class='aktuality' style="display:block; max-width:800px; padding-left:10px;">
</div>"""
        val noMessageElement =
            """<div id="aktuality" class='aktuality' style="display:block; max-width:800px; padding-left:10px;">
   <div class="row-fluid">
      <div class="span6">
         <div id="Info1" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Podolí" class="label label-important">Menza Strahov</span></p>
         </div>
      </div>
   </div>
</div>"""
        val noMessageText =
            """<div id="aktuality" class='aktuality' style="display:block; max-width:800px; padding-left:10px;">
   <div class="row-fluid">
      <div class="span6">
         <div id="Info1" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Podolí" class="label label-important">Menza Strahov</span></p>
            <p>
            </p>
         </div>
      </div>
   </div>
</div>"""
        val noMenzaId =
            """<div id="aktuality" class='aktuality' style="display:block; max-width:800px; padding-left:10px;">
   <div class="row-fluid">
      <div class="span6">
         <div id="Info" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Podolí" class="label label-important">Menza Strahov</span></p>
            <p>
               Zpráva
            </p>
         </div>
      </div>
   </div>
</div>"""
        val noMenzaIdAttribute =
            """<div id="aktuality" class='aktuality' style="display:block; max-width:800px; padding-left:10px;">
   <div class="row-fluid">
      <div class="span6">
         <div class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Podolí" class="label label-important">Menza Strahov</span></p>
            <p>
               Zpráva
            </p>
         </div>
      </div>
   </div>
</div>"""
        val malformedId =
            """<div id="aktuality" class='aktuality' style="display:block; max-width:800px; padding-left:10px;">
   <div class="row-fluid">
      <div class="span6">
         <div id="InfoABC" class="alert alert-error" style="height:auto; overflow:hidden; text-overflow: ellipsis;">
            <p><span title="Menza Podolí" class="label label-important">Menza Strahov</span></p>
            <p>
               Zpráva
            </p>
         </div>
      </div>
   </div>
</div>"""

        MessagesScraperImpl.scrape(noItems).shouldBeEmpty()
        MessagesScraperImpl.scrape("").shouldBeEmpty()
        shouldThrowAny { MessagesScraperImpl.scrape(noMessageElement) }
        shouldThrowAny { MessagesScraperImpl.scrape(noMessageText) }
        shouldThrowAny { MessagesScraperImpl.scrape(noMenzaId) }
        shouldThrowAny { MessagesScraperImpl.scrape(noMenzaIdAttribute) }
        shouldThrowAny { MessagesScraperImpl.scrape(malformedId) }
    }

}