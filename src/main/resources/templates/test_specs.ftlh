<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Spec Classes</title>
    <#include "arete-css.ftlh">
    <#include "root-font-awesome-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./index.html"><i class="fas fa-home"></i> Overview</a>
        <a href="./display_names.html"><i class="fas fa-hands-helping"></i> Specifications</a>
        <a class="active" href="./test_specs.html"><i class="fas fa-vial"></i> Spec Classes</a>
        <a href="./tags.html"><i class="fas fa-tags"></i> Tags</a>
    </div>
    <div class="card main">
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Classes.."/>
        </div>
        <ul id="specList">
            <#list plan.specsOrderedByHierarchy() as spec>
                <#if "${spec.cumulatedState()}" == "IGNORED">
                    <li class="ignored step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span class="mono">${spec.testClassName}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                    <li class="successful step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span class="mono">${spec.testClassName}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#elseif "${spec.cumulatedState()}" == "ABORTED">
                    <li class="aborted step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span class="mono">${spec.testClassName}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#elseif "${spec.cumulatedState()}" == "FAILED">
                    <li class="failed step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span class="mono">${spec.testClassName}</span>
                            <span style="float:right;">${spec.displayName}</span>
                        </a>
                    </li>
                <#else>
                    <li class="step">
                        <a href="./specs/${spec.uniqueHash}.html">
                            <span class="mono">${spec.testClassName}</span>
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