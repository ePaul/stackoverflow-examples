#!/usr/bin/lua

--[[
This program checks continuously the online status and
registers it (together with the time) in a file. This
file then can be analyzed graphically (by another program,
yet to be written).

All file and host names are hardcoded, for now.
--]]

-- we use the tabinout package for storing and
-- loading the data.
tabinout = require ("tabinout")

-- checks the reachability of some remote host
-- and saves the result in a table.
--
-- list  - the table to save the information. We will use the
--         current time as a key, and "online" or "offline" as
--         the value.
-- host  - a string indicating the host name or IP address.
function ping (list, host)
   local result = os.execute("ping -c 1 -n " .. host .. " > /dev/null ")
   date = os.date('%F %T')
   if result == 0
   then
      list[date] = "online"
      io.write("+")
   else
      list[date] = "offline"
      io.write(".")
   end
   -- print(date .. " => " .. list[date])
   io.flush()
end


-- checks the reachability of a host in a loop,
-- until the user interrupts the sleeping between
-- two checks. The results will be regularly (and
-- at the end) saved to a file.
function ping_loop(list, host)
--   io.output():setvbuf("no")
   local stop = false
   while not stop do
      for i = 1,20 do
	 -- ping
	 ping(list, host)
	 -- sleep a bit ...
	 if os.execute("sleep 3s") > 0 then
	    -- result > 0 => failure
	    -- => user did cancel the waiting.
	    -- => we should now stop.
	    stop = true
	    break
	 end
      end
      -- after each 20 results (and at the end, too) we save
      -- the table.
      save_table(list)
   end
end

-- saves a table in a sorted format to the
-- standard file name.
--
-- To guard against problems, we will
-- first write a temporary file, and then rename
-- it to the original one.
function save_table(list) 
   file = assert(io.open(filename .. ".tmp", "w+"))
   tabinout.serialize_sorted(list, file, "")
   file:close()
   os.remove(filename)
   assert(os.rename(filename .. ".tmp", filename))
   io.write(">>")
   io.flush()
end

-- loads table from the standard file name and
-- returns it. If the file does not exist (or there are
-- errors in loading it), we will return a new empty table.
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

-----------------------------------------------------
-- here the main program.

-- the file name will be used by both load_table and save_table.
filename = "onlinestatus.dat"

-- this is www.gmx.net.   --------
ping_loop(load_table(), "213.165.64.74")
