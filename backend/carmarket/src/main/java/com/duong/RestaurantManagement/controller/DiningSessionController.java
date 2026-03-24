package com.duong.RestaurantManagement.controller;


import com.duong.RestaurantManagement.dto.dining_session.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.service.DiningSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dining-sessions")
@RequiredArgsConstructor
public class DiningSessionController {

    private final DiningSessionService diningSessionService;

    @GetMapping("")
    public ResponseEntity<GetDiningSessionDTO> getDiningSessions(
            @RequestParam String tableQrToken
    ) {
      return ResponseEntity.ok(diningSessionService.getDiningSession(tableQrToken)) ;
    }

    @PatchMapping("/{diningSessionId}/deactivate") // only for testing during building era
    public ResponseEntity<String> deactivateDiningSession(
            @PathVariable Long diningSessionId
    ){
        diningSessionService.deactiveDinningSession(diningSessionId);
        return ResponseEntity.ok("Success");
    }
}
