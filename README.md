# Algorytm K Najbliższych Sąsiadów w Kotlinie
 
Algorytm K Najbliższych Sąsiadów (KNN) to jeden z podstawowych algorytmów uczenia maszynowego stosowany w problemach klasyfikacji i regresji. Opiera się na zasadzie podobieństwa – klasyfikacja nowego punktu odbywa się na podstawie etykiet jego K najbliższych sąsiadów. Implementacja w Kotlinie obejmuje zarówno wersję sekwencyjną, jak i równoległą.

## Reprezentacja danych
Każdy punkt danych jest reprezentowany jako instancja klasy `Point`, która zawiera:
- listę cech (`features`), czyli wartości liczbowe opisujące dany punkt;
- etykietę (`label`), która jest klasą przypisaną do punktu.

## Obliczanie odległości
Zastosowano metrykę euklidesową do obliczania odległości między punktami. Zwraca ona pierwiastek sumy różnic poszczególnych cech podniesionych do potęgi 10.

## Klasyfikacja punktu testowego
Implementacja sekwencyjna algorytmu KNN:

1. Obliczane są odległości między punktem testowym a każdym punktem w zbiorze treningowym.
2. Sortowana jest lista punktów według odległości.
3. Wybierane są `k` najbliższe punkty.
4. Punkt testowy zostaje przypisany do klasy, która pojawia się najczęściej wśród wybranych sąsiadów.

## Optymalizacja równoległa
Aby przyspieszyć obliczenia, implementacja została zoptymalizowana przy użyciu bibiloteki Kotlin (`kotlinx.coroutines`). Dane treningowe są dzielone na `numThreads` fragmentów, a odległości są obliczane równolegle:

1. Dane są dzielone na `numThreads` części.
2. Każda część przetwarzana jest asynchronicznie w oddzielnej korutynie.
3. Wyniki są łączone i sortowane.
4. Klasyfikacja następuje identycznie jak w wersji sekwencyjnej.

## Generowanie danych treningowych
Do celów testowych generowane są losowe punkty z losowymi cechami i przypisywaną etykietą
Testowanie i pomiar czasu
W funkcji `main()` następuje:
1. Wygenerowanie zbioru treningowego,
2. Stworzenie losowego punktu testowego,
3. Pomiar czasu działania wersji standardowej i równoległej:

## Wnioski
- Algorytm KNN jest łatwy w implementacji, ale obliczeniowo kosztowny.
- Wersja równoległa znacznie poprawia wydajność dzięki podziałowi obliczeń na wiele wątków.
- Kotlin i korutyny umożliwiają efektywne przetwarzanie dużych zbiorów danych.
- Zastosowanie odległości euklidesowej podniesionej do potęgi 10 może wpływać na wrażliwość algorytmu na duże różnice w poszczególnych cechach.

