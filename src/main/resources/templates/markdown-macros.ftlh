<#--
    Markdown rendering of the report tree, embedded per container for clipboard copy.
    The formatting rules are documented in docs/arete_mdcopy.md.
    State signs follow arete's ResultState.sign, rendered in backticks as inline code:
    `[x]` failed, `[*]` aborted, `[/]` ignored, `[#]` hidden, `[+]` successful.
-->

<#function stateColor state>
    <#-- the emph variant is the theme's accent for the state and reads well as text color -->
    <#if "${state}" == "FAILED">
        <#return colorScheme.arete_color_failed_emph>
    <#elseif "${state}" == "ABORTED">
        <#return colorScheme.arete_color_aborted_emph>
    <#elseif "${state}" == "IGNORED">
        <#return colorScheme.arete_color_ignored_emph>
    <#elseif "${state}" == "HIDDEN">
        <#return colorScheme.arete_color_neutral_emph>
    <#else>
        <#return colorScheme.arete_color_successful_emph>
    </#if>
</#function>

<#macro stateSign state><span style="color: ${stateColor(state)}">${state.sign}</span></#macro>

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

<#macro markdownHistoryList entries labelIndent>
${labelIndent}- History:
    <#list entries as entry>
${labelIndent}    - <@stateSign entry.state/> ${entry.timeStampLong}
        <#if entry.errorMsg?has_content>
            <@fencedBlock label="Message" indent="${labelIndent}        " text=entry.errorMsg/>
        </#if>
        <#if entry.stackTrace?has_content>
            <@fencedBlock label="Stack trace" indent="${labelIndent}        " text=entry.stackTrace trim=true/>
        </#if>
        <#if entry.stdout?has_content>
            <@fencedBlock label="Stdout" indent="${labelIndent}        " text=entry.stdout/>
        </#if>
        <#if entry.stderr?has_content>
            <@fencedBlock label="Stderr" indent="${labelIndent}        " text=entry.stderr/>
        </#if>
    </#list>
</#macro>

<#macro markdownLeaf step>
- <@stateSign step.resultState/> ${step.displayName}
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
        <@markdownHistoryList entries=step.history labelIndent="    "/>
    </#if>
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
        <#list step.steps as child>
            <@markdownStep step=child depth=depth + 1/>
        </#list>
    <#else>
        <@markdownLeaf step=step/>
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
- <@stateSign node.state/> ${node.displayName}
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
            <@markdownHistoryList entries=node.entries labelIndent="    "/>
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
