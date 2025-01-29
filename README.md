# Spendly - Smart Finance Tracker

<div align="center">
  <img src="screenshots/home_screen.png" width="250" alt="Home Screen"/>
  <img src="screenshots/transaction_details.png" width="250" alt="Transaction Details"/>
  <img src="screenshots/analytics_view.png" width="250" alt="Analytics View"/>
</div>

## Overview

Spendly is a modern Android application designed to help users track their finances effortlessly. It automatically captures and categorizes transactions from SMS notifications and provides intuitive visualization of spending patterns.

## Features

- 📱 **SMS Transaction Detection**: Automatically captures transactions from bank SMS notifications
- 💰 **Smart Categorization**: Intelligently categorizes transactions based on merchant and description
- 📊 **Visual Analytics**: Beautiful charts and graphs to visualize spending patterns
- ✏️ **Manual Transaction Management**: Add, edit, and categorize transactions manually
- 🔔 **Smart Notifications**: Real-time transaction notifications with quick action buttons
- 🎨 **Material Design**: Modern UI with Material 3 design system
- 🌙 **Dark Mode Support**: Seamless dark mode integration

## Tech Stack

- **Architecture**: Clean Architecture with MVI pattern
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Local Storage**: Room Database
- **Concurrency**: Kotlin Coroutines
- **UI Components**: Material 3
- **Navigation**: Jetpack Navigation
- **Build System**: Gradle with Kotlin DSL

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK 21 or later

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/spendly.git
   ```

2. Open the project in Android Studio

3. Sync project with Gradle files

4. Run the app on an emulator or physical device

## Required Permissions

- `READ_SMS`: For capturing transaction SMS
- `RECEIVE_SMS`: For real-time transaction detection
- `POST_NOTIFICATIONS`: For transaction notifications

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Code Style

This project follows the official Kotlin coding conventions and clean code principles:

- Use meaningful names for classes, functions, and variables
- Write small, focused functions (< 20 lines)
- Follow SOLID principles
- Write unit tests for business logic
- Document complex logic and public APIs

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

- Material 3 Design System
- Android Jetpack libraries
- Kotlin programming language
- Open-source community

## Contact

Your Name - [@NomadicProgramr](https://twitter.com/NomadicProgramr)

Project Link: [https://github.com/vikashprajapati/spendly](https://github.com/vikashprajapati/spendly) 