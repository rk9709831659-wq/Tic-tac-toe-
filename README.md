# 🎮 Tic Tac Toe Android App

A modern and interactive Tic Tac Toe game built using **Kotlin** and **Jetpack Compose**.
This app supports both **Single Player (AI)** and **Two Player** modes with a clean UI and smooth animations.

---

## 📱 Features

### 🎯 Core Features

* ✅ 3x3 Tic Tac Toe Board
* ✅ Two Game Modes:

  * 🤖 Single Player (vs AI)
  * 👥 Two Player (same device)
* ✅ Turn indicator (X / O)
* ✅ Winner detection
* ✅ Draw detection
* ✅ Restart game option

---

### 🎨 UI & UX Features

* 🌙 Dark Theme UI (modern look)
* 🎨 Colored X (Cyan) and O (Magenta)
* ✨ Smooth animations using Jetpack Compose
* 🟩 Winning cells highlight
* 📏 Winning line drawn across grid
* 🎉 Winner celebration message

---

### 🤖 AI Features

* Basic AI logic:

  * Random move selection
  * Plays as Player O
* AI delay simulation (realistic feel)

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel)
* **State Management:** StateFlow
* **Asynchronous Handling:** Kotlin Coroutines

---

## 📂 Project Structure

```
com.rohit.myapplication
│
├── game/
│   ├── TicTacToeViewModel.kt   # Game logic + state management
│   ├── TicTacToeScreen.kt      # UI (Compose)
│
├── MainActivity.kt             # Entry point
```

---

## 🚀 How to Run the Project

### 🧩 Requirements

* Android Studio (latest version recommended)
* Android device or emulator
* Minimum SDK: 21+

---

### ▶️ Steps

1. Clone or download the project
2. Open in Android Studio
3. Sync Gradle
4. Connect your device or start emulator
5. Click ▶️ **Run**

---

## 📦 Build APK

To generate APK:

```
Build → Build APK(s)
```

APK location:

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🎮 How to Play

* Player X always starts first
* Tap any empty cell to make a move
* First to align 3 marks (row/column/diagonal) wins
* If all cells are filled → Draw

---

## 🔄 Game Modes

### 🤖 Single Player

* Play against AI
* AI automatically makes a move after you

### 👥 Two Player

* Two users can play on the same device
* Players take turns

---

## 🎉 Game States

* 🟢 Ongoing → Game continues
* 🏆 Win → Winner declared + line drawn
* 🤝 Draw → No winner

---

## 📸 Screens (Optional)

*Add screenshots here if uploading to GitHub*

---

## 🔥 Future Improvements

* 🧠 Smart AI (Minimax Algorithm)
* 🔊 Sound Effects
* 🏆 Scoreboard
* 🎇 Win Animation (Fireworks)
* 🌐 Online Multiplayer

---

## 👨‍💻 Author

**Rohit (Rider Rohit King)**

---

## 📄 License

This project is open-source and free to use.

---

## 💡 Conclusion

This project is a great example of:

* Modern Android UI with Compose
* Clean architecture (MVVM)
* Game logic implementation

Perfect for beginners and intermediate Android developers 🚀
