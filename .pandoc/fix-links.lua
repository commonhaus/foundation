local mailing_list

local function Meta(m)
    mailing_list = m['mailing-list']
    return m
end

local function replace_mailing_list(el)
    local content = pandoc.utils.stringify(el.content)
    if not mailing_list then
        error("CFLUA: No mailing list metadata found")
    end
    if content == 'CONTACTS.yaml' then
        return el
    end

    local mailing_list_name = string.match(content, "(.-) mailing list")
    if not mailing_list_name then
        warn("CFLUA: No mailing list found in " .. content)
        return el
    end

    local mailing_list_value = mailing_list[mailing_list_name]
    if not mailing_list_value then
        warn("CFLUA: No mailing list value found for " .. mailing_list_name)
        return el
    end

    local address = pandoc.utils.stringify(mailing_list_value)
    local mailto_link = pandoc.Link(address .. " mailing list", "mailto:" .. address)
    print("CFLUA: Replaced '" .. content .. "' with " .. pandoc.utils.stringify(mailto_link))
    el.content = {mailto_link}
    return el
end

local function Link(el)
    if string.match(el.target, "CONTACTS.yaml") then
        el = replace_mailing_list(el)
    end

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

--------------------------------------------------------------------------------
-- The main function --
--------------------------------------------------------------------------------
return {
    { Meta = Meta },
    { Link = Link }
  }