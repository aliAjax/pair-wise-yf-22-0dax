package com.generated.qualityTrace.types;

public record LoginResponse(String token, Long userId, String username, String displayName,
                            String role, long expiresInSeconds) {
}
