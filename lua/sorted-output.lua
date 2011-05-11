
-- from http://www.lua.org/pil/12.1.1.html, a bit modified
-- and splitted to two met
function serialize_sorted (o, indent)
   if type(o) == "number" then
      io.write(o)
   elseif type(o) == "string" then
      io.write(string.format("%q", o))
   elseif type(o) == "boolean" then
      io.write( o and "true" or "false" )
   elseif type(o) == "table" then
      io.write("{\n")
      local subindent = indent .. "   "
      for k,v in pairsByKeys(o) do
         io.write(subindent)
         io.write("  [")
         serialize_sorted(k, subindent)
         io.write("] = ")
         serialize_sorted(v, subindent)
         io.write(",\n")
      end
      io.write(indent .. "}")
   else
      error("cannot serialize a " .. type(o))
   end
end



-- from http://www.lua.org/pil/19.3.html
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



testTable = {
    ["2"] = {
        ["Val1"] = true,
        ["ValX"] = true,
        ["Val2"] = true,
        ["Val3"] = false,
    },
    ["1"] = {
        ["ValX"] = true,
        ["Val1"] = true,
        ["Val2"] = true,
        ["Val3"] = false,
    },
    ["X"] = {
        ["Val3"] = false,
        ["ValX"] = true,
        ["Val1"] = true,
        ["Val2"] = true,
    },
}

io.write("SavedVars = ")
serialize_sorted(testTable, "")
