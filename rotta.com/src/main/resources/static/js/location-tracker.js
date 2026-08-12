(function () {
    const stompClient = new StompJs.Client({
        webSocketFactory: () => new SockJS('/ws'),
        reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
        console.log("WebSocket conectado");
        enviarLocalizacao();
        setInterval(enviarLocalizacao, 30000);
    };

    stompClient.activate();

    function enviarLocalizacao() {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const data = {
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude
                };
                stompClient.publish({
                    destination: "/app/location.update",
                    body: JSON.stringify(data)
                });
            },
            () => console.warn("Permissão de localização negada.")
        );
    }

    // opcional: fecha a conexão de forma limpa ao sair da página
    window.addEventListener("beforeunload", () => {
        stompClient.deactivate();
    });
})();