# TESTY POZIOMECZKI
Repozytorium będzie zawierać / zawiera testy do drugiego projektu zaliczeniowego.

## Sposób kontrybucji testów
1. Utwórz swojego własnego forka i do niego commituj wszystkie swoje testy.
2. Wyślij merge-requesta aby Twoje testy zostały zatwierdzone.
3. Jeśli ktokolwiek potrafi ulepszyć skrypt to jest to jak najbardziej wskazane :)).
4. Testy mogą być przesyłane również do mnie (autor posta) na privie.

## Sposób uruchamiania skryptu testującego
1. Pobierz skrypt testujący do danego folderu.
2. Wrzuć plik programu z rozszerzeniem .c do folderu, gdzie znajduje się skrypt testujący.
3. Uruchom skrypt poleceniem powłoki: "bash testuj.sh nazwa_pliku.c" (bez "), gdzie nazwa_pliku to nazwa pliku z programem .c.
4. Urchomienie skryptu poleceniem powłowki: "bash szybciorem.sh nazwa_pliku.c" uruchamia szybkie testy BEZ valgrinda.
5. Polecenie "bash testuj.sh nazwa_pliku.c programoutput" powoduje uruchomienie katalogu z testami z outputami innych programów. Analogicznie działa z szybciorem.sh

## Ważne informacje techniczne
1. Foldery w /testy mają strukturę P$POLA$_W$WIERSZE$_K$KOLUMNY$_WY$WYBOR$.
2. W pliku #def# znajdują się opcje kompilatora dotyczące #define.
3. Jeżeli już istnieje jakiś folder z danymi opcjami to nie tworzymy nowego!
4. Testy '.in' umieszczamy w folderze \in w danym katalogu i testy '.out' analogicznie.
5. TESTY MUSZĄ BYĆ PISANE POD LINUKSEM!
6. Nie ponoszę odpowiedzialności za testowanie skryptem - dla mnie działa, więc powinien działać
