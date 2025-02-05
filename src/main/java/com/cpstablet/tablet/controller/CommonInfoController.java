package com.cpstablet.tablet.controller;


import com.cpstablet.tablet.DTO.PNRSystemDTO;
import com.cpstablet.tablet.DTO.commonDTO.ObjectCommonInfoDTO;
import com.cpstablet.tablet.DTO.commonDTO.SubobjectCommonInfDTO;
import com.cpstablet.tablet.DTO.commonDTO.SystemCommonInfDTO;
import com.cpstablet.tablet.service.CommonInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/commons")
@AllArgsConstructor
public class CommonInfoController {

    private final CommonInfoService commonInfoService;

    @GetMapping("/objectCommonInf/{id}")
    public ResponseEntity<ObjectCommonInfoDTO> getObjectCommonInf(@PathVariable("id") String id) {

        return new ResponseEntity<>(commonInfoService.getObjectCommonInfo(id), HttpStatus.OK);
    }

    @GetMapping("/getStructureCommonInf/{CCSCode}")
    public ResponseEntity<List<SubobjectCommonInfDTO>> getStructureComInfo(@PathVariable("CCSCode") String CCSCode)  {


        return new ResponseEntity<>(commonInfoService.structureCommonInf(CCSCode), HttpStatus.OK);
    }

    @GetMapping("/getSystemCommonInfo/{id}")
    public SystemCommonInfDTO getPNRSystemCommonInf(@PathVariable("id") Long id) {

        return commonInfoService.getSystemCommonInfo(id);
    }


}
