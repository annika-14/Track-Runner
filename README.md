# Track Runner

A 3-track obstacle course Android game built with Kotlin for CMSC436.

## Game Overview

**Track Runner** is an endless runner-style game where players dodge obstacles across three vertical tracks. The game features a neon cyberpunk aesthetic with increasing difficulty over time.

### Gameplay
- Player controls a character fixed at the bottom of the screen
- Obstacles spawn from the top and move downward
- Use **LEFT** and **RIGHT** buttons to switch between 3 tracks
- Avoid collisions to survive and score points
- Difficulty increases over time (obstacles spawn faster)
- Phone vibrates on collision for tactile feedback

---

## Currently Implemented Features

### Core Gameplay
| 3-Track System | Player can move between left, center, and right tracks |
| Obstacle Spawning | Random obstacles spawn at configurable intervals |
| Collision Detection | Detects when player and obstacle occupy same position |
| Lives System | Configurable starting lives (1-5), game ends at 0 |
| Score Tracking | Points awarded for each obstacle dodged |
| Difficulty Scaling | Spawn rate increases every 10 seconds |
| Game Timer/Loop | 60 FPS game loop with Handler |

### Screens (Views)
| Screen | Status | Description |
|--------|--------|-------------|
| Start Screen | Main menu with Start, Settings, Leaderboard buttons |
| Game Screen | Active gameplay with HUD (score, lives, difficulty bar) |
| Game Over Screen | Shows final score, rating bar, leaderboard submission |
| Preferences Screen | Settings for lives, theme, difficulty, vibration |
| Leaderboard Screen | Displays top scores from Firebase |

### Architecture (MVC)
| Model | `Obstacles.kt` - Game state, obstacle/player data |
| View | `GameView.kt` - Custom canvas rendering, layouts |
| Controller | `GameActivity.kt` - Input handling, game flow |

---

## Technical Setup

### Prerequisites
- Android Studio (latest version)
- Android SDK 34+
- Kotlin 1.9+

### Running the Project
1. Open Android Studio
2. **File → Open** → Select `CMSC436-Final-Project` folder
3. Click "Use Embedded JDK" if prompted
4. Wait for Gradle sync to complete
5. Click **▶ Run** or press `Shift + F10`

### Firebase Setup (Optional - for Leaderboard)
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project
3. Add Android app with package: `com.example.trackobstaclecourse`
4. Download `google-services.json`
5. Place it in `app/` folder
6. Uncomment Firebase plugin in `build.gradle.kts` files
7. Enable **Realtime Database** in Firebase Console

---

## Project Structure

```
CMSC436-Final-Project/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/trackobstaclecourse/
│       │   ├── MainActivity.kt        # Start screen
│       │   ├── GameActivity.kt        # Game controller
│       │   ├── GameView.kt            # Custom game rendering
│       │   ├── GameOverActivity.kt    # End screen
│       │   ├── PreferencesActivity.kt # Settings
│       │   ├── LeaderboardActivity.kt # High scores
│       │   └── Obstacles.kt           # Game model
│       └── res/
│           ├── layout/                # XML layouts
│           ├── values/                # Colors, strings, themes
│           ├── drawable/              # Icons & graphics
│           └── xml/                   # Configs
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

---

## Design

### UI Components Used
- ConstraintLayout (all screens)
- RecyclerView (leaderboard)
- ProgressBar (difficulty indicator)
- SeekBar (lives selector)
- RatingBar (game rating)
- Spinner (theme/difficulty selection)
- Switch (vibration toggle)
- Custom View (GameView canvas)

---

## License

This project is for educational purposes as part of CMSC436 coursework.
