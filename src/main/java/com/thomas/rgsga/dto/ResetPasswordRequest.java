package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    @JsonProperty("token")
    @JsonAlias({"otp", "resetToken", "reset_token"})
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty("newPassword")
    @JsonAlias({"password", "new_password"})
    private String newPassword;

    public void setOtp(String otp) {
        if (otp != null && !otp.isBlank()) {
            this.token = otp;
        }
    }

    public void setResetToken(String resetToken) {
        if (resetToken != null && !resetToken.isBlank()) {
            this.token = resetToken;
        }
    }

    public void setPassword(String password) {
        if (password != null && !password.isBlank()) {
            this.newPassword = password;
        }
    }
}
