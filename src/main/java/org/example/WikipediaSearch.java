package org.example;

import com.google.gson.*;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.net.URLEncoder;

class WikipediaSearch {
    private static final String WIKIPEDIA_API_BASE_URL = "https://en.wikipedia.org/w/api.php";

    // this class uses OkHttp client to create an HTTP request to the API endpoint and retrieve the search results
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * @search() this method searches for a topic by making an Api request to the wikipedia API endpoint.
     * @param args passing the topic as arguments "topic:oman" topic[0] split by ':' and oman[1]
     */
    public void search(String[] args) {

        try {

            String encodedTopic = URLEncoder.encode(args[0].split(":")[1], "UTF-8");

            HttpUrl.Builder urlBuilder = HttpUrl.parse(WIKIPEDIA_API_BASE_URL).newBuilder();
            urlBuilder.addQueryParameter("action", "query");
            urlBuilder.addQueryParameter("format", "json");
            urlBuilder.addQueryParameter("list", "search");
            urlBuilder.addQueryParameter("srsearch", encodedTopic);

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (response.isSuccessful()) {
                    // Extract the response body as a String
                    String responseBody = response.body().string();

                    // Using a JSON parsing library allows the code to easily extract the relevant data from the API response and work with it in a structured way.
                    JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray searchResults = responseJson.getAsJsonObject("query").getAsJsonArray("search");

                    /**
                     * @for-each used to iterate over the search results, extracts the title and snippet for each search result, and prints them to the console
                     * the search results are represented as array of elements of json objects so to extract properties that are inside the objects we need to first cast it to a json object
                     */
                    for (JsonElement result : searchResults) {
                        JsonObject resultObject = result.getAsJsonObject();
                        String title = resultObject.get("title").getAsString();
                        String snippet = resultObject.get("snippet").getAsString();
                        System.out.println("Title: " + title);
                        System.out.println("Snippet: " + snippet + "\n");
                    }
                    ResponseSaver responseSaver = new ResponseSaver();
                    responseSaver.saveResponseToFile("response.json", responseBody);
                } else {
                    System.out.println("Request failed with code: " + response.code());
                }

            } catch (IOException e) {
                System.out.println("An error occurred while fetching the response: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error occurred while making API request: " + e.getMessage());
        }
    }
}