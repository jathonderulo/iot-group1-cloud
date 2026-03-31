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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {
    private final HttpHelper httpHelper;
    private final ArchiveService archiveService;

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String LIVE_TABLE = "test_table";

    public Service (HttpHelper httpHelper, ArchiveService archiveService) {
        this.httpHelper = httpHelper;
        this.archiveService = archiveService;
    }

    public boolean post(String deskId, boolean personPresent, boolean stuffOnDesk) {
        Map<String, Object> postRow = Map.of(
                "desk_id", deskId,
                "person_present", personPresent,
                "stuff_on_desk", stuffOnDesk
        );

        System.out.println("Received post");
        try {
            String postJson = MAPPER.writeValueAsString(postRow);
            httpHelper.sendPost(LIVE_TABLE, postJson);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean put(String deskId, boolean personPresent, boolean stuffOnDesk) {
        Map<String, Object> putRow = Map.of(
            "desk_id", deskId,
            "person_present", personPresent,
            "stuff_on_desk", stuffOnDesk
        );

        try {
            DeskStatus currentStatus = httpHelper.getOneDesk(LIVE_TABLE, deskId); // assume it's not null lol
            if (currentStatus.isPersonPresent() == personPresent && currentStatus.isStuffOnDesk() == stuffOnDesk) {
                System.out.println("Received put for " + deskId + ", but no state change");
                return true; // already true, don't update
            }

            System.out.println("Received put for " + deskId + ", with state change ");

            archiveService.writeArchive(currentStatus);

            String putJson = MAPPER.writeValueAsString(putRow);
            httpHelper.sendPut(LIVE_TABLE, "desk_id", deskId, putJson);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DeskState> get()  {
        try {
            List<DeskState> result = new ArrayList<>();
            for (DeskStatus ds : httpHelper.sendGet(LIVE_TABLE)) {
                String key = ds.isPersonPresent() + "," + ds.isStuffOnDesk();

                Status status = switch (key) {
                    case "true,true", "true,false" -> Status.OCCUPIED;
                    case "false,true" -> Status.RESERVED;
                    default -> Status.VACANT;
                };

                result.add(new DeskState(ds.getDeskId(), status));
            }

            System.out.println("Received get");

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
