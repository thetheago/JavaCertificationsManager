package com.theago.certification_nlw.modules.students.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.theago.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.theago.certification_nlw.modules.students.useCases.VerifyIfHasCertifictionUseCase;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    VerifyIfHasCertifictionUseCase verifyIfHasCertifictionUseCase;
    
    @PostMapping("/verifyIfHasCertification")
    public String verifyIfHasCertification(@RequestBody VerifyHasCertificationDTO verifyHasCertificationDTO) {
        var result = this.verifyIfHasCertifictionUseCase.execute(verifyHasCertificationDTO);
        if (result) {
            return "Usuário já fez a prova";
        }
        
        return "Usário pode fazer a prova";
    }
}
 