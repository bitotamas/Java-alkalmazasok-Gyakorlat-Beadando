


function reszletek(a) {
    // A gombbal azonos sorban lévő "hiddenData" kiválasztása
    const hiddenData = a.closest(".product-card").querySelector("#hiddenData");

    if (hiddenData) {
        // HiddenData tartalmának másolása a popupba
        const popupOverlay = document.getElementById("popupOverlay");
        const popupInnerContent = document.getElementById("popupInnerContent");

        popupInnerContent.innerHTML = hiddenData.innerHTML; // Tartalom másolása
        popupOverlay.style.display = "block"; // Popup megjelenítése
    }
}

function closePopup() {
    // Popup bezárása
    const popupOverlay = document.getElementById("popupOverlay");
    popupOverlay.style.display = "none";
}
