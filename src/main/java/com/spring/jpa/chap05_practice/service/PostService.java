package com.spring.jpa.chap05_practice.service;

import com.spring.jpa.chap05_practice.dto.*;
import com.spring.jpa.chap05_practice.entity.HashTag;
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

    public PostDetailResponseDTO insert(final PostCreateDTO dto) // 매개 변수를 final로 선언하면 변경 불가능.
        throws RuntimeException {

        // 게시물 저장
        Post saved = postRepository.save(dto.toEntity());

        // 해시태그 저장
        List<String> hashTags = dto.getHashTags();
        if(hashTags != null && hashTags.size() > 0) {
            hashTags.forEach(ht -> {
                HashTag savedTag = hashTagRepository.save(
                        HashTag.builder()
                                .tagName(ht)
                                .post(saved)
                                .build()
                );
                // Post Entity는 DB에 save를 진행할 때 HashTag에 대한 내용을 갱신하지 않습니다.
                // HashTag Entity는 따로 save를 진행합니다.
                // HashTag는 연관 관계의 주인이기 때문에 save를 진행할 때 Post를 전달하기 때문에
                // DB와 Entity와의 상태가 동일하지만, Post는 HashTag의 정보가 비어있는 상태입니다.
                // Post Entity에 연관 관계 편의 메서드를 작성하여 HashTag의 내용을 동기화 해야
                // 추후에 진행되는 과정에서 문제가 발생하지 않습니다. (연관 관계의 주인이 아니기 때문)
                saved.addHashTag(savedTag);
            });
        }

        return new PostDetailResponseDTO(saved);
    }
}
