package org.ict.intelligentclass.education.model.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ict.intelligentclass.education.jpa.entity.EducationEntity;
import org.ict.intelligentclass.education.jpa.repository.EducationRepository;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
@Service
public class EducationService {

    private final EducationRepository educationRepository;

    //닉네임으로 조회.
    public List<EducationEntity> getAllEducations(String nickname) {
        List<EducationEntity> educations = educationRepository.findByNickname(nickname);

        return educations;
    }



    public EducationEntity createEducation(EducationEntity education) {
        return educationRepository.save(education);
    }




    public EducationEntity updateEducationByNickname(Long educationId, EducationEntity updatedEducation) {
        Optional<EducationEntity> educationEntityOptional = educationRepository.findById(educationId);

        if (educationEntityOptional.isPresent()) {
            EducationEntity educationEntity = educationEntityOptional.get();

            educationEntity.setEducationLevel(updatedEducation.getEducationLevel());
            educationEntity.setSchoolName(updatedEducation.getSchoolName());
            educationEntity.setEducationStatus(updatedEducation.getEducationStatus());
            educationEntity.setMajor(updatedEducation.getMajor());
            educationEntity.setEntryDate(updatedEducation.getEntryDate());
            educationEntity.setGraduationDate(updatedEducation.getGraduationDate());
            educationEntity.setHomeAndTransfer(updatedEducation.getHomeAndTransfer());
            educationEntity.setPassDate(updatedEducation.getPassDate());

            educationRepository.save(educationEntity);
            return educationEntity;
        } else {
            return null;
        }
    }




    public void deleteEducationById(Long educationId) {
        educationRepository.deleteById(educationId);
    }
}