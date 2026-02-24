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
        service.post(postRequestDto.deskId(), postRequestDto.personPresent(), postRequestDto.stuffOnDesk());
        return ResponseEntity.ok("Good");
    }

    @PutMapping
    public ResponseEntity<String> put(@RequestBody PutRequestDto putRequestDto) {
        service.put(putRequestDto.deskId(), putRequestDto.personPresent(), putRequestDto.stuffOnDesk());
        return ResponseEntity.ok("Good");
    }

    @GetMapping
    public ResponseEntity<List<DeskStatus>> get() {
        List<DeskStatus> result = service.get();
        System.out.println(result);
        return ResponseEntity.ok(result);
    }
 }
