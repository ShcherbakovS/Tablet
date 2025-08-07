package com.cpstablet.tablet.records;

import com.cpstablet.tablet.DTO.JournalDTO;

import java.util.List;

public record EntryList(List<JournalDTO> journal, List<String> users) {
}
