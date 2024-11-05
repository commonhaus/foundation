local alerts = pandoc.List({'note', 'tip', 'important', 'warning', 'caution'})

if FORMAT:match 'latex' then
    function Div(d)
        local class = string.lower(d.classes[1])
        if alerts:includes(class) then
            local alert,alertidx = alerts:find(class)
            local alertText = pandoc.utils.stringify(d.content[1])
            d.content[1] = pandoc.RawInline('tex', '\\begin{' .. class .. 'block}')
            d.content[#d.content + 1] = pandoc.RawInline('latex', '\\end{' .. class .. 'block}')
            return d
        end
    end
end