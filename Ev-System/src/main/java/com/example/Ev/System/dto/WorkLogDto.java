package com.example.Ev.System.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class WorkLogDto {

    @NotNull(message = "Danh sách nhân viên không được để trống.")
    private List<@NotNull(message = "Mã nhân viên không được để trống.") Integer> staffId;

    @NotNull(message = "Mã lịch hẹn không được để trống.")
    private Integer appointmentId;

    @NotNull(message = "Thời gian làm việc không được để trống.")
    @DecimalMin(value = "0.1", inclusive = true, message = "Thời gian làm việc phải lớn hơn 0.")
    private BigDecimal hoursSpent;

    @NotBlank(message = "Nội dung công việc đã làm không được để trống.")
    private String tasksDone;
}
