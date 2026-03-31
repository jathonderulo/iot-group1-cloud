package iot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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

import org.springframework.stereotype.Service;

@Service
public class HttpHelper {

    private final String secret;
    private final String url;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public HttpHelper(@Value("${supabase.secret}") String secret, @Value("${supabase.url}") String url) {
        this.secret = secret;
        this.url = url;
    }

    public DeskStatus getOneDesk(String tableName, String deskId) throws Exception {
        String uri = url + tableName + "?" + "desk_id" + "=eq." + deskId + "&limit=1";
        HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                .header("apikey", secret)
                .header("Authorization", "Bearer " + secret)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = MAPPER.readTree(response.body());

        if (root.isArray()) {
            List<DeskStatus> rows = MAPPER.readValue(
                    response.body(),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, DeskStatus.class)
            );
            return rows.isEmpty() ? null : rows.get(0);
        }

        if (root.isObject() && root.has("desk_id")) {
            return MAPPER.treeToValue(root, DeskStatus.class);
        }

        throw new IllegalStateException("Unexpected getOneDesk response body: " + response.body());
    }

    public List<DeskStatus> sendGet(String tableName) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url + tableName))
                .header("apikey", secret)
                .header("Authorization", "Bearer " + secret)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Body: " + response);

        return MAPPER.readValue(response.body(), MAPPER.getTypeFactory().constructCollectionType(List.class, DeskStatus.class));
    }

    public void sendPost(String tableName, String json) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url + tableName))
                .header("apikey", secret)
                .header("Authorization", "Bearer" + secret)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Body: " + response);
    }

    // Assumes a simple primary key (not composite)
    public void sendPut(String tableName, String key, String value, String json) throws URISyntaxException, IOException, InterruptedException {
        String uri = url + tableName + "?" + key + "=eq." + value;
        HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                .header("apikey", secret)
                .header("Authorization", "Bearer" + secret)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Body: " + response);
    }
}
