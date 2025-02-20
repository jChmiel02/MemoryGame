# Gra Pamięciowa 🎮

Gra pamięciowa stworzona w **Kotlinie** z wykorzystaniem **Jetpack Compose**, zaprojektowana w celu prezentacji czystej architektury, zarządzania stanem oraz integracji bazy danych **Room**.

---

## 🚀 Funkcje

1. **Dynamiczna rozgrywka**:
   - Wyświetlana jest siatka kart, które na początku są ukryte.
   - Gracz odkrywa dwie karty na turę, próbując znaleźć pary na podstawie kolorów.

2. **Interfejs**:
   - Responsywny i nowoczesny interfejs zbudowany w **Jetpack Compose**.
   - Wykorzystanie komponentów takich jak `Column`, `Row`, `Card`, czy `LazyColumn`.

3. **Zapisywanie wyników**:
   - Wyniki (ruchy, czas, błędy) są zapisywane w lokalnej bazie danych **Room**.
   - Najlepsze wyniki można zobaczyć na ekranie z tabelą wyników.

4. **Obsługa wielu języków**:
   - Aplikacja wspiera wiele języków dzięki plikowi `strings.xml`.

5. **Powiadomienia Toast**:
   - Po zakończeniu gry wyświetlany jest spersonalizowany toast z wynikami (ruchy, czas, błędy).

---

## 📂 Struktura projektu

### 1. **Ekrany**:
- **MainMenuScreen**: Ekran główny, z którego można rozpocząć nową grę lub zobaczyć najlepsze wyniki.
- **NameInputScreen**: Prosty ekran, gdzie gracz wpisuje swoje imię przed rozpoczęciem gry.
- **GamePlayScreen**: Główny ekran gry, gdzie karty są odkrywane, dopasowywane i śledzone.
- **BestScoresScreen**: Ekran pokazujący tabelę najlepszych wyników.
- **EndGameScreen**: Ekran pojawiający się po zakończeniu gry z opcją restartu lub powrotu do menu.

### 2. **Baza danych**:
- **DatabaseHelper**: Klasa pomocnicza do obsługi bazy danych Room.
- **Room Database**:
  - `AppDatabase`: Definiuje strukturę bazy danych.
  - `ScoreDao`: Obsługuje zapytania do bazy danych (np. wstawianie wyników, pobieranie najlepszych wyników).
  - `Quadruple`: Klasa danych reprezentująca wynik (imię, ruchy, czas, błędy).

### 3. **Podstawowa funkcjonalność**:
- **Generowanie kart**:
  - Funkcja `generateShuffledCards` generuje 8 par kart (16 w sumie) w losowej kolejności.
- **Logika gry**:
  - Śledzi odkryte karty, dopasowane pary, ruchy, błędy i czas.
  - Wykrywa zakończenie gry, gdy wszystkie pary zostaną dopasowane.

---

## 🛠️ Technologie

- **Język programowania**: Kotlin
- **Framework UI**: Jetpack Compose
- **Baza danych lokalna**: Room
- **Gradle**: Konfiguracja projektu i zarządzanie zależnościami
