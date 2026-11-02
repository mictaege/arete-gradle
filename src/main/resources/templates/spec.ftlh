<#-- @ftlvariable name="step" type="com.github.mictaege.arete_gradle.SpecificationStep" -->
<#import "history-macros.ftlh" as history>
<#import "markdown-macros.ftlh" as md>
<html>
<head>
    <title>${step.displayName}</title>
    <#include "arete-css.ftlh">
    <#include "prism-css.ftlh">
    <#include "image-grid-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./../index.html"><i class="spec-menu-icon-home"></i> Overview</a>
        <a href="./../display_names.html"><i class="spec-menu-icon-handshake"></i> Specifications</a>
        <a href="./../test_specs.html"><i class="spec-menu-icon-flask"></i> Spec Classes</a>
        <a href="./../tags.html"><i class="spec-menu-icon-tags"></i> Tags</a>
        <a href="./../history.html"><i class="spec-menu-icon-weather"></i> History</a>
    </div>
    <div class="card main-step">
        <#macro stepDisplay step>
            <#if "${step.type}" == "EXAMPLE_GRID_INSTANCE">
                <table>
                    <tr>
                        <#list step.gridRowData as data>
                            <td>${data}</td>
                        </#list>
                    </tr>
                </table>
            <#else>
                <span>${step.displayName}</span>
            </#if>
        </#macro>

        <#macro stamp step>
            <#if "${step.type}" == "SPEC">
                <div class="stamp">
                    <span>${step.testClassName}</span>
                    <span>${step.timeStampLong}</span>
                </div>
            </#if>
        </#macro>

        <#macro historyDisplay step>
            <#if step.showHistory>
                <@history.historyStrip iconClass="tagged-clock"; area>
                    <#if area == "summary">
                        <span class="history-dot history-dot-${step.cumulatedState()}" title="Current run - ${step.historyTimeStamp}: ${step.cumulatedState()}"></span>
                        <#list step.history as historyEntry>
                            <span class="history-dot history-dot-${historyEntry.state}" title="${historyEntry.timeStampLong}: ${historyEntry.state}"></span>
                        </#list>
                    <#else>
                        <#assign currentState = "${step.cumulatedState()}">
                        <#local currentRunHasDetails = step.cumulatedState().failure>
                        <@history.historyRun id="${step.uniqueHash}HistoryCurrent" state=currentState timeStamp=step.historyTimeStamp
                            errorMsg=currentRunHasDetails?then(step.errorMsg, "")
                            stackTrace=currentRunHasDetails?then(step.stackTrace, "")
                            stdout=currentRunHasDetails?then(step.stdout, "")
                            stderr=currentRunHasDetails?then(step.stderr, "")
                            screenshotFileName=(currentRunHasDetails && step.hasScreenshot)?then("${step.uniqueHash}.png", "")/>
                        <#list step.history as historyEntry>
                            <@history.historyRun id="${step.uniqueHash}History${historyEntry.runId}" state="${historyEntry.state}" timeStamp=historyEntry.timeStampLong
                                errorMsg=historyEntry.errorMsg
                                stackTrace=historyEntry.stackTrace stdout=historyEntry.stdout stderr=historyEntry.stderr
                                screenshotFileName=historyEntry.screenshotFileName!""/>
                        </#list>
                    </#if>
                </@history.historyStrip>
            </#if>
        </#macro>

        <#macro anchor step>
            <#if step.type.container>
                <a id="${step.uniqueHash}">
                    <@history.copyLinkButton text=step.displayName target="#${step.uniqueHash}"/>
                </a>
                <@md.markdownCopyButton id="${step.uniqueHash}Markdown" iconClass="spec-markdown-icon">
                    <@md.markdownStep step=step/>
                </@md.markdownCopyButton>
            </#if>
        </#macro>

        <#macro pageMenu step>
            <#if step.type.container>
                <ul>
                    <#list step.steps as childStep>
                        <#if childStep.resultState.hidden>
                            <#continue/>
                        </#if>

                        <#if childStep.type.container>
                            <li style="width: 100%">
                                <div class="${history.stateClass(childStep.cumulatedState())}">
                                    <a href="#${childStep.uniqueHash}">${childStep.displayName}</a>
                                </div>
                                <@pageMenu childStep/>
                            </li>
                        </#if>
                    </#list>
                </ul>
            </#if>
        </#macro>

        <#macro failureDetails step>
            <textarea type="text" id="${step.uniqueHash}StackTrace" style="display: none;">${step.stackTrace}</textarea>
<pre><button onclick="traceToClipboard('${step.uniqueHash}StackTrace');" class="stacktrace-copy-btn"></button><code class="language-javastacktrace">${step.stackTrace}</code></pre>
            <#if step.hasStdout>
                <textarea type="text" id="${step.uniqueHash}Stdout" style="display: none;">${step.stdout}</textarea>
