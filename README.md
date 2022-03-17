# CTU Menza

This Android app shows dish menus for CTU cafeterias from the [agata.suz.cvut.cz](https://agata.suz.cvut.cz/) website in a much more pleasant and usable way for phones. It shows today's and this week's menu, opening hours, contacts, announcements and addresses for all canteens. It supports dark mode and Material You, including Android 12 dynamic theming.

Core parts are written in Kotlin Multiplatform, support for JVM target can be added right away. Other targets require more work (non-JVM web scraping library required), but it still may be manageable for somebody to port this codebase to native or js platforms.

##### What does 'Menza' mean?

Menza is the Czech word for school cafeteria.

[<img alt='Now on Google Play' height="80px" src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/>](https://play.google.com/store/apps/details?id=cz.lastaapps.menza&utm_source=github)

F-Droid coming hopefully soon.



## Technologies

- Jetpack libraries (Compose, ...)
- SQLDelight
- Skrape.it
- Coil-kt
- Kotlin Multiplatform



## Features

- Today's menu + dish details
- This week's menu
- Menza opening hours, contacts, announcements and addresses

## User experience

- Dark theme
- Support for Android 12 Material You dynamic theming
- Images download switch on metered networks (~0.7 MB per image)
- Image caching
- No private data collection
- Proper landscape mode and large screen device support



## Screenshots

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" alt="today dish menu" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" alt="today dish menu dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" alt="dish detail" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4_en-US.png" alt="dish detail dark" style="width:20%;"/>

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5_en-US.png" alt="week dish menu dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6_en-US.png" alt="week dish menu" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/7_en-US.png" alt="info page dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/8_en-US.png" alt="info page" style="width:20%;"/>



## Structure

- app - Android app code - UI, viewmodels, caching
- entity - entities representing data accessible from web
- scrapping - abstract and JMV/Android implementation for web scraping
- storage.db - database for storing persistent entities - contact info, menza list, ...
- storage.repo - repositories for common access to data from web or local database



#### Contributing

I don't accept any code contributions till I have this project accepted as term paper hopefully in May 2022. So if you are interested, please wait a bit.



## License

Menza is licensed under the `GNU GPL v3.0` license.
