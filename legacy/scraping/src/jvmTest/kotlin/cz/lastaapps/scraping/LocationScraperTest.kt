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

import cz.lastaapps.entity.menza.Coordinates
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class LocationScraperTest {

    @Test
    fun locationOnline() = runTest {
        val result = ContactsScraperImpl.createRequest().bodyAsText()
        val locations = LocationScraperImpl.scrape(result)

        // locations.forEach { println(it) }

        locations.shouldNotBeEmpty()
        locations.map { it.address.stringForm } shouldContain "Jezdecká 1920, 160 17 Praha 6"
        locations.map { it.coordinates } shouldContain Coordinates("50.100882", "14.386966")
    }

    @Test
    fun scrapeLocations() = runTest {
        val toTest = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí</td>
                        <td></td>
                        <td><a href="tel:+420725896859">725 896 859 </a></td>
                        <td><a href="mailto:%73%75%7A%2D%61%72%63%68%69%63%61%66%65%40%63%76%75%74%2E%63%7A">&#115;&#117;&#122;&#45;&#97;&#114;&#99;&#104;&#105;&#99;&#97;&#102;&#101;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.105102, 14.389751&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section5">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Masarykova kolej&nbsp;&nbsp;&nbsp;<small>Thákurova 1, 160 00 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí gastroúseku MK</td>
                        <td></td>
                        <td><a href="tel:+420234678480">234 678 480 </a></td>
                        <td><a href="mailto:%67%61%73%74%72%6F%2D%6D%6B%40%63%76%75%74%2E%63%7A">&#103;&#97;&#115;&#116;&#114;&#111;&#45;&#109;&#107;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.100882,14.386966&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.100882,14.386966&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section12">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>MEGA BUF FAT&nbsp;&nbsp;&nbsp;<small>Thákurova 7, 160 00 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí bufetu</td>
                        <td></td>
                        <td><a href="tel:+420234678590">234 678 590 </a></td>
                        <td><a href="mailto:%73%75%7A%2D%6D%65%67%61%62%75%66%66%61%74%40%63%76%75%74%2E%63%7A">&#115;&#117;&#122;&#45;&#109;&#101;&#103;&#97;&#98;&#117;&#102;&#102;&#97;&#116;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.103865,14.388157&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.103865,14.388157&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section9">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Menza Kladno&nbsp;&nbsp;&nbsp;<small>náměstí Sítná 3105, 272 01 Kladno</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí</td>
                        <td></td>
                        <td><a href="tel:+420234678580">234 678 580 </a></td>
                        <td><a href="mailto:%6D%65%6E%7A%61%2D%6B%6C%61%64%6E%6F%40%63%76%75%74%2E%63%7A">&#109;&#101;&#110;&#122;&#97;&#45;&#107;&#108;&#97;&#100;&#110;&#111;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.13508,14.104551&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.13508,14.104551&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section4">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Menza Podolí&nbsp;&nbsp;&nbsp;<small>Na Lysině 772/12, 147 45 Praha 4</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí menzy</td>
                        <td></td>
                        <td><a href="tel:+420234678550">234 678 550 </a></td>
                        <td><a href="mailto:%6D%65%6E%7A%61%2D%70%6F%64%6F%6C%69%40%63%76%75%74%2E%63%7A">&#109;&#101;&#110;&#122;&#97;&#45;&#112;&#111;&#100;&#111;&#108;&#105;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.053033,14.428932&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.053033,14.428932&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section1">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Menza Strahov&nbsp;&nbsp;&nbsp;<small>Jezdecká 1920, 160 17 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí menzy</td>
                        <td></td>
                        <td><a href="tel:+420234678291">234 678 291 </a></td>
                        <td><a href="mailto:%6D%65%6E%7A%61%2D%73%74%72%61%68%6F%76%40%63%76%75%74%2E%63%7A">&#109;&#101;&#110;&#122;&#97;&#45;&#115;&#116;&#114;&#97;&#104;&#111;&#118;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                     <tr>
                        <td>Provoz</td>
                        <td></td>
                        <td><a href="tel:+420234678361">234 678 361 </a></td>
                        <td><a href="mailto:%73%75%7A%2D%70%72%6F%76%6F%7A%6E%69%6D%73%40%63%76%75%74%2E%63%7A">&#115;&#117;&#122;&#45;&#112;&#114;&#111;&#118;&#111;&#122;&#110;&#105;&#109;&#115;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.079174,14.393236&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.079174,14.393236&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section2">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Menza Studentský dům&nbsp;&nbsp;&nbsp;<small>Bílá 2571/6, 160 00 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí menzy</td>
                        <td></td>
                        <td><a href="tel:+420234678560">234 678 560 </a></td>
                        <td><a href="mailto:%6D%65%6E%7A%61%2D%73%74%75%64%64%75%6D%40%63%76%75%74%2E%63%7A">&#109;&#101;&#110;&#122;&#97;&#45;&#115;&#116;&#117;&#100;&#100;&#117;&#109;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.105612,14.388666&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105612,14.388666&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section14">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Oddělění správy IT GÚ&nbsp;&nbsp;&nbsp;<small>Jezdecká 1920, 160 17 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>IT</td>
                        <td>Tomáš Kaňovský</td>
                        <td><a href="tel:+420234678370">234 678 370 </a></td>
                        <td><a href="mailto:%74%6F%6D%61%73%2E%6B%61%6E%6F%76%73%6B%79%40%63%76%75%74%2E%63%7A">&#116;&#111;&#109;&#97;&#115;&#46;&#107;&#97;&#110;&#111;&#118;&#115;&#107;&#121;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.079174,14.393236&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.079174,14.393236&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section3">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Technická menza&nbsp;&nbsp;&nbsp;<small>Jugoslávských partyzánů 3, 160 00 Praha 6</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí menzy</td>
                        <td></td>
                        <td><a href="tel:+420234678325">234 678 325 </a></td>
                        <td><a href="mailto:%6D%65%6E%7A%61%2D%74%65%63%68%6E%69%63%6B%61%40%63%76%75%74%2E%63%7A">&#109;&#101;&#110;&#122;&#97;&#45;&#116;&#101;&#99;&#104;&#110;&#105;&#99;&#107;&#97;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.103927,14.394534&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.103927,14.394534&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section6">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Výdejna Horská&nbsp;&nbsp;&nbsp;<small>Horská 3, 128 03 Praha 2</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí výdejny</td>
                        <td></td>
                        <td><a href="tel:+420234678559">234 678 559 </a></td>
                        <td><a href="mailto:%76%79%64%65%6A%6E%61%2D%68%6F%72%73%6B%61%40%63%76%75%74%2E%63%7A">&#118;&#121;&#100;&#101;&#106;&#110;&#97;&#45;&#104;&#111;&#114;&#115;&#107;&#97;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.067097,14.424157&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.067097,14.424157&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
   <section id="section8">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>Výdejna Karlovo náměstí&nbsp;&nbsp;&nbsp;<small>Karlovo náměstí 13, 121 35 Praha 2</small></h3>
            </div>
         </div>
         <div class="row-fluid">
            <div class="span8">
               <table class="table table-striped table-condensed">
                  <thead>
                     <tr>
                        <th>Pozice</th>
                        <th>Jméno</th>
                        <th>Telefon</th>
                        <th>E-mail</th>
                     </tr>
                  </thead>
                  <tbody>
                     <tr>
                        <td>Vedoucí výdejny</td>
                        <td></td>
                        <td><a href="tel:+420234678558">234 678 558 </a></td>
                        <td><a href="mailto:%76%79%64%65%6A%6E%61%2D%6B%61%72%6C%61%6B%40%63%76%75%74%2E%63%7A">&#118;&#121;&#100;&#101;&#106;&#110;&#97;&#45;&#107;&#97;&#114;&#108;&#97;&#107;&#64;&#99;&#118;&#117;&#116;&#46;&#99;&#122;</a></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.076228,14.417538&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.076228,14.417538&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
   <br>
</div>"""

        val locations = LocationScraperImpl.scrape(toTest)

        // locations.forEach { println(it) }

        locations shouldHaveSize 11
        locations.map { it.address.stringForm } shouldContain "Jezdecká 1920, 160 17 Praha 6"
        locations.map { it.coordinates } shouldContain Coordinates("50.100882", "14.386966")
    }

    @Test
    fun malformed() {
        val noMenzaId = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.105102, 14.389751&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val malformedMenzaId = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="sectionABC">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.105102, 14.389751&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val noItems = """<div id='otdoby' style="max-width:800px;padding-left:10px">
</div>"""
        val toFewRows = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val noAddress = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small></small></h3>
            </div>
            <div class="row-fluid">
               <div class="span4" style="text-align:center;">
                  <a href="http://maps.google.cz/maps?q=50.105102, 14.389751&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
               </div>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val noLink = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val noCoordinates = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val malformedCoordinates = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=50.ABC, 14.389751&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
</div>"""
        val oneCoordinateOnly = """<div id='otdoby' style="max-width:800px;padding-left:10px">
   <section id="section15">
      <div class="data2">
         <div class="row-fluid">
            <div class="span12" style="color: #007fc5">
               <h3>ArchiCafé&nbsp;&nbsp;&nbsp;<small>Thákurava 9, 160 00 Praha 6</small></h3>
            </div>
            <div class="span4" style="text-align:center;">
               <a href="http://maps.google.cz/maps?q=14.389751&amp;num=1&amp;hl=cs&amp;brcurrent=5,0,0&amp;ie=UTF8&amp;t=m&amp;ll=50.105102, 14.389751&amp;spn=0.010851,0.025578&amp;z=14&amp;source=embed" target="_blank" class="btn btn-small"><i class=" icon-map-marker"></i>&nbsp;Zobrazi polohu na mapě</a>
            </div>
         </div>
      </div>
   </section>
</div>"""

        shouldThrowAny { LocationScraperImpl.scrape("") }
        shouldThrowAny { LocationScraperImpl.scrape(noMenzaId) }
        shouldThrowAny { LocationScraperImpl.scrape(malformedMenzaId) }
        LocationScraperImpl.scrape(noItems).shouldBeEmpty()
        shouldThrowAny { LocationScraperImpl.scrape(toFewRows) }.printStackTrace()
        shouldThrowAny { LocationScraperImpl.scrape(noAddress) }
        shouldThrowAny { LocationScraperImpl.scrape(noLink) }
        shouldThrowAny { LocationScraperImpl.scrape(noCoordinates) }
        shouldThrowAny { LocationScraperImpl.scrape(malformedCoordinates) }
        shouldThrowAny { LocationScraperImpl.scrape(oneCoordinateOnly) }
    }
}
