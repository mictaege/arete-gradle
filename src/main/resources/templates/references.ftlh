<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Refs</title>
    <#include "arete-css.ftlh">
    <style>
        .search-ref[aria-pressed="true"] {
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
    <a href="./tags.html"><i class="menu-icon-tags"></i> Tags</a>
    <a class="active" href="./references.html"><i class="menu-icon-bookmark"></i> Refs</a>
    <a href="./history.html"><i class="menu-icon-weather"></i> History</a>
</div>
<div class="card main">
    <div style="padding: 0; margin: 16px">
        <#list plan.allRefLinkGroups() as group>
            <div style="display: flex; align-items: center; flex-wrap: wrap; column-gap: 8px;">
                <i class="search-ref-icon search-ref-bookmark"></i>
                <#noautoesc>
                    ${group.key?has_content?then(group.key, "(no link)")}:
                </#noautoesc>
                <#list group.links as link>
                    <button class="search-ref" type="button" data-ref="${link.value}" aria-pressed="false">
                        ${link.value}
                    </button>
                </#list>
            </div>
        </#list>
    </div>
    <ul id="specList">
        <#macro specCardContent spec>
            <a href="./specs/${spec.uniqueHash}.html">
                <div style="display: flex; align-items: center; flex-wrap: wrap; column-gap: 8px; float: left;">
                    <#list spec.allRefLinks as link>
                        <div class="card-ref">
                            <div style="display: none">#${link.value}</div>
                            <i class="card-ref-icon card-ref-bookmark"></i>
                            <#noautoesc>
                                ${link.value}
                            </#noautoesc>
                        </div>
                    </#list>
                </div>
                <span style="float:right; font-weight:500;">${spec.displayName}</span>
            </a>
        </#macro>
        <#macro dataRefs spec><#list spec.allRefLinks as link>${link.value}<#sep>|</#list></#macro>
        <#list plan.specsOrderedByRefs() as spec>
            <#if !spec.resultState.hidden && spec.allRefLinks?has_content>
                <#if "${spec.cumulatedState()}" == "IGNORED">
                    <li class="ignored step" data-refs="<@dataRefs spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#elseif "${spec.cumulatedState()}" == "SUCCESSFUL">
                    <li class="successful step" data-refs="<@dataRefs spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#elseif "${spec.cumulatedState()}" == "ABORTED">
                    <li class="aborted step" data-refs="<@dataRefs spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#elseif "${spec.cumulatedState()}" == "FAILED">
                    <li class="failed step" data-refs="<@dataRefs spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                <#else>
                    <li class="step" data-refs="<@dataRefs spec=spec />">
                        <@specCardContent spec=spec />
                    </li>
                </#if>
            </#if>
        </#list>
    </ul>
</div>

<script>
    function activeRefs() {
        return Array.from(document.querySelectorAll(".search-ref[aria-pressed='true']"))
            .map(function (button) {
                return button.dataset.ref;
            });
    }

    function searchFunction() {
        const selectedRefs = activeRefs();
        const specs = document.querySelectorAll("#specList li");

        specs.forEach(function (spec) {
            const specRefs = spec.dataset.refs
                ? spec.dataset.refs.split("|").map(function (ref) {
                    return ref.trim();
                })
                : [];

            const matches = selectedRefs.length === 0
                || selectedRefs.every(function (ref) {
                    return specRefs.includes(ref);
                });

            spec.style.display = matches ? "" : "none";
        });
    }

    function setRefActive(ref, active) {
        document.querySelectorAll(".search-ref").forEach(function (button) {
            if (button.dataset.ref === ref) {
                button.setAttribute("aria-pressed", active ? "true" : "false");
            }
        });
    }

    window.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".search-ref").forEach(function (button) {
            button.addEventListener("click", function () {
                const active = button.getAttribute("aria-pressed") === "true";
                button.setAttribute("aria-pressed", active ? "false" : "true");
                searchFunction();
            });
        });

        const params = new URLSearchParams(window.location.search);
        const ref = params.get("r");

        if (ref) {
            setRefActive(ref, true);
            searchFunction();
        }
    });
</script>

</body>
</html>
