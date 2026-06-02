<script>
    let openImageGridModalId = null;

    function openModal(modalId, imgId) {
        let modal = document.getElementById(modalId);
        let image = document.getElementById(imgId);

        modal.style.display = "block";
        modal.dataset.currentImageIndex = image.dataset.modalIndex;
        openImageGridModalId = modalId;

        let modalContent = modal.querySelector('.image-grid-modal-content');
        modalContent.src = image.src;
    }

    function closeModal(modalId) {
        let modal = document.getElementById(modalId);

        modal.style.display = "none";

        if (openImageGridModalId === modalId) {
            openImageGridModalId = null;
        }
    }

    function changeModalImage(modalId, direction) {
        let modal = document.getElementById(modalId);
        let images = document.querySelectorAll("img[onclick*=\"openModal('" + modalId + "'\"]");

        if (images.length <= 1) {
            return;
        }

        let currentIndex = parseInt(modal.dataset.currentImageIndex || "0", 10);
        let nextIndex = currentIndex + direction;

        if (nextIndex < 0) {
            nextIndex = images.length - 1;
        } else if (nextIndex >= images.length) {
            nextIndex = 0;
        }

        modal.dataset.currentImageIndex = nextIndex;

        let modalContent = modal.querySelector('.image-grid-modal-content');
        modalContent.src = images[nextIndex].src;
    }

    document.addEventListener("keydown", function(event) {
        if (!openImageGridModalId) {
            return;
        }

        if (event.key === "ArrowLeft") {
            changeModalImage(openImageGridModalId, -1);
        } else if (event.key === "ArrowRight") {
            changeModalImage(openImageGridModalId, 1);
        } else if (event.key === "Escape") {
            closeModal(openImageGridModalId);
        }
    });
</script>