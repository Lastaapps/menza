/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.exceptions.WeekNotAvailable
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.entity.week.WeekNumber
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.todayIn
import org.junit.jupiter.api.Test
import java.time.Month
import kotlin.time.Clock

@ExperimentalCoroutinesApi
class WeekScraperTest {
    @Test
    fun scrapeWeekOnline() =
        runTest {
            val date = Clock.System.todayIn(CET)
            val weekNumber = WeekNumber.of(date)
            println("Loading for $date, weekNumber is ${weekNumber.week}")

            shouldThrow<WeekNotAvailable> {
                val result = WeekScraperImpl.createRequest(MenzaId(15), weekNumber).bodyAsText()
                WeekScraperImpl.scrape(result)
            }
            val result = WeekScraperImpl.createRequest(MenzaId(1), weekNumber).bodyAsText()
            val weekDishSet = WeekScraperImpl.scrape(result)

            weekDishSet.forEach {
                println(it)
            }

            weekDishSet.shouldNotBeNull()
            weekDishSet.shouldNotBeEmpty()
            weekDishSet.map { it.courseType.type } shouldContain "Polévky"
            weekDishSet.map { it.courseType.type } shouldContain "Specialita dne"
        }

    @Test
    fun scrapeWeek() =
        runTest {
            val toTest = """<body><input type='hidden' id='PodsysActive' value='1'></body>
            <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class="jidelnicekheader">
    <div class="row-fluid">
      <div class="span3">
        <span><b>Studentský dům&nbsp;</b></span>
      </div>
      <div class="span9">
        <p>
          <a href="?clPodsystem=2&clTyden=2785" class="btn btn-small">Týden 1 - Obědy</a>      
        </p>
      </div>
    </div>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <thead>
        <tr>
          <th style="width:80px; text-align:left;">Datum</th>
          <th style="width:60px; text-align:left;">Den</th>
          <th style="width:120px; text-align:right;">&nbsp;</th>
          <th style="width:50px; text-align:right;">Váha</th>
          <th >Název</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
          <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
          <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;3. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pondělí</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Brokolicový krém</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Hovězí vývar s těstovinou</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            <b>Specialita dne</b>    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Hovězí flap steak s brusinkovou omáčkou, bramborové krokety</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Kuřecí Šuang-si, jasmínová rýže</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Krůtí prsa na rajčatech, těstoviny penne</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Hamburská vepřová kýta, houskové knedlíky</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;200 g</td>
          <td>Zeleninové lečo, vařené brambory</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Moučníky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Croissant</td>
        </tr>
        <tr>
          <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
          <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
          <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;4. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Úterý</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Zeleninový vývar s bulgurem</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Dršťková</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            <b>Specialita dne</b>    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Grilovaný steak z tuňáka, pečené brambory, dip ze zakysané smetany</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Plzeňský hovězí guláš, houskové knedlíky</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Smažený kuřecí řízek, bramborová kaše, řez citrónu</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Přírodní vepřový plátek, dušená rýže</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;200 g</td>
          <td>Houbové ragú, dušená rýže</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Moučníky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Listový řez s náplní</td>
        </tr>
        <tr>
          <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
          <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
          <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;5. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Středa</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Bramborová s houbami</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Uzená s rýží</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            <b>Specialita dne</b>    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Krůtí medailonky s grilovanou zeleninou, americké brambory</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;350 g</td>
          <td>Boloňské lasagne</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;200 g</td>
          <td>Kuřecí závitek, dušená rýže, / americké brambory /</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Vepřové výpečky, dušený špenát, bramborové knedlíky</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;200 g</td>
          <td>Zeleninové sabdží, jasmínová rýže</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Moučníky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Kremrole</td>
        </tr>
        <tr>
          <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
          <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
          <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;6. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Čtvrtek</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Thajská s kokosovým mlékem</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Zelná s uzeným masem</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            <b>Specialita dne</b>    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Kuřecí steak se šunkou a sýrem, smažené hranolky</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Smažené rybí filé, bramborová kaše, řez citrónu</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Hovězí maso vařené, rajská omáčka, houskové knedlíky, / vařené těstoviny /</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;350 g</td>
          <td>Plněná bramborová roláda uzeným masem, dušené zelí, smažená cibulka</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;350 g</td>
          <td>Dukátové buchtičky s vanilkovým krémem</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Moučníky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Kobliha krémová</td>
        </tr>
        <tr>
          <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
          <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
          <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
          <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;7. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pátek</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Toskánská ribolita</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Slepičí vývar s kapáním</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            <b>Specialita dne</b>    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;150 g</td>
          <td>Hovězí cheeseburger, smažené hranolky</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;200 g</td>
          <td>Restovaná vepřová játra, smažené hranolky, tatarská omáčka, / dušená rýže /</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Pikantní kuřecí nudličky, dušená rýže, / bramboráčky /</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Hlavní jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;120 g</td>
          <td>Pečená vepřová krkovička,dušené zelí, houskové knedlíky</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;2 ks</td>
          <td>Vařené vejce, hrachvá kaše, sterilovaná okurka</td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Vegetariánská jídla    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td></td>
        </tr>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;"></td>
          <td style="width:120px; text-align:left;">&nbsp;
            Moučníky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Lívancovník jablečný</td>
        </tr>
      </tbody>
    </table>
    <p><small>Na jídelním lístku je uvedena váha masa v syrovém stavu, u salátů a talířů celková hmotnost porce.<br>Změna jídelního lístku vyhrazena.</p>
    <div class="jpaticka">
      <!--
        <a class="btn btn-small btn-info" href="javascript:window.print()" target="_blank" style="margin-bottom:4px;"><i class="icon-print icon-white"></i> Tisk</a>
        -->
    </div>
    <br>
  </div>
  <noscript>
    <div style="text-align:center"><b>Pro správnou funkci jídelníčků, musíte mít zapnutou podporu JavaScriptu</b></div>
  </noscript>
</div>"""

            val result = WeekScraperImpl.scrape(toTest)

            result shouldHaveSize 40
            result.map { it.courseType }.toSet() shouldHaveSize 5
            result.map { it.date } shouldContain LocalDate(2022, Month.JANUARY, 4)
            result shouldContain
                WeekDish(
                    MenzaId(1),
                    LocalDate(2022, Month.JANUARY, 6),
                    CourseType("Specialita dne", 1),
                    Amount("150 g"),
                    "Kuřecí steak se šunkou a sýrem, smažené hranolky",
                )
        }

