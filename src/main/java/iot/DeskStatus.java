package iot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeskStatus {
    @JsonProperty("desk_id") private String deskId;
    @JsonProperty("person_present") private boolean personPresent;
    @JsonProperty("stuff_on_desk") private boolean stuffOnDesk;
    @JsonProperty("last_updated_at") private Instant lastUpdatedAt;

    public DeskStatus() {}

    public DeskStatus(String deskId, boolean personPresent, boolean stuffOnDesk, Instant lastUpdatedAt) {
        this.deskId = deskId;
        this.personPresent = personPresent;
        this.stuffOnDesk = stuffOnDesk;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getDeskId() { return deskId; }
    public boolean isPersonPresent() { return personPresent; }
    public boolean isStuffOnDesk() { return stuffOnDesk; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
}
