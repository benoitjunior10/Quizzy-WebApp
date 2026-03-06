package com.quizzy.quizzy_webapp.dto;
import lombok.Data;
import java.util.List;
@Data
public class PageResponseDto<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
}
