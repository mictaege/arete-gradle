<#include "arete-colors-css.ftlh">
<style>
    :root {
        --step-border-radius: 6px;
        --border-left-width: 12px;
        --page-icon-bars-bg: url("./../icons/icon-bars-bg.svg");
        --page-icon-bars-fg: url("./../icons/icon-bignoredars-fg.svg");
        --page-icon-bars-neutral: url("./../icons/icon-bars-neutral.svg");
        --page-icon-bell-bg: url("./../icons/icon-bell-bg.svg");
        --page-icon-bell-fg: url("./../icons/icon-bell-fg.svg");
        --page-icon-bell-neutral: url("./../icons/icon-bell-neutral.svg");
        --page-icon-broadcast-bg: url("./../icons/icon-broadcast-bg.svg");
        --page-icon-broadcast-fg: url("./../icons/icon-broadcast-fg.svg");
        --page-icon-broadcast-neutral: url("./../icons/icon-broadcast-neutral.svg");
        --page-icon-buffer-bg: url("./../icons/icon-buffer-bg.svg");
        --page-icon-buffer-fg: url("./../icons/icon-buffer-fg.svg");
        --page-icon-buffer-neutral: url("./../icons/icon-buffer-neutral.svg");
        --page-icon-building-bg: url("./../icons/icon-building-bg.svg");
        --page-icon-building-fg: url("./../icons/icon-building-fg.svg");
        --page-icon-building-neutral: url("./../icons/icon-building-neutral.svg");
        --page-icon-calendar-bg: url("./../icons/icon-calendar-bg.svg");
        --page-icon-calendar-fg: url("./../icons/icon-calendar-fg.svg");
        --page-icon-calendar-neutral: url("./../icons/icon-calendar-neutral.svg");
        --page-icon-camera-bg: url("./../icons/icon-camera-bg.svg");
        --page-icon-camera-fg: url("./../icons/icon-camera-fg.svg");
        --page-icon-camera-neutral: url("./../icons/icon-camera-neutral.svg");
        --page-icon-clipboard-bg: url("./../icons/icon-clipboard-bg.svg");
        --page-icon-clipboard-fg: url("./../icons/icon-clipboard-fg.svg");
        --page-icon-clipboard-neutral: url("./../icons/icon-clipboard-neutral.svg");
        --page-icon-external-link-bg: url(/.."./icons/icon-external-link-bg.svg");
        --page-icon-external-link-fg: url(/.."./icons/icon-external-link-fg.svg");
        --page-icon-external-link-neutral: url(/.."./icons/icon-external-link-neutral.svg");
        --page-icon-file-bg: url("./../icons/icon-file-bg.svg");
        --page-icon-file-fg: url("./../icons/icon-file-fg.svg");
        --page-icon-file-neutral: url("./../icons/icon-file-neutral.svg");
        --page-icon-flask-bg: url("./../icons/icon-flask-bg.svg");
        --page-icon-flask-fg: url("./../icons/icon-flask-fg.svg");
        --page-icon-flask-neutral: url("./../icons/icon-flask-neutral.svg");
        --page-icon-gear-bg: url("./../icons/icon-gear-bg.svg");
        --page-icon-gear-fg: url("./../icons/icon-gear-fg.svg");
        --page-icon-gear-neutral: url("./../icons/icon-gear-neutral.svg");
        --page-icon-handshake-bg: url("./../icons/icon-handshake-bg.svg");
        --page-icon-handshake-fg: url("./../icons/icon-handshake-fg.svg");
        --page-icon-handshake-neutral: url("./../icons/icon-handshake-neutral.svg");
        --page-icon-home-bg: url("./../icons/icon-home-bg.svg");
        --page-icon-home-fg: url("./../icons/icon-home-fg.svg");
        --page-icon-home-neutral: url("./../icons/icon-home-neutral.svg");
        --page-icon-link-bg: url("./../icons/icon-link-bg.svg");
        --page-icon-link-fg: url("./../icons/icon-link-fg.svg");
        --page-icon-link-neutral: url("./../icons/icon-link-neutral.svg");
        --page-icon-scroll-bg: url("./../icons/icon-scroll-bg.svg");
        --page-icon-scroll-fg: url("./../icons/icon-scroll-fg.svg");
        --page-icon-scroll-neutral: url("./../icons/icon-scroll-neutral.svg");
        --page-icon-share-bg: url("./../icons/icon-share-bg.svg");
        --page-icon-share-fg: url("./../icons/icon-share-fg.svg");
        --page-icon-share-neutral: url("./../icons/icon-share-neutral.svg");
        --page-icon-sitemap-bg: url("./../icons/icon-sitemap-bg.svg");
        --page-icon-sitemap-fg: url("./../icons/icon-sitemap-fg.svg");
        --page-icon-sitemap-neutral: url("./../icons/icon-sitemap-neutral.svg");
        --page-icon-tag-bg: url("./../icons/icon-tag-bg.svg");
        --page-icon-tag-fg: url("./../icons/icon-tag-fg.svg");
        --page-icon-tag-neutral: url("./../icons/icon-tag-neutral.svg");
        --page-icon-tags-bg: url("./../icons/icon-tags-bg.svg");
        --page-icon-tags-fg: url("./../icons/icon-tags-fg.svg");
        --page-icon-tags-neutral: url("./../icons/icon-tags-neutral.svg");
        --page-icon-users-bg: url("./../icons/icon-users-bg.svg");
        --page-icon-users-fg: url("./../icons/icon-users-fg.svg");
        --page-icon-users-neutral: url("./../icons/icon-users-neutral.svg");
        --root-icon-bars-bg: url("./icons/icon-bars-bg.svg");
        --root-icon-bars-fg: url("./icons/icon-bars-fg.svg");
        --root-icon-bars-neutral: url("./icons/icon-bars-neutral.svg");
        --root-icon-bell-bg: url("./icons/icon-bell-bg.svg");
        --root-icon-bell-fg: url("./icons/icon-bell-fg.svg");
        --root-icon-bell-neutral: url("./icons/icon-bell-neutral.svg");
        --root-icon-broadcast-bg: url("./icons/icon-broadcast-bg.svg");
        --root-icon-broadcast-fg: url("./icons/icon-broadcast-fg.svg");
        --root-icon-broadcast-neutral: url("./icons/icon-broadcast-neutral.svg");
        --root-icon-buffer-bg: url("./icons/icon-buffer-bg.svg");
        --root-icon-buffer-fg: url("./icons/icon-buffer-fg.svg");
        --root-icon-buffer-neutral: url("./icons/icon-buffer-neutral.svg");
        --root-icon-building-bg: url("./icons/icon-building-bg.svg");
        --root-icon-building-fg: url("./icons/icon-building-fg.svg");
        --root-icon-building-neutral: url("./icons/icon-building-neutral.svg");
        --root-icon-calendar-bg: url("./icons/icon-calendar-bg.svg");
        --root-icon-calendar-fg: url("./icons/icon-calendar-fg.svg");
        --root-icon-calendar-neutral: url("./icons/icon-calendar-neutral.svg");
        --root-icon-camera-bg: url("./icons/icon-camera-bg.svg");
        --root-icon-camera-fg: url("./icons/icon-camera-fg.svg");
        --root-icon-camera-neutral: url("./icons/icon-camera-neutral.svg");
        --root-icon-clipboard-bg: url("./icons/icon-clipboard-bg.svg");
        --root-icon-clipboard-fg: url("./icons/icon-clipboard-fg.svg");
        --root-icon-clipboard-neutral: url("./icons/icon-clipboard-neutral.svg");
        --root-icon-external-link-bg: url("./icons/icon-external-link-bg.svg");
        --root-icon-external-link-fg: url("./icons/icon-external-link-fg.svg");
        --root-icon-external-link-neutral: url("./icons/icon-external-link-neutral.svg");
        --root-icon-file-bg: url("./icons/icon-file-bg.svg");
        --root-icon-file-fg: url("./icons/icon-file-fg.svg");
        --root-icon-file-neutral: url("./icons/icon-file-neutral.svg");
        --root-icon-flask-bg: url("./icons/icon-flask-bg.svg");
        --root-icon-flask-fg: url("./icons/icon-flask-fg.svg");
        --root-icon-flask-neutral: url("./icons/icon-flask-neutral.svg");
        --root-icon-gear-bg: url("./icons/icon-gear-bg.svg");
        --root-icon-gear-fg: url("./icons/icon-gear-fg.svg");
        --root-icon-gear-neutral: url("./icons/icon-gear-neutral.svg");
        --root-icon-handshake-bg: url("./icons/icon-handshake-bg.svg");
        --root-icon-handshake-fg: url("./icons/icon-handshake-fg.svg");
        --root-icon-handshake-neutral: url("./icons/icon-handshake-neutral.svg");
        --root-icon-home-bg: url("./icons/icon-home-bg.svg");
        --root-icon-home-fg: url("./icons/icon-home-fg.svg");
        --root-icon-home-neutral: url("./icons/icon-home-neutral.svg");
        --root-icon-link-bg: url("./icons/icon-link-bg.svg");
        --root-icon-link-fg: url("./icons/icon-link-fg.svg");
        --root-icon-link-neutral: url("./icons/icon-link-neutral.svg");
        --root-icon-scroll-bg: url("./icons/icon-scroll-bg.svg");
        --root-icon-scroll-fg: url("./icons/icon-scroll-fg.svg");
        --root-icon-scroll-neutral: url("./icons/icon-scroll-neutral.svg");
        --root-icon-share-bg: url("./icons/icon-share-bg.svg");
        --root-icon-share-fg: url("./icons/icon-share-fg.svg");
        --root-icon-share-neutral: url("./icons/icon-share-neutral.svg");
        --root-icon-sitemap-bg: url("./icons/icon-sitemap-bg.svg");
        --root-icon-sitemap-fg: url("./icons/icon-sitemap-fg.svg");
        --root-icon-sitemap-neutral: url("./icons/icon-sitemap-neutral.svg");
        --root-icon-tag-bg: url("./icons/icon-tag-bg.svg");
        --root-icon-tag-fg: url("./icons/icon-tag-fg.svg");
        --root-icon-tag-neutral: url("./icons/icon-tag-neutral.svg");
        --root-icon-tags-bg: url("./icons/icon-tags-bg.svg");
        --root-icon-tags-fg: url("./icons/icon-tags-fg.svg");
        --root-icon-tags-neutral: url("./icons/icon-tags-neutral.svg");
        --root-icon-users-bg: url("./icons/icon-users-bg.svg");
        --root-icon-users-fg: url("./icons/icon-users-fg.svg");
        --root-icon-users-neutral: url("./icons/icon-users-neutral.svg");
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
        background-image: var(--page-icon-share-bg);
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
        background-image: var(--page-icon-clipboard-fg);
        background-repeat: no-repeat;
        background-origin: content-box;
    }

    .stacktrace-copy-btn:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
        background-image: var(--page-icon-clipboard-bg)
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
        background-image: var(--page-icon-file-fg);
        opacity: 1;
    }

    .attachment:hover > i {
        background-image: var(--page-icon-file-bg);
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
        background-image: var(--root-icon-home-bg)
    }

    .topnav a:hover .menu-icon-handshake {
        background-image: var(--root-icon-handshake-bg)
    }

    .topnav a:hover .menu-icon-flask {
        background-image: var(--root-icon-flask-bg)
    }

    .topnav a:hover .menu-icon-tags {
        background-image: var(--root-icon-tags-bg)
    }

    .topnav a:hover .spec-menu-icon-home {
        background-image: var(--page-icon-home-bg)
    }

    .topnav a:hover .spec-menu-icon-handshake {
        background-image: var(--page-icon-handshake-bg)
    }

    .topnav a:hover .spec-menu-icon-flask {
        background-image: var(--page-icon-flask-bg)
    }

    .topnav a:hover .spec-menu-icon-tags {
        background-image: var(--page-icon-tags-bg)
    }

    /* Add a color to the active/current link */
    .topnav a.active {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
        text-decoration: none;
    }

    .topnav a.active .menu-icon-home {
        background-image: var(--root-icon-home-bg)
    }

    .topnav a.active .menu-icon-handshake {
        background-image: var(--root-icon-handshake-bg)
    }

    .topnav a.active .menu-icon-flask {
        background-image: var(--root-icon-flask-bg)
    }

    .topnav a.active .menu-icon-tags {
        background-image: var(--root-icon-tags-bg)
    }

    .topnav a.active .spec-menu-icon-home {
        background-image: var(--page-icon-home-bg)
    }

    .topnav a.active .spec-menu-icon-handshake {
        background-image: var(--page-icon-handshake-bg)
    }

    .topnav a.active .spec-menu-icon-flask {
        background-image: var(--page-icon-flask-bg)
    }

    .topnav a.active .spec-menu-icon-tags {
        background-image: var(--page-icon-tags-bg)
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
        background-image: var(--page-icon-bars-bg)
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
        background-image: var(--root-icon-home-fg)
    }

    .menu-icon-handshake {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--root-icon-handshake-fg)
    }

    .menu-icon-flask {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--root-icon-flask-fg)
    }

    .menu-icon-tags {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--root-icon-tags-fg)
    }

    .spec-menu-icon-home {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--page-icon-home-fg)
    }

    .spec-menu-icon-handshake {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--page-icon-handshake-fg)
    }

    .spec-menu-icon-flask {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--page-icon-flask-fg)
    }

    .spec-menu-icon-tags {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--page-icon-tags-fg)
    }

    .spec-icon-camera {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--page-icon-camera-bg)
    }

    .spec-icon-file {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
        background-image: var(--page-icon-file-bg)
    }

    .nav-icon-bars {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1.25rem;
        height:1.25rem;
        background-image: var(--page-icon-bars-fg)
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
        background-image: var(--page-icon-external-link-fg);
        opacity: 1;
    }

    .see-also:hover > i {
        background-image: var(--page-icon-external-link-bg);
    }

    a.tagged {
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

    a.tagged:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
    }

    .tagged-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
    }

    .tagged-actor {
        background-image: var(--page-icon-users-fg);
    }

    .tagged:hover > i.tagged-actor {
        background-image: var(--page-icon-users-bg);
    }

    .tagged-entity {
        background-image: var(--page-icon-buffer-fg);
    }

    .tagged:hover > i.tagged-entity {
        background-image: var(--page-icon-buffer-bg);
    }

    .tagged-event {
        background-image: var(--page-icon-bell-fg);
    }

    .tagged:hover > i.tagged-event {
        background-image: var(--page-icon-bell-bg);
    }

    .tagged-external {
        background-image: var(--page-icon-broadcast-fg);
    }

    .tagged:hover > i.tagged-external {
        background-image: var(--page-icon-broadcast-bg);
    }

    .tagged-rule {
        background-image: var(--page-icon-scroll-fg);
    }

    .tagged:hover > i.tagged-rule {
        background-image: var(--page-icon-scroll-bg);
    }

    .tagged-schedule {
        background-image: var(--page-icon-calendar-fg);
    }

    .tagged:hover > i.tagged-schedule {
        background-image: var(--page-icon-calendar-bg);
    }

    .tagged-tag {
        background-image: var(--page-icon-tag-fg);
    }

    .tagged:hover > i.tagged-tag {
        background-image: var(--page-icon-tag-bg);
    }

    .tagged-unit {
        background-image: var(--page-icon-building-fg);
    }

    .tagged:hover > i.tagged-unit {
        background-image: var(--page-icon-building-bg);
    }

    .tagged-workflow {
        background-image: var(--page-icon-gear-fg);
    }

    .tagged:hover > i.tagged-workflow {
        background-image: var(--page-icon-gear-bg);
    }

    .card-tagged,
    .search-tagged {
        text-decoration:none;
        font-size: smaller;
        appearance: none;
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

    .search-tagged {
        margin-top: 4px;
        margin-bottom: 4px;
    }

    .search-tagged:hover {
        background-color: var(--arete-color-foreground);
        color: var(--arete-color-background);
    }

    .card-tagged-icon,
    .search-tagged-icon {
        display: inline-block;
        vertical-align: middle;
        background-size: contain;
        background-position: center center;
        background-repeat: no-repeat;
        width:1rem;
        height:1rem;
    }

    .card-tagged-actor,
    .search-tagged-actor {
        background-image: var(--root-icon-users-fg);
    }

    .search-tagged:hover > i.search-tagged-actor {
        background-image: var(--root-icon-users-bg);
    }

    .card-tagged-entity,
    .search-tagged-entity {
        background-image: var(--root-icon-buffer-fg);
    }

    .search-tagged:hover > i.search-tagged-entity {
        background-image: var(--root-icon-buffer-bg);
    }

    .card-tagged-event,
    .search-tagged-event {
        background-image: var(--root-icon-bell-fg);
    }

    .search-tagged:hover > i.search-tagged-event {
        background-image: var(--root-icon-bell-bg);
    }

    .card-tagged-external,
    .search-tagged-external {
        background-image: var(--root-icon-broadcast-fg);
    }

    .search-tagged:hover > i.search-tagged-external {
        background-image: var(--root-icon-broadcast-bg);
    }

    .card-tagged-rule,
    .search-tagged-rule {
        background-image: var(--root-icon-scroll-fg);
    }

    .search-tagged:hover > i.search-tagged-rule {
        background-image: var(--root-icon-scroll-bg);
    }

    .card-tagged-schedule,
    .search-tagged-schedule {
        background-image: var(--root-icon-calendar-fg);
    }

    .search-tagged:hover > i.search-tagged-schedule {
        background-image: var(--root-icon-calendar-bg);
    }

    .card-tagged-domain,
    .search-tagged-domain {
        background-image: var(--root-icon-sitemap-fg);
    }

    .search-tagged:hover > i.search-tagged-domain {
        background-image: var(--root-icon-sitemap-bg);
    }

    .card-tagged-tag,
    .search-tagged-tag {
        background-image: var(--root-icon-tag-fg);
    }

    .search-tagged:hover > i.search-tagged-tag {
        background-image: var(--root-icon-tag-bg);
    }

    .card-tagged-unit,
    .search-tagged-unit {
        background-image: var(--root-icon-building-fg);
    }

    .search-tagged:hover > i.search-tagged-unit {
        background-image: var(--root-icon-building-bg);
    }

    .card-tagged-workflow,
    .search-tagged-workflow {
        background-image: var(--root-icon-gear-fg);
    }

    .search-tagged:hover > i.search-tagged-workflow {
        background-image: var(--root-icon-gear-bg);
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