# CTU Menza

![GitHub release (latest by date)](https://img.shields.io/github/v/release/Lastaapps/menza)![F-Droid](https://img.shields.io/f-droid/v/cz.lastaapps.menza)

This Android app shows dish menus for CTU cafeterias from the [agata.suz.cvut.cz](https://agata.suz.cvut.cz/) 
and [studentcatering.cz](http://studentcatering.cz/jidelni-listek/) websites
in a much more pleasant and usable way for phones.
It shows today's and this week's menu, opening hours, contacts, announcements and addresses for all canteens.

##### What does 'Menza' mean?

Menza is the Czech word for school cafeteria.

[<img alt='Now on Google Play' height="80px" src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/>](https://play.google.com/store/apps/details?id=cz.lastaapps.menza&utm_source=github)[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/cz.lastaapps.menza)

## Related projects
- [Menza CLI (LastaApps)](https://github.com/Lastaapps/menza-cli)
- [Menza BE (LastaApps)](https://github.com/Lastaapps/menza-backend)


## Features

- Today's menu + dish details
- Account balance along with low balance warning,
  see [tutorial](docs/STRAVNIK_SIGNUP.md) for setup instructions.
- This week's menu
- Menza opening hours, contacts, announcements and addresses


## User experience

- Dark theme
- Many themes along with support for Android 12 Material You dynamic theming
- Image caching
- Images download switch on metered networks (~0.7 MB per image)
- No private data collection
- Proper landscape mode and large screen device support

## Code, Libraries

Core parts are written in Kotlin Multiplatform,
UI is written using Jetpack Compose (Android).

- AndroidX (Compose, ...)
- ArrowKt
- Decompose
- SQLDelight
- Koin
- Coil-kt
- Ktor
- Ktlint
- And more

## Screenshots

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" alt="today dish menu" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" alt="today dish menu dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" alt="dish detail" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4_en-US.png" alt="dish detail dark" style="width:20%;"/>

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5_en-US.png" alt="week dish menu dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6_en-US.png" alt="week dish menu" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/7_en-US.png" alt="info page dark" style="width:20%;"/><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/8_en-US.png" alt="info page" style="width:20%;"/>

## Contributing and project structure

In case you want to help or implement your own feature,
please see [CONTRIBUTING.md](docs/CONTRIBUTING.md) first.
I think this is a great project to lear how does
a simple yet corporate-like app look like,
how code should (should not?) be structured
and how functional approaches can be nicely used in an "imperative" language.

#### Data sources

Most of the data is obtained from the official Agata API,
see the documentation [here](https://agata.suz.cvut.cz/jidelnicky/JAPIV2/JAPI-popis.html).
To get your API key, please contact the IT center and don't steal mine,
they will be more than happy to give you one.

To get FS and FEL buffet data,
their webpages are scraped or hardcoded values are used.

## License

Menza is licensed under the `GNU GPL v3.0` license.
