<#include "arete-colors-css.ftlh">
<style>
    :root {
        --step-border-radius: 6px;
        --border-left-width: 12px;
        --menu-icon-home-fg: url("./icons/menu-icon-home-fg.svg");
        --menu-icon-home-neutral: url("./icons/menu-icon-home-neutral.svg");
        --menu-icon-home-bg: url("./icons/menu-icon-home-bg.svg");
        --menu-icon-handshake-fg: url("./icons/menu-icon-handshake-fg.svg");
        --menu-icon-handshake-neutral: url("./icons/menu-icon-handshake-neutral.svg");
        --menu-icon-handshake-bg: url("./icons/menu-icon-handshake-bg.svg");
        --menu-icon-flask-fg: url("./icons/menu-icon-flask-fg.svg");
        --menu-icon-flask-neutral: url("./icons/menu-icon-flask-neutral.svg");
        --menu-icon-flask-bg: url("./icons/menu-icon-flask-bg.svg");
        --menu-icon-tags-fg: url("./icons/menu-icon-tags-fg.svg");
        --menu-icon-tags-neutral: url("./icons/menu-icon-tags-neutral.svg");
        --menu-icon-tags-bg: url("./icons/menu-icon-tags-bg.svg");
        --spec-menu-icon-home-fg: url("./../icons/menu-icon-home-fg.svg");
        --spec-menu-icon-home-neutral: url("./../icons/menu-icon-home-neutral.svg");
        --spec-menu-icon-home-bg: url("./../icons/menu-icon-home-bg.svg");
        --spec-menu-icon-handshake-fg: url("./../icons/menu-icon-handshake-fg.svg");
        --spec-menu-icon-handshake-neutral: url("./../icons/menu-icon-handshake-neutral.svg");
        --spec-menu-icon-handshake-bg: url("./../icons/menu-icon-handshake-bg.svg");
        --spec-menu-icon-flask-fg: url("./../icons/menu-icon-flask-fg.svg");
        --spec-menu-icon-flask-neutral: url("./../icons/menu-icon-flask-neutral.svg");
        --spec-menu-icon-flask-bg: url("./../icons/menu-icon-flask-bg.svg");
        --spec-menu-icon-tags-fg: url("./../icons/menu-icon-tags-fg.svg");
        --spec-menu-icon-tags-neutral: url("./../icons/menu-icon-tags-neutral.svg");
        --spec-menu-icon-tags-bg: url("./../icons/menu-icon-tags-bg.svg");
        --spec-icon-camera-bg: url("./../icons/icon-camera-bg.svg");
        --spec-icon-file-fg: url("./../icons/icon-file-fg.svg");
        --spec-icon-file-neutral: url("./../icons/icon-file-neutral.svg");
        --spec-icon-file-bg: url("./../icons/icon-file-bg.svg");
        --spec-icon-link-fg: url("./../icons/icon-link-fg.svg");
        --spec-icon-link-neutral: url("./../icons/icon-link-neutral.svg");
        --spec-icon-link-bg: url("./../icons/icon-link-bg.svg");
        --spec-icon-external-link-fg: url("./../icons/icon-external-link-fg.svg");
        --spec-icon-external-link-neutral: url("./../icons/icon-external-link-neutral.svg");
        --spec-icon-external-link-bg: url("./../icons/icon-external-link-bg.svg");
        --spec-icon-share-fg: url("./../icons/icon-share-fg.svg");
        --spec-icon-share-neutral: url("./../icons/icon-share-neutral.svg");
        --spec-icon-share-bg: url("./../icons/icon-share-bg.svg");
        --nav-icon-bars-fg: url("./../icons/icon-bars-fg.svg");
        --nav-icon-bars-neutral: url("./../icons/icon-bars-neutral.svg");
        --nav-icon-bars-bg: url("./../icons/icon-bars-bg.svg");
        --trace-icon-clipboard-fg: url("./../icons/icon-clipboard-fg.svg");
        --trace-icon-clipboard-neutral: url("./../icons/icon-clipboard-neutral.svg");
        --trace-icon-clipboard-bg: url("./../icons/icon-clipboard-bg.svg");
    }

    html {
        scroll-padding-top: 5rem;
    }

    body {
        font-family: sans-serif;
        background-color: var(--arete-color-background);
    }

    a {
        text-decoration: none;
    }
    a:link, a:visited {
        color: var(--arete-color-background);
    }
    a:hover {
        color: var(--arete-color-background);
    }

    .ignored-no-hover {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-ignored);
        border-left: var(--border-left-width) solid var(--arete-color-ignored-emph);
    }

    .ignored {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-ignored);
        border-left: var(--border-left-width) solid var(--arete-color-ignored-emph);
    }

    .ignored:hover {
        background-color: var(--arete-color-ignored-emph);
    }

    .successful-no-hover {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-successful);
        border-left: var(--border-left-width) solid var(--arete-color-successful-emph);
    }

    .successful {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-successful);
        border-left: var(--border-left-width) solid var(--arete-color-successful-emph);
    }

    .successful:hover {
        background-color: var(--arete-color-successful-emph);
    }

    .aborted-no-hover {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-aborted);
        border-left: var(--border-left-width) solid var(--arete-color-aborted-emph);
    }

    .aborted {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-aborted);
        border-left: var(--border-left-width) solid var(--arete-color-aborted-emph);
    }

    .aborted:hover {
        background-color: var(--arete-color-aborted-emph);
    }

    .failed-no-hover {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-failed);
        border-left: var(--border-left-width) solid var(--arete-color-failed-emph);
    }

    .failed {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-failed);
        border-left: var(--border-left-width) solid var(--arete-color-failed-emph);
    }

    .failed:hover {
        background-color: var(--arete-color-failed-emph);
    }

    .neutral-no-hover {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-neutral);
        border-left: var(--border-left-width) solid var(--arete-color-neutral-emph);
    }

    .neutral {
        border-radius: var(--step-border-radius);
        background-color: var(--arete-color-neutral);
        border-left: var(--border-left-width) solid var(--arete-color-neutral-emph);
    }

    .neutral:hover {
        background-color: var(--arete-color-neutral-emph);
    }

    .mono {
        font-family: monospace;
    }

    .card {
        transition: 0.3s;
        border-radius: 5px;
        color: var(--arete-color-foreground);
        background-color: var(--arete-color-card);
        padding: 10px 25px 10px 0;
    }

    .step {
        color: var(--arete-color-background);
        margin-bottom: 2px;
        padding: 8px;
        display: flex;
        flex-direction: column;
    }

    .step-display {
        display: flex;
        gap: 12px;
        justify-content: flex-start;
        align-items: center;
        white-space: nowrap;
    }

    .step-content {
        display: flex;
        justify-content: stretch;
        align-items: center;
        white-space: nowrap;
        width: 100%;
    }

    .stamp {
        font-family: monospace;
        font-style: italic;
        font-size: 0.7em;
        margin-left: auto;
    }

    .narrative {
        padding: 2px;
        margin: 0;
    }

    .narrative a {
        text-decoration: underline;
    }

    .flex-container {
        display: flex;
    }

    .flex-container > div {
        flex: 1;
    }

    #searchInput {
        width: 100%;
        border-radius: var(--step-border-radius);
        padding: 12px 20px 12px 40px;
        border: 1px solid var(--arete-color-foreground);
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
        color: var(--arete-color-background);
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

    .overflow-content {
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
        background-image: var(--spec-icon-share-bg);
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
        background-color: var(--arete-color-background);
        border: none;
        border-radius: var(--step-border-radius);
        color: var(--arete-color-foreground);
        padding: 4px 4px;
        cursor: pointer;
        background-image: var(--trace-icon-clipboard-fg);
        background-repeat: no-repeat;
        background-origin: content-box;
    }

    .stacktrace-copy-btn:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
        background-image: var(--trace-icon-clipboard-bg)
    }

    .attachments {
        display: flex;
        margin-top: 10px;
        gap: 12px;
        justify-content: flex-start;
        align-items: center;
        white-space: nowrap;
        opacity: 0.6
    }

    a.attachment {
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
        background-color: var(--arete-color-background);
        color: var(--arete-color-foreground);
    }

    a.attachment:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
    }

    .attachment-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-file-fg);
        opacity: 1;
    }

    .attachment:hover > i {
        background-image: var(--spec-icon-file-bg);
    }

    /* Add a background color to the top navigation */
    .topnav {
        background-color: var(--arete-color-background);
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
        color: var(--arete-color-foreground);
        text-align: center;
        padding: 14px 16px;
        text-decoration: none;
        font-size: 1em;
    }

    /* Change the color of links on hover */
    .topnav a:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
    }

    .topnav a:hover .menu-icon-home {
        background-image: var(--menu-icon-home-bg)
    }

    .topnav a:hover .menu-icon-handshake {
        background-image: var(--menu-icon-handshake-bg)
    }

    .topnav a:hover .menu-icon-flask {
        background-image: var(--menu-icon-flask-bg)
    }

    .topnav a:hover .menu-icon-tags {
        background-image: var(--menu-icon-tags-bg)
    }

    .topnav a:hover .spec-menu-icon-home {
        background-image: var(--spec-menu-icon-home-bg)
    }

    .topnav a:hover .spec-menu-icon-handshake {
        background-image: var(--spec-menu-icon-handshake-bg)
    }

    .topnav a:hover .spec-menu-icon-flask {
        background-image: var(--spec-menu-icon-flask-bg)
    }

    .topnav a:hover .spec-menu-icon-tags {
        background-image: var(--spec-menu-icon-tags-bg)
    }

    /* Add a color to the active/current link */
    .topnav a.active {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
        text-decoration: none;
    }

    .topnav a.active .menu-icon-home {
        background-image: var(--menu-icon-home-bg)
    }

    .topnav a.active .menu-icon-handshake {
        background-image: var(--menu-icon-handshake-bg)
    }

    .topnav a.active .menu-icon-flask {
        background-image: var(--menu-icon-flask-bg)
    }

    .topnav a.active .menu-icon-tags {
        background-image: var(--menu-icon-tags-bg)
    }

    .topnav a.active .spec-menu-icon-home {
        background-image: var(--spec-menu-icon-home-bg)
    }

    .topnav a.active .spec-menu-icon-handshake {
        background-image: var(--spec-menu-icon-handshake-bg)
    }

    .topnav a.active .spec-menu-icon-flask {
        background-image: var(--spec-menu-icon-flask-bg)
    }

    .topnav a.active .spec-menu-icon-tags {
        background-image: var(--spec-menu-icon-tags-bg)
    }

    nav {
        background-color: var(--arete-color-background);
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
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
        background-image: var(--nav-icon-bars-bg)
    }

    nav li {
        list-style: none;
    }

    nav div {
        color: var(--arete-color-background);
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
        background-image: var(--menu-icon-home-fg)
    }

    .menu-icon-handshake {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-handshake-fg)
    }

    .menu-icon-flask {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-flask-fg)
    }

    .menu-icon-tags {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--menu-icon-tags-fg)
    }

    .spec-menu-icon-home {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-home-fg)
    }

    .spec-menu-icon-handshake {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-handshake-fg)
    }

    .spec-menu-icon-flask {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-flask-fg)
    }

    .spec-menu-icon-tags {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--spec-menu-icon-tags-fg)
    }

    .spec-icon-camera {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-camera-bg)
    }

    .spec-icon-file {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-file-bg)
    }

    .nav-icon-bars {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--nav-icon-bars-fg)
    }

    .search-by-tag-btn {
        display: inline-flex;
        transition: 0.3s;
        padding: 4px;
        gap: 2px;
        border-radius: var(--step-border-radius);
        border: none;
        cursor: pointer;
        background-color: var(--arete-color-background);
        color: var(--arete-color-foreground);
    }

    .search-by-tag-btn:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
    }

    .search-by-tag-btn:hover > i {
        background-image: var(--menu-icon-tags-bg)
    }

    .search-by-tag-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--menu-icon-tags-fg);
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
        background-color: var(--arete-color-background);
        color: var(--arete-color-foreground);
    }

    a.see-also:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
    }

    .see-also-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--spec-icon-external-link-fg);
        opacity: 1;
    }

    .see-also:hover > i {
        background-image: var(--spec-icon-external-link-bg);
    }

    table {
        width: clamp(30%, 75%, 95%);
        flex-shrink: 0;
        table-layout: fixed;
    }

    th, td {
        text-align: left;
    }
</style>