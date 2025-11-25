package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByInvoice_Appointment_CustomerId(Integer customerId);
}
