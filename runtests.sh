for f in tests/*.stmnt
do
    java -jar bin/interpreter.jar $f > $f.i
    java -jar bin/compile.jar $f
    java -jar  bin/am.jar ${f/.stmnt/.o} > $f.c
    cmp -s $f.i $f.c
    if [ $? -ne 0 ]; then
        @echo Trouble with $f
    fi
done
