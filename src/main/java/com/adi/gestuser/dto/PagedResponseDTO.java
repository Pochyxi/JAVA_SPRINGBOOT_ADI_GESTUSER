package com.adi.gestuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponseDTO<T> {

    private List<T> content;

    private int pageNo;

    private int pageSize;

    private Long totalElements;

    private int totalPages;

    private boolean last;

}
