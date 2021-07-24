<style>
    body {
        font-family: sans-serif;
    }
    .ignored {
        border-radius: 2px;
        background-color: lightgray;
        border-left: 6px solid grey;
    }

    .ignored:hover {
        background-color: grey;
    }

    .successful {
        border-radius: 2px;
        background-color: palegreen;
        border-left: 6px solid limegreen;
    }

    .successful:hover {
        background-color: limegreen;
    }

    .aborted {
        border-radius: 2px;
        background-color: lightyellow;
        border-left: 6px solid yellow;
    }

    .aborted:hover {
        background-color: yellow;
    }

    .failed {
        border-radius: 2px;
        background-color: lightpink;
        border-left: 6px solid red;
    }

    .failed:hover {
        background-color: red;
    }

    .neutral {
        border-radius: 2px;
        background-color: lightskyblue;
        border-left: 6px solid deepskyblue;
    }

    .neutral:hover {
        background-color: deepskyblue;
    }

    [class*="card-"] {
        box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
        transition: 0.3s;
        border-radius: 5px;
        padding-top: 10px;
        padding-right: 25px;
        padding-left: 0;
        padding-bottom: 10px;
    }

    .card-white {
        background-color: white;
    }

    .card-blue {
        background-color: lightskyblue;
    }

    .step {
        margin-bottom: 2px;
        padding: 8px;
        box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
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
        background-color: #333;
        overflow: hidden;
    }

    /* Style the links inside the navigation bar */
    .topnav a {
        float: left;
        color: lightskyblue;
        text-align: center;
        padding: 14px 16px;
        text-decoration: none;
        font-size: 17px;
    }

    /* Change the color of links on hover */
    .topnav a:hover {
        background-color: lightskyblue;
        color: black;
    }

    /* Add a color to the active/current link */
    .topnav a.active {
        background-color: deepskyblue;
        color: white;
        text-decoration: none;
    }

</style>