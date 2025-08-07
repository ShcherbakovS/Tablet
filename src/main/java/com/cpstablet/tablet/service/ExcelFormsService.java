package com.cpstablet.tablet.service;
import com.cpstablet.tablet.entity.*;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


    @Service
    @AllArgsConstructor
    public class ExcelFormsService {

        private final SystemRepo systemRepo;
        private final CapitalCSRepo capitalCSRepo;
        private final DefectiveActRepo defectiveActRepo;
        private final String monitoringPath = "Samples/Monitoring_sample.xlsx";
        private final String journalPath = "Samples/Journal.xlsx";
        private final SubObjectRepo subObjectRepo;
        CommentRepo commentRepo;

        public byte[] createMonitoring(String capitalCSCode) {

            List<Comment> commentsList = commentRepo.findCommentsByCodeCCS(capitalCSCode).stream()
                    .sorted(Comparator.comparing(Comment::getSerialNumber)).collect(Collectors.toList());

            List<DefectiveAct> defectiveActs = defectiveActRepo.findAllByCodeCCS(capitalCSCode).stream()
                    .sorted(Comparator.comparing(DefectiveAct::getSerialNumber)).collect(Collectors.toList());


            CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(capitalCSCode).orElseThrow(()-> new RuntimeException("Объект не найден"));

            try (InputStream monitoringSample = getClass().getClassLoader().getResourceAsStream(monitoringPath);
                 Workbook workbook =WorkbookFactory.create(monitoringSample))  {

                workbook.setForceFormulaRecalculation(true);

                createMarketConditionsSheet(workbook, capitalCS);

                createCommentsSheet(workbook, commentsList);

                createDefectiveActsSheet(workbook, defectiveActs);

                wrightCommentSheetHeader(workbook.getSheetAt(2));
                wrightDefectiveActSheetHeader(workbook.getSheetAt(3));

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

            subObjects.stream().forEach(sub-> sub.getPNRSystems().stream().sorted(Comparator.comparing(PNRSystem::getPNRSystemId))
                    .forEach(system -> systems.add(system)));

            sheet.getRow(15).getCell((CellReference.convertColStringToIndex("A"))).setCellValue("Мониторинг выполнения ПНР на объекте "+
                    capitalCSRepo.findByCodeCCS(subObjects.get(0).getCCSCode()).get().getCapitalCSName());


            final int[] startValue = {17};

            for (int i = startValue[0]; i < systems.stream().count() + startValue[0]; i++) {
                createRow(sheet, i);
            }

            for(var sub : subObjects) {

                sub.getPNRSystems().forEach(sys-> {

                    cells(sheet.getRow(startValue[0]),20, style, font);

                    wrightCellsToWorkSheet(sheet.getRow(startValue[0]), sys, style, creationHelper);

                    startValue[0]++;
                });

            }

//            for (var system :  systems) {
//
//                cells(sheet.getRow(startValue[0]),20, style, font);
//
//                wrightCellsToWorkSheet(sheet.getRow(startValue[0]), system, style, creationHelper);
//
//                startValue[0]++;
//            }
            wrightHeader(workbook);
            mergeCells(sheet, 2 );
            mergeCells(sheet, 3 );
            mergeCells(sheet, 14 );
            mergeCells(sheet, 15 );

        }
        // Заполнение листа "Замечания"
        private void createCommentsSheet(Workbook workbook, List<Comment> comments) {

        if(comments.isEmpty()) {
            return;
        }

        CellStyle style = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        Font font = workbook.createFont();

        Sheet sheet = workbook.getSheetAt(2);

            int startValue = 7;

            for (int i = startValue; i < comments.stream().count() + startValue; i++) {
                Row row = createRow(sheet, i);
                cells(row,14, style, font);
            }

            for (var comment : comments) {
                wrightCellsToCommentsSheet(sheet.getRow(startValue), comment, creationHelper, style);
                startValue ++;
            }


        }
        //Заполнение листа замечаний
        private void wrightCellsToCommentsSheet(Row row, Comment comment, CreationHelper creationHelper, CellStyle cellStyle) {

            cellStyle.setDataFormat(
                    creationHelper.createDataFormat().getFormat("dd.MM.yyyy")
            );

            PNRSystem system = systemRepo.getAllByCCSNumber(comment.getCodeCCS()).stream().filter(sys -> sys.getPNRSystemII().equals(comment.getIiNumber()))
                            .filter(sys -> sys.getPNRSystemName().equals(comment.getSystemName())).findFirst().orElseThrow(()->new RuntimeException("Лист замечаний. Система не найдена"));

            row.getCell(CellReference.convertColStringToIndex("A")).setCellValue(comment.getSerialNumber());
            row.getCell(CellReference.convertColStringToIndex("B")).setCellValue(comment.getExecutor());
            row.getCell(CellReference.convertColStringToIndex("C")).setCellValue(comment.getUserName());
            row.getCell(CellReference.convertColStringToIndex("D")).setCellValue(comment.getDescription());
            row.getCell(CellReference.convertColStringToIndex("E")).setCellValue(comment.getSubObject());
            row.getCell(CellReference.convertColStringToIndex("F")).setCellValue(comment.getSystemName());
            row.getCell(CellReference.convertColStringToIndex("G")).setCellValue(comment.getIiNumber());
            row.getCell(CellReference.convertColStringToIndex("H")).setCellValue(system.getPNRSystemRD());
            row.getCell(CellReference.convertColStringToIndex("I")).setCellValue(comment.getCommentCategory());
            row.getCell(CellReference.convertColStringToIndex("J")).setCellValue(comment.getStartDate());
            row.getCell(CellReference.convertColStringToIndex("K")).setCellValue(comment.getEndDatePlan().equals(" ")? null :comment.getEndDatePlan());
            row.getCell(CellReference.convertColStringToIndex("L"))
                    .setCellValue(comment.getExecutor());
            row.getCell(CellReference.convertColStringToIndex("M"))
                    .setCellValue(comment.getEndDateFact().equals(" ")? null : comment.getEndDateFact());
            row.getCell(CellReference.convertColStringToIndex("N")).setCellValue(comment.getCommentStatus());

            CellStyle numberCellStyle = createStyle(cellStyle, row.getSheet().getWorkbook().createFont(), (short) 0);
            row.getCell(CellReference.convertColStringToIndex("A")).
                    setCellStyle(numberCellStyle);

            row.getCell(CellReference.convertColStringToIndex("M")).setCellStyle(cellStyle);

                }
        // Формулы для заголовка лилста замечаний
        private void wrightCommentSheetHeader(Sheet sheet) {

            Row row = sheet.getRow(1);

            row.getCell(CellReference.convertColStringToIndex("E")).setCellFormula("COUNT(A8:A" + (sheet.getLastRowNum()+ 1) + ")");
            row.getCell(CellReference.convertColStringToIndex("F")).setCellFormula("COUNTIF(M8:M" + (sheet.getLastRowNum() + 1) + ",\"<>\")");


        }
        //Создание листа "Дефекты"
        private void createDefectiveActsSheet(Workbook workbook, List<DefectiveAct> defectiveActs) {

            if(defectiveActs.isEmpty()) {
                return;
            }

            CellStyle style = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            Font font = workbook.createFont();

            Sheet sheet = workbook.getSheetAt(3);

            int startValue = 7;

            for (int i = startValue; i < defectiveActs.stream().count() + startValue; i++) {
                Row row = createRow(sheet, i);
                cells(row,18, style, font);
            }

            for (var defAct : defectiveActs) {
                wrightCellsToDefectsSheet(sheet.getRow(startValue), defAct, creationHelper, style);
                startValue ++;
            }


        }
        // Заполнение листа дефектов
        private void wrightCellsToDefectsSheet(Row row, DefectiveAct defAct, CreationHelper creationHelper, CellStyle style) {

            style.setDataFormat(
                    creationHelper.createDataFormat().getFormat("dd.MM.yyyy")
            );
            PNRSystem system = systemRepo.getAllByCCSNumber(defAct.getCodeCCS()).stream().filter(sys -> sys.getPNRSystemII().equals(defAct.getIiNumber()))
                            .filter(sys -> sys.getPNRSystemName().equals(defAct.getSystemName())).findFirst().orElseThrow(()->new RuntimeException());

            row.getCell(CellReference.convertColStringToIndex("B")).setCellValue(defAct.getSerialNumber());
            row.getCell(CellReference.convertColStringToIndex("C")).setCellValue(defAct.getSerialNumber());
            row.getCell(CellReference.convertColStringToIndex("D")).setCellValue(defAct.getStartDate());
            row.getCell(CellReference.convertColStringToIndex("E")).setCellValue(defAct.getSubObject());
            row.getCell(CellReference.convertColStringToIndex("F")).setCellValue(defAct.getSystemName());
            row.getCell(CellReference.convertColStringToIndex("G")).setCellValue(defAct.getIiNumber());
            row.getCell(CellReference.convertColStringToIndex("H")).setCellValue(defAct.getSubObject());
            row.getCell(CellReference.convertColStringToIndex("I")).setCellValue(defAct.getManufacturer());
            row.getCell(CellReference.convertColStringToIndex("J")).setCellValue(system.getPNRSystemRD());
            row.getCell(CellReference.convertColStringToIndex("K")).setCellValue(defAct.getEquipment());
            row.getCell(CellReference.convertColStringToIndex("L")).setCellValue(defAct.getManufacturerNumber());
            row.getCell(CellReference.convertColStringToIndex("M")).setCellValue(defAct.getStartDate().equals(" ")? null : defAct.getStartDate());
            row.getCell(CellReference.convertColStringToIndex("N")).setCellValue(defAct.getDescription());
            row.getCell(CellReference.convertColStringToIndex("O")).setCellValue(defAct.getEndDatePlan().equals(" ")? null : defAct.getEndDatePlan());
            row.getCell(CellReference.convertColStringToIndex("P")).setCellValue(defAct.getEndDateFact().equals(" ")? null : defAct.getEndDateFact());
            // Зону ответственности не заполняем
//            row.getCell(CellReference.convertColStringToIndex("Q")).setCellValue(defAct.getExecutor());
            row.getCell(CellReference.convertColStringToIndex("R")).setCellValue(defAct.getDefectiveActStatus());
            // клетки с форматом ДАТА
            row.getCell(CellReference.convertColStringToIndex("M")).setCellStyle(style);
            row.getCell(CellReference.convertColStringToIndex("O")).setCellStyle(style);
            row.getCell(CellReference.convertColStringToIndex("P")).setCellStyle(style);

            //числовые значения для столбцов B и C
            CellStyle numberCellStyleB = createStyle(style, row.getSheet().getWorkbook().createFont(), (short) 0);
            row.getCell(CellReference.convertColStringToIndex("B")).
                    setCellStyle(numberCellStyleB);
            CellStyle numberCellStyleC = createStyle(style, row.getSheet().getWorkbook().createFont(), (short) 0);
            row.getCell(CellReference.convertColStringToIndex("A")).
                    setCellStyle(numberCellStyleC);



        }
        private void wrightDefectiveActSheetHeader(Sheet sheet) {

            Row row1 = sheet.getRow(0);
            Row row2 = sheet.getRow(1);

            row1.getCell(CellReference.convertColStringToIndex("M")).setCellFormula("COUNT(B8:B" + (sheet.getLastRowNum()+ 1) + ")");
            row2.getCell(CellReference.convertColStringToIndex("M")).setCellFormula("COUNTIF(P8:P" + (sheet.getLastRowNum() + 1) + ",\"<>\")");

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

         CellStyle newStyle = createStyle(style,font, (short) 0);

            newStyle.setDataFormat((short)0);

            for (int i = 0; i < cellsQuantity; i++) {
                row.createCell(i).setCellStyle(newStyle);
            }

        }
        //Заполнение значениями ячеек на листе "Ход работ"
        private void wrightCellsToWorkSheet(Row row, PNRSystem system, CellStyle cellStyle, CreationHelper creationHelper) {

            Sheet sheet = row.getSheet().getWorkbook().getSheetAt(2);

            cellStyle.setDataFormat(
                    creationHelper.createDataFormat().getFormat("dd.MM.yyyy")
            );
            int lastRow = sheet.getLastRowNum() + 1;


            CellStyle numberCellStyle = row.getSheet().getWorkbook().createCellStyle();
            numberCellStyle.cloneStyleFrom(cellStyle);
            numberCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"));


            row.getCell(CellReference.convertColStringToIndex("A")).setCellValue(" ");
            row.getCell(CellReference.convertColStringToIndex("B")).setCellValue(" ");
            row.getCell(CellReference.convertColStringToIndex("C")).setCellValue(system.getSubObject().getSubObjectName());
            row.getCell(CellReference.convertColStringToIndex("D")).setCellValue(system.getPNRSystemKO());
            row.getCell(CellReference.convertColStringToIndex("E")).setCellValue(system.getPNRSystemII());
            row.getCell(CellReference.convertColStringToIndex("F")).setCellValue(system.getPNRSystemName());
            row.getCell(CellReference.convertColStringToIndex("G")).setCellValue(system.getPNRSystemRD());
            row.getCell(CellReference.convertColStringToIndex("H")).setCellValue(system.getPNRPlanDate().equals(" ")? null : system.getPNRPlanDate());

            row.getCell(CellReference.convertColStringToIndex("K"))
                    .setCellValue(system.getPNRFactDate().equals(" ")? null : system.getPNRFactDate());
            row.getCell(CellReference.convertColStringToIndex("L")).setCellValue(system.getPNRSystemStatus());
            row.getCell(CellReference.convertColStringToIndex("M"))
                    .setCellValue(system.getIIPlanDate().equals(" ")? null : system.getIIPlanDate());
            row.getCell(CellReference.convertColStringToIndex("N"))
                    .setCellValue(system.getIIFactDate().equals(" ")? null : system.getIIFactDate());
            row.getCell(CellReference.convertColStringToIndex("O"))
                    .setCellValue(system.getKOPlanDate().equals(" ")? null : system.getKOPlanDate());
            row.getCell(CellReference.convertColStringToIndex("P"))
                    .setCellValue(system.getKOFactDate().equals(" ")? null : system.getKOFactDate());
            row.getCell(CellReference.convertColStringToIndex("Q")).setCellValue("Примечание");
            row.getCell(CellReference.convertColStringToIndex("R")).setCellValue(system.getCWExecutor());

            // клетки с форматом ДАТА
            row.getCell(CellReference.convertColStringToIndex("H")).setCellStyle(cellStyle);
            row.getCell(CellReference.convertColStringToIndex("K")).setCellStyle(cellStyle);
            row.getCell(CellReference.convertColStringToIndex("M")).setCellStyle(cellStyle);
            row.getCell(CellReference.convertColStringToIndex("N")).setCellStyle(cellStyle);
            row.getCell(CellReference.convertColStringToIndex("O")).setCellStyle(cellStyle);
            row.getCell(CellReference.convertColStringToIndex("P")).setCellStyle(cellStyle);

            Cell cellJ = row.getCell(CellReference.convertColStringToIndex("J"));
            cellJ.setCellFormula("IFERROR(K" + (row.getRowNum()+1) + "-H" + (row.getRowNum()+1) + ",\" \")");
            cellJ.setCellStyle(numberCellStyle);

    //        Cell cellS = row.getCell(CellReference.convertColStringToIndex("S"));
    //        cellS.setCellFormula("COUNTIFS(Замечания!$G$8:$G$" + lastRow + ",\"=\"&TEXTJOIN(E" +row.getRowNum() + ",1,2),Замечания!N$8" +
    //                ":N$" + lastRow + ",\"<>устранено\")");
    //        cellS.setCellStyle(numberCellStyle);

    //        Cell cellT = row.getCell(CellReference.convertColStringToIndex("T"));
    //        cellT.setCellFormula("COUNTIFS(Деф.акты!G$8:G$"+ lastRow + ",\"=\"&TEXTJOIN(E" + row.getRowNum() + ",1,2),Деф.акты!R$8:R$" + lastRow + ",\"<>устранено\")");
    //        cellT.setCellStyle(numberCellStyle);


        }
        // Заполнение шапки листа "Ход работ"
        private void wrightHeader(Workbook workbook) {

            Sheet sheet = workbook.getSheetAt(1);


            int lastRowNum = sheet.getLastRowNum() + 1;

            int firstRow = 18;

            System.out.println(lastRowNum + "ПОСЛЕДНИЙ РЯД ЛИСТА");

            sheet.getRow(0).getCell(CellReference.convertColStringToIndex("G"))
                    .setCellFormula("G7");

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
                    .setCellFormula("COUNTA(N18:N" + lastRowNum + ")");

            sheet.getRow(9).getCell(CellReference.convertColStringToIndex("G"))
                    .setCellFormula("COUNTIF(D18:D" + lastRowNum + ", \"<>\"\"\")");

            sheet.getRow(10).getCell(CellReference.convertColStringToIndex("G"))
                    .setCellFormula("COUNTIFS(L18:L" + lastRowNum + ",\"Акт КО на подписи\",D18:D" + lastRowNum + ",\"<>\"\"\")");

            sheet.getRow(11).getCell(CellReference.convertColStringToIndex("G"))
                    .setCellFormula("COUNTA(P18:P" + lastRowNum + ")");
        }

        private CellStyle createStyle(CellStyle style, Font font, short format) {

            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            font.setFontName("Times New Roman");
            font.setFontHeightInPoints((short)14);
            style.setFont(font);
            style.setDataFormat(format);

            return style;
        }

        private void mergeCells(Sheet sheet, int columnIndex)  {
            int firstRow = 17;
            int lastRow = sheet.getLastRowNum();

            int mergeStart = -1;
            String prevValue = null;

            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(columnIndex);
                String currentValue = (cell != null && cell.getCellType() == CellType.STRING)
                        ? cell.getStringCellValue()
                        : "";

                if (currentValue.equals(prevValue)) {
                    if (mergeStart == -1) {
                        mergeStart = i - 1;
                    }
                } else {
                    if (mergeStart != -1 && mergeStart < i - 1) {
                        sheet.addMergedRegion(new CellRangeAddress(
                                mergeStart, i - 1, columnIndex, columnIndex));
                    }
                    mergeStart = -1;
                }
                prevValue = currentValue;
            }
            if (mergeStart != -1 && mergeStart < lastRow) {
                sheet.addMergedRegion(new CellRangeAddress(
                        mergeStart, lastRow, columnIndex, columnIndex));
            }
        }
        public byte[] createJournal(String capitalCSCode) {

            CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(capitalCSCode)
                    .orElseThrow(() -> new EntityNotFoundException("При создании журнала не был найден ОКС"));

            try (InputStream journalSample = getClass().getClassLoader().getResourceAsStream(journalPath);
                 Workbook workbook =WorkbookFactory.create(journalSample))  {

                wrightJournalMainSheetValues(capitalCS, workbook.getSheet("Титул"));
                wrightJournalCommonInfoSheet(capitalCS, workbook.getSheet("Журнал №"));


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
        private void wrightJournalMainSheetValues(CapitalCS capitalCS, Sheet sheet) {


            sheet.getRow(12).getCell(CellReference.convertColStringToIndex("I")).setCellValue(capitalCS.getCapitalCSName());
            sheet.getRow(15).getCell(CellReference.convertColStringToIndex("H")).setCellValue("ООО \"Газпром Инвест\"");
            sheet.getRow(17).getCell(CellReference.convertColStringToIndex("H")).setCellValue(capitalCS.getCWExecutor());
            sheet.getRow(47).getCell(CellReference.convertColStringToIndex("P"))
                    .setCellValue(LocalDate.now().getYear() % 100);

        }
        private void wrightJournalCommonInfoSheet(CapitalCS capitalCS, Sheet sheet) {

            List<PNRSystem> systems = systemRepo.getAllByCCSNumber(capitalCS.getCodeCCS());

            String PNRDate = systems.stream().filter(system -> !system.getPNRPlanDate().equals(""))
                    .map(system -> system.getPNRPlanDate())
                    .sorted().findFirst().get();

            String KODate = Collections.max( systems.stream().filter(system -> !system.getKOFactDate().equals("") )
                    .map(system ->  system.getKOFactDate())
                    .collect(Collectors.toList()));


            sheet.getRow(2).getCell(CellReference.convertColStringToIndex("T")).setCellValue("1");
            sheet.getRow(4).getCell(CellReference.convertColStringToIndex("N")).setCellValue(capitalCS.getCapitalCSName());
            sheet.getRow(6).getCell(CellReference.convertColStringToIndex("K")).setCellValue(capitalCS.getLocationRegion());
            sheet.getRow(7).getCell(CellReference.convertColStringToIndex("B")).setCellValue(capitalCS.getCWExecutor());
            sheet.getRow(11).getCell(CellReference.convertColStringToIndex("B")).setCellValue(capitalCS.getCWSupervisor());
            sheet.getRow(14).getCell(CellReference.convertColStringToIndex("G")).setCellValue(PNRDate);
            sheet.getRow(16).getCell(CellReference.convertColStringToIndex("M")).setCellValue(KODate);



        }

    }
