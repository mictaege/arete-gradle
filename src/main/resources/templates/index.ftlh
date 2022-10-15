<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Overview</title>
    <#include "arete-css.ftlh">
    <#include "root-font-awesome-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a class="active" href="./index.html"><i class="fas fa-home"></i> Overview</a>
        <a href="./display_names.html"><i class="fas fa-hands-helping"></i> Specifications</a>
        <a href="./test_specs.html"><i class="fas fa-vial"></i> Spec Classes</a>
        <a href="./tags.html"><i class="fas fa-tags"></i> Tags</a>
    </div>
    <div class="card main">
        <div style="padding: 0; margin: 16px">
            <h3>Summaries</h3>
        </div>
        <div class="flex-container" style="margin: 16px">
            <div style="margin-right: 16px; padding-left: 16px">
                <div style="padding: 0; margin: 16px">
                    <h3>Specifications</h3>
                </div>
                <div class="neutral-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Overall
                        <span style="float:right;">${plan.specSummaries().overallCount}</span>
                    </div>
                </div>
                <div class="failed-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Failed
                        <span style="float:right;">${plan.specSummaries().failedCount}</span>
                    </div>
                </div>
                <div class="aborted-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Aborted
                        <span style="float:right;">${plan.specSummaries().abortedCount}</span>
                    </div>
                </div>
                <div class="ignored-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Ignored
                        <span style="float:right;">${plan.specSummaries().ignoredCount}</span>
                    </div>
                </div>
                <div class="successful-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Successful
                        <span style="float:right;">${plan.specSummaries().successCount}</span>
                    </div>
                </div>
            </div>
            <div style="margin-right: 16px; padding-left: 16px">
                <div style="padding: 0; margin: 16px">
                    <h3>Scenarios</h3>
                </div>
                <div class="neutral-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Overall
                        <span style="float:right;">${plan.scenarioSummaries().overallCount}</span>
                    </div>
                </div>
                <div class="failed-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Failed
                        <span style="float:right;">${plan.scenarioSummaries().failedCount}</span>
                    </div>
                </div>
                <div class="aborted-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Aborted
                        <span style="float:right;">${plan.scenarioSummaries().abortedCount}</span>
                    </div>
                </div>
                <div class="ignored-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Ignored
                        <span style="float:right;">${plan.scenarioSummaries().ignoredCount}</span>
                    </div>
                </div>
                <div class="successful-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Successful
                        <span style="float:right;">${plan.scenarioSummaries().successCount}</span>
                    </div>
                </div>
            </div>
            <div style="padding-left: 16px">
                <div style="padding: 0; margin: 16px">
                    <h3>Descriptions</h3>
                </div>
                <div class="neutral-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Overall
                        <span style="float:right;">${plan.descSummaries().overallCount}</span>
                    </div>
                </div>
                <div class="failed-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Failed
                        <span style="float:right;">${plan.descSummaries().failedCount}</span>
                    </div>
                </div>
                <div class="aborted-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Aborted
                        <span style="float:right;">${plan.descSummaries().abortedCount}</span>
                    </div>
                </div>
                <div class="ignored-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Ignored
                        <span style="float:right;">${plan.descSummaries().ignoredCount}</span>
                    </div>
                </div>
                <div class="successful-no-hover step" style="margin-bottom: 12px">
                    <div style="text-align: left">Successful
                        <span style="float:right;">${plan.descSummaries().successCount}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="card" style="margin-top: 10px">
        <div style="padding: 0; margin: 16px">
            <h2>Suspicious Specifications</h2>
        </div>
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Specifications.."/>
        </div>
        <ul id="specList">
            <#list plan.specsOrderedByPrio() as spec>
                <#if "${spec.cumulatedState()}" == "IGNORED">
                    <li class="ignored step"><a href="./specs/${spec.uniqueHash}.html">${spec.displayName}</a></li>
                <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                    <li class="successful step"><a href="./specs/${spec.uniqueHash}.html">${spec.displayName}</a></li>
                <#elseif "${spec.cumulatedState()}" == "ABORTED">
                    <li class="aborted step"><a href="./specs/${spec.uniqueHash}.html">${spec.displayName}</a></li>
                <#elseif "${spec.cumulatedState()}" == "FAILED">
                    <li class="failed step"><a href="./specs/${spec.uniqueHash}.html">${spec.displayName}</a></li>
                <#else>
                    <li class="step"><a href="./specs/${spec.uniqueHash}.html">${spec.displayName}</a></li>
                </#if>
            </#list>
        </ul>
    </div>

    <#include "search-js.ftlh">

</body>
</html>