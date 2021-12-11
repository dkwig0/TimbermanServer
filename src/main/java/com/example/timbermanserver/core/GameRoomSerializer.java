package com.example.timbermanserver.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Optional;

public class GameRoomSerializer extends JsonSerializer<GameRoom> {
    @Override
    public void serialize(GameRoom gameRoom, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", Optional.ofNullable(gameRoom.getId()).orElse(0L));
        jsonGenerator.writeNumberField("maxPlayers", gameRoom.maxPlayers);

        jsonGenerator.writeArrayFieldStart("scores");

        gameRoom.getScores().forEach((pid, sc) -> {
            try {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeNumberField("points", sc.getPoints());
                jsonGenerator.writeStringField("username", sc.getPlayer().getUsername());

                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
