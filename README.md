# 📱 Spendly - Smart Finance Tracker

<div align="center">
  <img src="screenshots/home_screen.png" width="250" alt="Home Screen"/>
  <img src="screenshots/transaction_details.png" width="250" alt="Transaction Details"/>
  <img src="screenshots/analytics_view.png" width="250" alt="Analytics View"/>

  [![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
  [![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
  [![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)
</div>

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Motivation](#motivation)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Architecture](#architecture)
- [Contributing](#contributing)
- [Code Style](#code-style)
- [License](#license)
- [Contact](#contact)
- [Acknowledgments](#acknowledgments)

## 🎯 Overview

Spendly is a modern Android application designed to help users track their finances effortlessly. It automatically captures and categorizes transactions from SMS notifications and provides intuitive visualization of spending patterns.

## 💡 Motivation

Managing personal finances can be tedious and time-consuming. Spendly was built to solve this problem by:
- Automating transaction tracking through SMS parsing
- Providing intelligent categorization of expenses
- Offering visual insights into spending patterns
- Making financial management accessible and user-friendly

## ✨ Features

- 📱 **SMS Transaction Detection**
  - Automatically captures transactions from bank SMS notifications
  - Real-time processing and categorization
  - Support for multiple bank formats

- 💰 **Smart Categorization**
  - AI-powered transaction categorization
  - Custom category management
  - Automatic merchant recognition

- 📊 **Visual Analytics**
  - Beautiful charts and graphs
  - Spending pattern analysis
  - Category-wise breakdowns
  - Time-based trends

- ✏️ **Manual Transaction Management**
  - Add custom transactions
  - Edit transaction details
  - Bulk operations support

- 🔔 **Smart Notifications**
  - Real-time transaction alerts
  - Quick action buttons
  - Customizable notification settings

- 🎨 **Modern Design**
  - Material 3 design system
  - Dark mode support
  - Responsive layouts
  - Smooth animations

## 🛠️ Tech Stack

- **Architecture**
  - Clean Architecture
  - MVI Pattern
  - Repository Pattern
  - SOLID Principles

- **Frontend**
  - Jetpack Compose
  - Material 3
  - AndroidX Navigation

- **Backend**
  - Room Database
  - Kotlin Coroutines
  - Hilt (Dependency Injection)

## 📂 Project Structure

```
app/
├── data/           # Data layer with repositories
├── di/             # Dependency injection modules
├── domain/         # Business logic and use cases
├── ui/             # UI components and theme
├── smsparser/      # SMS parsing and classification
├── notification/   # Notification system
└── utils/          # Utility classes
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK 21 or later
- Android device or emulator running Android 5.0 or later

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/vikashprajapati/spendly.git
   ```

2. Open the project in Android Studio

3. Sync project with Gradle files

4. Add required permissions in your device settings:
   - SMS Read Permission
   - Notification Permission

5. Run the app on your device or emulator

## 📱 Usage

1. **First Launch**
   - Grant required permissions
   - Complete initial setup
   - Choose your preferred banks

2. **Transaction Tracking**
   - App automatically detects transactions
   - View transactions in list or chart view
   - Filter by date, category, or amount

3. **Managing Categories**
   - Create custom categories
   - Set category icons and colors
   - Configure auto-categorization rules

4. **Analytics**
   - View spending trends
   - Analyze category-wise expenses
   - Export reports

## 🏗️ Architecture

The project follows Clean Architecture principles with MVI pattern:
- **Model**: Represents the data and business logic
- **View**: Declarative UI using Jetpack Compose
- **Intent**: User actions that modify the state

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add some amazing feature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## 📞 Contact

Vikash Prajapati - [@NomadicProgramr](https://twitter.com/NomadicProgramr)

Project Link: [https://github.com/vikashprajapati/spendly](https://github.com/vikashprajapati/spendly)

## 🙏 Acknowledgments

- [Material Design](https://m3.material.io/)
- [Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin](https://kotlinlang.org/)
- Open-source community

---
<div align="center">
Made with ❤️ by Vikash Prajapati
</div> 