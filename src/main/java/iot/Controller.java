package iot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import iot.dto.PostRequestDto;
import iot.dto.PutRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> post(@RequestBody PostRequestDto postRequestDto) {
        boolean success = service.post(postRequestDto.deskId(), postRequestDto.personPresent(), postRequestDto.stuffOnDesk());
        return success ? ResponseEntity.ok("worked") : ResponseEntity.badRequest().body("did not work");
    }

    @PutMapping
    public ResponseEntity<String> put(@RequestBody PutRequestDto putRequestDto) {
        boolean success = service.put(putRequestDto.deskId(), putRequestDto.personPresent(), putRequestDto.stuffOnDesk());
        return success ? ResponseEntity.ok("worked") : ResponseEntity.badRequest().body("did not work");
    }

    @GetMapping
    public ResponseEntity<List<DeskState>> get() {
        List<DeskState> result = service.get();
        System.out.println("Result: " + result);
        return ResponseEntity.ok(result);
    }
 }
