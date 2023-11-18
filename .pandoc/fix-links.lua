function Link(el)
    if string.match(el.target, "http") then
        return el
    end

    -- Access the variables
    local githubUrl = tostring(PANDOC_WRITER_OPTIONS.variables["github"] or "")

    -- Check if the link is a relative URL...
    if string.match(el.target, "^%.%./") then
        -- Modify the link target
        local modifiedTarget, _ = string.gsub(el.target, "^%.%./", githubUrl)
        print("CFLUA: Modified link target: " .. modifiedTarget)
        el.target = modifiedTarget
    end
    return el
end