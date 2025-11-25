package com.example.Ev.System.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestSuggestPart {

    @NotNull(message = "Appointment ID cannot be null")
    private Integer appointmentId;

    @NotNull(message = "Part ID cannot be null")
    private Integer partId;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;

    private String technicianNote;

}
