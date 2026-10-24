<#-- @ftlvariable name="overview" type="com.github.mictaege.arete_gradle.HistoryOverview" -->
<#import "history-macros.ftlh" as history>
<html>
<head>
    <title>History</title>
    <#include "arete-css.ftlh">
    <#include "prism-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./index.html"><i class="menu-icon-home"></i> Overview</a>
        <a href="./display_names.html"><i class="menu-icon-handshake"></i> Specifications</a>
        <a href="./test_specs.html"><i class="menu-icon-flask"></i> Spec Classes</a>
        <a href="./tags.html"><i class="menu-icon-tags"></i> Tags</a>
        <a class="active" href="./history.html"><i class="menu-icon-weather"></i> History</a>
    </div>
    <div class="card main">
        <#if !historyEnabled>
            <p>History is unavailable because history recording is disabled for this test task.</p>
        <#elseif !overview.specs?has_content>
            <p>No failed or aborted steps are available in the history store.</p>
        <#else>
            <#function stateClass state>
                <#if state == "IGNORED">
                    <#return "ignored">
                <#elseif state == "SUCCESSFUL">
                    <#return "successful">
                <#elseif state == "ABORTED">
                    <#return "aborted">
                <#elseif state == "FAILED">
                    <#return "failed">
                </#if>
                <#return "neutral">
            </#function>

            <#macro anchor hash displayName spec>
                <#if spec.reportAvailable>
                    <#assign target="specs/${spec.hash}.html#${hash}">
                <#else>
                    <#assign target="history.html#${hash}">
                </#if>
                <a id="${hash}">
                    <button onclick="anchorToClipboard('${displayName?js_string}', '${target?js_string}');" class="link-copy-btn"></button>
                </a>
            </#macro>

            <#macro historyDisplay node specHash>
                <details class="history" ontoggle="if (this.open) { this.scrollIntoView({behavior: 'smooth', block: 'nearest'}); }">
                    <summary title="History">
                        <i class="tagged-icon card-tagged-clock history-icon"></i>
                        <#list node.entries as entry>
                            <span class="history-dot history-dot-${entry.state}" title="${entry.timeStampLong}: ${entry.state}"></span>
                        </#list>
                    </summary>
                    <div class="history-runs">
                        <#list node.entries as entry>
                            <@history.historyRun
                                id="${specHash}_${node.hash}_${entry.runId}"
                                state="${entry.state}"
                                timeStamp=entry.timeStampLong
                                errorMsg=entry.errorMsg
                                stackTrace=entry.stackTrace
                                stdout=entry.stdout
                                stderr=entry.stderr
                                screenshotFileName=entry.screenshotFileName!""
                                screenshotPrefix="specs/"/>
                        </#list>
                    </div>
                </details>
            </#macro>

            <#macro historyNode node spec specHash level>
                <div style="margin-left: ${level * 20}px">
                    <#if node.container>
                        <div class="${stateClass("${node.state}")}-no-hover step" style="font-weight: bold">
                            <div class="step-display">
                                ${node.containerDisplayName}
                                <@anchor hash=node.hash displayName=node.displayName spec=spec/>
                            </div>
                        </div>
                        <#list node.children as child>
                            <@historyNode node=child spec=spec specHash=specHash level=level + 1/>
                        </#list>
                    <#else>
                        <div class="${stateClass("${node.state}")}-no-hover step">
                            <div class="step-display">${node.displayName}</div>
                        </div>
                        <@historyDisplay node=node specHash=specHash/>
                    </#if>
                </div>
            </#macro>

            <#list overview.specs as spec>
                <section>
                    <div class="${stateClass("${spec.state}")}-no-hover step" style="font-weight: bold">
                        <div class="step-display">
                            <#if spec.reportAvailable>
                                <a href="./specs/${spec.hash}.html">${spec.displayName}</a>
                            <#else>
                                ${spec.displayName}
                            </#if>
                            <@anchor hash=spec.hash displayName=spec.displayName spec=spec/>
                        </div>
                    </div>
                    <#list spec.nodes as node>
                        <@historyNode node=node spec=spec specHash=spec.hash level=1/>
                    </#list>
                </section>
            </#list>
        </#if>
    </div>

    <script>
        function traceToClipboard(elementId) {
            let copyText = document.getElementById(elementId);
            copyText.select();
            copyText.setSelectionRange(0, 99999);
            navigator.clipboard.writeText(copyText.value);
        }
        function anchorToClipboard(text, target) {
            let link = new URL(target, window.location.href).href;
            let markdownLink = '[' + text + '](' + link + ')';
            navigator.clipboard.writeText(markdownLink);
        }
    </script>
    <#include "prism-js.ftlh">
</body>
</html>