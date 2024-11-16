document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");
    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const passwordRepeatInput = document.getElementById("password-repeat");
    const errorMessage = document.getElementById("error-message");

    form.addEventListener("submit", function (event) {
        let isValid = true;
        let messages = [];

        // Név ellenőrzése (min. 4 karakter)
        if (nameInput.value.length < 4) {
            //alert("A névnek minimum 4 karakterből kell állnia!");
            messages.push("A névnek minimum 4 karakterből kell állnia!")
            isValid = false;
        }

        // Email formátum ellenőrzés
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(emailInput.value)) {
            //alert("Kérlek, adj meg egy érvényes email címet!");
            messages.push("Kérlek, adj meg egy érvényes email címet!")
            isValid = false;
        }

        // Jelszavak egyezésének ellenőrzése
        if (passwordInput.value !== passwordRepeatInput.value) {
            //alert("A két jelszónak egyeznie kell!");
            messages.push("A két jelszónak egyeznie kell!");
            isValid = false;
        }

        // Ha bármelyik feltétel nem teljesül, megakadályozza az űrlap elküldését
        if (!isValid) {
            event.preventDefault();
            errorMessage.style.display = "block;"
            errorMessage.style.color = "red";
            errorMessage.innerHTML = messages.join("<br>");
        }
    });
});