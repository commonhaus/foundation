# Copyright notices for project website footers

For those building or managing a website for a Commonhaus Foundation project, please incorporate one of the following standard footers.

The legal content and links are the important part. If you want to integrate the footer to your website's design (e.g. light and dark mode, different HTML or CSS elements), you may.  See the [artwork repository](https://github.com/commonhaus/artwork/tree/main/foundation/brand) for logo variations.

## HTML

```html
<p><img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30"/><br />Copyright © <a href="https://www.commonhaus.org">Commonhaus Foundation</a>. All rights reserved. For details on our trademarks, please visit our <a href="https://www.commonhaus.org/policies/trademark-policy/">Trademark Policy</a> and <a href="https://www.commonhaus.org/trademarks/">Trademark List</a>. Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.</p>
```
<!-- markdownlint-disable MD033 -->
<p><img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30" alt=""/><br />Copyright © <a href="https://www.commonhaus.org">Commonhaus Foundation</a>. All rights reserved. For details on our trademarks, please visit our <a href="https://www.commonhaus.org/policies/trademark-policy/">Trademark Policy</a> and <a href="https://www.commonhaus.org/trademarks/">Trademark List</a>. Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.</p>

## Markdown

```markdown
<img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30" alt=""/><br />
Copyright © [Commonhaus Foundation](https://www.commonhaus.org). All rights reserved. For details on our trademarks, please visit our [Trademark Policy](https://www.commonhaus.org/policies/trademark-policy/) and [Trademark List](https://www.commonhaus.org/trademarks/). Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.
```

<!-- markdownlint-disable MD033 -->
<img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30" alt=""/><br />
Copyright © [Commonhaus Foundation](https://www.commonhaus.org). All rights reserved. For details on our trademarks, please visit our [Trademark Policy](https://www.commonhaus.org/policies/trademark-policy/) and [Trademark List](https://www.commonhaus.org/trademarks/). Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.

## AsciiDoc

```asciidoc
image::https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg[height=30]

Copyright © link:https://www.commonhaus.org[Commonhaus Foundation]. All rights reserved. For details on our trademarks, please visit our link:https://www.commonhaus.org/policies/trademark-policy/[Trademark Policy] and link:https://www.commonhaus.org/trademarks/[Trademark List]. Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.
```
