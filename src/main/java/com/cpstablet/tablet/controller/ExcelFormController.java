package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.service.ExcelFormsService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/excelForms")
@AllArgsConstructor
public class ExcelFormController {

    private final ExcelFormsService excelFormsService;

    @GetMapping("/getMonitoring/{CCSCode}")
    public ResponseEntity getMonitoring(@PathVariable("CCSCode") String CCSCode) {


        byte[] excelContent = excelFormsService.createMonitoring(CCSCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("report.xlsx")
                .build());
        headers.add("Content-Transfer-Encoding", "binary");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

}
