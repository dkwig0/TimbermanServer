package com.example.timbermanserver.websocket;

import com.example.timbermanserver.core.GameRoom;
import com.example.timbermanserver.core.exceptions.MultipleRoomIdInitializationException;
import com.example.timbermanserver.entities.User;
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
            String message,
            @DestinationVariable("roomId") Long roomId,
            Principal principal
    ) {

        User user = userRepository.findUserByUsername(principal.getName());

        if (gameRoom == null) {
            gameRoom = new GameRoom(user, 2);
            LOG.info("Room created");

        } else {
            if (gameRoom.getUsers().contains(user)) {
                gameRoom.scorePointTo(user);
                LOG.info("point scored");
            } else {
                //
                gameRoom.joinPlayer(user);
                gameRoom.startPreparation();
                LOG.info("new player joined");
            }
        }

        System.out.println(message);
        simpMessagingTemplate.convertAndSend("/rooms/" + roomId, "test" + roomId);
    }

    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GameRoom>> getAllRooms(Principal principal) {
        return new ResponseEntity<>(rooms, null, HttpStatus.OK);
    }

    @PostMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameRoom> createRoom(Principal principal) throws MultipleRoomIdInitializationException {
        GameRoom gameRoom = new GameRoom(userRepository.findUserByUsername(principal.getName()), 2);
        gameRoom.initializeRoomId(
                rooms.stream()
                        .map(GameRoom::getId)
                        .max(Long::compareTo)
                        .orElse(0L)
        );
        ResponseEntity<GameRoom> response;
        if (rooms.add(gameRoom)) {
            response = new ResponseEntity<>(gameRoom, null, HttpStatus.CREATED);
        } else {
            response = new ResponseEntity<>(gameRoom, null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return response;
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
