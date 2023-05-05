package com.LMS.libraryManagementSystem.repositorys;

import com.LMS.libraryManagementSystem.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
