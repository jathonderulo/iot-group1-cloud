package iot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostRequestDto (
        @JsonProperty("desk_id") String deskId,
        @JsonProperty("person_present") boolean personPresent,
        @JsonProperty("stuff_on_desk") boolean stuffOnDesk
        ) {
}
