package org.ict.intelligentclass.lecture.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ict.intelligentclass.lecture.jpa.entity.LectureCommentEntity;
import org.ict.intelligentclass.lecture.jpa.entity.RatingEntity;
import org.ict.intelligentclass.lecture.jpa.entity.input.CommentInput;
import org.ict.intelligentclass.lecture.jpa.entity.input.LectureReadInput;
import org.ict.intelligentclass.lecture.jpa.entity.output.*;
import org.ict.intelligentclass.lecture.model.dto.LectureCommentDto;
import org.ict.intelligentclass.lecture_packages.jpa.entity.LecturePackageEntity;
import org.ict.intelligentclass.lecture_packages.jpa.repository.LecturePackageRepository;
import org.ict.intelligentclass.lecture.jpa.entity.LectureEntity;
import org.ict.intelligentclass.lecture.jpa.entity.LectureReadEntity;
import org.ict.intelligentclass.lecture.jpa.repository.LectureCommentRepository;
import org.ict.intelligentclass.lecture.jpa.repository.LectureReadRepository;
import org.ict.intelligentclass.lecture.jpa.repository.LectureRepository;
import org.ict.intelligentclass.lecture.jpa.repository.RatingRepository;
import org.ict.intelligentclass.lecture.jpa.entity.input.RatingInput;
import org.ict.intelligentclass.lecture.jpa.entity.input.LectureInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LectureService {

    private final LecturePackageRepository lecturePackageRepository;
    private final LectureRepository lectureRepository;
    private final LectureCommentRepository lectureCommentRepository;
    private final LectureReadRepository lectureReadRepository;
    private final RatingRepository ratingRepository;

    // 강의 패키지 타이틀 가져오기
    public LecturePackageEntity selectLecturePackageTitle(Long lecturePackageId) {
        Optional<LecturePackageEntity> lecturePackageEntity = lecturePackageRepository.findById(lecturePackageId);
        return lecturePackageEntity.get();
    }

    // 강의 패키지 소유자 정보
    public LectureOwnerDto getLecturePackageOwner(Long lecturePackageId) {
        LecturePackageEntity lecturePackageEntity = lecturePackageRepository.findById(lecturePackageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lecture package ID"));
        return new LectureOwnerDto(lecturePackageEntity);
    }


    // 패키지 Id 로 강의 목록 페이지
    public List<LectureListDto> getLectureList(Long lecturePackageId) {
        List<LectureEntity> lectureEntities = lectureRepository.findByLecturePackageIdOrderByLectureIdAsc(lecturePackageId);
        return lectureEntities.stream()
                .map(LectureListDto::new)
                .collect(Collectors.toList());
    }

    // 강의 패키지 평균 별점 가져오기
    public PackageRatingDto selectLecturePackageRating(Long lecturePackageId) {
        Double averageRating = ratingRepository.findAverageRatingByLecturePackageId(lecturePackageId);
        PackageRatingDto ratingDto = new PackageRatingDto(lecturePackageId, averageRating.floatValue());
        return ratingDto;
    }

    // 강의 패키지 별점 입력
    public void addRating(RatingInput ratingInput) {
        if (checkIfAlreadyRated(ratingInput.getLecturePackageId(), ratingInput.getNickname())) {
            throw new IllegalArgumentException("이미 별점을 등록했습니다.");
        }

        RatingEntity ratingEntity = RatingEntity.builder()
                .nickname(ratingInput.getNickname())
                .lecturePackageId(ratingInput.getLecturePackageId())
                .rating(ratingInput.getRating())
                .build();
        ratingRepository.save(ratingEntity);
    }

    // 별점 중복 체크 메서드
    public boolean checkIfAlreadyRated(Long lecturePackageId, String nickname) {
        return ratingRepository.existsByLecturePackageIdAndNickname(lecturePackageId, nickname);
    }

    // 강의 미리보기
    public LecturePreviewDto getLecturePreviewById(int lectureId) {
        return lectureRepository.findById(lectureId)
                .map(LecturePreviewDto::new)
                .orElse(null);
    }

    // 강의 읽음 가져오기
    public LectureReadStatusDto getLectureReadStatus(int lectureId, String nickname) {
        Optional<LectureReadEntity> lectureReadEntityOpt = lectureReadRepository.findByLectureIdAndNickname(lectureId, nickname);

        LectureReadStatusDto lectureReadStatusDto = new LectureReadStatusDto();
        lectureReadStatusDto.setLectureId(lectureId);
        lectureReadStatusDto.setNickname(nickname);

        if (lectureReadEntityOpt.isPresent()) {
            lectureReadStatusDto.setLectureRead(lectureReadEntityOpt.get().getLectureRead());
        } else {
            lectureReadStatusDto.setLectureRead("N");
        }

        return lectureReadStatusDto;
    }

    // 강의 읽음 추가
    public void updateLectureReadStatus(LectureReadInput lectureReadInput) {
        LectureReadEntity existingEntity = lectureReadRepository.findReadByLectureIdAndNickname(lectureReadInput.getLectureId(), lectureReadInput.getNickname());
        if (existingEntity == null) {
            LectureReadEntity newEntity = LectureReadEntity.builder()
                    .lectureId(lectureReadInput.getLectureId())
                    .nickname(lectureReadInput.getNickname())
                    .lectureRead(lectureReadInput.getLectureRead())
                    .build();
            lectureReadRepository.save(newEntity);
        } else {
            existingEntity.setLectureRead(lectureReadInput.getLectureRead());
            lectureReadRepository.save(existingEntity);
        }
    }

    // 강의 조회수 증가
    public void increaseViewCount(int lectureId) {
        Optional<LectureEntity> lectureEntityOptional = lectureRepository.findById(lectureId);
        if (lectureEntityOptional.isPresent()) {
            LectureEntity lectureEntity = lectureEntityOptional.get();
            lectureEntity.setLectureViewCount(lectureEntity.getLectureViewCount() + 1);
            lectureRepository.save(lectureEntity);
        }
    }

    // 강의 디테일 보기
    public LectureDetailDto getLectureDetailById(int lectureId) {
        return lectureRepository.findById(lectureId)
                .map(LectureDetailDto::new)
                .orElse(null);
    }

    // 강의 추가
    public void registerLecture(LectureInput lectureInput) {
        LectureEntity lectureEntity = LectureEntity.builder()
                .lectureName(lectureInput.getLectureName())
                .lectureContent(lectureInput.getLectureContent())
                .lectureThumbnail(lectureInput.getLectureThumbnail())
                .streamUrl(lectureInput.getStreamUrl())
                .lecturePackageId(lectureInput.getLecturePackageId()) // lecturePackageId 사용
                .nickname(lectureInput.getNickname()) // nickname 사용
                .build();

        lectureRepository.save(lectureEntity);
    }

    // 강의 삭제
    public void deleteLectures(List<Integer> lectureIds) {
        lectureRepository.deleteAllById(lectureIds);
    }

    public List<String> getFilePathsForLectures(List<Integer> lectureIds) {
        return lectureRepository.findAllById(lectureIds).stream()
                .flatMap(lecture -> Stream.of(lecture.getLectureThumbnail(), lecture.getStreamUrl()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 강의 댓글 목록
    public List<LectureCommentDto> getLectureComments(int lectureId) {
        List<LectureCommentEntity> lectureCommentEntities = lectureCommentRepository.findByLectureId(lectureId);
        List<LectureCommentDto> lectureCommentDtos = new ArrayList<>();
        for (LectureCommentEntity lectureCommentEntity : lectureCommentEntities) {
            LectureCommentDto lectureCommentDto = LectureCommentDto.builder()
                    .lectureCommentId(lectureCommentEntity.getLectureCommentId())
                    .lectureId(lectureCommentEntity.getLectureId())
                    .lectureCommentReply(lectureCommentEntity.getLectureCommentReply())
                    .lectureCommentContent(lectureCommentEntity.getLectureCommentContent())
                    .lectureCommentDate(lectureCommentEntity.getLectureCommentDate())
                    .nickname(lectureCommentEntity.getNickname())
                    .parentCommentId(lectureCommentEntity.getParentCommentId())
                    .build();
            lectureCommentDtos.add(lectureCommentDto);
        }
        return lectureCommentDtos;
    }

    // 강의 댓글 추가
    public void insertLectureComment(CommentInput commentInput) {
        LectureCommentEntity lectureCommentEntity = new LectureCommentEntity();
        lectureCommentEntity.setLectureId(commentInput.getLectureId());
        lectureCommentEntity.setNickname(commentInput.getNickname());
        lectureCommentEntity.setLectureCommentContent(commentInput.getLectureCommentContent());
        lectureCommentEntity.setLectureCommentDate(new Date());
        lectureCommentEntity.setParentCommentId(commentInput.getParentCommentId());

        lectureCommentRepository.save(lectureCommentEntity);
    }

    // 강의 댓글 수정
    public void updateLectureComment(int lectureCommentId, String content) {
        Optional<LectureCommentEntity> lectureCommentEntityOpt = lectureCommentRepository.findByLectureCommentId(lectureCommentId);
        if (lectureCommentEntityOpt.isPresent()) {
            LectureCommentEntity lectureCommentEntity = lectureCommentEntityOpt.get();
            lectureCommentEntity.setLectureCommentContent(content);
            lectureCommentRepository.save(lectureCommentEntity);
        }
    }

    // 강의 댓글 삭제
    public void deleteLectureComment(int lectureCommentId) {
        lectureCommentRepository.deleteByLectureCommentId(lectureCommentId);
    }
    
    // 강의 수정
    // 강의 삭제
    // 강의 댓글 유저 정보 가져오기

}










