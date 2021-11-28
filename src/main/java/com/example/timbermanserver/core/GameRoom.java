package com.example.timbermanserver.core;

import com.example.timbermanserver.core.exceptions.MultipleRoomIdInitializationException;
import com.example.timbermanserver.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameRoom {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final Integer MAX_PLAYERS;
    private Long id;
    /**
     * Contains player
     */
    private Map<Long, Score> scores = new HashMap<>();
    private boolean active = false;
    private Timer timer = new Timer();

    public GameRoom(User initialPlayer, Integer maxPlayers) {
        addPlayer(initialPlayer);
        this.MAX_PLAYERS = maxPlayers;
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
        return scores.size() >= this.MAX_PLAYERS;
    }

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

}
