# See-Also Rendering Specification

## Purpose

How `@SeeAlso` links between specifications/parts are rendered on spec pages. Because targets may
be written before their page exists, rendering uses a two-pass placeholder mechanism.

## Requirements

### Requirement: Placeholders during spec rendering

`spec.ftlh` SHALL, for every step with `@SeeAlso`/`@SeeAlsoDeclaration` (`hasSeeAlsoRefs`), emit an
HTML comment placeholder `<!--%%%%%<targetClassName>%%%%%-->` per reference (via `<#noautoesc>`),
instead of the final link.

#### Scenario: Placeholder present after writeSpec
- **WHEN** a spec page is written for a step with `@SeeAlso(OtherSpec.class)`
- **THEN** the HTML contains `<!--%%%%%com.example.OtherSpec%%%%%-->`

### Requirement: Placeholder replacement in finishPlan

`HtmlWriter.finishPlan` SHALL replace each placeholder with
`<a href="<targetUrl>" class="see-also"><i class="see-also-icon"></i> <displayName></a>` — but only
when the target is a known step in the plan (`validTarget`, via `findStepByType`). `targetUrl` SHALL be
`./<specHash>.html` when the target is a spec, otherwise `./<specHash>.html#<targetHash>` (spec page
plus anchor of the nested part). Unresolvable targets SHALL leave no link.

#### Scenario: Link to a nested scenario
- **WHEN** `@SeeAlso(CalculatorSpec.Addition.ShouldAddTwoNumbers.class)` targets a scenario
- **THEN** the spec page contains a see-also link to `./specs/<specHash>.html#<scenarioHash>` with the
  target's display name
