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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .user(user.getUserInfo().getFullName() + " " + user.getUserInfo().getOrganisation())
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

        LocalDate PNRDate = systems.stream().map(system -> LocalDate.parse(system.getPNRFactDate(), formatter))
                .sorted().findFirst().get();

        LocalDate KODate = Collections.max( systems.stream().map(system -> LocalDate.parse(system.getPNRFactDate(), formatter))
                .collect(Collectors.toList()));



        replacements.put("?{capitalCSName}", capitalCS.getCapitalCSName());
        replacements.put("?{locationRegion}", capitalCS.getLocationRegion());
        replacements.put("?{year}", String.valueOf(LocalDate.now().getYear() % 100));
        // рук ПНР
        replacements.put("${CWSupervisor}", capitalCS.getCWSupervisor());
        // куратор от зак-ка
        replacements.put("${customerSupervisor}", capitalCS.getCustomerSupervisor());
        replacements.put("${PNRDate}", PNRDate.format(formatter));
        replacements.put("${KODate}", KODate.format(formatter));

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
            addITRTable(document,"{ITR_TABLE}");


            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.write(baos);
                return baos.toByteArray();
            }
        }

    }
// Заменатекста по меткам
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


    private void addITRTable(XWPFDocument document, String placeholder) {

        int paragraphPos = -1;
        XWPFParagraph targetParagraph = null;

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (int i = 0; i < paragraphs.size(); i++) {
            XWPFParagraph p = paragraphs.get(i);
            if (p.getText().contains(placeholder)) {
                paragraphPos = i;
                targetParagraph = p;
                break;
            }
        }

        if (paragraphPos == -1) {
            throw new RuntimeException("Плейсхолдер " + placeholder + " не найден в документе!");
        }

        // 3. Удаляем параграф с плейсхолдером
        document.removeBodyElement(document.getPosOfParagraph(targetParagraph));

        // 4. Создаем новую таблицу
        XWPFTable table = document.createTable(4,3);

        // 5. Настраиваем таблицу (3 колонки, заголовок)
        // Заголовок таблицы
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("ФИО и должность");
        headerRow.addNewTableCell().setText("Дата начала работ");
        headerRow.addNewTableCell().setText("Дата окончания работ");

        // 6. Добавляем данные в таблицу (пример с 3 строками)
        for (int i = 0; i < 3; i++) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText("Инженер " + (i+1));
            row.getCell(1).setText("01.01.2023");
            row.getCell(2).setText("31.12.2023");
        }

        document.setTable(paragraphPos, table);

    }

}
