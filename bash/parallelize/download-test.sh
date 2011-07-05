#!/bin/bash

# from http://stackoverflow.com/q/6575492/600500


ARRAY=(
'www.gmx.de:80'
'www.gmx.net:80'
'www.gmx.at:80'
'www.gmx.li:80'
)


(( total = 0 ))
while read subtotal
do
   (( total = total + subtotal ))
   echo "subtotal: $subtotal, total: $total"
done < <(
    pexec --number ${#ARRAY[*]} --normal-redirection --environment hostname --parameters ${ARRAY[@]} --shell-command -- '
     lynx -dump http://$hostname/index.html | wc -l'
    )

echo "total: $total"

exit 0
