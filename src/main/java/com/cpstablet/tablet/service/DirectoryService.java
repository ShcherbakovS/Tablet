package com.cpstablet.tablet.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DirectoryService {

    public Map<String, String> getRegions() {

        Map <String, String> regions = new HashMap<>();

        regions.put("ru-ad", "Республика Адыгея");
        regions.put("ru-al", "Республика Алтай");
        regions.put("ru-ba", "Республика Башкортостан");
        regions.put("ru-bu", "Республика Бурятия");
        regions.put("ru-da", "Республика Дагестан");
        regions.put("ru-in", "Республика Ингушетия");
        regions.put("ru-kb", "Кабардино-Балкарская Республика");
        regions.put("ru-kl", "Республика Калмыкия");
        regions.put("ru-kc", "Карачаево-Черкесская Республика");
        regions.put("ru-kr", "Республика Карелия");
        regions.put("ru-ko", "Республика Коми");
        regions.put("ru-me", "Республика Марий Эл");
        regions.put("ru-mo", "Республика Мордовия");
        regions.put("ru-sa", "Республика Саха (Якутия)");
        regions.put("ru-se", "Республика Северная Осетия — Алания");
        regions.put("ru-ta", "Республика Татарстан");
        regions.put("ru-ty", "Республика Тыва");
        regions.put("ru-ud", "Удмуртская Республика");
        regions.put("ru-kk", "Республика Хакасия");
        regions.put("ru-ce", "Чеченская Республика");
        regions.put("ru-cu", "Чувашская Республика");
        regions.put("ru-alt", "Алтайский край");
        regions.put("ru-zab", "Забайкальский край");
        regions.put("ru-kam", "Камчатский край");
        regions.put("ru-kda", "Краснодарский край");
        regions.put("ru-kya", "Красноярский край");
        regions.put("ru-per", "Пермский край");
        regions.put("ru-pri", "Приморский край");
        regions.put("ru-sta", "Ставропольский край");
        regions.put("ru-kha", "Хабаровский край");
        regions.put("ru-amu", "Амурская область");
        regions.put("ru-ark", "Архангельская область");
        regions.put("ru-ast", "Астраханская область");
        regions.put("ru-bel", "Белгородская область");
        regions.put("ru-bry", "Брянская область");
        regions.put("ru-vla", "Владимирская область");
        regions.put("ru-vgg", "Волгоградская область");
        regions.put("ru-vlg", "Вологодская область");
        regions.put("ru-vor", "Воронежская область");
        regions.put("ru-iva", "Ивановская область");
        regions.put("ru-irk", "Иркутская область");
        regions.put("ru-kal", "Калининградская область");
        regions.put("ru-klu", "Калужская область");
        regions.put("ru-kem", "Кемеровская область");
        regions.put("ru-kir", "Кировская область");
        regions.put("ru-kos", "Костромская область");
        regions.put("ru-kgn", "Курганская область");
        regions.put("ru-krs", "Курская область");
        regions.put("ru-len", "Ленинградская область");
        regions.put("ru-lip", "Липецкая область");
        regions.put("ru-mag", "Магаданская область");
        regions.put("ru-mos", "Московская область");
        regions.put("ru-mur", "Мурманская область");
        regions.put("ru-niz", "Нижегородская область");
        regions.put("ru-ngr", "Новгородская область");
        regions.put("ru-nvs", "Новосибирская область");
        regions.put("ru-oms", "Омская область");
        regions.put("ru-ore", "Оренбургская область");
        regions.put("ru-orl", "Орловская область");
        regions.put("ru-pnz", "Пензенская область");
        regions.put("ru-psk", "Псковская область");
        regions.put("ru-ros", "Ростовская область");
        regions.put("ru-rya", "Рязанская область");
        regions.put("ru-sam", "Самарская область");
        regions.put("ru-sar", "Саратовская область");
        regions.put("ru-sak", "Сахалинская область");
        regions.put("ru-sve", "Свердловская область");
        regions.put("ru-smo", "Смоленская область");
        regions.put("ru-tam", "Тамбовская область");
        regions.put("ru-tve", "Тверская область");
        regions.put("ru-tom", "Томская область");
        regions.put("ru-tul", "Тульская область");
        regions.put("ru-tyu", "Тюменская область");
        regions.put("ru-uly", "Ульяновская область");
        regions.put("ru-che", "Челябинская область");
        regions.put("ru-yar", "Ярославская область");
        regions.put("ru-mow", "Москва");
        regions.put("ru-spb", "Санкт-Петербург");
        regions.put("ru-yev", "Еврейская автономная область");
        regions.put("ru-nen", "Ненецкий автономный округ");
        regions.put("ru-khm", "Ханты-Мансийский автономный округ — Югра");
        regions.put("ru-chu", "Чукотский автономный округ");
        regions.put("ru-yan", "Ямало-Ненецкий автономный округ");
        regions.put("ru-cr", "Республика Крым");
        regions.put("ru-sev", "Севастополь");

        return regions;
    }

    public List<String> getObjectTypes() {

        return List.of("АСУ", "АСУ1", "ВЖК", "ГИС",
                "ГРС", "Дорога", "КГС", "КИТСО", "КС", "МГ",
                "МГ1", "Подобъект ВС и ВО",
                "Подобъект НПН", "Подобъект НПН1", "Подобъект ТП",
                "Подобъект ТП без ПГ", "Подобъект ТП1", "Подобъект ТС",
                "Подобъект ЭС", "Промбаза", "ПХГ", "Связь", "УКПГ", "ЭХЗ", "ЭХЗ1");

    }

}
