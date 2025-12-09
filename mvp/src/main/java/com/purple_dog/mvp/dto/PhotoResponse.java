package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponse {
    private Long id;
    private String url;
    private Integer displayOrder;
    private Boolean isPrimary;
}
