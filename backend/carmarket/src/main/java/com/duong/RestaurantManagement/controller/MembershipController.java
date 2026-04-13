package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.member.request.AddMembershipRequest;
import com.duong.RestaurantManagement.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping()
    public ResponseEntity<Void> addMembership(@RequestBody AddMembershipRequest addMembershipRequest) {
        membershipService.addMembership(addMembershipRequest);
        return ResponseEntity.noContent().build();
    }




}
