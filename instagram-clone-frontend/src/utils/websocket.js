// websocket.js
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;

export function connectWebSocket(currentUserId, onMessageReceived) {

   if (!currentUserId || isNaN(currentUserId)) {
    console.error("❌ WebSocket connection aborted. Invalid user ID:", currentUserId);
    return;
  }
  if (stompClient) {
    console.warn("WebSocket already connected. Reconnecting...");
    disconnectWebSocket();
  }
  stompClient = new Client({
    brokerURL: "ws://localhost:8080/ws", // fallback to SockJS
    connectHeaders: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
    debug: (str) => console.log("STOMP: ", str),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,

    // Fallback for browsers that don't support WebSocket
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),

    onConnect: () => {
      console.log("✅ WebSocket connected");

      // Subscribe to personal topic
      stompClient.subscribe(`/topic/messages/${currentUserId}`, (message) => {
        const parsed = JSON.parse(message.body);
        onMessageReceived(parsed);
      });
    },

    onStompError: (frame) => {
      console.error("STOMP error:", frame.headers["message"]);
    },
  });

  stompClient.activate();
}

export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
}

export function sendWebSocketMessage(messageDTO) {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(messageDTO),
    });
  } else {
    console.warn("WebSocket not connected.");
  }
}

export function subscribeNotifications(currentUserId, onNotificationReceived) {
  if (!stompClient || !stompClient.connected) {
    console.warn("WebSocket not connected. Cannot subscribe to notifications.");
    return;
  }

  stompClient.subscribe(`/topic/notifications/${currentUserId}`, (message) => {
    const parsed = JSON.parse(message.body);
    onNotificationReceived(parsed);
  });
}


