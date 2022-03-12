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

/**
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

package cz.lastaapps.menza.di

import cz.lastaapps.scraping.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("UNCHECKED_CAST")
object ScraperModule {

    @Provides
    @Singleton
    fun provideAllergenScraper(): AllergenScraper =
        AllergensScraperImpl as AllergenScraper

    @Provides
    @Singleton
    fun provideContactsScraper(): ContactsScraper =
        ContactsScraperImpl as ContactsScraper

    @Provides
    @Singleton
    fun provideLocationScraper(): LocationScraper =
        LocationScraperImpl as LocationScraper

    @Provides
    @Singleton
    fun provideMenzaScraper(): MenzaScraper =
        MenzaScraperImpl as MenzaScraper

    @Provides
    @Singleton
    fun provideMessagesScraper(): MessagesScraper =
        MessagesScraperImpl as MessagesScraper

    @Provides
    @Singleton
    fun provideOpeningHoursScraper(): OpeningHoursScraper =
        OpeningHoursScraperImpl as OpeningHoursScraper

    @Provides
    @Singleton
    fun provideTodayScraper(): TodayScraper =
        TodayScraperImpl as TodayScraper

    @Provides
    @Singleton
    fun provideWeekScraper(): WeekScraper =
        WeekScraperImpl as WeekScraper

}