package com.techcup.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;
import java.util.Set;

@Value
public class UpdateRolesRequest {
    @NotEmpty
    Set<String> roles;
}
