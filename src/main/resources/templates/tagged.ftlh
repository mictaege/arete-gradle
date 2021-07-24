<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Tagged</title>
    <#include "arete-css.ftlh">
    <#include "font-awesome-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./index.html"><i class="fas fa-home"></i> Overview</a>
        <a href="./alphabetical.html"><i class="fas fa-font"></i> Alphabetical</a>
        <a href="./hierarchical.html"><i class="fas fa-sitemap"></i> Hierarchical</a>
        <a class="active" href="./tagged.html"><i class="fas fa-tags"></i> Tagged</a>
    </div>
    <div class="card-white" style="margin-top: 40px">
        <div style="padding: 0; margin: 16px">
            <h2>Specifications</h2>
        </div>
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Specifications.."/>
        </div>
        <ul id="specList">
            <#list plan.specsOrderedByTags() as spec>
                <#if "${spec.cumulatedState()}" == "IGNORED">
                    <li class="ignored step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span style="font-family: monospace">${spec.tags}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                    <li class="successful step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span style="font-family: monospace">${spec.tags}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#elseif "${spec.cumulatedState()}" == "ABORTED">
                    <li class="aborted step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span style="font-family: monospace">${spec.tags}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#elseif "${spec.cumulatedState()}" == "FAILED">
                    <li class="failed step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span style="font-family: monospace">${spec.tags}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#else>
                    <li class="step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span style="font-family: monospace">${spec.tags}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                </#if>
            </#list>
        </ul>
    </div>

    <#include "search-js.ftlh">

</body>
</html>