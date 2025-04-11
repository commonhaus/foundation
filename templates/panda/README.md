## Policy Panda

You run it like this: `jbang policypanda@commonhaus/foundation hibernate` where `hibernate` is the GitHub organization name.

NOTE: The script requires GitHub credentials in the form of either environment variables (see [Environmental Variables](https://hub4j.github.io/github-api/#Environmental_variables)[^wayback][^src]) or a `~/.github` properties file (see [Property File](https://hub4j.github.io/github-api/#Property_file)[^wayback][^src]).

If you want it to only check specific repositories, you can use the `-r` to specify a regular expression.

Example:

```shell
# Create a policy-report (markdown):
jbang policypanda@commonhaus/foundation hibernate -r "hibernate-orm|hibernate-validator"

# Create a yaml file describing GitHub organization (-a)
# --skip-policy-check (-s) disables the default policy check
jbang policypanda@commonhaus/foundation hibernate --skip-policy-check --init-asset-discovery

# Create a yaml file describing organization and repository settings (-g)
# --skip-policy-check (-s) disables the default policy check
jbang policypanda@commonhaus/foundation hibernate --skip-policy-check --init-org-settings
```

OR

```shell
cd templates/panda
jbang PolicyPanda.java hibernate
```

would only check the repositories that match `hibernate-orm` or `hibernate-validator`.

[^wayback]: https://web.archive.org/web/20250108173712/https://github-api.kohsuke.org/
[^src]: https://github.com/hub4j/github-api/blob/main/src/site/apt/index.apt
