package com.spring.jpa.chap05_practice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostCreateDTO {

    @NotBlank
    @Size(min = 2, max = 5)
    private String writer;

//    @NotNull -> null을 허용하지 않음. "", " "은 허용
//    @NotEmpty -> null, ""을 허용하지 않음. " "은 허용
    @NotBlank // -> null, "", " " 모두 허용하지 않음
    @Size(min = 1, max = 20)
    private String title;

    private String content;

    private List<String> hashTags;

}
