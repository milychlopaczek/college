#!/bin/bash

if [ $# -eq 0 ]; then
    echo Sposób użycia komendy: bash testuj.sh nazwa_pliku.c nazwa_folderu_z_testami
    exit 1
fi

touch tmp

echo ---ROZPOCZYNAM TESTOWANIE---
echo Nazywa pliku projektowego: $1
echo
for dir in ${2:-testy}/*/ ; do
    echo ---KOMPILACJA---
    echo Katalog: $dir
    printf "Użyte flagi: "
    cat $dir/def
    echo
    echo

    gcc @opcje @$dir/def $1 -o project
    if [ $? -ne 0 ]; then
        echo !-!-! BŁĄD KOMPILACJI !-!-!
        exit 1
    fi

    for file in $dir/in/*; do
        echo -"$(basename $file)"-
        valgrind --leak-check=full -q --error-exitcode=1 ./project <$file | diff - $dir/out/$(basename -s .in $file).out
        
        if [ $? -eq 0 ]; then
            printf "Pomyślnie ukończono próbę "$(basename -s .in $file)"."
            time(./project <$file >tmp)
            echo
        else
            echo !-!-! Błąd !-!-!
            echo Błędny wynik podczas wykonywania próby "$(basename $file)"
            echo Spodziewany wynik: 
            cat $dir/out/$(basename -s .in $file).out
            echo Otrzymany wynik:
            valgrind --leak-check=full -q --error-exitcode=1 ./project <$file
            break 2
        fi
    done

done

echo ----CZYSZCZENIE----
echo Usuwam plik wykonywalny projektu
rm project
rm tmp
printf "\n"
echo ----ZAKOŃCZENIE PROCESU TESTOWANIA----
echo Dziękujemy za skorzystanie z programu testującego!