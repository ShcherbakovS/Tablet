package com.cpstablet.tablet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoDTO {

    private String fileName;
    private String contentType;
    private Long size;
    private byte[] bytes;
    private Long commentId;
}
