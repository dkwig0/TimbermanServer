package com.example.timbermanserver.core;

import com.example.timbermanserver.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Score {

    private final User player;
    private Long points;
    private List<Long> timeStamps = new ArrayList<>();

    public Score(User player) {
        this.player = player;
    }

    public User getPlayer() {
        return player;
    }

    public Long getPoints() {
        return points;
    }

    public void recordPoint() {
        points++;
        timeStamps.add(System.currentTimeMillis());
    }

    public List<Long> getTimeStamps() {
        return timeStamps;
    }
}
