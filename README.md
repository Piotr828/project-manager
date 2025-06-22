Projekt zakłada stworzenie rozbudowanej aplikacji do zarządzania projektami, napisanej w języku Java w paradygmacie
programowania obiektowego. Główne funkcjonalności obejmują tworzenie i edytowanie projektów oraz przypisanych do nich
zadań, śledzenie postępu prac na podstawie zakończonych zadań, a także automatyczne obliczanie opóźnień poprzez
porównanie postępu z procentem upływu czasu. Aplikacja dynamicznie wyznacza również szacowaną datę zakończenia projektu
metodą interpolacji liniowej. Projekty można sortować według wielu kryteriów: nazwy, postępu, opóźnienia, dat, a także
dostosować ich wygląd (np. kolor). Dodatkowo planowana jest integracja z Google Calendar, pozwalająca na eksport
harmonogramu projektów i zadań do zewnętrznego kalendarza użytkownika.

Aplikacja zostanie zaimplementowana z użyciem bibliotek takich jak swing (do interfejsu graficznego), Gson (do zapisu i
odczytu danych w formacie JSON), oraz Google Calendar API, a także security, io, awt. Logika aplikacji zostanie
podzielona na klasy reprezentujące projekty, zadania, ustawienia użytkownika, eksportery danych i komponenty GUI, co
umożliwi zachowanie przejrzystej struktury i wysokiego poziomu abstrakcji. Projekt uwzględnia możliwość późniejszej
rozbudowy — np. o zarządzanie użytkownikami, powiadomienia czy synchronizację z chmurą — dzięki modularnej architekturze
opartej na jasnym podziale odpowiedzialności między klasami.

Skład grupy:
Piotr Wdowiak
Hanna Warońska
Laura Olszewska