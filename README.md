# Track Runner 🎮

A 3-track obstacle course Android game built with Kotlin for CMSC436.

## 📱 Game Overview

**Track Runner** is an endless runner-style game where players dodge obstacles across three vertical tracks. The game features a neon cyberpunk aesthetic with increasing difficulty over time.

### Gameplay
- Player controls a character fixed at the bottom of the screen
- Obstacles spawn from the top and move downward
- Use **LEFT** and **RIGHT** buttons to switch between 3 tracks
- Avoid collisions to survive and score points
- Difficulty increases over time (obstacles spawn faster)
- Phone vibrates on collision for tactile feedback

---

## ✅ Currently Implemented Features

### Core Gameplay
| Feature | Status | Description |
|---------|--------|-------------|
| 3-Track System | ✅ Done | Player can move between left, center, and right tracks |
| Obstacle Spawning | ✅ Done | Random obstacles spawn at configurable intervals |
| Collision Detection | ✅ Done | Detects when player and obstacle occupy same position |
| Lives System | ✅ Done | Configurable starting lives (1-5), game ends at 0 |
| Score Tracking | ✅ Done | Points awarded for each obstacle dodged |
| Difficulty Scaling | ✅ Done | Spawn rate increases every 10 seconds |
| Game Timer/Loop | ✅ Done | 60 FPS game loop with Handler |

### Screens (Views)
| Screen | Status | Description |
|--------|--------|-------------|
| Start Screen | ✅ Done | Main menu with Start, Settings, Leaderboard buttons |
| Game Screen | ✅ Done | Active gameplay with HUD (score, lives, difficulty bar) |
| Game Over Screen | ✅ Done | Shows final score, rating bar, leaderboard submission |
| Preferences Screen | ✅ Done | Settings for lives, theme, difficulty, vibration |
| Leaderboard Screen | ✅ Done | Displays top scores from Firebase |

### Architecture (MVC)
| Component | Status | Files |
|-----------|--------|-------|
| Model | ✅ Done | `Obstacles.kt` - Game state, obstacle/player data |
| View | ✅ Done | `GameView.kt` - Custom canvas rendering, layouts |
| Controller | ✅ Done | `GameActivity.kt` - Input handling, game flow |

### Project Requirements
| Requirement | Status | Implementation |
|-------------|--------|----------------|
| MVC Architecture | ✅ Done | Model/View/Controller separation |
| 3+ Views | ✅ Done | 5 screens implemented |
| 2 Views Share Data | ✅ Done | Score passed Game→GameOver; Settings shared across app |
| Local Persistent Storage | ✅ Done | SharedPreferences for high_score, starting_lives, theme_index, vibration_enabled |
| Remote Persistent Storage | ✅ Done | Firebase Realtime Database for leaderboard |
| Hardware Usage | ✅ Done | Vibrator on collision |
| 2 New GUI Components | ✅ Done | ProgressBar (difficulty), RatingBar (game rating), SeekBar (lives) |
| Listeners on Components | ✅ Done | SeekBar.OnSeekBarChangeListener, RatingBar.OnRatingBarChangeListener |
| AdMob Advertising | ✅ Done | Configured with test ad unit ID |

---

## 🚧 TODO: Features to Implement

### Required for Submission
| Task | Priority | Notes |
|------|----------|-------|
| Custom App Icon | 🔴 High | Replace default Android icon with game-themed icon |
| Player/Obstacle Sprites | 🔴 High | Replace circles/squares with actual game graphics |
| Firebase Setup | 🟡 Medium | Add `google-services.json` for leaderboard to work |

### Nice-to-Have Enhancements
| Task | Priority | Notes |
|------|----------|-------|
| Sound Effects | 🟢 Low | Add audio for collision, score, game over |
| Background Music | 🟢 Low | Looping game music |
| Theme Implementation | 🟡 Medium | Actually apply Neon/Retro/Minimal theme selections |
| Difficulty Presets | 🟢 Low | Easy/Normal/Hard affects spawn rate & speed |
| Animations | 🟢 Low | Smooth track transitions, obstacle explosions |
| Power-ups | 🟢 Low | Shield, slow-mo, extra life pickups |
| Tutorial Screen | 🟢 Low | First-time player instructions |

---

## 🛠️ Technical Setup

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

## 📁 Project Structure

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

## 🎨 Design

### Color Palette (Neon Theme)
| Color | Hex | Usage |
|-------|-----|-------|
| Cyan | `#00F5FF` | Player, primary buttons, tracks |
| Pink | `#FF1493` | Obstacles, accents |
| Purple | `#9B30FF` | Secondary buttons |
| Yellow | `#FFFF00` | Score, difficulty, highlights |
| Dark BG | `#0D0D1A` | Background |

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

## 👥 Team

CMSC436 Final Project - Fall 2024

---

## 📄 License

This project is for educational purposes as part of CMSC436 coursework.

