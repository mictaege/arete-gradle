<#-- @ftlvariable name="plan" type="com.github.mictaege.arete_gradle.SpecificationPlan" -->
<html>
<head>
    <title>Main</title>
    <#include "arete.ftlh">
    <style>
        .flex-container {
            display: flex;
        }

        .flex-container > div {
            flex-grow: 1;
            flex-basis: 0;
        }

        #searchInput {
            width: 100%;
            padding-top: 12px;
            padding-right: 20px;
            padding-left: 40px;
            padding-bottom: 12px;
            border: 1px solid darkgrey;
        }

        #specList {
            /* Remove default list styling */
            list-style-type: none;
            padding: 0;
            margin: 16px;
        }

        #specList li {
            margin-bottom: 15px;
            margin-top: 15px;
        }

        #specList li a {
            text-decoration: none;
            display: block;
            color: black;
        }

    </style>
    <#include "arete.ftlh">
</head>
<body style="font-family: sans-serif">
    <div style="margin-top: 40px">
        <div style="padding: 0; margin: 16px">
            <h3>Summaries</h3>
        </div>
        <div class="flex-container" style="margin: 16px">
            <div class="card-white" style="margin-right: 16px; padding-left: 16px">
                <div style="padding: 0; margin: 16px">
                    <h3>Specifications</h3>
                </div>
                <div class="failed step">
                    <div style="text-align: left">Failed
                        <span style="float:right;">${plan.specSummaries().failedCount}</span>
                    </div>
                </div>
                <div class="aborted step">
                    <div style="text-align: left">Aborted
                        <span style="float:right;">${plan.specSummaries().abortedCount}</span>
                    </div>
                </div>
                <div class="ignored step">
                    <div style="text-align: left">Ignored
                        <span style="float:right;">${plan.specSummaries().ignoredCount}</span>
                    </div>
                </div>
                <div class="successful step">
                    <div style="text-align: left">Successful
                        <span style="float:right;">${plan.specSummaries().successCount}</span>
                    </div>
                </div>
                <div class="neutral step">
                    <div style="text-align: left">Overall
                        <span style="float:right;">${plan.specSummaries().overallCount}</span>
                    </div>
                </div>
            </div>
            <div class="card-white" style="margin-right: 16px; padding-left: 16px">
                <div style="padding: 0; margin: 16px">
                    <h3>Scenarios</h3>
                </div>
                <div class="failed step">
                    <div style="text-align: left">Failed
                        <span style="float:right;">${plan.scenarioSummaries().failedCount}</span>
                    </div>
                </div>
                <div class="aborted step">
                    <div style="text-align: left">Aborted
                        <span style="float:right;">${plan.scenarioSummaries().abortedCount}</span>
                    </div>
                </div>
                <div class="ignored step">
                    <div style="text-align: left">Ignored
                        <span style="float:right;">${plan.scenarioSummaries().ignoredCount}</span>
                    </div>
                </div>
                <div class="successful step">
                    <div style="text-align: left">Successful
                        <span style="float:right;">${plan.scenarioSummaries().successCount}</span>
                    </div>
                </div>
                <div class="neutral step">
                    <div style="text-align: left">Overall
                        <span style="float:right;">${plan.scenarioSummaries().overallCount}</span>
                    </div>
                </div>
            </div>
            <div class="card-white" style="padding-left: 16px">
                <div style="padding: 0; margin: 16px">
                    <h3>Descriptions</h3>
                </div>
                <div class="failed step">
                    <div style="text-align: left">Failed
                        <span style="float:right;">${plan.descSummaries().failedCount}</span>
                    </div>
                </div>
                <div class="aborted step">
                    <div style="text-align: left">Aborted
                        <span style="float:right;">${plan.descSummaries().abortedCount}</span>
                    </div>
                </div>
                <div class="ignored step">
                    <div style="text-align: left">Ignored
                        <span style="float:right;">${plan.descSummaries().ignoredCount}</span>
                    </div>
                </div>
                <div class="successful step">
                    <div style="text-align: left">Successful
                        <span style="float:right;">${plan.descSummaries().successCount}</span>
                    </div>
                </div>
                <div class="neutral step">
                    <div style="text-align: left">Overall
                        <span style="float:right;">${plan.descSummaries().overallCount}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="card-white" style="margin-top: 40px">
        <div style="padding: 0; margin: 16px">
            <h2>Specifications</h2>
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

    <script>
        function searchFunction() {
            // Declare variables
            var input, filter, ul, li, a, i, txtValue;
            input = document.getElementById('searchInput');
            filter = input.value.toUpperCase();
            ul = document.getElementById("specList");
            li = ul.getElementsByTagName('li');

            // Loop through all list items, and hide those who don't match the search query
            for (i = 0; i < li.length; i++) {
                a = li[i].getElementsByTagName("a")[0];
                txtValue = a.textContent || a.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1) {
                    li[i].style.display = "";
                } else {
                    li[i].style.display = "none";
                }
            }
        }
    </script>

</body>
</html>