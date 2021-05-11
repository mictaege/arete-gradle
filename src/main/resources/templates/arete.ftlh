<style>
    .ignored {
        background-color: lightgray;
        border-left: 6px solid grey;
    }

    .ignored:hover {
        background-color: grey;
    }

    .successful {
        background-color: palegreen;
        border-left: 6px solid limegreen;
    }

    .successful:hover {
        background-color: limegreen;
    }

    .aborted {
        background-color: lightyellow;
        border-left: 6px solid yellow;
    }

    .aborted:hover {
        background-color: yellow;
    }

    .failed {
        background-color: lightpink;
        border-left: 6px solid red;
    }

    .failed:hover {
        background-color: red;
    }

    .neutral {
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

</style>