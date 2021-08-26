<#-- @ftlvariable name="step" type="com.github.mictaege.arete_gradle.SpecificationStep" -->
<html>
<head>
    <title>${step.displayName}</title>
    <#include "arete-css.ftlh">
    <#include "parent-font-awesome-css.ftlh">
    <#include "rainbow-theme.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./../index.html"><i class="fas fa-home"></i> Overview</a>
        <a href="./../display_names.html"><i class="fas fa-hands-helping"></i> Specifications</a>
        <a href="./../test_specs.html"><i class="fas fa-vial"></i></i> Spec Classes</a>
        <a href="./../tags.html"><i class="fas fa-tags"></i> Tags</a>
    </div>
    <div class="card-white" style="margin-top: 40px">
        <#assign level = 0>
        <#macro dumpSteps step>
            <#assign vMargin = 0>
            <#assign vMargin = 0>
            <#assign fWeight = "normal">
            <#assign screenshotContainer = "">
            <#if step.hasScreenshot>
                <#assign screenshotContainer = " collapsible">
            </#if>
            <#if step.type.container>
                <#assign vMargin = 15>
                <#assign fWeight = "bolder">
            </#if>
            <#assign indent = 25>
            <div style="margin-left: ${indent}px; margin-bottom: ${vMargin}; margin-top: ${vMargin}">

                <#if "${step.resultState}" == "IGNORED">
                    <div class="ignored step${screenshotContainer}" style="font-weight: ${fWeight}">
                        ${step.displayName}
                        <#if step.hasScreenshot>
                            <i class="fas fa-camera"></i>
                        </#if>
                    </div>
                    <#if step.hasScreenshot>
                        <div class="content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                <#elseif "${step.resultState}" == "SUCCESSFUL">
                    <div class="successful step${screenshotContainer}" style="font-weight: ${fWeight}">
                        ${step.displayName}
                        <#if step.hasScreenshot>
                            <i class="fas fa-camera"></i>
                        </#if>
                    </div>
                    <#if step.hasScreenshot>
                        <div class="content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                <#elseif "${step.resultState}" == "ABORTED">
                    <div class="aborted step${screenshotContainer}" style="font-weight: ${fWeight}">
                        ${step.displayName}
                        <#if step.hasScreenshot>
                            <i class="fas fa-camera"></i>
                        </#if>
                    </div>
                    <#if step.hasScreenshot>
                        <div class="content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                <#elseif "${step.resultState}" == "FAILED">
                    <div class="failed step collapsible" style="font-weight: ${fWeight}">
                        <div style="padding-bottom: 4px">
                            ${step.displayName}
                            <#if step.hasScreenshot>
                                <i class="fas fa-camera"></i>
                            </#if>
                            <i class="fas fa-file-alt"></i>
                        </div>
                        <pre>
                            <code data-language="shell">${step.errorMsg}</code>
                        </pre>
                    </div>
                    <div class="content">
                        <#if step.hasScreenshot>
                            <img src="./${step.uniqueHash}.png"/>
                        </#if>
                        <pre><code data-language="shell">${step.stackTrace}</code></pre>
                    </div>
                <#else>
                    <div class="step${screenshotContainer}" style="font-weight: ${fWeight}">
                        ${step.resultState.sign} ${step.displayName}
                    </div>
                    <#if step.hasScreenshot>
                        <div class="content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
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