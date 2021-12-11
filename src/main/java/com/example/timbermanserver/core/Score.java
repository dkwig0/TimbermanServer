package com.example.timbermanserver.core;

import com.example.timbermanserver.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

public class Score {

    @JsonIgnoreProperties({"email"})
    private final User player;
    private Long points = 0l;
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
