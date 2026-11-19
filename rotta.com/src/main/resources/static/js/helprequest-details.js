
document.addEventListener("DOMContentLoaded", () => {

    const container = document.querySelector(".tracking-container");

    if (!container) return;

    const requestId = container.dataset.requestId;

    const helperId = Number(container.dataset.helperId);

    const riderUserId = Number(container.dataset.riderUserId);

    const otherUserId = container.dataset.isHelper === "true" ? riderUserId : helperId;
    const otherAddress = document.getElementById("otherAddress");
    const updateMarker = initializeTrackingMap(otherUserId);

    updateMarker(helperId, container.dataset.helperLatitude, container.dataset.helperLongitude);
    updateMarker(riderUserId, container.dataset.riderLatitude, container.dataset.riderLongitude);

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

        if (userId !== helperId && userId !== riderUserId) return;

        updateMarker(userId, data.latitude, data.longitude);
        if (userId === otherUserId) {
            otherAddress.textContent = data.address ?? "Localização indisponível";
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

function initializeTrackingMap(otherUserId) {
    const mapElement = document.getElementById("trackingMap");
    const mapStatus = document.getElementById("trackingMapStatus");
    const fitButton = document.getElementById("fitTrackingMap");
    const otherLabel = document.getElementById("otherMapLabel").textContent;
    const leaflet = window.L;
    if (!leaflet) {
        mapStatus.textContent = "Mapa indisponível.";
        return () => {};
    }

    const map = leaflet.map(mapElement, { maxZoom: 19 });
    leaflet.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    const markers = new Map();

    function fitMap() {
        const bounds = leaflet.latLngBounds(Array.from(markers.values(), marker => marker.getLatLng()));
        if (bounds.isValid()) map.fitBounds(bounds, { padding: [35, 35], maxZoom: 16 });
    }

    fitButton.addEventListener("click", fitMap);

    return (userId, latitude, longitude) => {
        const position = [latitude, longitude].map(value =>
            value == null || value === "" ? NaN : Number(value));
        const marker = markers.get(userId);

        if (!position.every(Number.isFinite) || Math.abs(position[0]) > 90 || Math.abs(position[1]) > 180) {
            if (marker) {
                marker.remove();
                markers.delete(userId);
            }
        } else if (marker) {
            marker.setLatLng(position);
        } else {
            const isOther = userId === otherUserId;
            const label = isOther ? otherLabel : "Você";
            const icon = leaflet.divIcon({
                className: isOther ? "tracking-pin tracking-map-other" : "tracking-pin tracking-map-self",
                html: isOther ? "P" : "V",
                iconSize: [26, 26],
                iconAnchor: [13, 13]
            });
            const tooltip = document.createElement("span");
            tooltip.textContent = label;

            mapElement.hidden = false;
            map.invalidateSize();
            markers.set(userId, leaflet.marker(position, { icon, title: label, alt: label })
                .addTo(map)
                .bindTooltip(tooltip, { permanent: true, direction: isOther ? "right" : "left", offset: [isOther ? 14 : -14, 0] }));
            fitMap();
        }

        mapElement.hidden = fitButton.hidden = markers.size === 0;
        mapStatus.hidden = markers.size === 2;
        const missingLabel = markers.has(otherUserId) ? "a sua localização" : "a localização de quem está com você na solicitação";
        const message = markers.size === 0 ? "Aguardando localização dos participantes." : "Aguardando " + missingLabel + ".";
        if (mapStatus.textContent !== message) mapStatus.textContent = message;
    };
}