    @Test
    fun scrapeWeekDisabled() {
        val toTest =
            """<body><input type='hidden' id='PodsysActive' value='1'></body>
            <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
<div class='data'>
Tato provozovna nevystavuje týdenní jídelní lístek.
</div>
</div>"""

        shouldThrow<WeekNotAvailable> { WeekScraperImpl.scrape(toTest) }
    }

    @Test
    fun scrapeCelebration() {
        val toTestStrahovChristmas =
            """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
   <div class='data' style="display:none;" >
      Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
   </div>
   <div class="jidelnicekheader">
      <div class="row-fluid">
         <div class="span3">
            <span><b>Menza Strahov&nbsp;</b></span>
         </div>
         <div class="span9">
            <p>
               <a href="?clPodsystem=1&clTyden=2784" class="btn btn-small">Týden 51 - Oběd (7)</a>      
            </p>
         </div>
      </div>
   </div>
   <div class='data'  >
      <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
      <table class="table table-condensed">
         <thead>
            <tr>
               <th style="width:80px; text-align:left;">Datum</th>
               <th style="width:60px; text-align:left;">Den</th>
               <th style="width:120px; text-align:right;">&nbsp;</th>
               <th style="width:50px; text-align:right;">Váha</th>
               <th >Název</th>
            </tr>
         </thead>
         <tbody>
            <tr>
               <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
               <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
               <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
               <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
               <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
            </tr>
            <tr>
               <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;23. 12. 2021</td>
               <td class="thkategorie" style="width:60px; text-align:left;">Čtvrtek</td>
               <td style="width:120px; text-align:left;">&nbsp;
                  Hlavní jídla    
               </td>
               <td style="width:50px; text-align:right;">&nbsp;</td>
               <td>                                         Z  A  V  Ř  E  N  O</td>
            </tr>
            <tr>
               <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
               <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
               <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
               <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
               <td style="text-align:left; height:1px; background-color:#ffffff;"></td>
            </tr>
            <tr>
               <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;24. 12. 2021</td>
               <td class="thkategorie" style="width:60px; text-align:left;">Pátek</td>
               <td style="width:120px; text-align:left;">&nbsp;
                  Hlavní jídla    
               </td>
               <td style="width:50px; text-align:right;">&nbsp;</td>
               <td>                                    Š  T  Ě  D  R  Ý       D  E  N</td>
            </tr>
         </tbody>
      </table>
      <p><small>Na jídelním lístku je uvedena váha masa v syrovém stavu, u salátů a talířů celková hmotnost porce.<br>Změna jídelního lístku vyhrazena.</p>
      <div class="jpaticka">
         <!--
            <a class="btn btn-small btn-info" href="javascript:window.print()" target="_blank" style="margin-bottom:4px;"><i class="icon-print icon-white"></i> Tisk</a>
            -->
      </div>
      <br>
   </div>
   <noscript>
      <div style="text-align:center"><b>Pro správnou funkci jídelníčků, musíte mít zapnutou podporu JavaScriptu</b></div>
   </noscript>
</div>
<!-- /jidelnicek -->"""
        val toTestTechnickaChristmas =
            """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
<div class='data' style="display:none;" >
Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
</div>
<div class="jidelnicekheader">
    <div class="row-fluid">
      <div class="span3">
        <span><b>Technická menza&nbsp;</b></span>
      </div>
      <div class="span9"><p>
      <a href="?clPodsystem=3&clTyden=2782" class="btn btn-small">Týden 45</a><a href="?clPodsystem=3&clTyden=2783" class="btn btn-small">Týden 1</a>      </p>
      </div>
    </div>
  </div>
<div class='data'  >
 <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
<table class="table table-condensed">  
  <thead>   
    <tr>
      <th style="width:80px; text-align:left;">Datum</th>
      <th style="width:60px; text-align:left;">Den</th>
      <th style="width:120px; text-align:right;">&nbsp;</th>
      <th style="width:50px; text-align:right;">Váha</th>
      <th >Název</th>      
    </tr>
  </thead>
  <tbody>
<tr>
  <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
  <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
  <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
  <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
  <td style="text-align:left; height:1px; background-color:#ffffff;"></td>  
</tr> 
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;23. 12. 2021</td>
  <td class="thkategorie" style="width:60px; text-align:left;">Čtvrtek</td>
  <td style="width:120px; text-align:left;">&nbsp;
  Polévky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Polévky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  <b>Specialita dne</b>    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td>                                            Zavřeno</td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Moučníky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Minutky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Vegetariánská jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
  
<tr>
  <td style="width:80px; text-align:right; height:1px; background-color:#007fc5;"></td>
  <td style="width:60px; text-align:left; height:1px; background-color:#007fc5;"></td>
  <td style="width:120px; text-align:left; height:1px; background-color:#ffffff;"></td>
  <td style="width:50px; text-align:left; height:1px; background-color:#ffffff;"></td>
  <td style="text-align:left; height:1px; background-color:#ffffff;"></td>  
</tr> 
  
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;24. 12. 2021</td>
  <td class="thkategorie" style="width:60px; text-align:left;">Pátek</td>
  <td style="width:120px; text-align:left;">&nbsp;
  Polévky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Polévky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  <b>Specialita dne</b>    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td>                                            Štědrý den</td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Hlavní jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Moučníky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Minutky    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
<tr>
  <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
  <td class="thkategorie" style="width:60px; text-align:left;"></td>
  <td style="width:120px; text-align:left;">&nbsp;
  Vegetariánská jídla    
  </td>
  <td style="width:50px; text-align:right;">&nbsp;</td>
  <td></td>  
</tr>
  </tbody>
</table>
<p><small>Na jídelním lístku je uvedena váha masa v syrovém stavu, u salátů a talířů celková hmotnost porce.<br>Změna jídelního lístku vyhrazena.</p>    
</div>
</div>"""

        val strahov = WeekScraperImpl.scrape(toTestStrahovChristmas)
        val technicka = WeekScraperImpl.scrape(toTestTechnickaChristmas)

        strahov.shouldHaveSize(0)
        technicka.shouldHaveSize(0)
    }

