package co.com.bancolombia.api.dto;

public record LoginResponse(String token, long expiresIn) {}