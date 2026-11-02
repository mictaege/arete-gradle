# Arete References (`@Ref`)

References link specifications and their nested parts (features, descriptions, scenarios, journeys)
to external systems — e.g. issues, requirements or user stories in a tracking system — for
traceability. The core annotation lives in the `arete` library; this plugin renders the references
as links on the generated specification pages.

## Core API (`arete`)

`com.github.mictaege.arete.Ref` — repeatable, runtime-retained, applicable to types and annotation
types:

```java
@Ref(value = "PROJ-123", url = "https://tracker.example.com/PROJ-{0}")
```

- `value` — the reference id, e.g. `"PROJ-123"` or an issue number.
- `url` — optional URL **pattern**. A `{0}` placeholder in it is replaced by the `value`; without a
  placeholder the `url` is used as a **prefix** and the `value` is appended. Without a `url`, the
  reference is rendered as plain text (no link).

The repeatable container is `com.github.mictaege.arete.Refs`.

## Composed annotations

Because `@Ref` can annotate annotation types, domain-specific reference annotations can be composed
— the same pattern as tags with `@StereoType`. The composed annotation carries its own `value()` and
is meta-annotated with `@Ref(url = ...)`:

```java
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(GithubRefs.class)                 // own container, so it stays repeatable
@Ref(url = "https://github.com/mictaege/arete-gradle/issues/{0}")
public @interface GithubRef {
    String value();
}

@Target(TYPE)
@Retention(RUNTIME)
public @interface GithubRefs {
    GithubRef[] value();
}
```

Usage: `@GithubRef("14")` refers to `https://github.com/mictaege/arete-gradle/issues/14`.
A complete example is `GithubRef`/`GithubRefs` in
[arete-gradle-test](https://github.com/mictaege/arete-gradle-test/tree/master/src/test/java/com/github/mictaege/travel_agency),
applied to `BookingProcessSpec`.

## Report rendering

On each specification page (`specs/<hash>.html`), the references declared directly on a step are
rendered directly below the see-also block (`spec.ftlh`, `<#if step.hasRefLinks>`) as real HTML —
no placeholder pass:

```html
<a href="https://github.com/mictaege/arete-gradle/issues/14" target="_blank" class="see-also">
    <i class="see-also-icon"></i> 14
</a>
```

The `see-also` styling and the `see-also-icon` (external-link icon) are reused, so reference links
look like see-also links. When a reference has no `url`, the anchor is rendered without `href`.

## Plugin internals (`arete-gradle`)

- `Refs.kt` — class `Refs` reads the references of a step from `TestIdentifier.sourceAnnotations`
  (test templates are excluded) into `Refs.links: List<RefLink>`. Expansion rules:
  - `@Ref` → link with `value` and resolved `url`
  - `@Refs` (imported as `RefsContainer` to avoid shadowing by the local `Refs` class) → one link
    per element
  - an annotation meta-annotated with `@Ref` (e.g. `@GithubRef`) → `value` read from the
    annotation's own `value()` attribute, `url` = meta `@Ref.url` pattern resolved with that value
  - a container annotation whose `value()` is an array of annotations (e.g. `@GithubRefs`) → each
    element is expanded recursively
  - URL resolution: a `{0}` placeholder in `url` is replaced by the `value`; without a placeholder
    the `url` is used as a prefix and the `value` is appended
- `SpecificationPlan.kt` — `SpecificationStep.refs` (`Refs`), `.refLinks` (`List<RefLink>`),
  `.hasRefLinks`
- `spec.ftlh` — the rendering block below `step.hasSeeAlsoRefs`, iterating `step.refLinks`

Currently each step renders only the references declared **directly** on it; inheritance from
containers (spec → feature → scenario) and aggregation are not implemented.

## Not implemented (yet)

- Link patterns configured in the Gradle DSL (e.g. `arete { referenceLinkPatterns.put("PROJ", "...") }`)
  to derive URLs from values without annotating a `url`
- A references overview page in the report (reference → covering specs/scenarios matrix)
- Inheritance/aggregation of references from containers to nested steps
