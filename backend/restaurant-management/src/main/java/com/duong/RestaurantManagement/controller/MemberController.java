package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.member.request.AddMemberRequest;
import com.duong.RestaurantManagement.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping()
    public ResponseEntity<Void> addMember(@RequestBody @Valid AddMemberRequest addMemberRequest) {
        memberService.addMember(addMemberRequest);
        return ResponseEntity.ok().build();
    }
}
