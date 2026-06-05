<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Tags</title>
    <#include "arete-css.ftlh">
    <style>
        .search-tagged[aria-pressed="true"] {
            filter: brightness(0.85);
            font-weight: 700;
            outline: 2px solid currentColor;
        }
    </style>
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
        <#list plan.allTestTags() as type, tags>
            <div style="display: flex; align-items: center; flex-wrap: wrap; column-gap: 8px;">
                <i class="search-tagged-icon search-${type.iconClass}"></i>
                <#noautoesc>
                    ${type.title}:
                </#noautoesc>
                <#list tags as tag>
                    <button class="search-tagged" type="button" data-tag="${tag.tagName}" aria-pressed="false">
                        ${tag.tagName}
                    </button>
                </#list>
            </div>
        </#list>
    </div>
    <ul id="specList">
        <#macro specCardContent spec>
            <a href="./specs/${spec.uniqueHash}.html">
                <div style="display: flex; align-items: center; flex-wrap: wrap; column-gap: 8px; float: left;">
                    <#list spec.allTestTags as tag>
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
        <#macro dataTags spec><#list spec.allTestTags as tag>${tag.tagName}<#sep>|</#list></#macro>
        <#list plan.specsOrderedByTags() as spec>
            <#if !spec.resultState.hidden>
                <#if "${spec.cumulatedState()}" == "IGNORED">
                    <li class="ignored step" data-tags="<@dataTags spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                    <li class="successful step" data-tags="<@dataTags spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#elseif "${spec.cumulatedState()}" == "ABORTED">
                    <li class="aborted step" data-tags="<@dataTags spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#elseif "${spec.cumulatedState()}" == "FAILED">
                    <li class="failed step" data-tags="<@dataTags spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#else>
                    <li class="step" data-tags="<@dataTags spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                </#if>
            </#if>
        </#list>
    </ul>
</div>

<script>
    function activeTags() {
        return Array.from(document.querySelectorAll(".search-tagged[aria-pressed='true']"))
            .map(function (button) {
                return button.dataset.tag;
            });
    }

    function searchFunction() {
        const selectedTags = activeTags();
        const specs = document.querySelectorAll("#specList li");

        specs.forEach(function (spec) {
            const specTags = spec.dataset.tags
                ? spec.dataset.tags.split("|").map(function (tag) {
                    return tag.trim();
                })
                : [];

            const matches = selectedTags.length === 0
                || selectedTags.every(function (tag) {
                    return specTags.includes(tag);
                });

            spec.style.display = matches ? "" : "none";
        });
    }

    function setTagActive(tag, active) {
        document.querySelectorAll(".search-tagged").forEach(function (button) {
            if (button.dataset.tag === tag) {
                button.setAttribute("aria-pressed", active ? "true" : "false");
            }
        });
    }

    window.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".search-tagged").forEach(function (button) {
            button.addEventListener("click", function () {
                const active = button.getAttribute("aria-pressed") === "true";
                button.setAttribute("aria-pressed", active ? "false" : "true");
                searchFunction();
            });
        });

        const params = new URLSearchParams(window.location.search);
        const tag = params.get("t");

        if (tag) {
            setTagActive(tag, true);
            searchFunction();
        }
    });
</script>

</body>
</html>