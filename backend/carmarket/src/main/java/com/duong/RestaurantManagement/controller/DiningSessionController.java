package com.duong.RestaurantManagement.controller;


import com.duong.RestaurantManagement.dto.dining_session.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.service.DiningSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
