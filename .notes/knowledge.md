# Spendly - Finance Tracking Android App

## Project Overview
Spendly is an Android application designed to help users track their finances. The app appears to focus on parsing SMS messages for transaction data and providing a clean interface for financial management.

## Architecture
The project follows modern Android development practices with:
- Clean Architecture principles
- MVI (Model-View-Intent) pattern for UI
- Dependency Injection using Hilt
- Jetpack Compose for UI
- Room Database for local storage
- Material 3 Design System

## Package Structure

### 1. Base Package (`base/`)
Contains core utilities and base components:
- `DateUtils.kt` - Date manipulation utilities
- `LocaleUtils.kt` - Locale-specific utilities
- `TransactionStateHolder.kt` - State management
- `DataStore.kt` - Data persistence
- `TransactionCategoryResourceProvider.kt` - Category management
- `SampleLocalData.kt` - Sample data for development

### 2. Database Package (`database/`)
Room database implementation:
- `AppDatabase.kt` - Main database configuration
- `TransactionEntity.kt` - Transaction data model
- `TransactionDao.kt` - Data Access Object
- `TransactionCategoryConverter.kt` - Type converters

### 3. Dependency Injection (`di/`)
Hilt modules for dependency injection:
- `AppModule.kt` - Main application dependencies
- `Home.kt` - Home feature dependencies

### 4. Home Feature (`home/`)
Main screen implementation following clean architecture:
- **Presentation Layer**
  - `Home.kt` - Main UI composable
  - `HomeViewModel.kt` - Business logic
  - `HomeScreenState.kt` - UI state
  - `HomeEvent.kt` - User actions
  - `ViewBy.kt` - View options
- **Data Layer**
  - `TransactionRepository.kt` - Repository interface
  - `LocalTransactionRepository.kt` - Local data source
  - Various use cases for business operations

### 5. Navigation (`navigation/`)
Navigation implementation using Jetpack Navigation:
- `AppNavigator.kt` - Main navigation component
- `Screen.kt` - Screen route definitions

### 6. Notification (`notification/`)
Notification system implementation:
- `NotificationChannelManager.kt` - Notification channel handling
- Separate packages for actions, categories, and DI

### 7. SMS Parser (`smsparser/`)
SMS processing functionality:
- Transaction classification
- SMS parsing logic
- Common utilities
- Dependency injection

### 8. Transaction Management (`transaction/`)
Transaction-related features:
- Create transaction
- Edit transaction
- View transaction
- Show all transactions

### 9. UI (`ui/`)
Contains theme and common UI components

## Main Components
- `MainActivity.kt` - Entry point of the application
- `MainApp.kt` - Application class with Hilt integration

## Key Features
1. SMS-based transaction tracking
2. Manual transaction management
3. Transaction categorization
4. Notification system
5. Different view options for transactions

## Technical Stack
- Kotlin
- Jetpack Compose
- Hilt (Dependency Injection)
- Room Database
- Coroutines (implied from architecture)
- Material 3
- AndroidX Navigation

## Architecture Patterns
1. Clean Architecture
   - Clear separation of concerns
   - Use cases for business logic
   - Repository pattern for data access

2. MVI Pattern
   - State management
   - Event handling
   - Unidirectional data flow

3. Repository Pattern
   - Abstract data sources
   - Single source of truth
   - Clean data access

4. SOLID Principles
   - Modular design
   - Interface segregation
   - Dependency inversion

## Development Guidelines
- Kotlin coding standards
- Clean Architecture principles
- Material Design guidelines
- Comprehensive error handling
- Permission management
- State management best practices 