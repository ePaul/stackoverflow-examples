#!/bin/bash

# Reads the standard input line-by-line, splitting each
# line according to a regular expression. The three
# capturing groups are then used as arguments to another command.
#
# See http://stackoverflow.com/questions/5096925/in-gnu-grep-or-another-standard-bash-command-is-it-possible-to-get-a-resultset-f/5098449#5098449
# for an explanation.

USER=j
regex=" ([0-9]+) +$USER +([0-9]+).+(/tmp/Flash[0-9a-zA-Z]+) "

while read 
do
    if [[ $REPLY =~ $regex ]]
    then
        echo cp /proc/${BASH_REMATCH[1]}/fd/${BASH_REMATCH[2]} ~/${BASH_REMATCH[3]}
    fi
done