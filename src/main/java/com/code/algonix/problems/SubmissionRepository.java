package com.code.algonix.problems;

import com.code.algonix.problems.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Foydalanuvchining barcha submissionlarini olish
    List<Submission> findByUserId(Long userId);

    // Ma'lum masalaga tegishli barcha submissionlar
    List<Submission> findByProblemId(Long problemId);

    // Bitta foydalanuvchining bitta masalaga topshirganlari
    List<Submission> findByUserIdAndProblemId(Long userId, Long problemId);
}
