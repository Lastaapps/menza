# Contributing and project structure

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

## Structure

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

## Other modules are **deprecated** and should **not** be used/edited

Namely, modules `entity`, `html-parser`, `scraping` and `storage`.
They were used by the first version of menza and are kept for future in case
I lost access to the API.
