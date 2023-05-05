package com.LMS.libraryManagementSystem.repositorys;

import com.LMS.libraryManagementSystem.enums.Genre;
import com.LMS.libraryManagementSystem.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    public List<Book> findByGenre(Genre genre);

    public List<Book> findByPublication(String publication);
}
