package com.spring.jpa.chap05_practice.api;

import com.spring.jpa.chap05_practice.dto.*;
import com.spring.jpa.chap05_practice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "post API", description = "게시물 조회, 등록 및 수정, 삭제 api")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    // 리소스 : 게시물 (Post)
    /*
        게시물 목록 조회 : /posts       - GET
        게시물 개별 조회 : /posts/{id}  - GET
        게시물 등록 : /posts           - POST
        게시물 수정 : /posts           - PATCH (PUT도 가능)
        게시물 삭제 : /posts/{id}      - DELETE

        Rest API 관례
        PUT -> 전체를 수정할 때 (객체 자체를 갈아끼워야 할 때)
        PATCH -> 일부를 수정할 때

     */

    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/v1/posts?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        PostListResponseDTO dto = postService.getPosts(pageDTO);

        return ResponseEntity
                .ok()
                .body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable long id) {
        log.info("/api/v1/post/{} : GET", id);

        try {
            PostDetailResponseDTO dto = postService.getDetail(id);
            return ResponseEntity.ok().body(dto); // 문제가 없다면 이렇게 처리
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "게시물 작성", description = "게시물 작성을 담당하는 메서드입니다.")
    @Parameters(
            {
                    @Parameter(name = "writer", description = "게시물의 작성자 이름을 쓰세요!", example = "김뽀삐", required = true),
                    @Parameter(name = "title", description = "게시물의 제목을 쓰세요!", example = "글 제목", required = true),
                    @Parameter(name = "content", description = "게시물의 내용을 쓰세요!", example = "글 내용"),
                    @Parameter(name = "hashTags", description = "게시물의 해시태그를 작성하세요!", example = "['하하', '호호']")
            }
    )
    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody PostCreateDTO dto
            , BindingResult result // 검증 에러 정보를 가진 객체
    ) {

        log.info("/api/v1/posts : POST - payload : {}", dto);

        if(dto == null) {
            return ResponseEntity
                    .badRequest()
                    .body("등록 게시물 정보를 전달해 주세요!");
        }

        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);

        if (fieldErrors != null) return fieldErrors;

        try {
            PostDetailResponseDTO responseDTO = postService.insert(dto);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body("미안 서버 터졌어... 원인 : " + e.getMessage());
        }

    }

    // 게시물 수정
    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<?> update(
            @Validated @RequestBody PostModifyDTO dto,
            BindingResult result,
            HttpServletRequest request) {

        log.info("/api/v1/posts {} - dto : {}"
                    , request.getMethod(), dto);

        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if(fieldErrors != null) return fieldErrors;

        PostDetailResponseDTO responseDTO = postService.modify(dto);

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        log.info("api/v1/posts/{} DELETE!", id);

        try {
            postService.delete(id);
            return ResponseEntity
                    .ok("DEL SUCCESS!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }

    }

    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if(result.hasErrors()) { // 입력값 검증에 걸림
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString());
            });

            return ResponseEntity
                    .badRequest()
                    .body(fieldErrors);
        }
        return null;
    }

}
