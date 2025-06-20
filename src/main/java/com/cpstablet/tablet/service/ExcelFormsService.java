package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.*;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.SubObjectRepo;
import lombok.AllArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class ExcelFormsService {

    private final CapitalCSRepo capitalCSRepo;
    private final CommentService commentService;
    private final String monitoringPath = "src/main/resources/Samples/Monitoring_sample.xlsx";

    private final SubObjectRepo subObjectRepo;

    public byte[] createMonitoring(String capitalCSCode) {

        List<CommentDTO> commentsList = commentService.getAllComments(capitalCSCode);

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(capitalCSCode).orElseThrow(()-> new RuntimeException("Объект не найден"));

        try (InputStream monitoringSample = new FileInputStream(monitoringPath);
             Workbook workbook =WorkbookFactory.create(monitoringSample))  {

            workbook.setForceFormulaRecalculation(true);

            createMarketConditionsSheet(workbook, capitalCS);

            createProgressOfWorkSheet(workbook, subObjectRepo.findByCCSCode(capitalCSCode));

            try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                workbook.write(outputStream);
                return outputStream.toByteArray();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    // Заполнение листа "Конъюнктура
    private void createMarketConditionsSheet(Workbook workbook, CapitalCS capitalCS) {

        Sheet sheet = workbook.getSheetAt(0);

        sheet.getRow(1).getCell(CellReference.convertColStringToIndex("A")).setCellValue(capitalCS.getCapitalCSName());
        sheet.getRow(2).getCell(CellReference.convertColStringToIndex("H")).setCellValue(capitalCS.getCustomer());
        sheet.getRow(3).getCell(CellReference.convertColStringToIndex("H")).setCellValue("Эксплуатирующая организация");
        sheet.getRow(4).getCell(CellReference.convertColStringToIndex("H")).setCellValue(capitalCS.getCIWExecutor());
        sheet.getRow(5).getCell(CellReference.convertColStringToIndex("H")).setCellValue(capitalCS.getCWExecutor());
        sheet.getRow(6).getCell(CellReference.convertColStringToIndex("H")).setCellValue("Генеральный проектировщик");
        sheet.getRow(8).getCell(CellReference.convertColStringToIndex("E")).setCellValue(LocalDate.now());
        sheet.getRow(8).getCell(CellReference.convertColStringToIndex("I")).setCellValue(capitalCS.getCodeCCS());
        sheet.getRow(10).getCell(CellReference.convertColStringToIndex("D")).setCellValue(capitalCS.getCWExecutor());

    }
    // Заполнение листа "Ход работ"
    private void createProgressOfWorkSheet(Workbook workbook, List<SubObject> subObjects) {

        CellStyle style = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        Font font = workbook.createFont();

        Sheet sheet = workbook.getSheetAt(1);

        List<PNRSystem> systems = new ArrayList<>();

        subObjects.stream().forEach(sub-> sub.getPNRSystems().stream().forEach(system -> systems.add(system)));

        sheet.getRow(15).getCell((CellReference.convertColStringToIndex("A"))).setCellValue("Мониторинг выполнения ПНР на объекте "+
                capitalCSRepo.findByCodeCCS(subObjects.get(0).getCCSCode()).get().getCapitalCSName());


        int startValue = 17;

        for (int i = startValue; i < systems.stream().count() + startValue; i++) {
            createRow(sheet, i);
        }
        wrightHeader(workbook);

        for (var system :  systems) {

            cells(sheet.getRow(startValue),20, style, font);
            wrightCellsToWorkSheet(sheet.getRow(startValue), system, style, creationHelper);
            startValue ++;
        }

    }
    // Заполнение листа "Замечания"
    private void createCommentsSheet(List<Comment> comments) {



    }

    //Заполнение листа "Дефекты"
    private void createDefectiveActsSheet(List<DefectiveAct> defectiveActs) {

    }
    // Заполнение листа "Персонал"
    private void createStaffSheet() {
        /*
        По персоналу кроме количества сотрудников данных нет
         */
    }

    private Row createRow(Sheet sheet, int index) {

        return sheet.createRow(index);
    }
    private void cells(Row row, int cellsQuantity, CellStyle style, Font font) {

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short)14);
        style.setFont(font);


        for (int i = 0; i < cellsQuantity; i++) {
            row.createCell(i).setCellStyle(style);
        }

    }
    //Заполнение значениями ячеек на листе "Ход работ"
    private void wrightCellsToWorkSheet(Row row, PNRSystem system, CellStyle cellStyle, CreationHelper creationHelper) {

        cellStyle.setDataFormat(
                creationHelper.createDataFormat().getFormat("dd.MM.yyyy")
        );

        row.getCell(CellReference.convertColStringToIndex("A")).setCellValue(" ");
        row.getCell(CellReference.convertColStringToIndex("B")).setCellValue(system.getSubObject().getSubObjectName());
        row.getCell(CellReference.convertColStringToIndex("C")).setCellValue(system.getPNRSystemName());
        row.getCell(CellReference.convertColStringToIndex("D")).setCellValue(system.getPNRSystemKO());
        row.getCell(CellReference.convertColStringToIndex("E")).setCellValue(system.getPNRSystemII());
        row.getCell(CellReference.convertColStringToIndex("F")).setCellValue(system.getPNRSystemName());
        row.getCell(CellReference.convertColStringToIndex("G")).setCellValue(system.getPNRSystemRD());
        row.getCell(CellReference.convertColStringToIndex("H")).setCellValue(LocalDate.parse(system.getPNRPlanDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        row.getCell(CellReference.convertColStringToIndex("J")).setCellFormula("K" + (row.getRowNum()+1) + "-H" + (row.getRowNum()+1));
//        row.getCell(CellReference.convertColStringToIndex("J")).setCellValue(system.getPNRSystemStatus());
        row.getCell(CellReference.convertColStringToIndex("K"))
                .setCellValue(LocalDate.parse(system.getPNRFactDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        row.getCell(CellReference.convertColStringToIndex("L")).setCellValue(system.getPNRSystemStatus());
        row.getCell(CellReference.convertColStringToIndex("M"))
                .setCellValue(LocalDate.parse(system.getIIPlanDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        row.getCell(CellReference.convertColStringToIndex("N"))
                .setCellValue(LocalDate.parse(system.getIIFactDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        row.getCell(CellReference.convertColStringToIndex("O"))
                .setCellValue(LocalDate.parse(system.getKOPlanDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        row.getCell(CellReference.convertColStringToIndex("P"))
                .setCellValue(LocalDate.parse(system.getKOFactDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        row.getCell(CellReference.convertColStringToIndex("Q")).setCellValue("Примечание");
        row.getCell(CellReference.convertColStringToIndex("R")).setCellValue(system.getCWExecutor());


        // клетки с форматом ДАТА
        row.getCell(CellReference.convertColStringToIndex("H")).setCellStyle(cellStyle);
        row.getCell(CellReference.convertColStringToIndex("K")).setCellStyle(cellStyle);
        row.getCell(CellReference.convertColStringToIndex("M")).setCellStyle(cellStyle);
        row.getCell(CellReference.convertColStringToIndex("N")).setCellStyle(cellStyle);
        row.getCell(CellReference.convertColStringToIndex("O")).setCellStyle(cellStyle);
        row.getCell(CellReference.convertColStringToIndex("P")).setCellStyle(cellStyle);


    }
    private void wrightHeader(Workbook workbook) {

        Sheet sheet = workbook.getSheetAt(1);
        int lastRowNum = sheet.getLastRowNum() + 1;

        int firstRow = 18;

        System.out.println(lastRowNum + "ПОСЛЕДНИЙ РЯД ЛИСТА");

        sheet.getRow(1).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIF(H18:H" + lastRowNum + ",\"<=\"&K2)");
        sheet.getRow(2).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIFS(H18:H" + lastRowNum + ",\"<=\"&K2,L18:L" + lastRowNum + ",\"СМР\")");

        sheet.getRow(3).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNT(I" + firstRow + ":I" + lastRowNum + ")");

        sheet.getRow(4).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIF(K18:K" + lastRowNum + ", \"<>\"\"\")");

        sheet.getRow(5).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("G4-G5");

        sheet.getRow(6).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIF(E18:E" + lastRowNum + ", \"<>\"\"\")");

        sheet.getRow(7).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIFS(L18:L" + lastRowNum + ",\"Акт ИИ на подписи\")");

        sheet.getRow(8).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNT(N18:N" + lastRowNum + ")");

        sheet.getRow(9).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIF(D18:D" + lastRowNum + ", \"<>\"\"\")");

        sheet.getRow(10).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTIFS(L18:L" + lastRowNum + ",\"Акт КО на подписи\",D18:D" + lastRowNum + ",\"<>\"\"\")");

        sheet.getRow(11).getCell(CellReference.convertColStringToIndex("G"))
                .setCellFormula("COUNTA(P18:P" + lastRowNum + ")");
    }


}
