# Huze Muscle - Fitness Tracker

Huze Muscle is a comprehensive Android application designed to help users track their fitness journey, monitor daily activities, and achieve their health goals. With a focus on usability and data visualization, the app provides real-time insights into steps, hydration, and workouts.

## 🚀 Features

*   **Activity Tracking**: Monitor daily steps in real-time using built-in device sensors.
*   **Workout Management**: Browse various exercises and track workout durations.
*   **Hydration Tracker**: Log daily water intake with an intuitive visual interface.
*   **BMI Calculator**: Calculate and track Body Mass Index (BMI) based on user profile data.
*   **Visual Progress**: Data visualization for calories burned and activity trends using MPAndroidChart.
*   **Cloud Sync**: Securely store and sync user data using Firebase Realtime Database and Firestore.
*   **Local Persistence**: High-performance local storage using Room Database for offline access.
*   **Smart Reminders**: Automated notifications to keep you motivated and hydrated.

## 🛠 Tech Stack

*   **Language**: Java / Kotlin
*   **UI Framework**: Android Jetpack (Fragments, Navigation, ViewModel, LiveData)
*   **View Binding**: For safe and efficient UI interaction.
*   **Database**: 
    *   **Firebase**: Realtime Database, Firestore, and Authentication.
    *   **Room**: Local SQLite abstraction layer.
*   **Libraries**:
    *   **MPAndroidChart**: For elegant fitness progress charts.
    *   **Lottie**: For high-quality interactive animations.
    *   **Material Design**: Modern UI components and styling.

## 📦 Project Structure

```text
com.example.huzemuscle
├── activities      # UI Activities (Main, WorkoutTimer, etc.)
├── fragments       # Feature-specific UI Fragments (Home, Progress, Exercise)
├── viewmodels      # Logic and data management
├── database        # Room DB entities and DAOs
├── utils           # Helper classes and calculators
└── models          # Data models
```

## ⚙️ Setup & Installation

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/yourusername/Huze-Muscle.git
    ```
2.  **Open in Android Studio**:
    *   Select `File > Open` and navigate to the project root.
3.  **Firebase Configuration**:
    *   Create a project in the [Firebase Console](https://console.firebase.google.com/).
    *   Add an Android App with the package name `com.example.huzemuscle`.
    *   Download the `google-services.json` file and place it in the `app/` directory.
4.  **Build & Run**:
    *   Sync Gradle and run the app on an emulator or physical device.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request or open an issue for any bugs or feature requests.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
