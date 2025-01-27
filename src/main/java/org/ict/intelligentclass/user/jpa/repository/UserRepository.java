package org.ict.intelligentclass.user.jpa.repository;


import org.ict.intelligentclass.user.jpa.entity.UserEntity;
import org.ict.intelligentclass.user.jpa.entity.id.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UserId> {

    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findByPhone(String phone);

    List<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findById(UserId userId);


    @Query("SELECT u FROM UserEntity u WHERE u.userId.userEmail = :userEmail")
    Optional<UserEntity> findByEmail(@Param("userEmail") String userEmail);


    @Query("SELECT u FROM UserEntity u WHERE u.userId.userEmail = :userEmail AND u.userId.provider = :provider")
    Optional<UserEntity> findByEmailAndProvider(@Param("userEmail") String userEmail, @Param("provider") String provider);

    @Query("SELECT u FROM UserEntity u WHERE u.registerTime >= CURRENT_DATE")
    List<UserEntity> findTodayRegisteredUsers();

    @Query("SELECT u FROM UserEntity u WHERE u.registerTime BETWEEN :begin AND :end")
    List<UserEntity> findRegisteredUsersByPeriod(@Param("begin") Date begin, @Param("end") Date end);

    @Query("SELECT u.reportCount FROM UserEntity u WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    Optional<Integer> findReportCountByUserId(@Param("email") String email, @Param("provider") String provider);

    @Query("SELECT u.loginOk FROM UserEntity u WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    Optional<String> findLoginOkByUserId(@Param("email") String email, @Param("provider") String provider);

    @Query("SELECT u.faceLoginYn FROM UserEntity u WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    Optional<String> findFaceLoginYnByUserId(@Param("email") String email, @Param("provider") String provider);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.faceLoginYn = 'Y' WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    void updateFaceLoginYn(@Param("email") String email, @Param("provider") String provider);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.reportCount = u.reportCount + 1 WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    void updateReportCount(@Param("email") String email, @Param("provider") String provider);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.userType = :userType WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    void updateUserType(@Param("email") String email, @Param("provider") String provider, @Param("userType") int userType);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.loginOk = :loginOk WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    void updateLoginOk(@Param("email") String email, @Param("provider") String provider, @Param("loginOk") String loginOk);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.userPwd = :newPw WHERE u.userId.userEmail = :email AND u.userId.provider = :provider")
    void updateUserPwd(@Param("email") String email, @Param("provider") String provider, @Param("newPw") String newPw);

    @Query("SELECT u FROM UserEntity u WHERE "
            + "(:nickname IS NULL OR u.nickname <> :nickname) AND "
            + "((:addingOption = 'teachers' AND u.userType = 1) OR "
            + "(:addingOption = 'students' AND u.userType = 0) OR "
            + "(:addingOption = 'groups' AND u.userType <> 2)) "
            + "ORDER BY u.userName DESC")
    Page<UserEntity> findPeople(@Param("nickname") String nickname,
                                @Param("addingOption") String addingOption,
                                Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE "
            + "(:nickname IS NULL OR u.nickname <> :nickname) AND "
            + "((:addingOption = 'teachers' AND u.userType = 1) OR "
            + "(:addingOption = 'students' AND u.userType = 0) OR "
            + "(:addingOption = 'groups' AND u.userType <> 2)) AND "
            + "(:searchQuery IS NULL OR u.nickname LIKE %:searchQuery%) "
            + "ORDER BY u.userName DESC")
    Page<UserEntity> findPeopleWithQuery(@Param("nickname") String nickname,
                                         @Param("addingOption") String addingOption,
                                         @Param("searchQuery") String searchQuery,
                                         Pageable pageable);

    List<UserEntity> findAllByRegisterTimeBetween(LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT u FROM UserEntity u WHERE "
            + "(:searchQuery IS NULL OR u.userName LIKE %:searchQuery% OR u.userId.userEmail LIKE %:searchQuery% OR u.phone LIKE %:searchQuery%) AND "
            + "(:userType IS NULL OR u.userType = :userType) AND "
            + "(:startDate IS NULL OR u.registerTime >= :startDate) AND "
            + "(:endDate IS NULL OR u.registerTime <= :endDate)")
    Page<UserEntity> findAllUsers(@Param("searchQuery") String searchQuery,
                                  @Param("userType") Integer userType,
                                  @Param("startDate") LocalDateTime startDateTime,
                                  @Param("endDate") LocalDateTime endDateTime,
                                  Pageable pageable);

}