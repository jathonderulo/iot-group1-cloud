package iot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeskState {
    @JsonProperty("desk_id") private int deskId;
    @JsonProperty("status") private Status status;

    public DeskState(int deskId, Status status) {
        this.deskId = deskId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "desk_id="+deskId+", status="+status;
    }
}
