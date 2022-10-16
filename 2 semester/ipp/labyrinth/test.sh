tmp_dir=$(mktemp -d -t ipp_testXXXXXX)

for testcase in $2/*.in
do
    valgrind --error-exitcode=123 --leak-check=full --show-leak-kinds=all --errors-for-leak-kinds=all -q ./$1 < $testcase 1> program.out 2> program.err
	valgrind=$?
    echo ""
    echo "TEST ${testcase%.in}: "
    $1 <$testcase 1> $tmp_dir/test.out 2> $tmp_dir/test.err

    echo -n "exit code: $?     "


    if diff program.out ${testcase%in}out >/dev/null 2>&1
    then
        echo -n "out: OK      "
    else
        echo -n "out: ERROR   "
    fi

    if diff program.err ${testcase%in}err >/dev/null 2>&1
    then 
        echo -n " err: OK"
    else
        echo -n " err: ERROR"
    fi


    if ((valgrind != 0 ))
    then
        echo "    memory: ERROR"
    else
        echo "    memory: OK"
    fi

done

rm -rf $tmp_dir