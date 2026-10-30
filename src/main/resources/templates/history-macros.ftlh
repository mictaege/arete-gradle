<#function stateClass state>
    <#local stateName = state?string>
    <#if stateName == "IGNORED">
        <#return "ignored">
    <#elseif stateName == "SUCCESSFUL">
        <#return "successful">
    <#elseif stateName == "ABORTED">
        <#return "aborted">
    <#elseif stateName == "FAILED">
        <#return "failed">
    </#if>
    <#return "neutral">
</#function>

<#macro historyStrip iconClass>
    <div class="outline-children">
        <div class="outline-item history-item">
            <details class="history" ontoggle="if (this.open) { this.scrollIntoView({behavior: 'smooth', block: 'nearest'}); }">
                <summary title="History">
                    <i class="tagged-icon ${iconClass} history-icon"></i>
                    <#nested "summary">
                </summary>
                <div class="history-runs outline-children">
                    <#nested "runs">
                </div>
            </details>
        </div>
    </div>
</#macro>

<#macro historyRun id state timeStamp errorMsg stackTrace stdout stderr screenshotFileName="" screenshotPrefix="" iconPrefix="spec">
    <#local runClass = stateClass(state)>
    <div class="outline-item">
        <#if !(stackTrace?has_content || stdout?has_content || stderr?has_content || screenshotFileName?has_content)>
            <div class="history-run">
                <div class="${runClass}-no-hover history-run-box">
                    <div class="history-run-stamp">
                        <span>${timeStamp}</span>
                        <#if screenshotFileName?has_content><i class="${iconPrefix}-icon-camera"></i></#if>
                        <#if stackTrace?has_content><i class="${iconPrefix}-icon-file"></i></#if>
                    </div>
                    <@historyErrorMsg errorMsg/>
                </div>
            </div>
        <#else>
            <details class="history-run">
                <summary class="${runClass} history-run-box">
                    <div class="history-run-stamp">
                        <span>${timeStamp}</span>
                        <#if screenshotFileName?has_content><i class="${iconPrefix}-icon-camera"></i></#if>
                        <#if stackTrace?has_content><i class="${iconPrefix}-icon-file"></i></#if>
                    </div>
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
        </#if>
    </div>
</#macro>

<#macro clipboardScript>
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
</#macro>

<#macro outlineScript>
    <script>
        (function () {
            function outlineBodyOf(header) {
                var body = header.nextElementSibling;
                while (body && !body.classList.contains("outline-body")) {
                    body = body.nextElementSibling;
                }
                return body;
            }
            function setCollapsed(header, collapsed) {
                var body = outlineBodyOf(header);
                if (!body) {
                    return;
                }
                header.classList.toggle("collapsed", collapsed);
                body.classList.toggle("collapsed", collapsed);
                header.setAttribute("aria-expanded", String(!collapsed));
            }
            function expandAbove(element) {
                var node = element.parentElement;
                while (node && node !== document.body) {
                    if (node.classList.contains("outline-body")) {
                        var header = node.previousElementSibling;
                        while (header && !header.classList.contains("outline-header")) {
                            header = header.previousElementSibling;
                        }
                        if (header) {
                            setCollapsed(header, false);
                        }
                    }
                    node = node.parentElement;
                }
            }
            function unfoldTargetOf(link) {
                if (link.hash.length < 2) {
                    return;
                }
                var target = document.getElementById(decodeURIComponent(link.hash.substring(1)));
                if (!target) {
                    return;
                }
                var targetHeader = target.closest(".outline-header");
                if (targetHeader) {
                    setCollapsed(targetHeader, false);
                }
                expandAbove(target);
            }
            document.querySelectorAll(".outline-header").forEach(function (header) {
                header.setAttribute("tabindex", "0");
                header.setAttribute("role", "button");
                header.setAttribute("aria-expanded", "true");
                header.addEventListener("click", function (event) {
                    if (event.target.closest("a, button")) {
                        return;
                    }
                    setCollapsed(header, !header.classList.contains("collapsed"));
                });
                header.addEventListener("keydown", function (event) {
                    if (event.key === "Enter" || event.key === " ") {
                        event.preventDefault();
                        setCollapsed(header, !header.classList.contains("collapsed"));
                    }
                });
            });
            document.addEventListener("click", function (event) {
                if (!event.target.closest) {
                    return;
                }
                var link = event.target.closest('a[href^="#"]');
                if (link) {
                    unfoldTargetOf(link);
                }
            });
        })();
    </script>
</#macro>

<#macro copyLinkButton text target>
    <button onclick='anchorToClipboard("${text?js_string}", "${target?js_string}");' class="link-copy-btn"></button>
</#macro>

<#macro historyErrorMsg errorMsg>
    <#if errorMsg?has_content>
        <div class="step-content">
            <pre style="width: 100%;"><code class="language-javastacktrace">${errorMsg}</code></pre>
        </div>
    </#if>
</#macro>