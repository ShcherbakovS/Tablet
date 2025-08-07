package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.JournalDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.Journal;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.JournalRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import com.cpstablet.tablet.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JournalService {
    private final CapitalCSRepo capitalCSRepo;
    private final JournalRepo journalRepo;
    private final UserRepo userRepo;
    private final SystemRepo systemRepo;
    private final String journalPath = "Samples/Journal.docx";

    public HttpStatus create(JournalDTO journalDTO) {

        User user = userRepo.findById(Long.valueOf(journalDTO.getUser())).orElseThrow(() -> new EntityNotFoundException("При создании защписи в журнале ПНР" +
                "пользователь с id " + journalDTO.getId() + " не был найден"));

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(journalDTO.getCapitalCS()).orElseThrow(
                () -> new EntityNotFoundException("Призаписи в Журнал ПНР не найден ОКС"));

        if (capitalCS.getJournalEntryCounter() == null) {
            capitalCS.setJournalEntryCounter(1L);
            capitalCS = capitalCSRepo.save(capitalCS);
        }
        Long newCounter = capitalCS.getJournalEntryCounter();

        Journal journal = Journal.builder()
                .description(journalDTO.getDescription())
                .user(user.getUserInfo().getFullName())
                .organisation(user.getUserInfo().getOrganisation())
                .capitalCS(capitalCS)
                .subObject(journalDTO.getSubObject())
                .system(journalDTO.getSystem())
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .serialNumber(capitalCS.getJournalEntryCounter())
                .build();
        capitalCS.setJournalEntryCounter(newCounter + 1);
        capitalCS.getJournalList().add(journal);

        capitalCSRepo.save(capitalCS);

        return HttpStatus.OK;

    }

    public HttpStatus updateJournalEntry(JournalDTO journalDTO) {

        Journal journal = journalRepo.findById(journalDTO.getId()).orElseThrow(
                () -> new EntityNotFoundException("Запись журнала с id " + journalDTO.getId() + "не найдена"));

        journal.setDescription(journalDTO.getDescription());
        journal.setSubObject(journalDTO.getSubObject());
        journal.setSystem(journalDTO.getSystem());


        journalRepo.save(journal);


        return HttpStatus.OK;
    }

    public JournalDTO getJournalEntry(Long id) {

        Journal journal = journalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Запись журнала с id " + id + "не найдена"));
        return createDTO(journal);
    }

    public List<JournalDTO> getJournal(String codeCSS) {

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(codeCSS).orElseThrow(
                () -> new EntityNotFoundException("Призаписи в Журнал ПНР не найден ОКС"));

        return capitalCS.getJournalList().stream().map(journal -> createDTO(journal)).collect(Collectors.toList());

    }

    private JournalDTO createDTO(Journal journal) {

        return JournalDTO.builder()
                .id(journal.getId())
                .description(journal.getDescription())
                .capitalCS(journal.getCapitalCS().getCodeCCS())
                .subObject(journal.getSubObject())
                .system(journal.getSystem())
                .user(journal.getUser())
                .organisation(journal.getOrganisation() == null? " " : journal.getOrganisation())
                .date(journal.getDate())
                .serialNumber(journal.getSerialNumber())
                .build();
    }

    public HttpStatus deleteJournalEntry(Long id) {

        Journal journalEntry = journalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Не найдена запись журнала с id" + id + "при попыткке удаления"));

        journalRepo.delete(journalEntry);


        return HttpStatus.OK;
    }

    public byte[] getJournalDoc(String codeCSS) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        Map<String, String> replacements = new HashMap<>();

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(codeCSS).orElseThrow(()-> new EntityNotFoundException("Журнал ПНР ОКС не найден"));

        List<PNRSystem> systems = systemRepo.getAllByCCSNumber(codeCSS);

        String PNRDate = systems.stream().filter(system -> !system.getPNRPlanDate().equals(""))
                .map(system -> system.getPNRPlanDate())
                .sorted().findFirst().get();

        String KODate = Collections.max( systems.stream().filter(system -> !system.getKOFactDate().equals("") )
                .map(system ->  system.getKOFactDate())
                .collect(Collectors.toList()));

        List<User> userList = userRepo.findAll().stream().filter(user -> user.getAllowedObjects().contains(capitalCS)).collect(Collectors.toList());

        List<Journal> journalList = capitalCS.getJournalList();


        replacements.put("?{capitalCSName}", capitalCS.getCapitalCSName() == null? "" : capitalCS.getCapitalCSName());
        replacements.put("?{locationRegion}", capitalCS.getLocationRegion() == null? "" : capitalCS.getLocationRegion());
        replacements.put("?{year}", String.valueOf(LocalDate.now().getYear() % 100));
        // рук ПНР
        replacements.put("${CWSupervisor}", capitalCS.getCWSupervisor() == null? "" : capitalCS.getCWSupervisor());
        // куратор от зак-ка
        replacements.put("${customerSupervisor}", capitalCS.getCustomerSupervisor() == null? "" : capitalCS.getCustomerSupervisor());
        replacements.put("${PNRDate}", PNRDate == null? "" : PNRDate);
        replacements.put("${KODate}", KODate == null? "" : KODate);

        ClassPathResource resource = new ClassPathResource(journalPath);
        try (InputStream templateStream = resource.getInputStream();
             XWPFDocument document = new XWPFDocument(templateStream)) {

            // Заменяем текст в параграфах
//            for (XWPFParagraph paragraph : document.getParagraphs()) {
//                replaceText(paragraph, replacements);
//            }

            // Заменяем текст в таблицах
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            System.out.println(replacements);
                            replaceText(paragraph, replacements);
                        }
                    }
                }
           }
            addITRTable(document,"{ITR_TABLE}", userList);
            addSubcontractorTable(document, "{SUBCONTRACTOR}", systems);
            addJournalTable(document, "{JOURNAL_TABLE}", capitalCS.getJournalList());


            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.write(baos);
                return baos.toByteArray();
            }
        }

    }
