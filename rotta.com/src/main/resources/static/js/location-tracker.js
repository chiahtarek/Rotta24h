(function () {
    const stompClient = new StompJs.Client({
        webSocketFactory: () => new SockJS('/ws', null, { transports: ['websocket'] }), reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
        enviarLocalizacao();
        setInterval(enviarLocalizacao, 30000);

        stompClient.subscribe('/user/queue/notifications', (msg) => {
            const data = JSON.parse(msg.body);
            mostrarNotificacao(data);
        });

    };

    stompClient.activate();

    function enviarLocalizacao() {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                stompClient.publish({
                    destination: "/app/location.update",
                    body: JSON.stringify({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    })
                });
            },
            () => console.warn("Permissão de localização negada.")
        );
    }

    // function mostrarNotificacao(data) {
    //      alert(`${data.title}\n${data.message}\nDistância: ${data.distance} m`);
    // }

    function mostrarNotificacao(data) {
        if (data.type === "CANCELLED") {
            removerNotificacao(data.helpRequestId);
            return;
        }
        if (data.type === "ACCEPTED") {
            alert(`${data.title}\n${data.message}`);
            return;
        }

        const container = document.createElement("div");
        container.id = `notif-${data.helpRequestId}`;
        container.className = "notificacao";
        container.innerHTML = `
        <strong>${data.title}</strong>
        <p>${data.message} ${data.distance}</p>
        <button class="aceitar">Aceitar</button>
        <button class="recusar">Recusar</button>
    `;

        container.querySelector(".aceitar").onclick = () => {
            stompClient.publish({
                destination: "/app/notify.accept",
                body: JSON.stringify({ helpRequestId: data.helpRequestId })
            });
            container.remove();
        };

        container.querySelector(".recusar").onclick = () => container.remove();

        document.body.appendChild(container);
    }

    function removerNotificacao(helpRequestId) {
        const el = document.getElementById(`notif-${helpRequestId}`);
        if (el) el.remove();
    }

    window.addEventListener("beforeunload", () => stompClient.deactivate());
})();