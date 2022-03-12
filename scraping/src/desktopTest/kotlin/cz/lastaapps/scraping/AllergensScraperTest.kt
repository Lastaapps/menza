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

import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.entity.allergens.AllergenId
import cz.lastaapps.entity.day.DishAllergensPage
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class AllergensScraperTest {

    @Test
    fun scrapeAllAllergensOnline() = runTest {

        val result = AllergensScraperImpl.createRequestForAll().bodyAsText()
        val allergens = AllergensScraperImpl.scrape(result)

        //allergens.forEach { println(it) }

        allergens shouldContain Allergen(
            AllergenId(1),
            "Obiloviny obsahující lepek",
            "pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich"
        )

        allergens shouldHaveSize 14
    }

    @Test
    fun scrapeFoodAllergensOnline() = runTest {

        val result =
            AllergensScraperImpl.createRequestForDish(DishAllergensPage(336173)).bodyAsText()
        val allergens = AllergensScraperImpl.scrape(result)

        //allergens.forEach { println(it) }

        allergens shouldContain Allergen(
            AllergenId(1),
            "Obiloviny obsahující lepek",
            "pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich"
        )
        allergens shouldContain Allergen(
            AllergenId(3),
            "Vejce",
            "a výrobky z nich"
        )
        allergens shouldContain Allergen(
            AllergenId(7),
            "Mléko",
            "a výrobky z něj"
        )

        allergens shouldHaveSize 3
    }

    @Test
    fun scrapeAllAllergens() = runTest {
        val toTest = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <thead>
          <tr>            
            <th style="width:30px; text-align:left"></th>
            <th style="width:160px; text-align:left">Alergeny - popis</th>
            <th style="width:400px; text-align:left"></th>            
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt="1"></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
            <td style="width:400px; text-align:left">pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen2.png" alt="2"></td>
            <td style="width:160px; text-align:left">Korýši</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen3.png" alt="3"></td>
            <td style="width:160px; text-align:left">Vejce</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen4.png" alt="4"></td>
            <td style="width:160px; text-align:left">Ryby</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen5.png" alt="5"></td>
            <td style="width:160px; text-align:left">Jádra podzemnice olejné (arašídy)</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen6.png" alt="6"></td>
            <td style="width:160px; text-align:left">Sójové boby</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen7.png" alt="7"></td>
            <td style="width:160px; text-align:left">Mléko</td>
            <td style="width:400px; text-align:left">a výrobky z něj</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen8.png" alt="8"></td>
            <td style="width:160px; text-align:left">Skořápkové plody</td>
            <td style="width:400px; text-align:left">mandle, lískové ořechy, vlašské ořechy, kešu ořechy, pekanové ořechy, para ořechy, pistácie, makadamie a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen9.png" alt="9"></td>
            <td style="width:160px; text-align:left">Celer</td>
            <td style="width:400px; text-align:left">a výrobky z něj</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen10.png" alt="10"></td>
            <td style="width:160px; text-align:left">Hořčice</td>
            <td style="width:400px; text-align:left">a výrobky z ní</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen11.png" alt="11"></td>
            <td style="width:160px; text-align:left">Sezamová semena</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen12.png" alt="12"></td>
            <td style="width:160px; text-align:left">Oxid siřičitý a siřičitany</td>
            <td style="width:400px; text-align:left">v koncentracích vyšších než 10 mg/kg nebo 10 mg/l, vyjádřeno jako celkový SO2</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen13.png" alt="13"></td>
            <td style="width:160px; text-align:left">Vlčí bob (lupina)</td>
            <td style="width:400px; text-align:left">a výrobky z něj</td>            
          </tr>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen14.png" alt="14"></td>
            <td style="width:160px; text-align:left">Měkkýši</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>   
        </tbody>
      </table>
  
  </div>"""

        val allergens = AllergensScraperImpl.scrape(toTest)

        allergens.forEach {
            println(it)
        }

        allergens shouldContain Allergen(
            AllergenId(1),
            "Obiloviny obsahující lepek",
            "pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich"
        )

        allergens shouldHaveSize 14
    }

    @Test
    fun scrapeFoodAllergens() = runTest {
        val toTest = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <thead>
          <tr>            
            <th style="width:30px; text-align:left"></th>
            <th style="width:160px; text-align:left">Alergeny - popis</th>
            <th style="width:400px; text-align:left"></th>            
          </tr>
        </thead>
        <tbody>
          <tr style="display:blok">
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt="1"></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
            <td style="width:400px; text-align:left">pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen2.png" alt="2"></td>
            <td style="width:160px; text-align:left">Korýši</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr style="display:blok">
            <td style="width:30px; text-align:left"><img src="img/Alergen3.png" alt="3"></td>
            <td style="width:160px; text-align:left">Vejce</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen4.png" alt="4"></td>
            <td style="width:160px; text-align:left">Ryby</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen5.png" alt="5"></td>
            <td style="width:160px; text-align:left">Jádra podzemnice olejné (arašídy)</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen6.png" alt="6"></td>
            <td style="width:160px; text-align:left">Sójové boby</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr style="display:blok">
            <td style="width:30px; text-align:left"><img src="img/Alergen7.png" alt="7"></td>
            <td style="width:160px; text-align:left">Mléko</td>
            <td style="width:400px; text-align:left">a výrobky z něj</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen8.png" alt="8"></td>
            <td style="width:160px; text-align:left">Skořápkové plody</td>
            <td style="width:400px; text-align:left">mandle, lískové ořechy, vlašské ořechy, kešu ořechy, pekanové ořechy, para ořechy, pistácie, makadamie a výrobky z nich</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen9.png" alt="9"></td>
            <td style="width:160px; text-align:left">Celer</td>
            <td style="width:400px; text-align:left">a výrobky z něj</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen10.png" alt="10"></td>
            <td style="width:160px; text-align:left">Hořčice</td>
            <td style="width:400px; text-align:left">a výrobky z ní</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen11.png" alt="11"></td>
            <td style="width:160px; text-align:left">Sezamová semena</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen12.png" alt="12"></td>
            <td style="width:160px; text-align:left">Oxid siřičitý a siřičitany</td>
            <td style="width:400px; text-align:left">v koncentracích vyšších než 10 mg/kg nebo 10 mg/l, vyjádřeno jako celkový SO2</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen13.png" alt="13"></td>
            <td style="width:160px; text-align:left">Vlčí bob (lupina)</td>
            <td style="width:400px; text-align:left">a výrobky z něj</td>            
          </tr>
          <tr style="display:none">
            <td style="width:30px; text-align:left"><img src="img/Alergen14.png" alt="14"></td>
            <td style="width:160px; text-align:left">Měkkýši</td>
            <td style="width:400px; text-align:left">a výrobky z nich</td>            
          </tr>
          
   
        </tbody>
      </table>
  <p><a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a></p>    
  </div>"""

        val allergens = AllergensScraperImpl.scrape(toTest)

        allergens.forEach {
            println(it)
        }

        allergens shouldContain Allergen(
            AllergenId(1),
            "Obiloviny obsahující lepek",
            "pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich"
        )
        allergens shouldContain Allergen(
            AllergenId(3),
            "Vejce",
            "a výrobky z nich"
        )
        allergens shouldContain Allergen(
            AllergenId(7),
            "Mléko",
            "a výrobky z něj"
        )

        allergens shouldHaveSize 3
    }

    @Test
    fun malformed() = runTest {
        val noCode = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png"></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
            <td style="width:400px; text-align:left">pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich</td>            
          </tr>
        </tbody>
      </table>
  </div>"""
        val emptyCode = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt=""></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
            <td style="width:400px; text-align:left">pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich</td>            
          </tr>
        </tbody>
      </table>
  </div>"""
        val invalidCode = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt="abc"></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
            <td style="width:400px; text-align:left">pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich</td>            
          </tr>
        </tbody>
      </table>
  </div>"""
        val emptyName = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt="1"></td>
            <td style="width:160px; text-align:left"></td>
            <td style="width:400px; text-align:left">pšenice, žito, ječmen, oves, špalda, kamut nebo jejich hybridní odrůdy a výrobky z nich</td>            
          </tr>
        </tbody>
      </table>
  </div>"""
        val emptyDescription = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt="1"></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
            <td style="width:400px; text-align:left"></td>            
          </tr>
        </tbody>
      </table>
  </div>"""
        val noItems = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
        </tbody>
      </table>
  </div>"""
        val notEnoughItems = """<div id='otdoby' style="max-width:900px">
  <table class="table table-condensed" style=" font-size: 13px;"> 
        <tbody>
          <tr>
            <td style="width:30px; text-align:left"><img src="img/Alergen1.png" alt="1"></td>
            <td style="width:160px; text-align:left">Obiloviny obsahující lepek</td>
          </tr>
        </tbody>
      </table>
  </div>"""

        shouldThrowAny { AllergensScraperImpl.scrape("") }
        shouldThrowAny { AllergensScraperImpl.scrape(noCode) }
        shouldThrowAny { AllergensScraperImpl.scrape(emptyCode) }
        shouldThrowAny { AllergensScraperImpl.scrape(invalidCode) }
        shouldThrowAny { AllergensScraperImpl.scrape(emptyName) }
        shouldThrowAny { AllergensScraperImpl.scrape(emptyDescription) }
        shouldThrowAny { AllergensScraperImpl.scrape(notEnoughItems) }
        AllergensScraperImpl.scrape(noItems).shouldBeEmpty()
    }

}
