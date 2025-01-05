document.addEventListener('DOMContentLoaded', function () {
        document.getElementById('registrationForm').addEventListener('submit', function(event) {
            const termsCheckbox = document.getElementById('terms1');
            const errorMessage = document.querySelector('.error-message');

            if (!termsCheckbox.checked) {
                event.preventDefault(); // Prevent form submission
                errorMessage.style.display = 'inline'; // Show error message
            } else {
                errorMessage.style.display = 'none'; // Hide error message
            }
        });
});