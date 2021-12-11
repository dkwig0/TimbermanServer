package com.example.timbermanserver.core;

import com.example.timbermanserver.core.exceptions.MultipleRoomIdInitializationException;
import com.example.timbermanserver.entities.User;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@JsonSerialize(using = GameRoomSerializer.class)
public class GameRoom {

    public final Integer maxPlayers;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private Long id;
    /**
     * Contains player inside
     */
    private Map<Long, Score> scores = new HashMap<>();
    private boolean active = false;
    private Timer timer = new Timer();

    public GameRoom(User initialPlayer, Integer maxPlayers) {
        addPlayer(initialPlayer);
        this.maxPlayers = maxPlayers;
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
        LOG.info("Game started:" + this.id);
    }

    private void endGame() {
        this.active = false;
        LOG.info("Game ended:" + this.id);
    }

    public List<User> getUsers() {
        return scores.entrySet().stream()
                .map(Map.Entry::getValue)
                .map(Score::getPlayer)
                .collect(Collectors.toList());
    }

}
