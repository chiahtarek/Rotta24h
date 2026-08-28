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

    function mostrarNotificacao(data) {
         alert(`${data.title}\n${data.message}\nDistância: ${data.distance} m`);
    }

    window.addEventListener("beforeunload", () => stompClient.deactivate());
})();