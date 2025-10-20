package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartUsageDto {

    @NotNull(message = "Mã linh kiện không được để trống.")
    private Integer partId;

    @NotNull(message = "Số lượng sử dụng không được để trống.")
    @Min(value = 1, message = "Số lượng sử dụng phải lớn hơn hoặc bằng 1.")
    private Integer quantityUsed;

    @NotNull(message = "Đơn giá không được để trống.")
    private Double unitCost;
}
