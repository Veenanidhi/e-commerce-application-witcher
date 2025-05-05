package com.witcher.e_commerce.application.witcher.controller.admin;

import com.witcher.e_commerce.application.witcher.entity.WalletTransaction;
import com.witcher.e_commerce.application.witcher.service.WalletTransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/wallet/transaction")
public class TransactionManagement {

    private final WalletTransactionService walletTransactionService;

    public TransactionManagement(WalletTransactionService walletTransactionService) {
        this.walletTransactionService = walletTransactionService;
    }

    @GetMapping
    public String viewAllTransactions(Model model) {
        model.addAttribute("transactions", walletTransactionService.getAllTransactions());
        return "transaction-wallet";
    }

    @GetMapping("/{id}")
    public String viewTransactionDetail(@PathVariable Long id, Model model) {
        WalletTransaction transaction = walletTransactionService.getTransactionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID: " + id));
        model.addAttribute("transaction", transaction);
        return "transaction-details";
    }
}
