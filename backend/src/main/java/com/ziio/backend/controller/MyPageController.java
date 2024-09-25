package com.ziio.backend.controller;

import com.ziio.backend.dto.MyPageDTO;
import com.ziio.backend.entity.MyPage;
import com.ziio.backend.service.MyPageService;
import com.ziio.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/mypages")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;
    @Autowired
    private JwtUtil jwtUtil;

    // 사용자의 모든 마이페이지 일정을 반환하는 메소드(= 마이페이지 첫 화면)
    @GetMapping
    public ResponseEntity<List<MyPageDTO.GetResponse>> getUserMyPages(
            @RequestHeader("Authorization") String authorizationHeader) {

        // 토큰에서 유저 이메일 가져오기
        String jwtToken = jwtUtil.getJwtTokenFromHeader(authorizationHeader);
        String userEmail = jwtUtil.getEmailFromToken(jwtToken);

        // 사용자의 마이페이지 목록
        List<MyPage> myPages = myPageService.getAllMyPagesByUserEmail(userEmail);

        // 응답 객체 생성 및 반환
//        List<MyPageDTO.GetResponse> response = new ArrayList<>();
//        for (MyPage mypage : myPages) {
//            MyPageDTO.GetResponse res = MyPageDTO.GetResponse.builder()
//                    .my_page_id(mypage.getMy_page_id())
//                    .start_date(mypage.getStart_date())
//                    .end_date(mypage.getEnd_date())
//                    .title(mypage.getTitle())
//                    .url(mypage.getUrl())
//                    .color_code(mypage.getColor_code())
//                    .memo(mypage.getMemo())
//                    .build();
//
//            response.add(res);
//        }
        //for 루프 -> streamAPI 사용으로 수정
        List<MyPageDTO.GetResponse> response = myPages.stream()
                .map(mypage -> MyPageDTO.GetResponse.builder()
                        .my_page_id(mypage.getMy_page_id())
                        .start_date(mypage.getStart_date())
                        .end_date(mypage.getEnd_date())
                        .title(mypage.getTitle())
                        .url(mypage.getUrl())
                        .color_code(mypage.getColor_code())
                        .memo(mypage.getMemo())
                        .build())
                .collect(Collectors.toList());


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자의 개인 일정을 등록하는 메소드
    @PostMapping
    public ResponseEntity<MyPageDTO.PostResponse> addMyPage(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody MyPageDTO.Request request) {

        // 토큰에서 유저 이메일 가져오기
        String jwtToken = jwtUtil.getJwtTokenFromHeader(authorizationHeader);
        String userEmail = jwtUtil.getEmailFromToken(jwtToken);

        // 저장 및 응답 객체 반환
        return new ResponseEntity<>(myPageService.addMyPage(request, userEmail), HttpStatus.CREATED);
    }

    // 사용자의 개인 일정을 삭제하는 메소드
    @DeleteMapping
    public ResponseEntity<MyPageDTO.DeleteResponse> removeMyPage(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody MyPageDTO.Request request) {

        // 토큰에서 유저 이메일 가져오기
        String jwtToken = jwtUtil.getJwtTokenFromHeader(authorizationHeader);
        String userEmail = jwtUtil.getEmailFromToken(jwtToken);

        // 마이페이지 id는 필수값
        if (request.getMy_page_id() == null) {
            throw new IllegalArgumentException("MyPage ID cannot be null.");
        }
        Long myPageId = request.getMy_page_id();

        // 삭제 및 응답 객체 반환
        return new ResponseEntity<>(myPageService.removeMyPage(myPageId, userEmail), HttpStatus.OK);
    }

    // 사용자의 요청에 따라 일정을 수정하는 메소드
    @PatchMapping
    public ResponseEntity<MyPageDTO.PostResponse> updateMyPage(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody MyPageDTO.Request request) {

        // 토큰에서 유저 이메일 가져오기
        String jwtToken = jwtUtil.getJwtTokenFromHeader(authorizationHeader);
        String userEmail = jwtUtil.getEmailFromToken(jwtToken);

        // 마이페이지 id는 필수값
        if (request.getMy_page_id() == null) {
            throw new IllegalArgumentException("MyPage ID cannot be null.");
        }
        Long myPageId = request.getMy_page_id();

        // 마이페이지 일정 업데이트
        MyPage updatedMyPage = myPageService.updateMyPage(myPageId, request, userEmail);

        // 응답 객체 생성 및 반환
        MyPageDTO.PostResponse updatedMyPageResponse = MyPageDTO.PostResponse.builder()
                .my_page_id(myPageId)
                .start_date(updatedMyPage.getStart_date())
                .end_date(updatedMyPage.getEnd_date())
                .title(updatedMyPage.getTitle())
                .url(updatedMyPage.getUrl())
                .color_code(updatedMyPage.getColor_code())
                .memo(updatedMyPage.getMemo())
                .message("successfully updated.")
                .build();

        return new ResponseEntity<>(updatedMyPageResponse, HttpStatus.OK);
    }
}
