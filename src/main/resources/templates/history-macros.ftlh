<#macro historyRun id state timeStamp errorMsg stackTrace stdout stderr screenshotFileName="" screenshotPrefix="">
    <#local runClass = "neutral">
    <#if state == "IGNORED">
        <#local runClass = "ignored">
    <#elseif state == "SUCCESSFUL">
        <#local runClass = "successful">
    <#elseif state == "ABORTED">
        <#local runClass = "aborted">
    <#elseif state == "FAILED">
        <#local runClass = "failed">
    </#if>
    <#if !(stackTrace?has_content || stdout?has_content || stderr?has_content || screenshotFileName?has_content)>
        <div class="history-run">
            <div class="${runClass}-no-hover history-run-box">
                <div>${timeStamp}</div>
                <@historyErrorMsg errorMsg/>
            </div>
        </div>
        <#return/>
    </#if>
    <details class="history-run">
        <summary class="${runClass} history-run-box">
            <div>${timeStamp}</div>
            <@historyErrorMsg errorMsg/>
        </summary>
        <div class="history-run-content">
            <#if screenshotFileName?has_content>
                <img class="history-screenshot" src="./${screenshotPrefix}${screenshotFileName}"/>
            </#if>
            <#if stackTrace?has_content>
                <textarea type="text" id="${id}StackTrace" style="display: none;">${stackTrace}</textarea>
                <pre><button onclick="traceToClipboard('${id}StackTrace');" class="stacktrace-copy-btn"></button><code class="language-javastacktrace">${stackTrace}</code></pre>
            </#if>
            <#if stdout?has_content>
                <textarea type="text" id="${id}Stdout" style="display: none;">${stdout}</textarea>
                <pre><button onclick="traceToClipboard('${id}Stdout');" class="stacktrace-copy-btn"></button><code class="language-text">Stdout:
${stdout}</code></pre>
            </#if>
            <#if stderr?has_content>
                <textarea type="text" id="${id}Stderr" style="display: none;">${stderr}</textarea>
                <pre><button onclick="traceToClipboard('${id}Stderr');" class="stacktrace-copy-btn"></button><code class="language-text">Stderr:
${stderr}</code></pre>
            </#if>
        </div>
    </details>
</#macro>

<#macro historyErrorMsg errorMsg>
    <#if errorMsg?has_content>
        <div class="step-content">
            <pre style="width: 100%;"><code class="language-javastacktrace">${errorMsg}</code></pre>
        </div>
    </#if>
</#macro>