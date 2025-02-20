-- Horizontal separator in Word DOCX
local horizontalSeparator = [[<w:p>
      <w:pPr>
        <w:pBdr></w:pBdr>
        <w:spacing />
        <w:ind />
        <w:rPr></w:rPr>
      </w:pPr>
      <w:r>
        <mc:AlternateContent>
          <mc:Choice Requires="wps">
            <w:drawing>
              <wp:inline distT="0" distB="0" distL="0" distR="0">
                <wp:extent cx="5400040" cy="635" />
                <wp:effectExtent l="3175" t="3175" r="3175" b="3175" />
                <wp:docPr id="1" name="Shape1"></wp:docPr>
                <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
                  <a:graphicData
                    uri="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
                    <wps:wsp>
                      <wps:cNvSpPr />
                      <wps:spPr>
                        <a:xfrm>
                          <a:off x="0" y="0" />
                          <a:ext cx="5400000" cy="720" />
                        </a:xfrm>
                        <a:prstGeom prst="line">
                          <a:avLst />
                        </a:prstGeom>
                        <a:ln w="12699">
                          <a:solidFill>
                            <a:srgbClr val="a6a6a6" />
                          </a:solidFill>
                        </a:ln>
                      </wps:spPr>
                      <wps:style>
                        <a:lnRef idx="1">
                          <a:schemeClr val="accent1">
                            <a:shade val="50000" />
                          </a:schemeClr>
                        </a:lnRef>
                        <a:fillRef idx="0">
                          <a:schemeClr val="accent1" />
                        </a:fillRef>
                        <a:effectRef idx="0">
                          <a:schemeClr val="accent1" />
                        </a:effectRef>
                        <a:fontRef idx="minor" />
                      </wps:style>
                      <wps:bodyPr />
                    </wps:wsp>
                  </a:graphicData>
                </a:graphic>
              </wp:inline>
            </w:drawing>
          </mc:Choice>
          <mc:Fallback>
            <w:pict>
              <v:line id="shape_0" from="0pt,11.5pt" to="425.15pt,11.5pt" ID="Shape1" stroked="t"
                o:allowincell="f" style="position:absolute">
                <v:stroke color="#a6a6a6" weight="12600" joinstyle="miter" endcap="flat" />
                <v:fill o:detectmouseclick="t" on="false" />
                <w10:wrap type="square" />
              </v:line>
            </w:pict>
          </mc:Fallback>
        </mc:AlternateContent>
      </w:r>
      <w:r></w:r>
    </w:p>]]

local pagebreak = '<w:p><w:r><w:br w:type="page"/></w:r></w:p>'

local function HorizontalRule(elem)
  print("CFLUA: Found horizontal rule ")
  return pandoc.RawBlock('openxml', horizontalSeparator)
end

local function Header(header)
  if header.level == 1 then
    local content = pandoc.utils.stringify(header.content)
    content = content:gsub("Commonhaus Foundation%s*(.*)", "%1")
    return pandoc.Header(1, content)
  elseif header.level == 2 then
    local content = pandoc.utils.stringify(header.content)
    if string.match(content, 'Exhibits') or string.match(content, 'Schedules') or string.match(content, 'Recognition') then
      print("CFLUA: Found L2 header " .. content)
      return { pandoc.RawBlock('openxml', pagebreak), header }
    end
  end
  return header
end

local function Para(para)
  local content = pandoc.utils.stringify(para.content)
  if string.match(content, "%[Insert.*]") then
    content = content:gsub("(.*)%[Insert.*](.*)", "%1`______________________________`%2")
    --print("CFLUA: Found insert " .. content)
    return pandoc.read(content, 'markdown').blocks
  end
  if FORMAT:match 'docx' and string.match(content, "By: ") then
    print("CFLUA: Found insert item " .. tostring(para))
    return pandoc.Div(para, pandoc.Attr("",{},{{'custom-style','List Paragraph'}}))
  end
  return para
end

local function BulletList(list)
  local content = pandoc.utils.stringify(list.content)
  if string.match(content, "%[Insert.*]") then
    for i, item in ipairs(list.content) do
      local itemContent = pandoc.utils.stringify(item[1].content)
      itemContent = itemContent:gsub("(.*)%[Insert.*](.*)", "%1`______________________________`%2")
      print("CFLUA: Found insert " .. itemContent)
      list.content[i][1] = pandoc.Plain(itemContent)
    end
  end

  return list
end

return {
  { HorizontalRule = HorizontalRule },
  { Header = Header },
  { BulletList = BulletList },
  { Para = Para },
}