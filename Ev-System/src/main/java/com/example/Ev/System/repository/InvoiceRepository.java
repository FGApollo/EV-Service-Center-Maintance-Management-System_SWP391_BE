package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    public List<Invoice> findByStatusAndPaymentDateBetween(String status, LocalDateTime startDate, LocalDateTime endDate);
    List<Invoice> findByStatus(String status);

    @Query("SELECT i FROM Invoice i JOIN i.appointment a JOIN a.serviceCenter c WHERE i.status = :status AND c.id = :centerId")
    List<Invoice> findInvoicesByStatusAndCenter(
            @Param("status") String status,
            @Param("centerId") Integer centerId);

    boolean existsByAppointment_Id(Integer appointmentId);
    Invoice findByAppointment_IdAndStatus(Integer appointmentId, String status);
}
