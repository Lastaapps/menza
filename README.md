# CTU Menza

![GitHub release (latest by date)](https://img.shields.io/github/v/release/Lastaapps/menza)![F-Droid](https://img.shields.io/f-droid/v/cz.lastaapps.menza)

This Android app shows dish menus for CTU cafeterias from the [agata.suz.cvut.cz](https://agata.suz.cvut.cz/) 
and [studentcatering.cz](http://studentcatering.cz/jidelni-listek/) websites
in a much more pleasant and usable way for phones.
It shows today's and this week's menu, opening hours, contacts, announcements and addresses for all canteens.

Core parts are written in Kotlin Multiplatform, support for JVM target can be added right away. Other targets require more work (non-JVM web scraping library required), but it still may be manageable for somebody to port this codebase to native or js platforms.

See **contributing** below.

##### What does 'Menza' mean?

Menza is the Czech word for school cafeteria.

[<img alt='Now on Google Play' height="80px" src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/>](https://play.google.com/store/apps/details?id=cz.lastaapps.menza&utm_source=github)[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/cz.lastaapps.menza)

## Related projects
- [Menza CLI (LastaApps)](https://github.com/Lastaapps/menza-cli)
- [Menza BE (LastaApps)](https://github.com/Lastaapps/menza-backend)

## Libraries

- AndroidX (Compose, ...)
- ArrowKt
- Appyx
- SQLDelight
- Koin
- Coil-kt
- Ktor
- And more

The core of the all in written in Kotlin Multiplatform!



## Features

- Today's menu + dish details
- This week's menu
- Menza opening hours, contacts, announcements and addresses

## User experience

- Dark theme
- Many themes along with support for Android 12 Material You dynamic theming
- Images download switch on metered networks (~0.7 MB per image)
- Image caching
- No private data collection
- Proper landscape mode and large screen device support


## Screenshots

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" alt="today dish menu" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" alt="today dish menu dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" alt="dish detail" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4_en-US.png" alt="dish detail dark" style="width:20%;"/>

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5_en-US.png" alt="week dish menu dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6_en-US.png" alt="week dish menu" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/7_en-US.png" alt="info page dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/8_en-US.png" alt="info page" style="width:20%;"/>


#### Contributing and project structure

Feel free to contribute, but **contact me before please**, so we don't do the same thing twice.
If you are new to Kotlin/Android, you can write the code yourself,
and I'll adjust it to the rules below.
So it's fine to write it *wrong* (different),
but don't be surprised when I rewrite it.

Most of the modules are multiplatform, and you should respect it.
That means write as much code as possible into the common code and
if there is no (nice) way to implement feature in a multiplatform manner,
this is the time when you can use platform specific package.
Even in that case put at least an interface into the common code
and tie it using DI (preferred over `expected`/`actual` keywords).

The whole app tries to respect clean architecture principles
and MVVM architecture. There is also domain layer presented (use cases).
The whole app is tied together using DI.

##### Structure
- `api/core` - core classes for network communication, store, synchronization, ...
- `api/agata`, `api/buffet` - implementation for individual canteen provider
- `api/main` - packs all the providers into one interface, contains related business logic
- `app` - only UI related code, purely Android (for now)
  - `features` - stores all the UI code
    - `root` - decides if the user is already logged in
    - `starting` - stores setup related code
    - `main` - holds main user interface (after login), drawer, top/bottom bar, core navigation
    - `today` - the main screen, shows today canteen offering
    - `week` - shows week canteen offering
    - `info` - shows canteen info
    - `panels` - panels for the today screen (rate us, report a crash, what's new, ...)
    - `settings` - self-explanatory, includes about screen
    - `others` - privacy policy, library notices, ošťurák, ...
- `core` - holds main shared domain logic and templates
  - `Outcome` - like Rust's `Result`, success or `DomainError`.
      all the functions that can fail should return `Outcome`.
  - `UseCase` - base usecase class
  - `BaseViewmodel` - parent of all the ViewModels
- `lastaapps` - my legacy common shared code (+ crash reporting)

##### Other modules are **deprecated** and should **not** be used/edited
Namely, modules `entity`, `html-parser`, `scraping` and `storage`.
They were used by the first version of menza and are kept for future in case
I lost access to the API.

#### Data sources

Most of the data is obtained from the official Agata API, see the documentation [here](https://agata.suz.cvut.cz/jidelnicky/JAPIV2/JAPI-popis.html).
To get your API key, please contact the IT center and don't steal mine, they will be more than happy to give you one.

To get FS and FEL buffet data I do scrape their webpages/hardcode info.

## License

Menza is licensed under the `GNU GPL v3.0` license.
