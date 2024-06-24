# Copyright notices for project website footers

For those building or managing a website for a Commonhaus Foundation project, you must include mention of the Commonhaus Foundation in your website footers. Some examples are provided below (replace the links to YOUR PROJECT to point to the full URL of the main landing page for your project's website).

The Trademark text and links are the important part. If you want to integrate the footer to your website's design (e.g. light and dark mode, different HTML or CSS elements), you may.  See the [artwork repository](https://github.com/commonhaus/artwork/tree/main/foundation/brand) for logo variations.

## HTML

```html
<p><img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30"/><br />Copyright © <a href="https://www.commonhaus.org">YOUR PROJECT</a>. All rights reserved. For details on our trademarks, please visit our <a href="https://www.commonhaus.org/policies/trademark-policy/">Trademark Policy</a> and <a href="https://www.commonhaus.org/trademarks/">Trademark List</a>. Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.</p>
```
<!-- markdownlint-disable MD033 -->
<p><img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30" alt=""/><br />Copyright © <a href="https://www.commonhaus.org">YOUR PROJECT</a>. All rights reserved. For details on our trademarks, please visit our <a href="https://www.commonhaus.org/policies/trademark-policy/">Trademark Policy</a> and <a href="https://www.commonhaus.org/trademarks/">Trademark List</a>. Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.</p>

## Markdown

```markdown
<img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30" alt=""/><br />
Copyright © [YOUR PROJECT](https://www.commonhaus.org). All rights reserved. For details on our trademarks, please visit our [Trademark Policy](https://www.commonhaus.org/policies/trademark-policy/) and [Trademark List](https://www.commonhaus.org/trademarks/). Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.
```

<!-- markdownlint-disable MD033 -->
<img src="https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg" height="30" alt=""/><br />
Copyright © [YOUR PROJECT](https://www.commonhaus.org). All rights reserved. For details on our trademarks, please visit our [Trademark Policy](https://www.commonhaus.org/policies/trademark-policy/) and [Trademark List](https://www.commonhaus.org/trademarks/). Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.

## AsciiDoc

```asciidoc
image::https://raw.githubusercontent.com/commonhaus/artwork/main/foundation/brand/svg/CF_logo_horizontal_single_default.svg[height=30]

Copyright © link:https://www.commonhaus.org[YOUR PROJECT]. All rights reserved. For details on our trademarks, please visit our link:https://www.commonhaus.org/policies/trademark-policy/[Trademark Policy] and link:https://www.commonhaus.org/trademarks/[Trademark List]. Trademarks of third parties are owned by their respective holders and their mention here does not suggest any endorsement or association.
```
