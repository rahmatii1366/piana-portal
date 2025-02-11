package ir.piana.dev.openidc;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
public class TestController {
    static List<String> data = new ArrayList<>();

    @PostConstruct
    public void init() {
        data.addAll(Arrays.asList("a1", "a2"));
    }

    @GetMapping
    public ResponseEntity<List<String>> getData() {
        return ResponseEntity.ok(data);
//        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Map map) {
        data.add(map.get("item").toString());
//        return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(map.get("item").toString());
    }
}
