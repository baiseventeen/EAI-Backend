package com.njuzr.eaibackend.controller;

import com.njuzr.eaibackend.dto.TranslationDTO;
import com.njuzr.eaibackend.service.TranslateService;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/18 - 23:44
 * @Package: EAI-Backend
 */

@RestController
@RequestMapping("/api/translate")
public class TranslateController {

    private final TranslateService translateService;

    public TranslateController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @PostMapping
    public MyResponse translate(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId,
            @RequestBody TranslationDTO translationDTO
    ) {
        return MyResponse.success(translateService.translate(assignmentId, studentId, translationDTO));
    }


    @GetMapping
    public MyResponse getTranslation(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId
    ) {
        return MyResponse.success(translateService.getTranslations(assignmentId, studentId));
    }

}
