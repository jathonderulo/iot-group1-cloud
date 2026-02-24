package iot;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeskStatus {
    int deskId;
    boolean personPresent;
    boolean stuffOnDesk;
    Instant lastUpdatedAt;

    public DeskStatus() {}

    public DeskStatus(int deskId, boolean personPresent, boolean stuffOnDesk, Instant lastUpdatedAt) {
        this.deskId = deskId;
        this.personPresent = personPresent;
        this.stuffOnDesk = stuffOnDesk;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getDeskId() { return deskId; }
    public boolean isPersonPresent() { return personPresent; }
    public boolean isStuffOnDesk() { return stuffOnDesk; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
}
