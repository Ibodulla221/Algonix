package com.code.algonix.problems;

import com.code.algonix.problems.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // Agar kerak bo‘lsa, keyinchalik qidiruv uchun qo‘shimcha metodlar qo‘shish mumkin, masalan:
    // List<Problem> findByTitleContainingIgnoreCase(String keyword);
}