// Замена текста по меткам
    private void replaceText(XWPFParagraph paragraph, Map<String, String> replacements) {

        String text = paragraph.getText();
        if (text != null && !text.isEmpty()) {

            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                String key = entry.getKey();
                if (text.contains(key)) {
                    text = text.replace(key, entry.getValue());
                    // Удаляем все существующие runs
                    for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }
                    // Добавляем новый run с обновленным текстом
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                    break; // предполагаем, что один ключ на параграф
                }
            }
        }
    }
    private void addITRTable(XWPFDocument doc, String placeholder, List<User> userList) {
        XWPFParagraph targetParagraph = null;

        List<XWPFParagraph> paragraphs = doc.getParagraphs();
        for (XWPFParagraph p : paragraphs) {
            if (p.getText().contains(placeholder)) {
                targetParagraph = p;
                break;
            }
        }

        if (targetParagraph == null) {
            throw new RuntimeException("Плейсхолдер {ITR_TABLE} не найден!");
        }

        // 3. Создаем курсор в позиции перед параграфом с плейсхолдером
        XmlCursor cursor = targetParagraph.getCTP().newCursor();

        // 4. Создаем таблицу в этой позиции
        XWPFTable table = doc.insertNewTbl(cursor);

        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.PCT);  // Указываем, что ширина в процентах
        tblWidth.setW(BigInteger.valueOf(4000));
        // 5. Настраиваем стиль для всей таблицы
        String fontFamily = "Arial";
        int fontSize = 9;

        // 6. Настраиваем таблицу (4 колонки)
        // Добавляем 3 дополнительные колонки (первая создается автоматически)
        for (int i = 0; i < 3; i++) {
            table.getRow(0).addNewTableCell();
        }

        // Заполняем заголовки с установкой стиля
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < 4; i++) {
            XWPFParagraph p = headerRow.getCell(i).getParagraphs().get(0);
            XWPFRun run = p.createRun();
            run.setFontFamily(fontFamily);
            run.setFontSize(fontSize);
        }

        headerRow.getCell(0).setText("Фамилия, имя, отчество, занимаемая должность, участок работы ");
        headerRow.getCell(1).setText("Дата начала работ на объекте");
        headerRow.getCell(2).setText("Отметка о получении разрешения на право производства работ или \n" +
                "о прохождении аттестации \n");
        headerRow.getCell(3).setText("Дата окончания работ на объекте ");

        // 7. Добавляем данные с установкой стиля
        for (int i = 0; i < userList.size(); i++) {
            XWPFTableRow row = table.createRow();
            for (int j = 0; j < 4; j++) {
                XWPFParagraph p = row.getCell(j).getParagraphs().get(0);
                XWPFRun run = p.createRun();
                run.setFontFamily(fontFamily);
                run.setFontSize(fontSize);
            }

            row.getCell(0).setText(userList.get(i).getUserInfo().getFullName() + " " + userList.get(i).getUserInfo().getOrganisation());
            row.getCell(1).setText(userList.get(i).getUserInfo().getRegistrationDate());
            row.getCell(2).setText("");
            row.getCell(3).setText("");
        }

        // 7. Удаляем оригинальный параграф с плейсхолдером
        doc.removeBodyElement(doc.getPosOfParagraph(targetParagraph));
    }
    private void addSubcontractorTable(XWPFDocument doc, String placeholder, List<PNRSystem> systems) {

        XWPFParagraph targetParagraph = null;

        List<String> subcontractors = systems.stream().map(system -> system.getCIWExecutor()).collect(Collectors.toSet())
                .stream().collect(Collectors.toList());

        // TODO: в метод
        List<XWPFParagraph> paragraphs = doc.getParagraphs();

        for (XWPFParagraph p : paragraphs) {
            if (p.getText().contains(placeholder)) {
                targetParagraph = p;
                break;
            }
        }

        if (targetParagraph == null) {
            throw new RuntimeException("Плейсхолдер {ITR_TABLE} не найден!");
        }

        // 3. Создаем курсор в позиции перед параграфом с плейсхолдером
        XmlCursor cursor = targetParagraph.getCTP().newCursor();

        // 4. Создаем таблицу в этой позиции
        XWPFTable table = doc.insertNewTbl(cursor);

        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.PCT);  // Указываем, что ширина в процентах
        tblWidth.setW(BigInteger.valueOf(4000));

        // 5. Настраиваем стиль для всей таблицы
        String fontFamily = "Arial";
        int fontSize = 9;

        // Добавляем 3 дополнительные колонки (первая создается автоматически)
        for (int i = 0; i < 2; i++) {
            table.getRow(0).addNewTableCell();
        }

        // Заполняем заголовки с установкой стиля
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < 2; i++) {
            XWPFParagraph p = headerRow.getCell(i).getParagraphs().get(0);
            XWPFRun run = p.createRun();
            run.setFontFamily(fontFamily);
            run.setFontSize(fontSize);
        }

        headerRow.getCell(0).setText("№ п/п ");
        headerRow.getCell(1).setText("Субподрядчик ");
        headerRow.getCell(2).setText("Выполняемые работы ");

        // 7. Добавляем данные с установкой стиля
        for (int i = 0; i < subcontractors.size(); i++) {
            XWPFTableRow row = table.createRow();
            for (int j = 0; j < 2; j++) {
                XWPFParagraph p = row.getCell(j).getParagraphs().get(0);
                XWPFRun run = p.createRun();
                run.setFontFamily(fontFamily);
                run.setFontSize(fontSize);
            }

            row.getCell(0).setText(String.valueOf(i + 1));
            row.getCell(1).setText(subcontractors.get(i));
            row.getCell(2).setText("");
        }

        // 7. Удаляем оригинальный параграф с плейсхолдером
        doc.removeBodyElement(doc.getPosOfParagraph(targetParagraph));
    }
    private void addJournalTable(XWPFDocument doc, String placeholder, List<Journal> journalList) {

        XWPFParagraph targetParagraph = null;


        // TODO: в метод
        List<XWPFParagraph> paragraphs = doc.getParagraphs();

        for (XWPFParagraph p : paragraphs) {
            if (p.getText().contains(placeholder)) {
                targetParagraph = p;
                break;
            }
        }

        if (targetParagraph == null) {
            throw new RuntimeException("Плейсхолдер {ITR_TABLE} не найден!");
        }

        // 3. Создаем курсор в позиции перед параграфом с плейсхолдером
        XmlCursor cursor = targetParagraph.getCTP().newCursor();

        // 4. Создаем таблицу в этой позиции
        XWPFTable table = doc.insertNewTbl(cursor);

        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.PCT);  // Указываем, что ширина в процентах
        tblWidth.setW(BigInteger.valueOf(4000));

        // 5. Настраиваем стиль для всей таблицы
        String fontFamily = "Arial";
        int fontSize = 9;

        // Добавляем 3 дополнительные колонки (первая создается автоматически)
        for (int i = 0; i < 1; i++) {
            table.getRow(0).addNewTableCell();
        }

        // Заполняем заголовки с установкой стиля
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < 1; i++) {
            XWPFParagraph p = headerRow.getCell(i).getParagraphs().get(0);
            XWPFRun run = p.createRun();
            run.setFontFamily(fontFamily);
            run.setFontSize(fontSize);
        }

        headerRow.getCell(0).setText("Дата ");
        headerRow.getCell(1).setText("Краткое описание и условия производства работ (со ссылкой, при необходимости, на работы, выполняемые субподрядными организациями), должность, инициалы и подпись ответственного лица ");

        // 7. Добавляем данные с установкой стиля
        for (int i = 0; i < journalList.size(); i++) {
            XWPFTableRow row = table.createRow();
            for (int j = 0; j < 2; j++) {
                XWPFParagraph p = row.getCell(j).getParagraphs().get(0);
                XWPFRun run = p.createRun();
                run.setFontFamily(fontFamily);
                run.setFontSize(fontSize);
            }

            row.getCell(0).setText(journalList.get(i).getDate());
            row.getCell(1).setText(journalList.get(i).getSubObject() +
                    " " + journalList.get(i).getSystem() + " " + journalList.get(i).getDescription() + " " + journalList.get(i).getUser() + "\n\n");
        }

        // 7. Удаляем оригинальный параграф с плейсхолдером
        doc.removeBodyElement(doc.getPosOfParagraph(targetParagraph));
    }


}
