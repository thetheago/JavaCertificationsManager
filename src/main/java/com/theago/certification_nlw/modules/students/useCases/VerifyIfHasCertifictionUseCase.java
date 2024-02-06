package com.theago.certification_nlw.modules.students.useCases;

import org.springframework.stereotype.Service;

import com.theago.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;

@Service
public class VerifyIfHasCertifictionUseCase {
    
    public boolean execute(VerifyHasCertificationDTO dto) {
        if(dto.getEmail().equals("thiago@gmail.com") && dto.getTechnology().equals("Java")) {
            return true;
        }

        return false;
    }
}
