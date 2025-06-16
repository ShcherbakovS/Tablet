package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CommentDTO;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ExcelFormsService {

    private final CommentService commentService;


    public ByteArrayResource CreateCommentsForm(String capitalCSCode) {

        List<CommentDTO> commentsList = commentService.getAllComments(capitalCSCode);

        Workbook workbook = new XSSFWorkbook();

        return null;

    }


    private Row createRow() {






        return null;
    }

}
