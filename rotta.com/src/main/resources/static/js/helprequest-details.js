
document.addEventListener("DOMContentLoaded", () => {

    const container = document.querySelector(".tracking-container");

    if (!container) return;

    const requestId = container.dataset.requestId;

    const helperId = Number(container.dataset.helperId);

    const riderUserId = Number(container.dataset.riderUserId);

    const status = document.getElementById("connectionStatus");

    function formatDistance(meters) {

        if (meters >= 1000) {
            return (meters / 1000).toFixed(2) + " km";
        }

        return Math.round(meters) + " metros";
    }

    function updateTracking(data) {

        if (Number(data.helpRequestId) !== Number(requestId)) {
            return;
        }

        const userId = Number(data.userId);

        // Atualização do Helper
        if (userId === helperId) {

            if (data.userName) {
                document.getElementById("helperName")
                    .textContent = data.userName;
            }

            if (data.address) {
                document.getElementById("helperAddress")
                    .textContent = data.address;
            }

        }

        // Atualização do Rider
        else if (userId === riderUserId) {

            if (data.userName) {
                document.getElementById("riderName")
                    .textContent = data.userName;
            }

            if (data.address) {
                document.getElementById("riderAddress")
                    .textContent = data.address;
            }
        }

        // Distância entre os dois
        if (data.distanceInMeters != null) {

            document.getElementById("distanceValue")
                .textContent = formatDistance(
                    Number(data.distanceInMeters)
                );
        }
    }

    function registerTracking() {

        if (typeof window.subscribeHelpRequestTracking !== "function") {
            console.error("Integração de tracking não encontrada.");
            status.textContent = "WebSocket indisponível";
            return;
        }

        window.subscribeHelpRequestTracking(
            requestId,
            updateTracking
        );

        status.textContent = "Acompanhamento ativo";
        status.classList.add("online");
    }

    registerTracking();

});
