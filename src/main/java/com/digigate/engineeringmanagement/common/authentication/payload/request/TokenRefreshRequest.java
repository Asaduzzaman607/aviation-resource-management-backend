package com.digigate.engineeringmanagement.common.authentication.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class TokenRefreshRequest {
  @NotBlank(message = ErrorId.REFRESH_TOKEN_IS_REQUIRED)
  private String refreshToken;
}