<style>
    :root {
        --step-border-radius: 6px;
        --border-left-width: 12px;
        --bk-color: #282a36;
        --com-color: #44475a;
        --fg-color: #f8f8f2;
    }
    body {
        font-family: sans-serif;
        background-color: var(--bk-color);
    }

    .ignored-no-hover {
        border-radius: var(--step-border-radius);
        background-color: lightgray;
        border-left: var(--border-left-width) solid grey;
    }

    .ignored {
        border-radius: var(--step-border-radius);
        background-color: lightgray;
        border-left: var(--border-left-width) solid grey;
    }

    .ignored:hover {
        background-color: grey;
    }

    .successful-no-hover {
        border-radius: var(--step-border-radius);
        background-color: palegreen;
        border-left: var(--border-left-width) solid limegreen;
    }

    .successful {
        border-radius: var(--step-border-radius);
        background-color: palegreen;
        border-left: var(--border-left-width) solid limegreen;
    }

    .successful:hover {
        background-color: limegreen;
    }

    .aborted-no-hover {
        border-radius: var(--step-border-radius);
        background-color: lightyellow;
        border-left: var(--border-left-width) solid yellow;
    }

    .aborted {
        border-radius: var(--step-border-radius);
        background-color: lightyellow;
        border-left: var(--border-left-width) solid yellow;
    }

    .aborted:hover {
        background-color: yellow;
    }

    .failed-no-hover {
        border-radius: var(--step-border-radius);
        background-color: lightpink;
        border-left: var(--border-left-width) solid red;
    }

    .failed {
        border-radius: var(--step-border-radius);
        background-color: lightpink;
        border-left: var(--border-left-width) solid red;
    }

    .failed:hover {
        background-color: red;
    }

    .neutral-no-hover {
        border-radius: var(--step-border-radius);
        background-color: lightskyblue;
        border-left: var(--border-left-width) solid deepskyblue;
    }

    .neutral {
        border-radius: var(--step-border-radius);
        background-color: lightskyblue;
        border-left: var(--border-left-width) solid deepskyblue;
    }

    .neutral:hover {
        background-color: deepskyblue;
    }

    .mono {
        font-family: monospace;
    }

    .stamp {
        font-family: monospace;
        font-style: italic;
        font-size: 0.7em;
        float:right;
    }

    .card {
        transition: 0.3s;
        border-radius: 5px;
        color: var(--fg-color);
        background-color: var(--com-color);
        padding-top: 10px;
        padding-right: 25px;
        padding-left: 0;
        padding-bottom: 10px;
    }

    .step {
        color: var(--bk-color);
        margin-bottom: 2px;
        padding: 8px;
    }

    .narrative {
        padding: 2px;
        margin: 0;
    }

    .flex-container {
        display: flex;
    }

    .flex-container > div {
        flex-grow: 1;
        flex-basis: 0;
    }

    #searchInput {
        width: 100%;
        border-radius: var(--step-border-radius);
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
        color: var(--bk-color);
    }

    .collapsible {
        cursor: pointer;
        outline: none;
    }

    .collapsible:before {

        font-weight: bold;
        float: right;
        margin-left: 5px;
    }

    .content {
        max-height: 0;
        overflow: hidden;
        border: none;
        transition: max-height 0.2s ease-out;
    }

    /* Add a black background color to the top navigation */
    .topnav {
        background-color: var(--com-color);
        border-radius: var(--step-border-radius);
        overflow: hidden;
    }

    /* Style the links inside the navigation bar */
    .topnav a {
        float: left;
        color: lightskyblue;
        text-align: center;
        padding: 14px 16px;
        text-decoration: none;
        font-size: 1em;
    }

    /* Change the color of links on hover */
    .topnav a:hover {
        background-color: lightskyblue;
        color: var(--bk-color);
    }

    /* Add a color to the active/current link */
    .topnav a.active {
        background-color: deepskyblue;
        color: var(--fg-color);
        text-decoration: none;
    }

    img {
        max-width: 100%;
        height: auto;
    }

</style>