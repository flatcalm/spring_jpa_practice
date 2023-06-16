package com.spring.jpa.chap03_pagination.repository;

import com.spring.jpa.chap02_querymethod.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// JPA는 INSERT, UPDATE, DELETE 시에 트랜잭션을 기준으로 동작하는 경우가 많음.
// 기능을 보장받기 위해 웬만하면 트랜잭션 기능을 함께 사용해야 합니다.
// 나중에 MVC 구조에서 Service 클래스에 아노테이션을 첨부하면 됩니다.
@Transactional
@Rollback(false)
class StudentPageRepositoryTest {

    @Autowired
    StudentPageRepository studentPageRepository;

    @BeforeEach
    void bulkInsert() {
        // 학생을 147명 저장
        for (int i=1; i<=147; i++) {
            Student s = Student.builder()
                    .name("김테스트" + i)
                    .city("도시" + i)
                    .major("전공" + i)
                    .build();
            studentPageRepository.save(s);
        }
    }

    @Test
    @DisplayName("기본 페이징 테스트")
    void testBasicPagination() {
        //given
        int pageNo = 3;
        int amount = 10;

        // 페이지 정보 생성
        // 페이지 번호가 zero-based
        Pageable pageInfo
                = PageRequest.of(pageNo - 1
                , amount
//              , Sort.by("name").descending() // 정렬 기준 엔터티 필드명으로 작성!
                , Sort.by(
                        Sort.Order.desc("name"),
                        Sort.Order.asc("city")
                )
        );

        //when
        Page<Student> students = studentPageRepository.findAll(pageInfo);

        // getContent() : 페이징이 완료된 데이터셋을 가져오는 메서드
        List<Student> studentList = students.getContent();

        // 총 페이지 수
        int totalPages = students.getTotalPages();
        // 총 학생 수
        long totalElements = students.getTotalElements();
        // 다음 페이지가 있는지 여부 (다음 버튼)
        boolean next = students.hasNext();
        // 이전 페이지가 있는지 여부 (이전 버튼)
        boolean prev = students.hasPrevious();

        //then
        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("totalElements = " + totalElements);
        System.out.println("prev = " + prev);
        System.out.println("next = " + next);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("이름 검색 + 페이징")
    void testSearchAndPagination() {
        //given
        int pageNo = 1;
        int size = 10;
        Pageable pageInfo = PageRequest.of(pageNo - 1, size);

        //when
        Page<Student> students = studentPageRepository.findByNameContaining("3", pageInfo);

        int totalPages = students.getTotalPages();
        long totalElements = students.getTotalElements();

        //then
        System.out.println("\n\n\n");
        System.out.println("totalElements = " + totalElements);
        System.out.println("totalPages = " + totalPages);
        students.getContent().forEach(System.out::println);
        System.out.println("\n\n\n");
    }

}