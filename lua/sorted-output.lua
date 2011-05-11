
-- serializes some object to the standard output.
--
-- o      - the object to be formatted.
-- indent - a string used for indentation for tables.
-- cmp    - a comparison function to sort the subtables.
--          May be nil, then we sort alphabetically (strings)
--          or numerically (numbers).
--
-- from http://www.lua.org/pil/12.1.1.html, modified to include
-- indentation and sorting.
--
function serialize_sorted (o, indent, cmp)
   if type(o) == "nil" then
      -- this should not really happen on recursion, as nil can
      -- be neither key nor value in a table.
      io.write("nil")
   elseif type(o) == "number" then
      io.write(o)
   elseif type(o) == "string" then
      io.write(string.format("%q", o))
   elseif type(o) == "boolean" then
      io.write( tostring(o) )
   elseif type(o) == "table" then
      io.write("{\n")
      local subindent = indent .. "   "
      for k,v in pairsByKeys(o) do
         io.write(subindent)
         io.write("[")
         serialize_sorted(k, subindent, cmp)
         io.write("] = ")
         serialize_sorted(v, subindent, cmp)
         io.write(",\n")
      end
      io.write(indent .. "}")
   else
      error("cannot serialize a " .. type(o))
   end
end


-- iterates over a table by key order.
--
-- t - the table to iterate over.
-- f - a comparator function used to sort the keys.
--     It may be nil, then we use the default order
--     for strings or numbers.
--
-- from http://www.lua.org/pil/19.3.html
--
function pairsByKeys (t, f)
   local a = {}
   for n in pairs(t) do table.insert(a, n) end
   table.sort(a, f)
   local i = 0      -- iterator counter
   local iter =  function ()   -- iterator function
                    i = i + 1
                    if a[i] == nil then return nil
                    else return a[i], t[a[i]]
                    end
                 end
   return iter
end


-- loads a string to a table.
--   this executes the string with the
--   environment of a new table, and then
--   returns the table.
--
-- The code in the string should not need
-- any variables it does not declare itself,
-- as these are not available on runtime.
-- It runs in a really empty environment.
function loadTable(data)
   local table = {}
   local f = assert(loadstring(data))
   setfenv(f, table)
   f()
   return table
end




-- read input from stdin
local data = io.read("*all")
-- load table
local testTable = loadTable(data)

-- output everything
for k, v in  pairsByKeys(testTable) do
   io.write(k .. " = ")
   serialize_sorted(v, "")
   io.write("\n")
end