    @Test
    fun malformed() =
        runTest {
            val emptyList =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <tbody>
      </tbody>
    </table>
  </div>
</div>"""
            val noDishName =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <tbody>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;3. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pondělí</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td></td>
        </tr>
      </tbody>
    </table>
  </div>
</div>"""
            val noDate =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <tbody>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pondělí</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Brokolicový krém</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>"""
            val noFoodType =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <tbody>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;3. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pondělí</td>
          <td style="width:120px; text-align:left;">&nbsp; </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Brokolicový krém</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>"""
            val missingLine =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <tbody>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;3. 1. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pondělí</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>"""
            val malformedDate =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="thkategorie" style="text-align:center"><b>Tento jídelní lístek je pouze orientační a může se měnit!!! </b></div>
    <table class="table table-condensed">
      <tbody>
        <tr>
          <td class="thkategorie" style="width:80px; text-align:right;">&nbsp;3. 1A. 2022</td>
          <td class="thkategorie" style="width:60px; text-align:left;">Pondělí</td>
          <td style="width:120px; text-align:left;">&nbsp;
            Polévky    
          </td>
          <td style="width:50px; text-align:right;">&nbsp;</td>
          <td>Brokolicový krém</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>"""

            WeekScraperImpl.scrape(emptyList).shouldBeEmpty()
            shouldThrowAny { WeekScraperImpl.scrape("") }
            WeekScraperImpl.scrape(noDishName).shouldBeEmpty()
            shouldThrowAny { WeekScraperImpl.scrape(noDate) }
            shouldThrowAny { WeekScraperImpl.scrape(noFoodType) }
            shouldThrowAny { WeekScraperImpl.scrape(missingLine) }
            shouldThrowAny { WeekScraperImpl.scrape(malformedDate) }
        }

    @Test
    fun malformedWeekNotSupported() =
        runTest {
            val noMessage =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
<div class='data'>
</div>
</div>"""
            val wrongMessage =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
<div class='data'>
Tato provozovna ABC nevystavuje týdenní jídelní lístek.
</div>
</div>"""
            val noElement =
                """<body><input type='hidden' id='PodsysActive' value='1'></body>
 <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
</div>"""

            shouldThrowAny { shouldNotThrow<WeekNotAvailable> { WeekScraperImpl.scrape(noMessage) } }
            shouldThrowAny { shouldNotThrow<WeekNotAvailable> { WeekScraperImpl.scrape(wrongMessage) } }
            shouldThrowAny { shouldNotThrow<WeekNotAvailable> { WeekScraperImpl.scrape(noElement) } }
        }
}
