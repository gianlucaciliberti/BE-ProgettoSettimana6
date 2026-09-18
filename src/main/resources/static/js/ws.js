const ChatSocket = (() => {
    let stompClient = null;

    function connect(onMessage, onConnected) {
        const socket = new SockJS('/ws');
        stompClient = new StompJs.Client({
            webSocketFactory: () => socket,
            reconnectDelay: 4000,
            onConnect: () => {
                stompClient.subscribe('/user/queue/messages', (frame) => {
                    onMessage(JSON.parse(frame.body));
                });
                if (onConnected) onConnected();
            }
        });
        stompClient.activate();
    }

    function sendMessage(chatId, content) {
        if (!stompClient || !stompClient.connected) {
            throw new Error('Connessione WebSocket non attiva');
        }
        stompClient.publish({
            destination: '/app/chat.send',
            body: JSON.stringify({ chatId, content })
        });
    }

    function disconnect() {
        if (stompClient) stompClient.deactivate();
    }

    return { connect, sendMessage, disconnect };
})();
