#!/bin/bash

#
# Inspired by http://stackoverflow.com/q/5607824/600500
#

# Regex for lines describing "good words":
# - empty lines (after each line of input, i.e. at the end)
# - lines with only a '*' (indicating a good word)
# - a line with '@(#) '   (at the start of the output)
# All other lines indicate a bad word.
good_words='^[*]?$|^@\(#\) '

while read # read one line of input
do
    echo $REPLY | # pipe the line to aspell
    aspell pipe | # let aspell check the line
    egrep -q -v $good_words || # have a look if aspell found misspellings
    # no words with mistake, output the line
    echo $REPLY
done
