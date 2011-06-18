#!/usr/bin/lua

tabinout = require ("tabinout");


function ping (list, host)
   result = os.execute("ping -c 1 -n " .. host .. " > /dev/null ")
   date = os.date('%F %T')
   if result == 0
   then
      list[date] = "online"
   else
      list[date] = "offline"
   end
   print(date .. " => " .. list[date])
end

function ping_loop(list, host) 
   while true do
      for i = 1,10 do
	 ping(list, host)
	 os.execute("sleep 3s")
      end
      save_table(list)
   end
end

-- saves a table in a sorted format to the
-- standard file name.
function save_table(list) 
   file = assert(io.open(filename .. ".tmp", "w+"))
   tabinout.serialize_sorted(list, file, "")
   file:close()
   os.remove(filename)
   assert(os.rename(filename .. ".tmp", filename))
   print("saved.")
end

function load_table()
   file = io.open(filename)
   if file == nil then
      return {}
   else
      text = file:read("*all")
      file:close()
      return tabinout.loadData(text)
   end
end


-- here the main program.

-- the file name will be used by both load_table and save_table.
filename = "onlinestatus.dat"

ping_loop(load_table(), "192.168.0.42")
