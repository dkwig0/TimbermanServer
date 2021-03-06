package com.example.timbermanserver.controllers;

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
import java.util.stream.Collectors;

@Controller
public class GameController {

    private final static Logger LOG = LoggerFactory.getLogger(GameController.class);

    private static List<GameRoom> rooms = new ArrayList<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/{roomId}")
    public void registerClientActivity(
            @DestinationVariable("roomId") Long roomId,
            Principal principal
    ) throws Exception {
        User user = userRepository.findUserByUsername(principal.getName());
        GameRoom room = rooms.stream()
                .filter(r -> r.getScores().get(user.getId()) != null)
                .findFirst()
                .orElseThrow(() -> new Exception("No such room!"));

        room.scorePointTo(user);
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
            Principal principal
    ) throws MultipleRoomIdInitializationException {
        User user = userRepository.findUserByUsername(principal.getName());
        GameRoom room = new GameRoom(
                user,
                2,
                user.getUsername(),
                simpMessagingTemplate
        );
        room.initializeRoomId(
                rooms.stream()
                        .map(GameRoom::getId)
                        .max(Long::compareTo)
                        .orElse(0L) + 1
        );
        try {
            rooms.add(room);
            LOG.info("Room was successfully created, ID:" + room.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch (RuntimeException e) {
            LOG.warn("Room was NOT created, Entity:" + room.toString());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(room);
        }
    }

    @PatchMapping(value = "/rooms/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameRoom> joinRoom(@PathVariable(name = "roomId") Long roomId, Principal principal) {
        GameRoom room = rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findAny()
                .orElse(null);

        if (room == null) {
            LOG.warn("User tried to join room, which doesn't exists, ID:" + room.getId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        room.joinPlayer(userRepository.findUserByUsername(principal.getName()));

        LOG.info("User joined room, ID:" + room.getId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    public static void deleteEndedRoom(GameRoom room) {
        rooms.remove(room);
        LOG.info("Room deleted, ID:" + room.getId());
    }

}
