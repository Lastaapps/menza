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

package cz.lastaapps.menza.api.agata.domain.model

internal enum class Func(val funName: String) {
    MenuHash("GetJidelnickyHash"),
    Menu("GetJidelnicky"),
    SubsystemHash("GetPodsystemyHash"),
    Subsystem("GetPodsystemy"),
    SubsystemAll("GetPodsystemyAll"),
    ServingPacesHash("GetVydejnyHash"),
    ServingPaces("GetVydejny"),
    TypesHash("GetKategorieHash"),
    Types("GetKategorie"),
    DishHash("GetJidlaHash"),
    Dish("GetJidla2"),
    InfoHash("GetInfoHash"),
    Info("GetInfo"),
    News("GetAktuality"),

    //    InfoGlobal("GetGlobalInfo"),
    OpeningHash("GetOtDobyHash"),
    Opening("GetOtDoby"),
    ContactsHash("GetKontaktyHash"),
    Contacts("GetKontakty"),
    PictogramHash("GetPiktogramyHash"),
    Pictogram("GetPiktogramy"),
    AddressHash("GetAdresyHash"),
    Address("GetAdresy"),
    LinkHash("GetOdkazyHash"),
    Link("GetOdkazy"),
    Week("GetTydny"),
    WeekDays("GetTydnyDny"),
    Strahov("GetMinutkySH"),
    ;
}
