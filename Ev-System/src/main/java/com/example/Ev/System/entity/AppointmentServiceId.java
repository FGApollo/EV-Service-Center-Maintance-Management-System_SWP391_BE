package com.example.Ev.System.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class AppointmentServiceId implements Serializable {
    private static final long serialVersionUID = -7673425440924701302L;
    @Column(name = "appointment_id", nullable = false)
    private Integer appointmentId;

    @Column(name = "service_type_id", nullable = false)
    private Integer serviceTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppointmentServiceId entity = (AppointmentServiceId) o;
        return Objects.equals(this.appointmentId, entity.appointmentId) &&
                Objects.equals(this.serviceTypeId, entity.serviceTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, serviceTypeId);
    }

}