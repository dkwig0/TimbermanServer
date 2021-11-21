package com.example.timbermanserver.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/{roomId}")
    public void registerClientActivity(String message, @DestinationVariable("roomId") Long roomId) {
        System.out.println(message);
        simpMessagingTemplate.convertAndSend("/room/" + roomId, "test" + roomId);
    }

}
