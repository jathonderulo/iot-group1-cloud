package iot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private final String secret;
    private final String url;
    private final String tableName = "test_table";
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public Service(@Value("${supabase.secret}") String secret, @Value("${supabase.url}") String url) {
        this.secret = secret;
        this.url = url;
    }

    public void post(int deskId, boolean personPresent, boolean stuffOnDesk) {
        Map<String, Object> postRow = Map.of(
                "desk_id", deskId,
                "person_present", personPresent,
                "stuff_on_desk", stuffOnDesk
        );

        try {
            String postJson = MAPPER.writeValueAsString(postRow);
            sendPost(postJson);
        } catch (Exception e) {
            // ...
        }
    }

    public void put(int deskId, boolean personPresent, boolean stuffOnDesk) {
        Map<String, Object> putRow = Map.of(
                "desk_id", deskId,
                "person_present", personPresent,
                "stuff_on_desk", stuffOnDesk
        );

        try {
            String putJson = MAPPER.writeValueAsString(putRow);
            sendPut("desk_id", String.valueOf(deskId), putJson);
        } catch (Exception e) {
            // ...
        }
    }

    public List<DeskStatus> get()  {
        try {
            return sendGet();
        } catch (Exception e) {
            // ...
        }

        return null;
    }

    private List<DeskStatus> sendGet() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url + tableName))
                .header("apikey", secret)
                .header("Authorization", "Bearer" + secret)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        return MAPPER.readValue(response.body(), MAPPER.getTypeFactory().constructCollectionType(List.class, DeskStatus.class));
    }

    private void sendPost(String json) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url + tableName))
                .header("apikey", secret)
                .header("Authorization", "Bearer" + secret)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    // Assumes a simple primary key (not composite)
    private void sendPut(String key, String value, String json) throws URISyntaxException, IOException, InterruptedException {
        String uri = url + tableName + "?" + key + "=eq." + value;
        HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                .header("apikey", secret)
                .header("Authorization", "Bearer" + secret)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
