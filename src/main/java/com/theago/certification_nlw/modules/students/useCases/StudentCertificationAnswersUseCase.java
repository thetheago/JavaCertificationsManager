package com.theago.certification_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.theago.certification_nlw.modules.questions.entities.QuestionEntity;
import com.theago.certification_nlw.modules.questions.repositories.QuestionRepository;
import com.theago.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.theago.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.theago.certification_nlw.modules.students.entities.AnswersCertificationsEntity;
import com.theago.certification_nlw.modules.students.entities.CertificationStudentEntity;
import com.theago.certification_nlw.modules.students.entities.StudentEntity;
import com.theago.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import com.theago.certification_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertifictionUseCase verifyIfHasCertifictionUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

        var hasCertification = this.verifyIfHasCertifictionUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology()));

        if (hasCertification) {
            throw new Exception("Você já tirou sua certificação");
        }

        List<QuestionEntity> questionsEntity = this.questionRepository.findByTechnology(dto.getTechnology());
        List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();

        AtomicInteger correctAnswers = new AtomicInteger(0);

        dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
            var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionId()))
                .findFirst().get();

            var correctAlternative = question.getAlternatives().stream().filter(alternative -> alternative.isCorrect())
                .findFirst().get();

            if (correctAlternative.getId().equals(questionAnswer.getAlternativeId())) {
                questionAnswer.setCorrect(true);
                correctAnswers.incrementAndGet();
            } else {
                questionAnswer.setCorrect(false);
            }

            var answersCertificationsEntity = AnswersCertificationsEntity.builder()
                .answerId(questionAnswer.getAlternativeId())
                .questionId(questionAnswer.getQuestionId())
                .isCorrect(questionAnswer.isCorrect())
                .build();

            answersCertifications.add(answersCertificationsEntity);

        });

        var student = this.studentRepository.findByEmail(dto.getEmail());
        UUID studentId;
        if(student.isEmpty()) {
            var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = this.studentRepository.save(studentCreated);
            studentId = studentCreated.getId();
        } else {
            studentId = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
            .technology(dto.getTechnology())
            .studentID(studentId)
            .grate(correctAnswers.get())
            .build();

        var certificationStudentCreated = this.certificationStudentRepository.save(certificationStudentEntity);

        answersCertifications.stream().forEach(answersCertification -> {
            answersCertification.setCertificationId(answersCertification.getId());
            answersCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationsEntity(answersCertifications);

        this.certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;

    }
}
