#!/bin/bash

# from http://stackoverflow.com/q/6575492/600500

total=0
getPages(){
    printf "%s:%s" $1 $2
  subTotal=$(lynx -dump http://"$(printf "%s:%s" $1 $2)"/ | wc -l)
  total=$(( $total + $subTotal ))
  echo "SubTotal: " $subTotal "Total: " $total
}
# /output/ SubTotal:  22 Total:  22
# /output/ SubTotal:  48 Total:  48   //Note Total should be 70

ARRAY=(
'www.gmx.de:80'
'www.gmx.net:80'
)

for server in ${ARRAY[@]} ; do
  KEY=${server%%:*}
  VALUE=${server##*:}
  getPages $KEY $VALUE
done
wait
  echo $total
exit 0        

# /output/ 0