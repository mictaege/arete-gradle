<style>
    :root {
        --step-border-radius: 6px;
        --border-left-width: 12px;
        --dracula-dark: #282a36;
        --dracula-grey: #44475a;
        --dracula-light: #f8f8f2;
        --menu-icon-home-light: url("./icons/menu-icon-home-light.svg");
        --menu-icon-home-blue: url("./icons/menu-icon-home-blue.svg");
        --menu-icon-home-dark: url("./icons/menu-icon-home-dark.svg");
        --menu-icon-handshake-light: url("./icons/menu-icon-handshake-light.svg");
        --menu-icon-handshake-blue: url("./icons/menu-icon-handshake-blue.svg");
        --menu-icon-handshake-dark: url("./icons/menu-icon-handshake-dark.svg");
        --menu-icon-flask-light: url("./icons/menu-icon-flask-light.svg");
        --menu-icon-flask-blue: url("./icons/menu-icon-flask-blue.svg");
        --menu-icon-flask-dark: url("./icons/menu-icon-flask-dark.svg");
        --menu-icon-tags-light: url("./icons/menu-icon-tags-light.svg");
        --menu-icon-tags-blue: url("./icons/menu-icon-tags-blue.svg");
        --menu-icon-tags-dark: url("./icons/menu-icon-tags-dark.svg");
        --spec-menu-icon-home-light: url("./../icons/menu-icon-home-light.svg");
        --spec-menu-icon-home-blue: url("./../icons/menu-icon-home-blue.svg");
        --spec-menu-icon-home-dark: url("./../icons/menu-icon-home-dark.svg");
        --spec-menu-icon-handshake-light: url("./../icons/menu-icon-handshake-light.svg");
        --spec-menu-icon-handshake-blue: url("./../icons/menu-icon-handshake-blue.svg");
        --spec-menu-icon-handshake-dark: url("./../icons/menu-icon-handshake-dark.svg");
        --spec-menu-icon-flask-light: url("./../icons/menu-icon-flask-light.svg");
        --spec-menu-icon-flask-blue: url("./../icons/menu-icon-flask-blue.svg");
        --spec-menu-icon-flask-dark: url("./../icons/menu-icon-flask-dark.svg");
        --spec-menu-icon-tags-light: url("./../icons/menu-icon-tags-light.svg");
        --spec-menu-icon-tags-blue: url("./../icons/menu-icon-tags-blue.svg");
        --spec-menu-icon-tags-dark: url("./../icons/menu-icon-tags-dark.svg");
        --spec-icon-camera-dark: url("./../icons/icon-camera-dark.svg");
        --spec-icon-file-dark: url("./../icons/icon-file-dark.svg");
        --spec-icon-link-dark: url("./../icons/icon-link-dark.svg");
        --spec-icon-external-link-light: url("./../icons/icon-external-link-light.svg");
        --spec-icon-external-link-blue: url("./../icons/icon-external-link-blue.svg");
        --spec-icon-external-link-dark: url("./../icons/icon-external-link-dark.svg");
        --spec-icon-share-light: url("./../icons/icon-share-light.svg");
        --spec-icon-share-blue: url("./../icons/icon-share-blue.svg");
        --spec-icon-share-dark: url("./../icons/icon-share-dark.svg");
        --nav-icon-bars-light: url("./../icons/icon-bars-light.svg");
        --nav-icon-bars-blue: url("./../icons/icon-bars-blue.svg");
        --nav-icon-bars-dark: url("./../icons/icon-bars-dark.svg");
        --trace-icon-clipboard-blue: url("./../icons/icon-clipboard-blue.svg");
        --trace-icon-clipboard-dark: url("./../icons/icon-clipboard-dark.svg");
        --trace-icon-clipboard-light: url("./../icons/icon-clipboard-light.svg");
    }

    html {
        scroll-padding-top: 5rem;
    }

    body {
        font-family: sans-serif;
        background-color: var(--dracula-dark);
    }

    a {
        text-decoration: none;
    }
    a:link, a:visited {
        color: var(--dracula-dark);
    }
    a:hover {
        color: var(--dracula-dark);
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
        color: lightskyblue;
        background-color: var(--dracula-grey);
        padding-top: 10px;
        padding-right: 25px;
        padding-left: 0;
        padding-bottom: 10px;
    }

    .step {
        color: var(--dracula-dark);
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
        color: var(--dracula-dark);
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
        position: relative;
    }

    .link-copy-btn {
        display: inline-flex;
        transition: 0.3s;
        width:1.1rem;
        height:1.1rem;
        margin: 0;
        padding: 0;
        border: none;
        cursor: pointer;
        background-color: transparent;
        background-image: var(--spec-icon-share-dark);
        background-repeat: no-repeat;
        background-origin: content-box;
        opacity: 0.6;
    }

    .link-copy-btn:hover {
        opacity: 1;
    }

    .stacktrace-copy-btn {
        position: absolute;
        transition: 0.3s;
        top: auto;
        right: 15px;
        width:1.5rem;
        height:1.5rem;
        background-color: var(--dracula-dark);
        border: none;
        border-radius: var(--step-border-radius);
        color: var(--dracula-light);
        padding: 4px 4px;
        cursor: pointer;
        background-image: var(--trace-icon-clipboard-blue);
        background-repeat: no-repeat;
        background-origin: content-box;
    }

    .stacktrace-copy-btn:hover {
        background-color: lightskyblue;
        color: var(--dracula-dark);
        background-image: var(--trace-icon-clipboard-dark)
    }

    /* Add a black background color to the top navigation */
    .topnav {
        background-color: var(--dracula-dark);
        border-radius: var(--step-border-radius);
        overflow: hidden;
        position: fixed;
        top: 5px;
        width: calc(100% - 15px);
        z-index: 1999;
    }

    .main {
        margin-top: 65px; /* Add a top margin to avoid content overlay */
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
        color: var(--dracula-dark);
    }

    .topnav a:hover .menu-icon-home {
        background-image: var(--menu-icon-home-dark)
    }

    .topnav a:hover .menu-icon-handshake {
        background-image: var(--menu-icon-handshake-dark)
    }

    .topnav a:hover .menu-icon-flask {
        background-image: var(--menu-icon-flask-dark)
    }

    .topnav a:hover .menu-icon-tags {
        background-image: var(--menu-icon-tags-dark)
    }

    .topnav a:hover .spec-menu-icon-home {
        background-image: var(--spec-menu-icon-home-dark)
    }

    .topnav a:hover .spec-menu-icon-handshake {
        background-image: var(--spec-menu-icon-handshake-dark)
    }

    .topnav a:hover .spec-menu-icon-flask {
        background-image: var(--spec-menu-icon-flask-dark)
    }

    .topnav a:hover .spec-menu-icon-tags {
        background-image: var(--spec-menu-icon-tags-dark)
    }

    /* Add a color to the active/current link */
    .topnav a.active {
        background-color: deepskyblue;
        color: var(--dracula-light);
        text-decoration: none;
    }

    .topnav a.active .menu-icon-home {
        background-image: var(--menu-icon-home-light)
    }

    .topnav a.active .menu-icon-handshake {
        background-image: var(--menu-icon-handshake-light)
    }

    .topnav a.active .menu-icon-flask {
        background-image: var(--menu-icon-flask-light)
    }

    .topnav a.active .menu-icon-tags {
        background-image: var(--menu-icon-tags-light)
    }

    .topnav a.active .spec-menu-icon-home {
        background-image: var(--spec-menu-icon-home-light)
    }

    .topnav a.active .spec-menu-icon-handshake {
        background-image: var(--spec-menu-icon-handshake-light)
    }

    .topnav a.active .spec-menu-icon-flask {
        background-image: var(--spec-menu-icon-flask-light)
    }

    .topnav a.active .spec-menu-icon-tags {
        background-image: var(--spec-menu-icon-tags-light)
    }

    nav {
        background-color: var(--dracula-dark);
        border-radius: var(--step-border-radius);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        position: fixed;
        top: 15px;
        right: 20px;
        padding: 5px;
        max-width: 50%;
        z-index: 2000;
    }

    nav summary {
        list-style-type: none;
    }

    nav summary:hover .nav-icon-bars {
        border-radius: var(--step-border-radius);
        background-color: lightskyblue;
        color: var(--dracula-dark);
        background-image: var(--nav-icon-bars-dark)
    }

    nav li {
        list-style: none;
    }

    nav div {
        color: var(--dracula-dark);
        margin-bottom: 8px;
        padding: 4px;
    }

    img {
        max-width: 100%;
        height: auto;
    }

    .menu-icon-home {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-home-blue)
    }

    .menu-icon-handshake {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-handshake-blue)
    }

    .menu-icon-flask {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-flask-blue)
    }

    .menu-icon-tags {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-tags-blue)
    }

    .spec-menu-icon-home {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-home-blue)
    }

    .spec-menu-icon-handshake {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-handshake-blue)
    }

    .spec-menu-icon-flask {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-flask-blue)
    }

    .spec-menu-icon-tags {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-tags-blue)
    }

    .spec-icon-camera {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-camera-dark)
    }

    .spec-icon-file {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-file-dark)
    }

    .nav-icon-bars {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--nav-icon-bars-blue)
    }

    .search-by-tag-btn {
        display: inline-flex;
        transition: 0.3s;
        padding: 4px;
        gap: 2px;
        border-radius: var(--step-border-radius);
        border: none;
        cursor: pointer;
        background-color: var(--dracula-dark);
        color: lightskyblue;
    }

    .search-by-tag-btn:hover {
        background-color: lightskyblue;
        color: var(--dracula-dark);
    }

    .search-by-tag-btn:hover > i {
        background-image: var(--menu-icon-tags-dark)
    }

    .search-by-tag-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--menu-icon-tags-blue);
        opacity: 1;
    }

    a.see-also {
        text-decoration:none;
        font-size: smaller;
        appearance: none;
        display: inline-flex;
        transition: 0.3s;
        padding: 4px;
        margin-top: 4px;
        margin-bottom: 4px;
        gap: 2px;
        border-radius: var(--step-border-radius);
        border: none;
        cursor: pointer;
        background-color: var(--dracula-dark);
        color: lightskyblue;
    }

    a.see-also:hover {
        background-color: lightskyblue;
        color: var(--dracula-dark);
    }

    .see-also-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-external-link-blue);
        opacity: 1;
    }

    .see-also:hover > i {
        background-image: var(--spec-icon-external-link-dark);
    }

</style>