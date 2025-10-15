package com.dcl.accommodate.dto.response;

import com.dcl.accommodate.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UserResponse(
        @JsonProperty("user_id")
        UUID userId,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("email")
        String email,

        @JsonProperty("date_of_birth")
        LocalDate dateOfBirth,

        @JsonProperty("user_role")
        UserRole userRole,

        @JsonProperty("phone_number")
        String phoneNumber,

        @JsonProperty("avatar")
        String avatar,

        @JsonProperty("created_date")
        Instant createdDate,

        @JsonProperty("last_modified_date")
        Instant lastModifiedDate
) {
}
