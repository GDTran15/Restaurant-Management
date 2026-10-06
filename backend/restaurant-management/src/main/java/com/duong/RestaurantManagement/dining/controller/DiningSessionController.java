package com.duong.RestaurantManagement.dining.controller;


import com.duong.RestaurantManagement.dining.dto.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dining-sessions")
@RequiredArgsConstructor
public class DiningSessionController {
    private final com.duong.RestaurantManagement.auth.service.SessionAccess sessionAccess;

    private final DiningSessionService diningSessionService;

    @GetMapping("")
    public ResponseEntity<GetDiningSessionDTO> getDiningSessions(
            @RequestParam String tableQrToken
    ) {
      return ResponseEntity.ok(diningSessionService.getDiningSession(tableQrToken)) ;
    }

    @GetMapping("/{diningSessionId}/order-items")
    public ResponseEntity<List<GetOrderItemDTO>> getDiningSessionOrderItems(@PathVariable Long diningSessionId) {
        sessionAccess.requireSession(diningSessionId);
        return  ResponseEntity.ok(diningSessionService.getDiningSessionOrderItems(diningSessionId));
    }

 
}
