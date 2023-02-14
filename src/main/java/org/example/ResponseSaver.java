package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.FileWriter;
import java.io.IOException;

public class ResponseSaver {

    private final Gson gson;

    public ResponseSaver() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveResponseToFile(String fileName, String response) {

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(gson.toJson(gson.fromJson(response, JsonElement.class)));
        } catch (IOException e) {
            System.out.println("An error occurred while saving the response: " + e.getMessage());
        }
    }
}

