#!bin/bash
echo ---- ETAP 1: KOMPILACJA PROGRAMU ----
echo Kompiluję program projektu.
start=$(date +%s.%N)
touch tmp
gcc @opcje $1 -o project
ReturnCode=$?
TestNumber=1

if [ $ReturnCode -ne 0 ]
then
    echo Wystąpił błąd podczas kompilacji!
    echo Skrypt kończy działanie.
    rm tmp
    exit 1
else
    echo Kompilacja zakończona pomyślnie!
fi

echo
echo ---- ETAP 2: FAZA TESTÓW ----

for file in ${2:-testy}/in/* ;
do
    echo "<=================================>"
    printf "TEST %03d : $file\n" $TestNumber
    ((TestNumber+=1))
    time(./project <$file >tmp)
    echo
    ReturnCode=$?
    if [ $ReturnCode -ne 0 ]
    then
        echo PROGRAM zwrócił błąd "(nie return 0)"!
        echo Zwrócono kod wykonania programu: $ReturnCode
        exit 1
    fi

    diff tmp ${2:-testy}/out/$(basename -s .in $file).out
    ReturnCode=$?
    if [ $ReturnCode -ne 0 ]
    then
        echo DIFF wykrył różnicę!
        echo Zwrócono kod wykonana diffa: $ReturnCode
        echo Treść różnicy:
        echo
        diff -c tmp ${2:-testy}/out/$(basename -s .in $file).out
        echo
        echo Skrypt kończy działanie.
        rm project
        rm tmp
        exit 1
    fi

    echo Pomyślnie ukończono "test" $file!
    echo
done
echo ---- ETAP 3: KONIEC TESTOWANIA ----
((TestNumber-=1))
printf "Pomyślnie ukończono wszystkie %d testów!\n" $TestNumber
duration=$(echo "$(date +%s.%N) - $start" | bc)
echo Skrypt wykonywał swoją pracę $duration sekund.
echo Proces testowania zakończony pozytywnie.
rm tmp
rm project