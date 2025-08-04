package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.JournalDTO;
import com.cpstablet.tablet.service.JournalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(("/journal"))
@AllArgsConstructor
public class JournalController {

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;

    private final JournalService journalService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/createEntry")
    public HttpStatus createJournalEntry(@RequestBody String journalDTO) {

        try {
            return journalService.create(myMapper.readValue(journalDTO, JournalDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/updateEntry")
    public HttpStatus updateJournalEntry(@RequestBody String journalDTO) {
        try {
            return journalService.updateJournalEntry(myMapper.readValue(journalDTO, JournalDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/getEntry/{id}")
    public JournalDTO getEntry(@PathVariable("id")Long id) {
        return journalService.getJournalEntry(id);
    }
    @GetMapping("/getEntryList/{codeCCS}")
    public List<JournalDTO> getJournalEntryList(@PathVariable String codeCCS) {

        return journalService.getJournal(codeCCS);
    }

    @DeleteMapping("/deleteEntry/{id}")
    public HttpStatus deleteJournalEntry(@PathVariable("id") Long id) {
        return journalService.deleteJournalEntry(id);
    }
    @GetMapping("/getJournal/{CCSCode}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity getJournal(@PathVariable("CCSCode") String CCSCode) throws IOException {

        byte[] docxContent = journalService.getJournalDoc(CCSCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("report.docx")
                .build());
        headers.add("Content-Transfer-Encoding", "binary");

        return new ResponseEntity<>(docxContent, headers, HttpStatus.OK);

    }


}
