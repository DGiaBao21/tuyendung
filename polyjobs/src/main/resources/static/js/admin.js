// admin.js
document.addEventListener('DOMContentLoaded', function() {
    // Modal logic
    const modalTriggers = document.querySelectorAll('[data-admin-modal-target]');
    const closeButtons = document.querySelectorAll('.admin-modal-close');
    const backdrops = document.querySelectorAll('.admin-modal-backdrop');

    function openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'flex'; // Ensure display is flex before animating
            // Small timeout to allow display:flex to apply before adding class for transition
            setTimeout(() => {
                modal.classList.add('show');
            }, 10);
            document.body.style.overflow = 'hidden';
        }
    }

    function closeModal(modal) {
        if (modal) {
            modal.classList.remove('show');
            // Wait for transition
            setTimeout(() => {
                modal.style.display = 'none';
                document.body.style.overflow = '';
            }, 300);
        }
    }

    modalTriggers.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const target = this.getAttribute('data-admin-modal-target');
            openModal(target);
        });
    });

    closeButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const modal = this.closest('.admin-modal-backdrop');
            closeModal(modal);
        });
    });

    backdrops.forEach(backdrop => {
        backdrop.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal(this);
            }
        });
    });
});
