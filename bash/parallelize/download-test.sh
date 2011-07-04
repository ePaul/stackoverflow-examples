#!/bin/bash

# from http://stackoverflow.com/q/6575492/600500

total=0
getPages(){
  subTotal=$(lynx -dump http://"$(printf "%s:%s" $1 $2)"/file.html | awk -F, 'NR==1 {print $1}' | sed 's/\s//g')
  total=$(($total+$subTotal))
  echo "SubTotal: " $subTotal "Total: " $total
}
# /output/ SubTotal:  22 Total:  22
# /output/ SubTotal:  48 Total:  48   //Note Total should be 70

ARRAY=(
'pf2.server.com:6599'
'pf5.server.com:1199'
...
)

for server in ${ARRAY[@]} ; do
  KEY=${server%%:*}
  VALUE=${server##*:}
  getPages $KEY $VALUE &
done
wait
  echo $total
exit 0        

# /output/ 0