<pre><button onclick="traceToClipboard('${step.uniqueHash}Stdout');" class="stacktrace-copy-btn"></button><code class="language-text">Stdout:
${step.stdout}</code></pre>
            </#if>
            <#if step.hasStderr>
                <span>Stderr:</span>
                <textarea type="text" id="${step.uniqueHash}Stderr" style="display: none;">${step.stderr}</textarea>
<pre><button onclick="traceToClipboard('${step.uniqueHash}Stderr');" class="stacktrace-copy-btn"></button><code class="language-text">Stderr:
${step.stderr}</code></pre>
            </#if>
        </#macro>

        <#macro dumpSteps step parent=step>
            <#if step.resultState.hidden>
                <#return/>
            </#if>
            <#assign noHoverSuffix = "-no-hover">
            <#assign collapsible = "">
            <#if step.type.container>
                <#-- containers are collapsible outline nodes, so they signal interactivity by hover -->
                <#assign noHoverSuffix = "">
            <#elseif step.hasScreenshot || step.hasErrorMsg>
                <#assign collapsible = "collapsible">
                <#assign noHoverSuffix = "">
            </#if>
            <#assign fWeight = "normal">
            <#assign headerClass = "">
            <#if step.type.container>
                <#assign fWeight = "bold">
                <#assign headerClass = " outline-header">
            </#if>
            <div class="outline-item<#if step.type.container> container-step</#if>">

                <#if parent.isFirst(step) && parent.type == "EXAMPLE_GRID_CONTAINER">
                    <div class="neutral-no-hover step" style="margin-bottom: 2px; margin-top: 2px; font-weight: normal">
                        <div class="step-display">
                            <table>
                                <tr>
                                    <#list parent.gridColumns as column>
                                        <th>${column}</th>
                                    </#list>
                                </tr>
                            </table>
                        </div>
                    </div>
                </#if>

                <#if "${step.resultState}" == "IGNORED">
                    <div class="ignored${noHoverSuffix} step ${collapsible}${headerClass}" style="font-weight: ${fWeight}">
                        <div class="step-display">
                            <@stepDisplay step/>
                            <#if step.hasScreenshot>
                                <i class="spec-icon-camera"></i>
                            </#if>
                            <@anchor step/>
                            <@stamp step/>
                        </div>
                    </div>
                    <#if !step.type.container && step.hasScreenshot>
                        <div class="overflow-content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                <#elseif "${step.resultState}" == "SUCCESSFUL">
                    <div class="successful${noHoverSuffix} step ${collapsible}${headerClass}" style="font-weight: ${fWeight}">
                        <div class="step-display">
                            <@stepDisplay step/>
                            <#if step.hasScreenshot>
                                <i class="spec-icon-camera"></i>
                            </#if>
                            <@anchor step/>
                            <@stamp step/>
                        </div>
                    </div>
                    <#if !step.type.container && step.hasScreenshot>
                        <div class="overflow-content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                <#elseif "${step.resultState}" == "ABORTED">
                    <div class="aborted${noHoverSuffix} step ${collapsible}${headerClass}" style="font-weight: ${fWeight}">
                        <div class="step-display">
                            <@stepDisplay step/>
                            <#if step.hasScreenshot>
                                <i class="spec-icon-camera"></i>
                            </#if>
                            <@anchor step/>
                            <@stamp step/>
                        </div>
                    </div>
                    <#if !step.type.container && step.hasScreenshot>
                        <div class="overflow-content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                <#elseif "${step.resultState}" == "FAILED">
                    <div class="failed${noHoverSuffix} step ${collapsible}${headerClass}" style="font-weight: ${fWeight}">
                        <div class="step-display" style="padding-bottom: 4px">
                            <@stepDisplay step/>
                            <#if step.hasScreenshot>
                                <i class="spec-icon-camera"></i>
                            </#if>
                            <i class="spec-icon-file"></i>
                            <@anchor step/>
                            <@stamp step/>
                        </div>
                        <#if step.hasErrorMsg>
                            <div class="step-content">
