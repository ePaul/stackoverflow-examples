# From http://stackoverflow.com/q/6034820/600500, a bit modified for my answer.

meta = function ()
    for i = 1, 10 do
        coroutine.yield(i)
    end
end

for i in coroutine.wrap(function() val = meta() print ("----") print (val) return 7 end) do
    print(i)
end