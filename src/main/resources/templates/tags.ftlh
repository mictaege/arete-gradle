<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Tags</title>
    <#include "arete-css.ftlh">
</head>
<body>
    <div class="topnav">
        <a href="./index.html"><i class="menu-icon-home"></i> Overview</a>
        <a href="./display_names.html"><i class="menu-icon-handshake"></i> Specifications</a>
        <a href="./test_specs.html"><i class="menu-icon-flask"></i> Spec Classes</a>
        <a class="active" href="./tags.html"><i class="menu-icon-tags"></i> Tags</a>
    </div>
    <div class="card main">
        <div style="padding: 0; margin: 16px">
            <#list plan.allTags() as tag>
                <button class="search-by-tag-btn" onclick="searchByTag('#${tag}')">
                    <i class="search-by-tag-icon"></i>
                    ${tag}
                </button>
            </#list>
        </div>
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Tags.."/>
        </div>
        <ul id="specList">
            <#list plan.specsOrderedByTags() as spec>
                <#if !spec.resultState.hidden>
                    <#if "${spec.cumulatedState()}" == "IGNORED">
                        <li class="ignored step">
                            <a href="./specs/${spec.uniqueHash}.html">
                                <span class="mono">${spec.tags}</span>
                                <span style="float:right;">${spec.displayName}</span>
                            </a>
                        </li>
                    <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                        <li class="successful step">
                            <a href="./specs/${spec.uniqueHash}.html">
                                <span class="mono">${spec.tags}</span>
                                <span style="float:right;">${spec.displayName}</span>
                            </a>
                        </li>
                    <#elseif "${spec.cumulatedState()}" == "ABORTED">
                        <li class="aborted step">
                            <a href="./specs/${spec.uniqueHash}.html">
                                <span class="mono">${spec.tags}</span>
                                <span style="float:right;">${spec.displayName}</span>
                            </a>
                        </li>
                    <#elseif "${spec.cumulatedState()}" == "FAILED">
                        <li class="failed step">
                            <a href="./specs/${spec.uniqueHash}.html">
                                <span class="mono">${spec.tags}</span>
                                <span style="float:right;">${spec.displayName}</span>
                            </a>
                        </li>
                    <#else>
                        <li class="step">
                            <a href="./specs/${spec.uniqueHash}.html">
                                <span class="mono">${spec.tags}</span>
                                <span style="float:right;">${spec.displayName}</span>
                            </a>
                        </li>
                    </#if>
                </#if>
            </#list>
        </ul>
    </div>

    <script>
        var searchInput = document.getElementById("searchInput");

        function searchByTag(tag) {
            searchInput.value = tag;
            searchFunction()
        }
    </script>

    <#include "search-js.ftlh">

</body>
</html>