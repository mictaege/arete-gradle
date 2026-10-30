<#--
    Markdown rendering of the report tree, embedded per container for clipboard copy.
    The formatting rules are documented in docs/arete_mdcopy.md.
    State signs follow arete's ResultState.sign, rendered in backticks as inline code:
    `[x]` failed, `[*]` aborted, `[/]` ignored, `[#]` hidden, `[+]` successful.
-->

<#macro fencedBlock label indent text trim=false>
${indent}- ${label}:
${indent}  ```
    <#list text?split("\n") as contentLine>
        <#if trim>
            <#local renderedLine = contentLine?trim>
        <#else>
            <#local renderedLine = contentLine>
        </#if>
${indent}  ${renderedLine}
    </#list>
${indent}  ```
</#macro>

<#macro markdownStep step depth=1>
    <#if step.resultState.hidden>
        <#return/>
    </#if>
    <#if step.type.container>
        <#local headingDepth = (depth > 6)?then(6, depth)>

<#list 1..headingDepth as i>#</#list> ${step.displayName}
        <#if step.hasNarrative>

> <#noautoesc>${step.narrative.header}</#noautoesc>
            <#list step.narrative.lines as narrativeLine>
                <#list narrativeLine?split("\n") as line>
                    <#if line?has_content>
> <#noautoesc>${line}</#noautoesc>
                    <#else>
>
                    </#if>
                </#list>
            </#list>
        </#if>
        <#if "${step.type}" == "EXAMPLE_GRID_CONTAINER">

| ${step.gridColumns?join(" | ")} |
| ${step.gridColumns?map(col -> "---")?join(" | ")} |
            <#list step.steps as child>
                <#if !child.resultState.hidden>
| ${child.gridRowData?join(" | ")} |
                </#if>
            </#list>
        <#else>
            <#list step.steps as child>
                <@markdownStep step=child depth=depth + 1/>
            </#list>
        </#if>
    <#else>
- `${step.resultState.sign}` ${step.displayName}
        <#if step.hasErrorMsg>
            <@fencedBlock label="Message" indent="    " text=step.errorMsg/>
        </#if>
        <#if step.stackTrace?has_content>
            <@fencedBlock label="Stack trace" indent="    " text=step.stackTrace trim=true/>
        </#if>
        <#if step.hasStdout>
            <@fencedBlock label="Stdout" indent="    " text=step.stdout/>
        </#if>
        <#if step.hasStderr>
            <@fencedBlock label="Stderr" indent="    " text=step.stderr/>
        </#if>
        <#if step.showHistory>
    - History:
            <#list step.history as historyEntry>
        - `${historyEntry.state.sign}` ${historyEntry.timeStampLong}
                <#if historyEntry.errorMsg?has_content>
                    <@fencedBlock label="Message" indent="            " text=historyEntry.errorMsg/>
                </#if>
                <#if historyEntry.stackTrace?has_content>
                    <@fencedBlock label="Stack trace" indent="            " text=historyEntry.stackTrace trim=true/>
                </#if>
                <#if historyEntry.stdout?has_content>
                    <@fencedBlock label="Stdout" indent="            " text=historyEntry.stdout/>
                </#if>
                <#if historyEntry.stderr?has_content>
                    <@fencedBlock label="Stderr" indent="            " text=historyEntry.stderr/>
                </#if>
            </#list>
        </#if>
    </#if>
</#macro>

<#macro markdownNode node depth=2>
    <#if node.container>
        <#local headingDepth = (depth > 6)?then(6, depth)>

<#list 1..headingDepth as i>#</#list> ${node.containerDisplayName}
        <#list node.children as child>
            <@markdownNode node=child depth=depth + 1/>
        </#list>
    <#else>
- `${node.state.sign}` ${node.displayName}
        <#if node.entries?has_content>
            <#local newest = node.entries?first>
            <#if newest.errorMsg?has_content>
                <@fencedBlock label="Message" indent="    " text=newest.errorMsg/>
            </#if>
            <#if newest.stackTrace?has_content>
                <@fencedBlock label="Stack trace" indent="    " text=newest.stackTrace trim=true/>
            </#if>
            <#if newest.stdout?has_content>
                <@fencedBlock label="Stdout" indent="    " text=newest.stdout/>
            </#if>
            <#if newest.stderr?has_content>
                <@fencedBlock label="Stderr" indent="    " text=newest.stderr/>
            </#if>
    - History:
            <#list node.entries as entry>
        - `${entry.state.sign}` ${entry.timeStampLong}
                <#if entry.errorMsg?has_content>
                    <@fencedBlock label="Message" indent="            " text=entry.errorMsg/>
                </#if>
                <#if entry.stackTrace?has_content>
                    <@fencedBlock label="Stack trace" indent="            " text=entry.stackTrace trim=true/>
                </#if>
                <#if entry.stdout?has_content>
                    <@fencedBlock label="Stdout" indent="            " text=entry.stdout/>
                </#if>
                <#if entry.stderr?has_content>
                    <@fencedBlock label="Stderr" indent="            " text=entry.stderr/>
                </#if>
            </#list>
        </#if>
    </#if>
</#macro>

<#macro markdownOverviewSpec spec>

# ${spec.displayName}
    <#list spec.nodes as node>
        <@markdownNode node=node depth=2/>
    </#list>
</#macro>

<#macro markdownCopyButton id iconClass>
    <textarea type="text" id="${id}" style="display: none;"><#nested></textarea>
    <button onclick="traceToClipboard('${id}');" class="markdown-copy-btn" title="Copy as markdown"><i class="${iconClass}"></i></button>
</#macro>
