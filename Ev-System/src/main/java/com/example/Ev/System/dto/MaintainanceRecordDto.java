package com.example.Ev.System.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaintainanceRecordDto {

    @NotBlank(message = "Tình trạng xe không được để trống.")
    @Size(max = 255, message = "Tình trạng xe không được vượt quá 255 ký tự.")
    private String vehicleCondition;

    @NotBlank(message = "Danh sách kiểm tra không được để trống.")
    private String checklist;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự.")
    private String remarks;

    @NotNull(message = "Danh sách linh kiện sử dụng không được để trống.")
    @Size(min = 1, message = "Phải có ít nhất một linh kiện được sử dụng.")
    private List<@Valid PartUsageDto> partsUsed; //dung valid de goi check cua PartUsageDto

    @NotNull(message = "Danh sách nhân viên thực hiện không được để trống.")
    @Size(min = 1, message = "Phải có ít nhất một nhân viên thực hiện bảo trì.")
    private List<@NotNull(message = "Mã nhân viên không được để trống.") Integer> staffIds;
}
