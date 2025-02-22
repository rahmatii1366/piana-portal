package ir.piana.boot.endpoint.provider.two.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/provider/two")
public class TwoController {
    @GetMapping(path = "inquiry")
    public ResponseEntity<Response> inquiry(@Valid Request request) {
        return ResponseEntity.ok(new Response("123", "12", "T", "22"));
    }

    public record Request(@NotNull String vin, String productionYear) {
    }

    public record Response(String partOne, String partTwo, String letter, String serial) {
    }
}
