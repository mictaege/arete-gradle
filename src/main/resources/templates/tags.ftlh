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
            <#list plan.allTags() as type, tags>
                <div style="display: flex; align-items: center; flex-wrap: wrap; column-gap: 8px;">
                    <i class="search-tagged-icon search-${type.iconClass}"></i>
                    <#noautoesc>
                        ${type.title}:
                    </#noautoesc>
                    <#list tags as tag>
                        <button class="search-tagged" onclick="searchByTag('${tag.tagName}')">
                            ${tag.tagName}
                        </button>
                    </#list>
                </div>
            </#list>
        </div>
        <div style="padding: 0; margin: 16px">
            <input type="text" id="searchInput" onkeyup="searchFunction()" placeholder="Search for Tags.."/>
        </div>
        <ul id="specList">
            <#macro specCardContent spec>
                <a href="./specs/${spec.uniqueHash}.html">
                    <div style="display: flex; align-items: center; flex-wrap: wrap; column-gap: 8px; float: left;">
                        <#list spec.testTags.tags as tag>
                            <div class="card-tagged">
                                <div style="display: none">#${tag.tagName}</div>
                                <i class="card-tagged-icon search-${tag.stereoType.iconClass}"></i>
                                <#noautoesc>
                                    ${tag.tagName}
                                </#noautoesc>
                            </div>
                        </#list>
                    </div>
                    <span style="float:right; font-weight:500;">${spec.displayName}</span>
                </a>
            </#macro>
            <#list plan.specsOrderedByTags() as spec>
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

    <script>
        var searchInput = document.getElementById("searchInput");

        function searchByTag(tag) {
            searchInput.value = tag;
            searchFunction()
        }

        window.addEventListener("DOMContentLoaded", function () {
            const params = new URLSearchParams(window.location.search);
            const tag = params.get("t");

            if (tag) {
                searchByTag(tag);
            }
        });
    </script>

    <#include "search-js.ftlh">

</body>
</html>