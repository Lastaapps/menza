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

package cz.lastaapps.menza.di

import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.*
import cz.lastaapps.storage.repo.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.skrape.fetcher.Request
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideAllergenRepo(
        database: MenzaDatabase,
        scraper: AllergenScraper<Request>
    ): AllergenRepo = AllergenRepoImpl(database, scraper)

    @Provides
    @Singleton
    fun provideContactsRepo(
        database: MenzaDatabase,
        scraper: ContactsScraper<Request>
    ): ContactsRepo = ContactsRepoImpl(database, scraper)

    @Provides
    @Singleton
    fun provideLocationRepo(
        database: MenzaDatabase,
        scraper: LocationScraper<Request>
    ): LocationRepo = LocationRepoImpl(database, scraper)

    @Provides
    @Singleton
    fun provideMenzaRepo(
        database: MenzaDatabase,
        scraper: MenzaScraper<Request>
    ): MenzaRepo = MenzaRepoImpl(database, scraper)

    @Provides
    @Singleton
    fun provideMessagesRepo(
        database: MenzaDatabase,
        scraper: MessagesScraper<Request>
    ): MessagesRepo = MessagesRepoImpl(database, scraper)

    @Provides
    @Singleton
    fun provideOpeningHoursRepo(
        database: MenzaDatabase,
        scraper: OpeningHoursScraper<Request>
    ): OpeningHoursRepo = OpeningHoursRepoImpl(database, scraper)

    @Provides
    @Singleton
    fun provideTodayRepoFactory(
        scraper: TodayScraper<Request>
    ): TodayRepoFactory = TodayRepoFactoryImpl(scraper)

    @Provides
    @Singleton
    fun provideWeekRepoFactory(
        scraper: WeekScraper<Request>
    ): WeekRepoFactory = WeekRepoFactoryImpl(scraper)

}