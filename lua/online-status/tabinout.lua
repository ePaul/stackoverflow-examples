--[[
 A package containing some utility methods for serializing
 and deserializing Lua objects, specially tables in a pretty
 format.
--]]


local mod = {}

--------
-- serializes the content of a map in a format which
-- Lua would accept inside a { ... } table constructor.
-- The leading and trailing braces are not included.
-- The entries will be sorted by key.
--
-- o      - the object to be formatted.
-- file   - the file to which to write the data.
-- indent - a string used for indentation for tables.
-- cmp    - a comparison function to sort the subtables.
--          May be nil, then we sort alphabetically (strings)
--          or numerically (numbers).
function mod.serialize_mapcontent(o, file, subindent, cmp)
   for k,v in mod.pairsByKeys(o) do
      file:write(subindent)
      file:write("[")
      mod.serialize_sorted(k, file, subindent, cmp)
      file:write("] = ")
      mod.serialize_sorted(v, file, subindent, cmp)
      file:write(",\n")
   end
end

--------
-- serializes some object to the standard output.
--
-- o      - the object to be formatted.
-- file   - the file to which to write the data.
-- indent - a string used for indentation for tables.
-- cmp    - a comparison function to sort the subtables.
--          May be nil, then we sort alphabetically (strings)
--          or numerically (numbers).
--
-- from http://www.lua.org/pil/12.1.1.html, modified to include
-- indentation and sorting.
--
function mod.serialize_sorted (o, file, indent, cmp)
   if type(o) == "nil" then
      -- this should not really happen on recursion, as nil can
      -- be neither key nor value in a table.
      file:write("nil")
   elseif type(o) == "number" then
      file:write(o)
   elseif type(o) == "string" then
      file:write(string.format("%q", o))
   elseif type(o) == "boolean" then
      file:write( tostring(o) )
   elseif type(o) == "table" then
      file:write("{\n")
      mod.serialize_mapcontent(o, file, indent .. "   ", cmp)
      file:write(indent .. "}")
   else
      error("cannot serialize a " .. type(o))
   end
end


--------
-- iterates over a table by key order.
--
-- t - the table to iterate over.
-- f - a comparator function used to sort the keys.
--     It may be nil, then we use the default order
--     for strings or numbers.
--
-- from http://www.lua.org/pil/19.3.html
--
function mod.pairsByKeys (t, f)
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

----
-- Loads some string as a function, runs this
-- function in an empty environment, and returns
-- this environment as well as the return values
-- of the function.
--
-- This is used as the implementation of the 
-- individual load functions.
local function runInSandbox(code)
   local tab = {}
   local f = assert(loadstring(code))
   setfenv(f, tab)
   return tab, f()
end

-- returns all arguments but the first one.
local function stripFirst(first, ...)
   return ...
end


--------
-- loads a string to a table.
--   this executes the string with the
--   environment of a new table, and then
--   returns the table.
--
-- The code in the string should not need
-- any variables it does not declare itself
-- (including the usual global functions),
-- as these are not available on runtime.
--
-- It runs in a really empty environment.
function mod.loadUnwrappedTable(data)
   return (runInSandbox(data))
end


--------
-- Loads a string to a table.
--  The string should contain syntax valid inside
--  a Lua table constructor (but not including this
--  constructor itself).
--
-- The code in the string should use any
-- any variables (including the usual global
-- functions), as these are not available on runtime.
-- It runs in a really empty environment.
function mod.loadMapContent(data) 
   return stripFirst(runInSandbox("return {" .. data .. "}"))
end


--------
-- Loads a string as a lua expression
-- (or an expression list, really).
--
-- The code in the string should use any
-- any variables (including the usual global
-- functions), as these are not available on runtime.
-- It runs in a really empty environment.
function mod.loadData(data)
   return stripFirst(runInSandbox("return " .. data))
end



return mod
