<script>
    function openModal(modalId, imgId) {
        let modal = document.getElementById(modalId)
        let image = document.getElementById(imgId)
        modal.style.display = "block";
        let modalContent = modal.querySelector('.image-grid-modal-content');
        modalContent.src = image.src;
    }
    function closeModal(modalId) {
        document.getElementById(modalId).style.display = "none";
    }
    window.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            const modals = document.getElementsByClassName('image-grid-modal');
            for (let i = 0; i < modals.length; i++) {
                if (modals[i].style.display === 'block') {
                    closeModal(modals[i].id);
                }
            }
        }
    });
</script>