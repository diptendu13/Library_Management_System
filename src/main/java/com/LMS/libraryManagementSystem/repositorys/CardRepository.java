package com.LMS.libraryManagementSystem.repositorys;

import com.LMS.libraryManagementSystem.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
}
