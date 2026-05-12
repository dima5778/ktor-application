# DirectoryApplication — Клиент (Android)

Мобильное Android-приложение "Справочник сотрудников". Разработано на Jetpack Compose с архитектурой Clean Architecture, авторизацией через Firebase и взаимодействием с Ktor-сервером через Retrofit.

## Технологии

| Технология | Описание |
|---|---|
| Jetpack Compose | Декларативный UI |
| Material Design 3 | Дизайн-система |
| Hilt | Внедрение зависимостей |
| Retrofit + OkHttp | Работа с сетью |
| Firebase Auth | Авторизация по email/паролю |
| Coroutines + Flow | Асинхронная работа |
| Navigation Compose | Навигация между экранами |

## Архитектура (Clean Architecture)

```
com.example.directoryapplication/
├── di/                             # Hilt модули
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── data/                           # Data Layer
│   ├── remote/
│   │   ├── DirectoryApi.kt         # Retrofit интерфейс
│   │   ├── AuthInterceptor.kt      # Добавление токена в запросы
│   │   └── dto/
│   │       └── EmployeeDto.kt
│   └── repository/
│       └── EmployeeRepositoryImpl.kt
├── domain/                         # Domain Layer
│   ├── model/
│   │   └── Employee.kt
│   ├── repository/
│   │   └── EmployeeRepository.kt
│   └── usecase/
│       ├── GetEmployeesUseCase.kt
│       └── SearchEmployeesUseCase.kt
└── presentation/                   # Presentation Layer
    ├── theme/
    ├── navigation/
    │   └── AppNavigation.kt
    ├── auth/
    │   ├── LoginScreen.kt
    │   └── LoginViewModel.kt
    └── directory/
        ├── DirectoryScreen.kt
        ├── DirectoryViewModel.kt
        ├── DetailScreen.kt
        ├── DetailViewModel.kt
        └── components/
            └── EmployeeCard.kt
```

## Требования

- Android Studio Hedgehog или новее
- Android SDK 26+
- Запущенный сервер DirectoryApplication-Server
- Файл `google-services.json` из Firebase Console

## Настройка и запуск

### 1. Клонировать репозиторий

```bash
git clone https://github.com/ваш-username/DirectoryApplication-Client.git
cd DirectoryApplication-Client
```

### 2. Добавить Firebase конфиг

Скачать `google-services.json` из Firebase Console:
- Настройки проекта → General → Your apps → Android app → Download

Положить файл в папку `app/`:
```
app/
└── google-services.json
```

### 3. Указать адрес сервера

В файле `di/NetworkModule.kt` изменить BASE_URL:

```kotlin
// Для эмулятора Android
private const val BASE_URL = "http://10.0.2.2:8080/"

// Для реального устройства — IP вашего компьютера
private const val BASE_URL = "http://192.168.x.x:8080/"
```

### 4. Запустить приложение

Открыть проект в Android Studio и нажать Run.

## Экраны приложения

| Экран | Описание |
|---|---|
| LoginScreen | Вход по email и паролю через Firebase |
| DirectoryScreen | Список всех сотрудников с поиском |
| DetailScreen | Подробная информация о сотруднике |

## Тестовый аккаунт

```
Email: test@test.com
Пароль: 123456
```

## Запуск тестов

```bash
# Unit тесты
./gradlew test

# UI тесты (нужен эмулятор)
./gradlew connectedAndroidTest
```
