# SmartsGram for Android

![SmartsGram Logo](.github/Smart.jpg)

## Features

**SmartsGram** is built on top of AyuGram4A with reworked patches from Telegraher:

- Built with official keys
- No emulator detection
- Screenshots in secret chats
- Messages history (+ deleted ones)
- No ads
- Probably something else, check commits

...and **ghost mode**.

SmartsGram does **NOT** include proprietary exteraGram features.

## Downloads?

Follow our [Telegram channel](https://t.me/Smarts_Uz).

## Why there's no `X` feature from Telegraher?

Because I'm too lazy, and some Telegraher's features are useless for the most of people.

## Want to contribute?

I'd be grateful for any contribution, since I don't really like Java. :)

Work on any feature you want.

## Want to fork?

If you're making a fork, you **should** specify **SmartsGram** & **AyuGram** in credits.
Otherwise open source doesn't really work.

Depending on your GitHub profile, we'll decide send an invite to the proprietary repo or not.

And, if you're a kid that don't really want to give credits properly - go and cry writing boilerplate code.

## How to build

1. Clone source code using `git clone https://github.com/smarts-uz/SmartGram.git`
2. Open the project in Android Studio. It should be opened, **not imported**
3. Implement the `AyuMessageUtils` & `AyuHistoryHook` classes. It's not that hard, but if you're
   making your **very** own fork, then you should take some time to write this part of code
4. Create dummy classes for extera's proprietary methods & classes (in `boost` folder)
5. Replace `google-services.json` (we don't want to see crash reports from your app...)
6. Generate application certificate and fill API_KEYS:
   ```
   APP_ID = 6
   APP_HASH = "eb06d4abfb49dc3eeb1aeb98ae0f581e"
   MAPS_V2_API = NA
   
   SIGNING_KEY_PASSWORD = <...>
   SIGNING_KEY_ALIAS = <...>
   SIGNING_KEY_STORE_PASSWORD = <...>
   ```
6. You are ready to compile `SmartGram`

- **SmartsGram** can be built with **Android Studio** or from the command line with **Gradle**:

```
./gradlew assembleAfatRelease
```

## SmartsGram Localization

[![Crowdin](https://badges.crowdin.net/ayugram/localized.svg)](https://crowdin.com/project/smartsgram)
[![Crowdin](https://badges.crowdin.net/exteralocales/localized.svg)](https://crowdin.com/project/exteralocales)

We have our own [Crowdin](https://crowdin.com/project/smartsgram).

But since SmartsGram is based on **AyuGram**, also join their project
at [Crowdin](https://crowdin.com/project/ayugram)!

## Credits
- [AyuGram](https://github.com/AyuGram/AyuGram4A)
- [exteraGram](https://github.com/exteraSquad/exteraGram)
- [Telegraher](https://github.com/nikitasius/Telegraher)
- [Cherrygram](https://github.com/arsLan4k1390/Cherrygram)
- [Nagram](https://github.com/NextAlone/Nagram)
- [Telegram FOSS](https://github.com/Telegram-FOSS-Team/Telegram-FOSS)
