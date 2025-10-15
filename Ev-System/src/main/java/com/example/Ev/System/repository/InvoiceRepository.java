package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    public List<Invoice> findByStatusAndPaymentDateBetween(String status, LocalDateTime startDate, LocalDateTime endDate);
}
