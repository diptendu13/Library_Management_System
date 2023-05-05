package com.LMS.libraryManagementSystem.services;

import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksResponseDto;

import java.util.List;

public interface BookService {

    public List<FindAllBooksResponseDto> findBooksByGenre(String genre) throws Exception;

    public List<FindAllBooksResponseDto> findBooksByPublication(String publication) throws Exception;

    public String deleteBookById(int bookId) throws Exception;
}
