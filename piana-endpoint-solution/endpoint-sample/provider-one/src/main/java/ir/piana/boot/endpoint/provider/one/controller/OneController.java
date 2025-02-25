package ir.piana.boot.endpoint.provider.one.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/provider/one")
public class OneController {
    @PostMapping(path = "authenticate")
    public ResponseEntity<AuthResponse> auth(AuthRequest request) {
//    public ResponseEntity<Response> inquiry(@RequestBody Request request) {
        return ResponseEntity.ok(new AuthResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
    }


    @GetMapping(path = "inquiry")
    public ResponseEntity<Response> inquiry(Request request) {
//    public ResponseEntity<Response> inquiry(@RequestBody Request request) {
        return ResponseEntity.ok(new Response("123T12-IR22"));
    }

    public record AuthRequest(String clientId, String secretKey) {
    }

    public record AuthResponse(String accessToken, String refreshToken) {
    }

    public record Request(String vin, String productionYear) {
    }

    public record Response(String plaque) {
    }
}
