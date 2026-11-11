
document.addEventListener("DOMContentLoaded", () => {

    const container = document.querySelector(".tracking-container");

    if (!container) return;

    const requestId = container.dataset.requestId;

    const helperId = Number(container.dataset.helperId);

    const riderUserId = Number(container.dataset.riderUserId);

    const status = document.getElementById("connectionStatus");

    const arrivedButton = document.getElementById("arrivedButton");
    const arrivalStatus = document.getElementById("arrivalStatus");
    let requestStatus = container.dataset.status;
    let submitting = false;

    function updateArrival(distance) {
        const meters = distance === null || distance === undefined || distance === ""
            ? NaN : Number(distance);
        if (arrivedButton) arrivedButton.hidden = requestStatus !== "ACCEPTED" || !Number.isFinite(meters)
            || meters < 0 || meters > 50;
        if (requestStatus === "ARRIVED") arrivalStatus.textContent = "Chegada confirmada.";
    }

    updateArrival(container.dataset.distance);
    arrivedButton?.addEventListener("click", async () => {
        if (submitting || arrivedButton.hidden) return;
        submitting = true;
        arrivedButton.disabled = true;
        try {
            const response = await fetch(container.dataset.arrivalUrl, {
                method: "POST",
                headers: { "Accept": "application/json" }
            });
            if (!response.ok || response.redirected) throw new Error("Arrival failed");
            requestStatus = "ARRIVED";
            updateArrival(null);
        } catch (error) {
            arrivalStatus.textContent = "Não foi possível confirmar. Verifique se estão a até 50 metros e tente novamente.";
        } finally {
            submitting = false;
            arrivedButton.disabled = false;
        }
    });

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

        if (data.status) requestStatus = data.status;
        updateArrival(data.distanceInMeters);

        // Distância entre os dois
        if (data.distanceInMeters != null) {

            document.getElementById("distanceValue")
                .textContent = formatDistance(
                    Number(data.distanceInMeters)
                );
        } else {
            document.getElementById("distanceValue").textContent = "Aguardando localização";
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
