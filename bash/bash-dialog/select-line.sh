#!/bin/bash



function showArgs () {
    echo "---- $1 ----"
    shift
    while [[ $# > 0 ]]
    do
        echo "»$1«"
        shift
    done
}

function ignored () {
echo "- example.txt ---"
cat example.txt
echo "- sed -----"
sed -e 's/^.*$/"&" ""/' < example.txt


showArgs '$(< ... )' $(< example.txt )

showArgs "sed ''" $( sed -e "s/^.*$/'&' ''/" < example.txt )
showArgs 'sed ""' $( sed -e 's/^.*$/"&" ""/' < example.txt )

args=( $( sed -e 's/^.*@.*$/"&" ""/' < example.txt ) )

showArgs "using array" "${args[@]}"

(
IFS=',' args=( $( sed -e 's/^.*$/&,_/' < example.txt ) )
unset IFS

showArgs "using array with special IFS" "${args[@]}"
)

arg="$(  sed -e "s/^.*$/'&' ''/" < example.txt )"
showArgs "via variable" $arg
showArgs "via variable" "$arg"

# eval showArgs "'via eval'" "$arg"

showArgs "direct parameter" "Name Surname mymail@mail.com" "" "Another New person@database.loc" ""
}


declare -a args=()

while read
do
    args+=("$REPLY" "")
done < <( cat example.txt )

showArgs "read + array" "${args[@]}"

dialog --menu "Please select the line you want to edit" 40 60 34 "${args[@]}"

