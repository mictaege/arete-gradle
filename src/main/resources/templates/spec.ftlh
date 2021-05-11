<#-- @ftlvariable name="step" type="com.github.mictaege.arete_gradle.SpecificationStep" -->
<html>
<head>
    <title>${step.displayName}</title>
    <#include "arete-css.ftlh">
    <#include "rainbow-theme.ftlh">
</head>
<body>
    <div class="topnav">
        <a class="active" href="./../index.html">Overview</a>
        <a href="./../alphabetical.html">Alphabetical</a>
        <a href="./../hierarchical.html">Hierarchical</a>
        <a href="./../tagged.html">Tagged</a>
    </div>
    <div class="card-white" style="margin-top: 40px">
        <#assign level = 0>
        <#macro dumpSteps step>
            <#assign vMargin = 0>
            <#assign vMargin = 0>
            <#assign fWeight = "normal">
            <#if step.type.container>
                <#assign vMargin = 15>
                <#assign fWeight = "bolder">
            </#if>
            <#assign indent = 25>
            <div style="margin-left: ${indent}px; margin-bottom: ${vMargin}; margin-top: ${vMargin}">
                <#if "${step.resultState}" == "IGNORED">
                    <div class="ignored step" style="font-weight: ${fWeight}">${step.displayName}</div>
                <#elseif "${step.resultState}" == "SUCCESSFUL">
                    <div class="successful step" style="font-weight: ${fWeight}">${step.displayName}</div>
                <#elseif "${step.resultState}" == "ABORTED">
                    <div class="aborted step" style="font-weight: ${fWeight}">${step.displayName}</div>
                <#elseif "${step.resultState}" == "FAILED">
                    <div class="failed step collapsible" style="font-weight: ${fWeight}">
                        <div style="padding-bottom: 4px">${step.displayName}</div>
                        <pre>
                            <code data-language="shell">${step.errorMsg}</code>
                        </pre>
                    </div>
                    <div class="content">
                        <pre><code data-language="shell">${step.stackTrace}</code></pre>
                    </div>
                <#else>
                    <div class="step" style="font-weight: ${fWeight}">${step.resultState.sign} ${step.displayName}</div>
                </#if>
                <#list step.steps as childStep>
                    <#assign level++>
                    <@dumpSteps childStep/>
                    <#assign level-->
                </#list>
            </div>
        </#macro>
        <@dumpSteps step/>
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
    <#include "rainbow-js.ftlh">

</body>
</html>