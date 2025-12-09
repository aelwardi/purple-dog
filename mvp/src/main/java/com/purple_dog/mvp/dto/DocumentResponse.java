package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String description;
}
