#!/bin/bash
#
# Skrypt testujący
#
# Sposób użycia
#
# ./testuj.sh nazwa_programu.c katalog_z_testami
#
# jeśli testy znajdują się katalogu "testy/" nie ma potrzeby podawania katalogu
#

function error() {
    echo "$1"
    exit 1
}

if [ -z "$1" ] ; then
    error "sposob uzycia: $0 PLIK_ZRODLOWY [ katalog z testami ]"
fi

TESTS_DIR="testy"
COMPILER_OPTIONS=@opcje

SOL="$1"
if [ -n "$2" -a -d "$2" ] ; then
    TESTS_DIR="$2"
fi
TESTS=`cd $TESTS_DIR ; ls -1 *.in | sort -n -k 3 -t t`
TEMP_DIR=`pwd`
EXE="$TEMP_DIR/a.e"

function compile() {
    gcc $COMPILER_OPTIONS "$SOL" -o"$EXE" 
    retcode=$?
    if [ $retcode -ne 0 ] ; then
        error "compile error"
    else
        echo "Compile OK"
    fi
}

function test_solution() {
    tname="$1"
    in_file="$TESTS_DIR/$1"
    out_file="${in_file%.in}.out"
    user_out_file="$TEMP_DIR/a.out"
    user_err_file="$TEMP_DIR/a.err"
    $EXE < $in_file > $user_out_file 2> $user_err_file
    retcode=$?
    echo -n "$tname .. "
    if [ $retcode -ne 0 ] ; then
	   echo "RUNTIME ERROR: $retcode"
       echo "STDERR":
       cat $user_err_file
       echo "STDOUT":
       cat $user_out_file
    elif ! diff -q -b "$out_file" "$user_out_file" > /dev/null ; then
	   echo "WRONG ANSWER"
	   diff -b "$out_file" "$user_out_file" 
    elif ! diff -q "$out_file" "$user_out_file" > /dev/null ; then
	   echo "ALMOST OK"
	   diff "$out_file" "$user_out_file" 
    else
	   echo "OK"
    fi
    rm -f "${user_out_file}" "${user_err_file}"
}

compile
for t in $TESTS ; do
    test_solution "$t"
done
rm -f "$EXE"
