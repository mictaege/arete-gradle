<#-- @ftlvariable name="step" type="com.github.mictaege.arete_gradle.SpecificationStep" -->
<html>
<head>
    <title>${step.displayName}</title>
    <#include "arete-css.ftlh">
    <#include "parent-font-awesome-css.ftlh">
    <#include "prism-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./../index.html"><i class="fas fa-home"></i> Overview</a>
        <a href="./../display_names.html"><i class="fas fa-hands-helping"></i> Specifications</a>
        <a href="./../test_specs.html"><i class="fas fa-vial"></i> Spec Classes</a>
        <a href="./../tags.html"><i class="fas fa-tags"></i> Tags</a>
    </div>
    <div class="card-white" style="margin-top: 40px">
        <#assign level = 0>
        <#macro dumpSteps step>
            <#assign vMargin = 0>
            <#assign vMargin = 0>
            <#assign fWeight = "normal">
            <#assign noHoverSuffix = "-no-hover">
            <#assign collapsible = "">
            <#if step.hasScreenshot>
                <#assign collapsible = "collapsible">
                <#assign noHoverSuffix = "">
            </#if>
            <#if step.hasErrorMsg>
                <#assign collapsible = "collapsible">
                <#assign noHoverSuffix = "">
            </#if>
            <#if step.type.container>
                <#assign vMargin = 15>
                <#assign fWeight = "bolder">
            </#if>
            <#assign indent = 25>
            <div style="margin-left: ${indent}px; margin-bottom: ${vMargin}; margin-top: ${vMargin}">

                <#if "${step.resultState}" == "IGNORED">
                    <div class="ignored${noHoverSuffix} step ${collapsible}" style="font-weight: ${fWeight}">
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
                    <div class="successful${noHoverSuffix} step ${collapsible}" style="font-weight: ${fWeight}">
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
                    <div class="aborted${noHoverSuffix} step ${collapsible}" style="font-weight: ${fWeight}">
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
                    <div class="failed${noHoverSuffix} step ${collapsible}" style="font-weight: ${fWeight}">
                        <div style="padding-bottom: 4px">
                            ${step.displayName}
                            <#if step.hasScreenshot>
                                <i class="fas fa-camera"></i>
                            </#if>
                            <i class="fas fa-file-alt"></i>
                        </div>
                        <#if step.hasErrorMsg>
<pre><code class="language-javastacktrace">${step.errorMsg}</code></pre>
                        </#if>
                    </div>
                    <div class="content">
                        <#if step.hasScreenshot>
                            <img src="./${step.uniqueHash}.png"/>
                        </#if>
<pre><code class="language-javastacktrace">${step.stackTrace}</code></pre>
                    </div>
                <#else>
                    <div class="step ${collapsible}" style="font-weight: ${fWeight}">
                        ${step.resultState.sign} ${step.displayName}
                    </div>
                    <#if step.hasScreenshot>
                        <div class="content">
                            <img src="./${step.uniqueHash}.png"/>
                        </div>
                    </#if>
                </#if>

                <#if step.hasNarrative>
                    <div class="neutral-no-hover step" style="margin-bottom: ${vMargin}; margin-top: ${vMargin};  font-weight: normal">
                        <p class="narrative" style="font-weight: ${fWeight}">${step.narrative.header}</p>
                        <#list step.narrative.lines as line>
                            <p class="narrative">${line}</p>
                        </#list>
                    </div>
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
    <#include "prism-js.ftlh">

</body>
</html>