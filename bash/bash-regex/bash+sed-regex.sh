#!/bin/bash

# Reads the standard input line-by-line, splitting each
# line according to a regular expression. The three
# capturing groups are then used as arguments to another command.
#
#
# See http://stackoverflow.com/questions/5096925/in-gnu-grep-or-another-standard-bash-command-is-it-possible-to-get-a-resultset-f/5097045#5097045
# for an explanation.
#
USER=j
regex=" ([0-9]+) +$USER +([0-9]+).+(/tmp/Flash[0-9a-zA-Z]+) "


sed -r -n -e " s%^.*$regex.*\$%\1 \2 \3%p " |
while read -a array
do
   echo cp /proc/${array[0]}/fd/${array[1]} ~/${array[2]}
done