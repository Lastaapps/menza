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

package cz.lastaapps.scraping

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class OpeningHoursScraperTest {

    @Test
    fun scrapOpeningHoursOnline() = runTest {
        val result = OpeningHoursScraperImpl.createRequest().bodyAsText()
        val hours = OpeningHoursScraperImpl.scrape(result)

        hours.shouldNotBeEmpty()

        val strahov = hours.filter { it.menzaId.id == 1 }
        strahov.shouldNotBeEmpty()

        val canteen = strahov.filter { it.locationName == "Jídelna" }
        canteen.shouldNotBeEmpty()

        val canteenOpenMorning = LocalTime(11, 0, 0)
        val canteenCloseMorning = LocalTime(14, 30, 0)
        val canteenOpenEvening = LocalTime(17, 0, 0)
        val canteenCloseEvening = LocalTime(20, 0, 0)

        for (day in listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
        )) {
            val found = strahov.find { it.dayOfWeek == day }
            found.shouldNotBeNull()
            found.open shouldBeIn arrayOf(canteenOpenMorning, canteenOpenEvening)
            found.close shouldBeIn arrayOf(canteenCloseMorning, canteenCloseEvening)
        }

//        val restaurant = strahov.filter { it.locationName == "Restaurace" }
//        restaurant.shouldNotBeEmpty()
//
//        val restaurantOpen = LocalTime(11, 0, 0)
//        val restaurantClose = LocalTime(20, 30, 0)
//
//        for (day in listOf(
//            DayOfWeek.MONDAY,
//            DayOfWeek.TUESDAY,
//            DayOfWeek.WEDNESDAY,
//            DayOfWeek.THURSDAY,
//        )) {
//            val found = strahov.find { it.dayOfWeek == day }
//            found.shouldNotBeNull()
//            found.open shouldBe restaurantOpen
//            found.close shouldBe restaurantClose
//        }
//        val found = strahov.find { it.dayOfWeek == DayOfWeek.FRIDAY }
//        found.shouldNotBeNull()
//        found.open shouldBe restaurantOpen
//        found.close shouldBe LocalTime(19, 30, 0)
    }

    @Test
    fun scrapeOpeningHours() = runTest {
        val toTest = """
        <div id='otdoby' style="max-width:800px;padding-left:10px">
        <section id="section15"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>ArchiCafé</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">ArchiCafé</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">8:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">17:30</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">8:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section5"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Masarykova kolej</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Restaurace</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">15:00</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section12"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>MEGA BUF FAT</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Bufet FSv</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">7:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">18:00</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">7:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section9"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Kladno</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Restaurace</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">10:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Kantýna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">8:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">15:00</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid"><div class="span6 offset6">&nbsp;</div><div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section4"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Podolí</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Jídelna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;Obědy</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp;Obědy</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Pokladna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp;Pokladna</td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid"><div class="span6 offset6">&nbsp;</div><div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Restaurace</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">20:30</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">19:30</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Jídelna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">6:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">9:30</td>
        <td style="text-align:left;" >&nbsp;Snídaně</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;Oběd</td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Snack Bar blok 1</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">7:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">15:30</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">7:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp; </td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section2"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Studentský dům</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Jídelna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">0:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">0:00</td>
        <td style="text-align:left;" >&nbsp;ZŠ Lvíčata</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Jídelna 2</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">10:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Jídelna 3</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">10:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Pokladna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">8:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid"><div class="span6 offset6">&nbsp;</div><div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section14"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Oddělění správy IT GÚ</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Oddělení správy IT Gastroprovozů</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">9:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">15:30</td>
        <td style="text-align:left;" >&nbsp; není zaručeno</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section3"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Technická menza</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Jídelna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">8:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">10:00</td>
        <td style="text-align:left;" >&nbsp;snídaně</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">10:45</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;obědy </td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Kavárna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">8:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;</td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid"><div class="span6 offset6">&nbsp;</div><div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section6"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Výdejna Horská</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Výdejna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Pá</td>
        <td style="text-align:right; width:30px;">11:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp;Obědy</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Bufet</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">8:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">15:30</td>
        <td style="text-align:left;" >&nbsp; Pondělí - Čtvrtek</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">8:00</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp; Pátek</td>
        </tr>
        </tbody>
        </table>
        </div>
        </div><div class="row-fluid"><div class="span6 offset6">&nbsp;</div><div class="span6 offset6">&nbsp;</div></div></div><br></section><section id="section8"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Výdejna Karlovo náměstí</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
        <table class="table table-striped table-condensed" style="max-width:500px;">
        <thead>
        <tr>
        <th colspan="7">Výdejna</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td style="text-align:right; width:25px;">Po</td>
        <td style="text-align:center; width:10px;">–</td>
        <td style="text-align:left; width:25px;">Čt</td>
        <td style="text-align:right; width:30px;">10:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:30</td>
        <td style="text-align:left;" >&nbsp;Obědy</td>
        </tr>
        <tr>
        <td style="text-align:right; width:25px;">Pá</td>
        <td style="text-align:center; width:10px;"></td>
        <td style="text-align:left; width:25px;"></td>
        <td style="text-align:right; width:30px;">10:30</td>
        <td style="text-align:center; width:8px;">–</td>
        <td style="text-align:left; width:30px;">14:00</td>
        <td style="text-align:left;" >&nbsp;Obědy</td>
        </tr>
        </tbody>
        </table>
        </div>
        <div class="span6 offset6">&nbsp;</div></div></div><br></section></div>
        </div>
        </div>"""

        val hours = OpeningHoursScraperImpl.scrape(toTest)

        hours.map { it.menzaId to it.locationName }.toSet().shouldHaveSize(20)

        val strahov = hours.filter { it.menzaId.id == 1 }
        strahov.shouldNotBeEmpty()

        val restaurant = strahov.filter { it.locationName == "Restaurace" }
        restaurant.shouldNotBeEmpty()

        val restaurantOpen = LocalTime(11, 0, 0)
        val restaurantClose = LocalTime(20, 30, 0)

        for (day in listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
        )) {
            val found = strahov.find { it.dayOfWeek == day }
            found.shouldNotBeNull()
            found.open shouldBe restaurantOpen
            found.close shouldBe restaurantClose
        }
        val found = strahov.find { it.dayOfWeek == DayOfWeek.FRIDAY }
        found.shouldNotBeNull()
        found.open shouldBe restaurantOpen
        found.close shouldBe LocalTime(19, 30, 0)
    }

    @Test
    fun malformed() = runTest {
        val emptyList = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      </div>
      </div>
      </section>
</div>"""
        val missingId = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val missingIdElement = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val malformedId = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="sectionABC"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val noPlaceName = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7"></th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val switchedDays = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Čt</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Po</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val sameDay = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Po</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val switchedTimes = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">20:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">11:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val sameTimes = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">11:00</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val missingRows = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val malformedTime = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">AA:BB</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val unknownDay = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Poneděle</td>
          <td style="text-align:right; width:30px;">11:00</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""
        val hoursOnly = """<div id='otdoby' style="max-width:800px;padding-left:10px">
<section id="section1"><div class="data2"><div class="row-fluid"><div class="span12"><div style="color: #007fc5"><h3>Menza Strahov</h3><br></div></div></div><div class="row-fluid">      <div class="span6">
      <table class="table table-striped table-condensed" style="max-width:500px;">
       <thead> <tr> <th colspan="7">Restaurace</th>       </tr> </thead>
       <tbody>     
              <tr>          
          <td style="text-align:right; width:25px;">Po</td>
          <td style="text-align:center; width:10px;">–</td>
          <td style="text-align:left; width:25px;">Čt</td>
          <td style="text-align:right; width:30px;">11</td>
          <td style="text-align:center; width:8px;">–</td>
          <td style="text-align:left; width:30px;">20:30</td>
          <td style="text-align:left;" >&nbsp;</td>
        </tr>
              </tbody>
      </table>
      </div>
      </div>
      </section>
</div>"""

        OpeningHoursScraperImpl.scrape(emptyList).shouldBeEmpty()
        shouldThrowAny { OpeningHoursScraperImpl.scrape("") }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(missingId) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(missingIdElement) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(malformedId) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(noPlaceName) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(switchedDays) }
        OpeningHoursScraperImpl.scrape(sameDay).shouldNotBeEmpty()
        shouldThrowAny { OpeningHoursScraperImpl.scrape(switchedDays) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(switchedTimes) }
        OpeningHoursScraperImpl.scrape(sameTimes).shouldNotBeEmpty()
        shouldThrowAny { OpeningHoursScraperImpl.scrape(missingRows) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(malformedTime) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(unknownDay) }
        shouldThrowAny { OpeningHoursScraperImpl.scrape(hoursOnly) }
    }
}
