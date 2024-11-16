document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");
    const emailInput = document.getElementById("email");
    const errorMessage = document.getElementById("error-message");
    const userEmail = document.getElementById("userEmail").textContent;

    // Kitöltjük az email input mezőt, ha az userEmail nem üres
    if (userEmail) {
        document.getElementById("email").value = userEmail;
        document.getElementById("email").readOnly = true;
    }
    form.addEventListener("submit", function (event) {
        let isValid = true;
        let messages = [];


        // Email formátum ellenőrzés
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(emailInput.value)) {
            //alert("Kérlek, adj meg egy érvényes email címet!");
            messages.push("Kérlek, adj meg egy érvényes email címet!")
            isValid = false;
        }

        if (!isValid) {
            event.preventDefault();
            errorMessage.style.display = "block;"
            errorMessage.style.color = "red";
            errorMessage.innerHTML = messages.join("<br>");
        }
    });
});