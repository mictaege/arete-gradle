<#include "arete-colors-css.ftlh">
<style>
    .image-grid-row {
        display: flex;
        flex-wrap: wrap;
        padding: 0 4px;
    }

    /* Create two equal columns that sits next to each other */
    .image-grid-column {
        flex: 25%;
        max-width: 24%;
        padding: 0 4px;
    }

    .image-grid-column img {
        margin-top: 8px;
        vertical-align: middle;
        width: 100%;
        border-radius: 5px;
        cursor: pointer;
        transition: 0.3s;
    }

    .image-grid-column img:hover {
        opacity: 0.7;
    }

    /* The Modal (background) */
    .image-grid-modal {
        display: none; /* Hidden by default */
        position: fixed; /* Stay in place */
        z-index: 1; /* Sit on top */
        padding-top: 100px; /* Location of the box */
        left: 0;
        top: 0;
        width: 100%; /* Full width */
        height: 100%; /* Full height */
        overflow: auto; /* Enable scroll if needed */
        background-color: rgb(0,0,0); /* Fallback color */
        background-color: rgba(0,0,0,0.9); /* Black w/ opacity */
    }

    /* Modal Content (Image) */
    .image-grid-modal-content {
        margin: auto;
        display: block;
        width: 80%;
        max-width: 98%;
        max-height: 87%;
    }

    /* Add Animation - Zoom in the Modal */
    .image-grid-modal-content {
        animation-name: zoom;
        animation-duration: 0.6s;
    }

    @keyframes zoom {
        from {transform:scale(0)}
        to {transform:scale(1)}
    }

    /* The Close Button */
    .image-grid-modal-close {
        position: absolute;
        top: 5%;
        right: 35px;
        color: var(--arete-color-foreground);
        font-size: 40px;
        font-weight: bold;
        transition: 0.3s;
    }

    .image-grid-modal-close:hover,
    .image-grid-modal-close:focus {
        color: var(--arete-color-foreground);
        text-decoration: none;
        cursor: pointer;
    }

    /* 100% Image Width on Smaller Screens */
    @media only screen and (max-width: 700px){
        .image-grid-modal-content {
            width: 100%;
        }
    }

</style>
