package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.SubObjectRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubObjectService {

    final SubObjectRepo subObjectRepo;

    public List<SubObject> findAllCCSCode(String CCSCode) {
        return subObjectRepo.findByCCSCode(CCSCode);
    }

    public void changeStatus(String status)  {
        /*TODO: метод вызывается при любом изменении статуса системы
        логика изменения статусов:
        Для статусов системы связаными с КО- если хоть одна система имеет статус связвнный с КО, то все системы приравниваются к одному статусу т.к.
        КО проводится по всем системам подобъекта одновременно.
        "Принято в ПНР",
        */

    }

}
