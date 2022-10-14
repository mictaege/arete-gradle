<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Specifications</title>
    <#include "arete-css.ftlh">
    <#include "root-font-awesome-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./index.html"><i class="fas fa-home"></i> Overview</a>
        <a class="active" href="./display_names.html"><i class="fas fa-hands-helping"></i> Specifications</a>
        <a href="./test_specs.html"><i class="fas fa-vial"></i> Spec Classes</a>
        <a href="./tags.html"><i class="fas fa-tags"></i> Tags</a>
    </div>
    <div class="card" style="margin-top: 40px">
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Specifications.."/>
        </div>
        <ul id="specList">
            <#list plan.specsOrderedByName() as spec>
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