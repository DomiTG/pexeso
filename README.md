# Pexeso – Memory Card Game (Android)

An Android memory card game (Pexeso) written in **Java**, targeting Android 5.0+ (API 21+).

## Gameplay

- **4 × 8 grid** of 32 cards (16 pairs).
- Tap a card to flip it face-up and reveal its emoji.
- Flip a second card; if both emojis match they stay face-up (green background).
- If they don't match they flip back after 800 ms.
- The game ends when all 16 pairs are matched.
- Score = total number of pair-flip attempts (**fewer moves = better**).
- The all-time lowest score (best game) is persisted with `SharedPreferences`.

## Screenshots / UI

| Screen | Description |
|--------|-------------|
| Main   | Toolbar + move counter + 4-column card grid |
| High Score | Best score ever, with a reset button |

## Project Structure

```
app/
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/pexeso/
    │   ├── Card.java              # Card model (id, pairId, isFaceUp, isMatched)
    │   ├── ScoreManager.java      # SharedPreferences high-score manager
    │   ├── GameAdapter.java       # RecyclerView adapter for the card grid
    │   ├── MainActivity.java      # Game logic & UI controller
    │   └── HighScoreActivity.java # High-score display & reset
    └── res/
        ├── layout/
        │   ├── activity_main.xml
        │   ├── activity_high_score.xml
        │   └── item_card.xml
        ├── menu/menu_main.xml
        └── values/
            ├── strings.xml  (Czech language)
            ├── colors.xml
            ├── dimens.xml
            └── themes.xml
```

## Technical Details

| Item | Value |
|------|-------|
| Language | Java |
| Min SDK | 21 (Android 5.0 Lollipop) |
| Target / Compile SDK | 34 (Android 14) |
| Gradle Wrapper | 8.2 |
| Android Gradle Plugin | 8.1.0 |
| Java source compatibility | 1.8 |
| RecyclerView | 1.3.2 |
| Material Components | 1.11.0 |
| CardView | 1.0.0 |

## Key Design Decisions

- **`GridLayoutManager(4)`** renders the 4-column grid; card height is forced to equal card width via a 100 % `paddingTop` spacer `View` inside each `FrameLayout`, giving a perfect square on any screen size.
- **Input blocking** (`isChecking` flag) prevents tapping extra cards while the match-evaluation `Handler.postDelayed` (800 ms) is running.
- **`ScoreManager`** uses a single `SharedPreferences` file (`pexeso_prefs`) with the key `high_score`; a value of `-1` means "no score yet".
- Card emojis: 🐶 🐱 🐭 🐹 🐰 🦊 🐻 🐼 🐨 🐯 🦁 🐮 🐷 🐸 🐵 🐔

## Building

```bash
# With Android SDK installed and ANDROID_HOME set:
./gradlew assembleDebug
```

The resulting APK is at `app/build/outputs/apk/debug/app-debug.apk`.

## Localization

All user-facing strings are in Czech (`app/src/main/res/values/strings.xml`).
Notable strings:

| Key | Czech | English meaning |
|-----|-------|-----------------|
| `moves_label` | `Tahy: %d` | Moves: %d |
| `high_score_value` | `Nejlepší skóre: %d tahů` | Best score: %d moves |
| `dialog_play_again` | `Hrát znovu` | Play again |
| `dialog_game_over_title` | `Gratulujeme!` | Congratulations! |
