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
            /**
             * URL encoding converts characters into a format that can be transmitted over the Internet.
             * URLs can only be sent over the Internet using the ASCII character-set.
             * Since URLs often contain characters outside the ASCII set, the URL has to be converted into a valid ASCII format
             * URL encoding replaces unsafe ASCII characters with a "%" followed by two hexadecimal digits
             */
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

                    /**
                     * Using a JSON parsing library allows the code to easily extract the relevant data from the API response and work with it in a structured way.
                     * the JSON response received from the Wikipedia API is first parsed as a String,
                     * and then converted to a JsonObject using the JsonParser class provided by the Gson library.
                     * The JsonObject represents the entire JSON response.
                     * Next, the searchResults variable is assigned the value of the "search" array within the JsonObject.
                     * This is accomplished by first calling getAsJsonObject("query") on the JsonObject to get the "query" object, then calling getAsJsonArray("search") on that object to get the "search" array.
                     * Finally, the for loop iterates over each JsonElement in the searchResults array. Within the loop, each JsonElement is converted to a JsonObject using the getAsJsonObject() method.
                     * The title and snippet properties of each search result are extracted from the JsonObject using the getAsString() method, and printed to the console
                     * In summary, JsonObject, JsonArray, and JsonElement classes are used together to parse a JSON response, navigate through its structure, and extract the relevant data.
                      */
                    JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray searchResults = responseJson.getAsJsonObject("query").getAsJsonArray("search");

                    /**
                    * a for-each loop to iterate over the search results,
                     * and for each result, we cast the JsonElement representing the result to a JsonObject, using the getAsJsonObject() method.
                     * This allows us to access the properties of the JSON object, such as the title and snippet properties.
                     */
                    for (JsonElement result : searchResults) {
                        JsonObject resultObject = result.getAsJsonObject();
                        String title = resultObject.get("title").getAsString();

                        /**
                         * @replaceAll() this regular expression helps to clean up the output by removing any unwanted HTML tags that may have been included in the Wikipedia search results.
                         * So, the regular expression "<.*?\\>" matches any HTML tag, and when combined with the replaceAll() method on the snippet string, replaces all instances of HTML tags with an empty string.
                         * This results in only the plain text content of the snippet being printed to the console.
                         */
                        String snippet = resultObject.get("snippet").getAsString().replaceAll("<.*?\\>","");
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