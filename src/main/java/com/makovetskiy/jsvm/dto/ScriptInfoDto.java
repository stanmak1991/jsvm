package com.makovetskiy.jsvm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.makovetskiy.jsvm.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScriptInfoDto {

    private String id;

    private String request;

    @JsonProperty("console_response")
    private String consoleResponse;

    @JsonProperty("console_error")
    private String consoleError;

    private Status status;
}
