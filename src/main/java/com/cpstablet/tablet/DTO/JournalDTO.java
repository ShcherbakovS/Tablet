package com.cpstablet.tablet.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JournalDTO {

    private Long id;

    private String description;

    private String user;

    private String subObject;

    private String system;

    private String capitalCS;

    private String date;

    private Long serialNumber;
}
