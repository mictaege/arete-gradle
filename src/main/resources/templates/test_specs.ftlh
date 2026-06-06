<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Spec Classes</title>
    <#include "arete-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./index.html"><i class="menu-icon-home"></i> Overview</a>
        <a href="./display_names.html"><i class="menu-icon-handshake"></i> Specifications</a>
        <a class="active" href="./test_specs.html"><i class="menu-icon-flask"></i> Spec Classes</a>
        <a href="./tags.html"><i class="menu-icon-tags"></i> Tags</a>
    </div>
    <div class="card main">
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Classes.."/>
        </div>
        <ul id="specList">
            <#macro specCardContent spec>
                <a href="./specs/${spec.uniqueHash}.html">
                    <span class="mono" style="float: left;">${spec.testClassName}</span>
                    <span style="float:right; font-weight:500;">${spec.displayName}</span>
                </a>
            </#macro>
            <#list plan.specsOrderedByHierarchy() as spec>
                <#if !spec.resultState.hidden>
                    <#if "${spec.cumulatedState()}" == "IGNORED">
                        <li class="ignored step">
                            <@specCardContent spec=spec />
                        </li>
                    <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                        <li class="successful step">
                            <@specCardContent spec=spec />
                        </li>
                    <#elseif "${spec.cumulatedState()}" == "ABORTED">
                        <li class="aborted step">
                            <@specCardContent spec=spec />
                        </li>
                    <#elseif "${spec.cumulatedState()}" == "FAILED">
                        <li class="failed step">
                            <@specCardContent spec=spec />
                        </li>
                    <#else>
                        <li class="step">
                            <@specCardContent spec=spec />
                        </li>
                    </#if>
                </#if>
            </#list>
        </ul>
    </div>

    <#include "search-js.ftlh">

</body>
</html>