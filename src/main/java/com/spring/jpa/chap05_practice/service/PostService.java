package com.spring.jpa.chap05_practice.service;

import com.spring.jpa.chap05_practice.dto.*;
import com.spring.jpa.chap05_practice.entity.Post;
import com.spring.jpa.chap05_practice.repository.HashTagRepository;
import com.spring.jpa.chap05_practice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional // JPA 레파지토리는 트랜잭션 단위로 동작하기 때문에 작성해 주세요!
public class PostService {

    private final PostRepository postRepository;
    private final HashTagRepository hashTagRepository;

    public PostListResponseDTO getPosts(PageDTO pageDTO) {

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(
                pageDTO.getPage() -1,
                pageDTO.getSize(),
                Sort.by("createDate").descending()
        );

        // 데이터베이스에서 게시물 목록 조회
        Page<Post> posts = postRepository.findAll(pageable);

        // 게시물 정보만 꺼내기
        List<Post> postList = posts.getContent();

        // 엔터티 객체를 DTO 객체로 변환한 결과 리스트.
        List<PostDetailResponseDTO> deatilList
                = postList.stream()
                .map(post -> new PostDetailResponseDTO(post))
                .collect(Collectors.toList());

        // DB에서 조회한 정보(ENTITY)를 JSON 형태에 맞는 DTO로 변환
        return PostListResponseDTO.builder()
                .count(deatilList.size()) // 총 게시물 수가 아니라 조회된 게시물 수
                .pageInfo(new PageResponseDTO(posts))  // 생성자에게 Page 정보가 담긴 객체를 그대로 전달
                .posts(deatilList)
                .build();
    }

    public PostDetailResponseDTO getDetail(long id) {

        Post postEntity = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException(id + "번 게시물이 존재하지 않습니다.")
        );

        return new PostDetailResponseDTO(postEntity);

    }

    public PostDetailResponseDTO insert(PostCreateDTO dto) {

        return null;
    }
}
