package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.services.AuthService;
import com.generated.qualityTrace.types.LoginRequest;
import com.generated.qualityTrace.types.LoginResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 登录发 token。该路径在 WebMvcConfig 中免认证。 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    return ControllerSupport.call(() -> authService.login(request));
  }
}
