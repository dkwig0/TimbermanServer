package com.example.timbermanserver.core;

import com.example.timbermanserver.core.exceptions.MultipleRoomIdInitializationException;
import com.example.timbermanserver.entities.User;
import com.example.timbermanserver.controllers.GameController;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

@JsonSerialize(using = GameRoomSerializer.class)
public class GameRoom {

    public final Integer maxPlayers;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private Long id;
    private final String name;
    /**
     * Contains player inside
     */
    private final Map<Long, Score> scores = new HashMap<>();
    private boolean active = false;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Timer timer = new Timer();

    public GameRoom(User initialPlayer, Integer maxPlayers, String name, SimpMessagingTemplate simpMessagingTemplate) {
        addPlayer(initialPlayer);
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public Long getId() {
        return id;
    }

    public Map<Long, Score> getScores() {
        return scores;
    }

    public boolean isActive() {
        return active;
    }

    private void addPlayer(User player) {
        scores.put(player.getId(), new Score(player));
    }

    public void scorePointTo(User player) {
        if (this.isActive()) {
            scores.get(player.getId()).recordPoint();
        }
    }

    public void initializeRoomId(Long initialId) throws MultipleRoomIdInitializationException {
        if (this.id == null) {
            this.id = initialId;
        } else {
            throw new MultipleRoomIdInitializationException();
        }
    }

    public void joinPlayer(User player) {
        if (!this.isReady()) {
            addPlayer(player);
        }
        if (this.isReady()) {
            startPreparation();
        }
    }

    public boolean isReady() {
        return scores.size() >= this.maxPlayers;
    }

    // TODO cannot start prep before game end
    public void startPreparation() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startGame();
            }
        }, 5000);
        simpMessagingTemplate.convertAndSend("/rooms/" + this.id, "{\"message\":\"prep\"}");
        LOG.info("Game prep started:" + this.id);
    }

    private void startGame() {
        this.active = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame();
            }
        }, 5000);
        simpMessagingTemplate.convertAndSend("/rooms/" + this.id, "{\"message\":\"start\"}");
        LOG.info("Game started:" + this.id);
    }

    private void endGame() {
        this.active = false;
        simpMessagingTemplate.convertAndSend(
                "/rooms/" + this.id,
                "{\"message\":\"end\",\"scores\":[" +
                        this.scores.values().stream()
                                .map(s -> "{\"points\":" + s.getPoints() + ",\"username\":\"" +
                                        s.getPlayer().getUsername() + "\"}")
                                .reduce((a,b) -> a + ',' + b)
                                .orElse("") +
                        "]}"
        );
        LOG.info("Game ended:" + this.id);
        GameController.deleteEndedRoom(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameRoom{");
        sb.append("maxPlayers=").append(maxPlayers);
        sb.append(", id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", scores=").append(scores);
        sb.append(", active=").append(active);
        sb.append(", simpMessagingTemplate=").append(simpMessagingTemplate);
        sb.append(", timer=").append(timer);
        sb.append('}');
        return sb.toString();
    }
}
