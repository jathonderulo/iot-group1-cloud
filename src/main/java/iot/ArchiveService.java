package iot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
public class ArchiveService {
    private final HttpHelper httpHelper;
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final String OCCUPIED_TABLE = "occupied_archive";
    private static final String RESERVED_TABLE = "reserved_archive";
    private static final String VACANT_TABLE = "vacant_archive";
    private static final String LIVE_TABLE = "test_table";

    public ArchiveService (HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    public void writeArchive(DeskStatus oldStatus) throws IOException, URISyntaxException, InterruptedException {
        String status = oldStatus.isPersonPresent() + "," + oldStatus.isStuffOnDesk();
        String targetTable =
            switch (status) {
                case "true,true", "true,false" -> OCCUPIED_TABLE;
                case "false,true" -> RESERVED_TABLE;
                case "false,false" -> VACANT_TABLE;
                default -> throw new IllegalStateException("Unexpected value: " + status);
            };

        String durationTextField =
            switch (targetTable) {
                case OCCUPIED_TABLE -> "occupied_archive";
                case RESERVED_TABLE -> "reserved_archive";
                case VACANT_TABLE -> "vacant_archive";
                default -> throw new IllegalStateException("Unexpected value: " + targetTable);
            };

        Instant now = Instant.now();
        long elapsedSeconds = Math.max(0, Duration.between(oldStatus.getLastUpdatedAt(), now).getSeconds());
        String elapsedText = formatHms(elapsedSeconds);

        Map<String, Object> jsonMap = Map.of(
                "desk_id", oldStatus.getDeskId(),
                "timestamp", now,
                durationTextField, elapsedText
        );

        String json = MAPPER.writeValueAsString(jsonMap);
        System.out.printf("Last person/desk state was %s, writing to %s\n", status, durationTextField);
        httpHelper.sendPost(targetTable, json);
    }

    private static String formatHms(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d h %02d m %02d s", hours, minutes, seconds);
    }
}
