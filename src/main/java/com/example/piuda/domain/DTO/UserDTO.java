package com.example.piuda.domain.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    @JsonProperty("userName") @JsonAlias({"name","username"})
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
    private String userName;

    @JsonProperty("userEmail") @JsonAlias({"email"})
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
    private String userEmail;

    @JsonProperty("userPw") @JsonAlias({"password","pwd"})
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 60, message = "비밀번호는 8~60자입니다.")
    private String userPw;

    @JsonProperty("userPhone") @JsonAlias({"phone","phoneNumber","tel"})
    @NotBlank(message = "전화번호는 필수입니다.")
    @Size(max = 30, message = "전화번호는 30자 이하여야 합니다.")
    private String userPhone;
}
