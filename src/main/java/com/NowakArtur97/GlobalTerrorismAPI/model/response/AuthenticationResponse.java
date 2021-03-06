package com.NowakArtur97.GlobalTerrorismAPI.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(description = "API key")
@Data
@AllArgsConstructor
public class AuthenticationResponse {

    @ApiModelProperty(notes = "Generated token")
    private String token;
}