<pre style="width: 100%;"><code class="language-javastacktrace">${step.errorMsg}</code></pre>
                            </div>
                        </#if>
                    </div>
                    <#if !step.type.container>
                        <div class="overflow-content">
                            <#if step.hasScreenshot>
                                <img src="./${step.uniqueHash}.png"/>
                            </#if>
                            <@failureDetails step/>
                        </div>
                    </#if>
                <#else>
                    <div class="step ${collapsible}${headerClass}" style="font-weight: ${fWeight}">
                        <div class="step-display" style="padding-bottom: 4px">
                            <@stepDisplay step/>
                            <#if step.hasScreenshot>
                                <i class="spec-icon-camera"></i>
                            </#if>
                        </div>
                    </div>
                    <#if !step.type.container && step.hasScreenshot>
                        <div class="overflow-content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                </#if>

                <div class="outline-body">
                    <#if step.type.container>
                        <#if step.hasScreenshot || "${step.resultState}" == "FAILED">
                            <div class="step-details">
                                <#if step.hasScreenshot>
                                    <img src="./${step.uniqueHash}.png"/>
                                </#if>
                                <#if "${step.resultState}" == "FAILED">
                                    <@failureDetails step/>
                                </#if>
                            </div>
                        </#if>
                    </#if>

                    <@historyDisplay step/>

                    <#list step.testTags.tags as tag>
                        <a href="./../tags.html?t=${tag.tagName}" class="tagged">
                            <i class="tagged-icon ${tag.stereoType.iconClass}"></i>
                            <#noautoesc>
                                ${tag.tagName}
                            </#noautoesc>
                        </a>
                    </#list>

                    <#if step.hasSeeAlsoRefs>
                        <#assign startComment="<!--%%%%%">
                        <#assign endComment="%%%%%-->">
                        <#list step.seeAlsoRefs.refs as ref>
                            <#noautoesc>
                                ${startComment}${ref.className}${endComment}
                            </#noautoesc>
                        </#list>
                    </#if>

                    <#if step.hasNarrative>
                        <div class="neutral-no-hover step" style="margin-bottom: 2px; margin-top: 2px; font-weight: normal">
                            <p class="narrative" style="font-weight: ${fWeight}">${step.narrative.header}</p>
                            <#list step.narrative.formattedLines as line>
                                <div class="narrative">
                                    <#noautoesc>
                                        ${line}
                                    </#noautoesc>
                                </div>
                            </#list>
                            <#if step.narrative.hasImages>
                                <#assign imgIdx = 0>
                                <#assign modalId = "${step.uniqueHash}Modal">
                                <#assign imageCount = step.narrative.images?size>
                                <div class="image-grid-row">
                                    <#list step.narrative.images as img>
                                        <#assign imgIdx++>
                                        <div class="image-grid-column">
                                            <#assign imgId = "${step.uniqueHash}Img${imgIdx}">
                                            <img id="${imgId}"
                                 data-modal-index="${imgIdx - 1}"
                                 onclick="openModal('${modalId}', '${imgId}')"
                                 src="./${img.fileName}"/>
                                        </div>
                                    </#list>
                                </div>
                                <div id="${modalId}" class="image-grid-modal">
                                    <span class="image-grid-modal-close" onclick="closeModal('${modalId}')">&times;</span>
                                    <#if imageCount gt 1>
                                        <button class="image-grid-modal-control image-grid-modal-prev"
                            onclick="changeModalImage('${modalId}', -1)">&#10094;</button>
                                        <button class="image-grid-modal-control image-grid-modal-next"
                            onclick="changeModalImage('${modalId}', 1)">&#10095;</button>
                                        <div class="image-grid-modal-counter"></div>
                                    </#if>
                                    <img class="image-grid-modal-content" id="modal-image">
                                </div>
                            </#if>
                            <#if step.narrative.hasAttachments>
                                <div class="attachments">
                                    <#list step.narrative.attachments as atmnt>
                                        <a href="./${atmnt.fileName}" target="_blank" class="attachment">
                                            <i class="attachment-icon"></i>
                                            ${atmnt.fileName}
                                        </a>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </#if>

                    <#if step.type.container>
                        <div class="outline-children">
                            <#list step.steps as childStep>
                                <@dumpSteps childStep step/>
                            </#list>
                        </div>
                    </#if>

                </div>
            </div>
        </#macro>

        <div class="page-layout">
            <nav class="page-outline">
                <details open>
                    <summary>
                        <i class="page-outline-icon-bars"></i>
                    </summary>
                    <div>
                        <@pageMenu step/>
                    </div>
                </details>
            </nav>

            <main class="page-content">
                <@dumpSteps step=step/>
            </main>
        </div>
    </div>


    <script>
        var coll = document.getElementsByClassName("collapsible");
        var i;

        for (i = 0; i < coll.length; i++) {
          coll[i].addEventListener("click", function() {
            this.classList.toggle("active");
            var content = this.nextElementSibling;
            if (content.style.maxHeight){
              content.style.maxHeight = null;
            } else {
              content.style.maxHeight = content.scrollHeight + "px";
            }
          });
        }

    </script>
    <@history.clipboardScript/>
    <@history.outlineScript/>
    <#include "prism-js.ftlh">
    <#include "image-grid-js.ftlh">

</body>
</html>