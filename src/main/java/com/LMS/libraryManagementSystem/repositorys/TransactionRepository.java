package com.LMS.libraryManagementSystem.repositorys;

import com.LMS.libraryManagementSystem.dtos.responseDtos.BookResponseDto;
import com.LMS.libraryManagementSystem.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    public Transaction findByUniqueTransactionCode(String utc);

    @Query(value = "select * from transaction t where t.card_id=:cardId and t.book_id=:bookId", nativeQuery = true)
    public List<Transaction> getTransactionListUsingCardIdAndBookId(Integer cardId, Integer bookId);
}
