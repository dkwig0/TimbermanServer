package com.example.timbermanserver.websocket;

import com.example.timbermanserver.core.GameRoom;
import com.example.timbermanserver.core.exceptions.MultipleRoomIdInitializationException;
import com.example.timbermanserver.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GameController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private List<GameRoom> rooms = new ArrayList<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepository;

    private GameRoom gameRoom;

    @MessageMapping("/{roomId}")
    public void registerClientActivity(
            @DestinationVariable("roomId") Long roomId,
            Principal principal
    ) {

    }

    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GameRoom>> getAllRooms() {
        return new ResponseEntity<>(
                rooms.stream()
                        .filter(r -> !r.isReady())
                        .collect(Collectors.toList()),
                null,
                HttpStatus.OK
        );
    }

    @PostMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameRoom> createRoom(
            @RequestBody String roomName,
            Principal principal
    ) throws MultipleRoomIdInitializationException {
        GameRoom gameRoom = new GameRoom(
                userRepository.findUserByUsername(principal.getName()),
                2,
                roomName
        );
        gameRoom.initializeRoomId(
                rooms.stream()
                        .map(GameRoom::getId)
                        .max(Long::compareTo)
                        .orElse(0L) + 1
        );
        try {
            rooms.add(gameRoom);
            return new ResponseEntity<>(gameRoom, null, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(gameRoom, null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PatchMapping(value = "/rooms/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameRoom> joinRoom(@PathVariable(name = "roomId") Long roomId, Principal principal) {
        GameRoom room = rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findAny()
                .orElse(null);

        if (room == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        room.joinPlayer(userRepository.findUserByUsername(principal.getName()));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
