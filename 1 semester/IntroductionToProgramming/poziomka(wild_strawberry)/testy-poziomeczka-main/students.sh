#!/bin/bash

if [ $# -ne 4 ]; then
    echo Sposób użycia komendy: bash students.sh ab123456@students.mimuw.edu.pl "(szybciorem/testuj)" nazwa_pliku.c nazwa_foleru_z_testami
    exit 1
fi

ssh $1 "mkdir -p ~/testy_programow_WPI21/ ; cd ~/testy_programow_WPI21/ ; git init ; git clone https://gitlab.com/wupei2021/testy-poziomeczka"
scp $3 $1:~/testy_programow_WPI21/testy-poziomeczka/
ssh $1 "cd ~/testy_programow_WPI21/testy-poziomeczka/ ; bash ${2:-testuj}.sh $3 $4; rm -r ~/testy_programow_WPI21"
