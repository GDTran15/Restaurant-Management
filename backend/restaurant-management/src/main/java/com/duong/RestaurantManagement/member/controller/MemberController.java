package com.duong.RestaurantManagement.member.controller;

import com.duong.RestaurantManagement.member.dto.request.AddMemberRequest;
import com.duong.RestaurantManagement.member.dto.response.GetMemberInfoDTO;
import com.duong.RestaurantManagement.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/phone")
    public ResponseEntity<GetMemberInfoDTO> getMemberByPhoneNumber(@RequestParam String phoneNumber){
         return ResponseEntity.ok(memberService.getMemberByPhoneNumber(phoneNumber));
    }

}
