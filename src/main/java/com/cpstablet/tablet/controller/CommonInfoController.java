package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.commonInfoDTO.ObjectCommonInfoDTO;
import com.cpstablet.tablet.DTO.commonInfoDTO.SubobjectCommonInfDTO;
import com.cpstablet.tablet.DTO.commonInfoDTO.SystemCommonInfDTO;
import com.cpstablet.tablet.service.CommonInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commons")
@AllArgsConstructor
public class CommonInfoController {

    private final CommonInfoService commonInfoService;

    @GetMapping("/objectCommonInf/{codeCCS}")
    public ResponseEntity<ObjectCommonInfoDTO> getObjectCommonInf(@PathVariable("codeCCS") String codeCCS) {

        return new ResponseEntity<>(commonInfoService.getObjectCommonInfo(codeCCS), HttpStatus.OK);
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
