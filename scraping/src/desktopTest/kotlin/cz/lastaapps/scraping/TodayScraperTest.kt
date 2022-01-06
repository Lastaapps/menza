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

import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.FoodType
import cz.lastaapps.entity.common.Price
import cz.lastaapps.entity.day.Food
import cz.lastaapps.entity.day.FoodAllergens
import cz.lastaapps.entity.day.IssueLocation
import cz.lastaapps.entity.menza.MenzaId
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class TodayScraperTest {

    @Test
    fun scrapeTodayOnline() = runTest {

        val id = 1
        val result = TodayScraperImpl.createRequest(MenzaId(id))
        val foodList = TodayScraperImpl.scrape(result)

        foodList.forEach { println(it) }

        foodList.shouldNotBeEmpty()
        foodList.forEach {
            it.menzaId.id shouldBe id
        }
        foodList.map { it.foodType.type } shouldContain "Polévky"
    }

    @Test
    fun scrapeToday() = runTest {
        val toTest = """<body><input type='hidden' id='PodsysActive' value='4'></body>
            <div id="jidelnicek" style="display:block; max-width:800px; padding-left:10px;">
  <div class='data' style="display:none;" >
    Výběr dle aktuální nabídky na provozovně. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a>
  </div>
  <div class='data'  >
    <div class="jidelnicekheaderprint">
      <h2>Menza Podolí&nbsp;&nbsp;&nbsp; <small>6. 1. 2022&nbsp;&nbsp;&nbsp;Oběd</small></h2>
    </div>
    <div class="jidelnicekheader">
      <div class="row-fluid">
        <div class="span6">
          <span class="label label-success">Oběd</span>  
          <span><b>Menza Podolí,&nbsp;čtvrtek&nbsp;6. 1. 2022</b></span>
        </div>
        <div class="span3">&nbsp;
          <span class="label"
            onmouseover="xPopOver(terminal);"
            onclick="xPopOver(terminal);"
            id="terminal"
            title="Terminál zůstatku"
            data-content="Na této provozovně se nachází terminál pro zjištění zůstatku na účtu">Terminál</span>
        </div>
        <div class="span3" style="text-align:right;">	
          <a data-toggle="modal" href="#myModal" class="btn btn-danger btn-mini" >Důležité informace!</a>
        </div>
      </div>
    </div>
    <table class="table table-condensed">
      <thead>
        <tr>
          <th ></th>
          <th style="width:40px">Váha</th>
          <th >Název</th>
          <th >&nbsp;</th>
          <th >&nbsp;</th>
          <th style="width:50px; text-align:center">Student</th>
          <th style="width:55px; text-align:center">Ostatní</th>
          <th style="width:40px;padding-left:2px;padding-right:0px;">Výdej</th>
          <th style="width:60px;padding-left:2px;padding-right:0px;"></th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <th colspan='9' class="thkategorie">Polévky</th>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;</td>
          <td>Dršťková  </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340615" target="_blank" style="padding-left:5px;" title="Alergeny: 1 3 6  7  9 "><img id='Alergen340615' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
            </div>
          </td>
          <td style="width:50px; text-align:right">
            12,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            20,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v0v21" onmouseover="xToolTip(v0v21);" onclick="xToolTip(v0v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <th colspan='9' class="thkategorie">Hlavní jídla</th>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;120 g</td>
          <td>Hovězí pečeně frankfurtská, karlovarské knedlíky </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340616" target="_blank" style="padding-left:5px;" title="Alergeny: 1 3 5 6 7 9 10 14"><img id='Alergen340616' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
              <a href="imgshow.php?clPodsystem=4&xFile=IMG-2022-01-06-100010746.JPG" target="_blank" style="padding-left:5px;" title="Fotografie jídla"><img src="files/camera_16_2.png" alt="Alt"></a>
            </div>
          </td>
          <td style="width:50px; text-align:right">
            79,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            101,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v1v21" onmouseover="xToolTip(v1v21);" onclick="xToolTip(v1v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;300 g</td>
          <td>Kuřecí stehno na paprice, vařené těstoviny </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340617" target="_blank" style="padding-left:5px;" title="Alergeny: 1 3 7 8 "><img id='Alergen340617' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
              <a href="imgshow.php?clPodsystem=4&xFile=IMG-2022-01-06-100032479.JPG" target="_blank" style="padding-left:5px;" title="Fotografie jídla"><img src="files/camera_16_2.png" alt="Alt"></a>
            </div>
          </td>
          <td style="width:50px; text-align:right">
            78,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            100,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v2v21" onmouseover="xToolTip(v2v21);" onclick="xToolTip(v2v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;150 g</td>
          <td>Smažený karbanátek, vařené brambory,okurka sterilovaná</td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340618" target="_blank" style="padding-left:5px;" title="Alergeny: 1 3 6 7 8 9 10 11 14"><img id='Alergen340618' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
              <a href="imgshow.php?clPodsystem=4&xFile=IMG-2022-01-06-100018701.JPG" target="_blank" style="padding-left:5px;" title="Fotografie jídla"><img src="files/camera_16_2.png" alt="Alt"></a>
            </div>
          </td>
          <td style="width:50px; text-align:right">
            79,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            101,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v3v21" onmouseover="xToolTip(v3v21);" onclick="xToolTip(v3v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;70 g</td>
          <td>Talíř s uzeným lososem a zeleninou </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340619" target="_blank" style="padding-left:5px;" title="Alergeny: 1 3 4 "><img id='Alergen340619' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
            </div>
          </td>
          <td style="width:50px; text-align:right">
            78,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            100,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v4v21" onmouseover="xToolTip(v4v21);" onclick="xToolTip(v4v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;300 g</td>
          <td>Talíř s kuřecím masem  </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340620" target="_blank" style="padding-left:5px;" title="Alergeny: 1 4 7 10 11"><img id='Alergen340620' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
            </div>
          </td>
          <td style="width:50px; text-align:right">
            69,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            91,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v5v21" onmouseover="xToolTip(v5v21);" onclick="xToolTip(v5v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;300 g</td>
          <td>Talíř s tuňákem  </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340621" target="_blank" style="padding-left:5px;" title="Alergeny: 1 3 4  10"><img id='Alergen340621' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
            </div>
          </td>
          <td style="width:50px; text-align:right">
            65,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            87,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v6v21" onmouseover="xToolTip(v6v21);" onclick="xToolTip(v6v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <th colspan='9' class="thkategorie">Bezmasá jídla</th>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;300 g</td>
          <td>Talíř s mozzarelou  </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340622" target="_blank" style="padding-left:5px;" title="Alergeny: 1 7 11"><img id='Alergen340622' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
            </div>
          </td>
          <td style="width:50px; text-align:right">
            53,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            75,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v7v21" onmouseover="xToolTip(v7v21);" onclick="xToolTip(v7v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
        <tr>
          <td style="width:10px;">    
          </td>
          <td style="width:40px; text-align:right;">&nbsp;300 g</td>
          <td>Talíř s balkánským sýrem  </td>
          <td style="width:10px;">
            <div class="starradek">                        
              <a href="alergeny.php?alergen=340623" target="_blank" style="padding-left:5px;" title="Alergeny: 1 4 7 10"><img id='Alergen340623' src="files/Alergeny16.png" alt="Al"></a>
            </div>
          </td>
          <td style="width:10px;">
            <div class="starradek">                  
            </div>
          </td>
          <td style="width:50px; text-align:right">
            53,00&nbsp;Kč  
          </td>
          <td style="width:55px; text-align:right">
            75,00&nbsp;Kč  
          </td>
          <td  style="width:40px;padding-left:2px;padding-right:0px;">
            <span id="v8v21" onmouseover="xToolTip(v8v21);" onclick="xToolTip(v8v21);" title="Jídelna" class="label label-success">J</span>   
          </td>
          <td style="width:60px; font-size:11px; text-align:right;padding-left:2px;padding-right:0px;" >
          </td>
        </tr>
      </tbody>
    </table>
    <p><small>Veškeré uvedené ceny jsou smluvní včetně DPH. Na jídelním lístku je uvedena váha masa v syrovém stavu, u salátů a talířů celková hmotnost porce.<br>Změna jídelního lístku vyhrazena. <a href="alergenyall.php" target="_blank" style="padding-left:5px;" title="Alergeny">Seznam všech alergenů <img src="files/Alergeny16.png" alt="Al"></a></small></p>
  </div>
  <div class="jpaticka">
    <a class="btn btn-small btn-info" href="javascript:window.print()" target="_blank" style="margin-bottom:4px;"><i class="icon-print icon-white"></i> Tisk</a>
    <a href="http://www.suz.cvut.cz/menzy/menza-podoli" target="_blank" class="btn btn-primary btn-small" style="margin-bottom:4px;"><i class="icon-file icon-white"></i>Stránky menzy</a>
  </div>
  <br>
  <div class="dulinfo">
    <div id="myModal" class="modal hide fade">
      <div class="modal-header">
        <a class="close" data-dismiss="modal" >&times;</a>
        <h3>Menza Podolí</h3>
      </div>
      <div class="modal-body">
        <p>
        <P ALIGN="LEFT">
          <SPAN style="color: #000000; font-size: 8pt; font-family: Tahoma;">
          </SPAN>
        </P>
        <P ALIGN="CENTER"><SPAN style="color: #000000; font-size: 14pt; font-family: Calibri;"><B>
          <BR>ZÁKAZ VSTUPU OSOB BEZ CERTIFIKÁTU O OČKOVÁNÍ,PRODĚLANÉM ONEMOCNĚNÍ  COVID-19.
          <BR>Možnost namátkové kontroly.
          <BR>Nevztahuje se na studenty a zaměstnance VŠ
          </B></SPAN>
        </P>
        </p>
      </div>
      <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal" >Zavřít</a>              
      </div>
    </div>
  </div>
  <!-- okno pro podrobne informace o jidle  #F5F6F8  #A0A0A0-->
  <div id="myInfo" class="modal hide fade" style="width:400px;">
    <div class="modal-header" style="background-color:#72B4E5; color:#ffffff">
      <a class="close" data-dismiss="modal" >&times;</a>
      <h4>Informace o pokrmu:</h4>
    </div>
    <div id="myInfoJidlo" class="modal-body" style="text-align:center;">
      Informace o jídle
    </div>
  </div>
  <noscript>
    <div style="text-align:center"><b>Pro správnou funkci jídelníčků, musíte mít zapnutou podporu JavaScriptu</b></div>
  </noscript>
</div>"""

        val foodList = TodayScraperImpl.scrape(toTest)

        foodList.forEach { println(it) }

        foodList.shouldHaveSize(9)
        foodList.forEach {
            it.menzaId.id shouldBe 4
        }
        foodList.map { it.foodType.type } shouldContainAll
               listOf("Polévky", "Hlavní jídla", "Bezmasá jídla")

        foodList shouldContain Food(
            MenzaId(4),
            FoodType("Hlavní jídla"),
            Amount("120 g"),
            "Hovězí pečeně frankfurtská, karlovarské knedlíky",
            FoodAllergens(340616),
            "https://agata.suz.cvut.cz/jidelnicky/imgshow.php?clPodsystem=4&xFile=IMG-2022-01-06-100010746.JPG",
            Price(79),
            Price(101),
            listOf(IssueLocation(21, 1, "J", "Jídelna"))
        )
    }

    @Test
    fun malformed() = runTest {
        val valid = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noMenzaIdTag = """<body>
  <div id="jidelnicek">
    <div class='data'>
      <table>
      <tbody>
        <tr>
          <th colspan='9' class="thkategorie">Polévky</th>
        </tr>
        <tr>
          <td></td>
          <td>100&nbsp;ml</td>
          <td>Hovězí vývar se zeleninou a nudlemi </td>
          <td>
            <div>                        
              <a href="alergeny.php?alergen=340633"></a>
            </div>
          </td>
          <td></td>
          <td>12,00&nbsp;Kč</td>
          <td>20,00&nbsp;Kč</td>
          <td>
            <span id="v0v18" title="Jídelna">J</span>   
          </td>
          <td></td>
        </tr>
      </tbody>
      </table>
    </div>
  </div>
</body>"""
        val invalidMenzaId = """<body>
  <input type='hidden' id='PodsysActive' value='abc'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
      <tbody>
        <tr>
          <th colspan='9' class="thkategorie">Polévky</th>
        </tr>
        <tr>
          <td></td>
          <td>100&nbsp;ml</td>
          <td>Hovězí vývar se zeleninou a nudlemi </td>
          <td>
            <div>                        
              <a href="alergeny.php?alergen=340633"></a>
            </div>
          </td>
          <td></td>
          <td>12,00&nbsp;Kč</td>
          <td>20,00&nbsp;Kč</td>
          <td>
            <span id="v0v18" title="Jídelna">J</span>   
          </td>
          <td></td>
        </tr>
      </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noMenzaId = """<body>
  <input type='hidden' id='PodsysActive' value=''>
  <div id="jidelnicek">
    <div class='data'>
      <table>
      <tbody>
        <tr>
          <th colspan='9' class="thkategorie">Polévky</th>
        </tr>
        <tr>
          <td></td>
          <td>100&nbsp;ml</td>
          <td>Hovězí vývar se zeleninou a nudlemi </td>
          <td>
            <div>                        
              <a href="alergeny.php?alergen=340633"></a>
            </div>
          </td>
          <td></td>
          <td>12,00&nbsp;Kč</td>
          <td>20,00&nbsp;Kč</td>
          <td>
            <span id="v0v18" title="Jídelna">J</span>   
          </td>
          <td></td>
        </tr>
      </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noCategory = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
      <tbody>
        <tr>
          <td></td>
          <td>100&nbsp;ml</td>
          <td>Hovězí vývar se zeleninou a nudlemi </td>
          <td>
            <div>                        
              <a href="alergeny.php?alergen=340633"></a>
            </div>
          </td>
          <td></td>
          <td>12,00&nbsp;Kč</td>
          <td>20,00&nbsp;Kč</td>
          <td>
            <span id="v0v18" title="Jídelna">J</span>   
          </td>
          <td></td>
        </tr>
      </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noTable = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
    </div>
  </div>
</body>"""
        val noCategoryName = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie"></th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noCategoryTag = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noAmount = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td></td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noFoodName = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td></td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noAllergenId = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen="></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noAllergenIdTag = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val invalidAllergenId = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=asdfmovie"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noPrice = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td></td>
            <td></td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val invalidPrice = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>aA,BC&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val priceDecimalPointFormat = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12.00&nbsp;Kč</td>
            <td>20.00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val priceOtherCurrency = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;USD</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val priceNoDecimal = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val priceNonNullDecimal = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,69&nbsp;Kč</td>
            <td>20,42&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noCurrency = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;</td>
            <td>
              <span id="v0v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val toShortLocationId = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v18" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noLocationId = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="" title="Jídelna">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noLocationName = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="">J</span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noLocationAbbrev = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
              <span id="v0v18" title="Jídelna"></span>   
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val noLocationTag = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
            <td></td>
            <td>100&nbsp;ml</td>
            <td>Hovězí vývar se zeleninou a nudlemi </td>
            <td>
              <div>                        
                <a href="alergeny.php?alergen=340633"></a>
              </div>
            </td>
            <td></td>
            <td>12,00&nbsp;Kč</td>
            <td>20,00&nbsp;Kč</td>
            <td>
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""
        val missingTags = """<body>
  <input type='hidden' id='PodsysActive' value='4'>
  <div id="jidelnicek">
    <div class='data'>
      <table>
        <tbody>
          <tr>
            <th colspan='9' class="thkategorie">Polévky</th>
          </tr>
          <tr>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>"""

        shouldThrowAny { TodayScraperImpl.scrape("") }
        shouldThrowAny { TodayScraperImpl.scrape(noMenzaId) }
        shouldThrowAny { TodayScraperImpl.scrape(invalidMenzaId) }
        shouldThrowAny { TodayScraperImpl.scrape(noMenzaIdTag) }
        shouldThrowAny { TodayScraperImpl.scrape(noCategory) }
        shouldThrowAny { TodayScraperImpl.scrape(noCategoryName) }
        shouldThrowAny { TodayScraperImpl.scrape(noCategoryTag) }
        shouldThrowAny { TodayScraperImpl.scrape(noFoodName) }
        shouldThrowAny { TodayScraperImpl.scrape(noAllergenId) }
        shouldThrowAny { TodayScraperImpl.scrape(noAllergenIdTag) }
        shouldThrowAny { TodayScraperImpl.scrape(invalidAllergenId) }
        shouldThrowAny { TodayScraperImpl.scrape(noPrice) }
        shouldThrowAny { TodayScraperImpl.scrape(invalidPrice) }
        shouldThrowAny { TodayScraperImpl.scrape(toShortLocationId) }
        shouldThrowAny { TodayScraperImpl.scrape(noLocationId) }
        shouldThrowAny { TodayScraperImpl.scrape(noLocationName) }
        shouldThrowAny { TodayScraperImpl.scrape(noLocationAbbrev) }
        shouldThrowAny { TodayScraperImpl.scrape(noLocationTag) }
        shouldThrowAny { TodayScraperImpl.scrape(missingTags) }

        TodayScraperImpl.scrape(noTable).shouldBeEmpty()
        TodayScraperImpl.scrape(noAmount).shouldNotBeEmpty()
        TodayScraperImpl.scrape(priceDecimalPointFormat).shouldNotBeEmpty()
        TodayScraperImpl.scrape(priceOtherCurrency).shouldNotBeEmpty()
        TodayScraperImpl.scrape(priceNoDecimal).shouldNotBeEmpty()
        TodayScraperImpl.scrape(priceNonNullDecimal).shouldNotBeEmpty()
        TodayScraperImpl.scrape(noCurrency).shouldNotBeEmpty()
    }
}