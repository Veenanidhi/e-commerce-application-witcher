package com.witcher.e_commerce.application.witcher.service.transaction;

import com.witcher.e_commerce.application.witcher.dao.TransactionRepository;
import com.witcher.e_commerce.application.witcher.entity.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        transaction.setDescription(transaction.getDescription() + " - " + LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}
