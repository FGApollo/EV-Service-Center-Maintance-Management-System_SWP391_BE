package com.example.Ev.System.controller;

import com.example.Ev.System.dto.VehicleDto;
import com.example.Ev.System.dto.VehicleRespone;
import com.example.Ev.System.service.VehicleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Validated
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService){
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('customer')")
    public List<VehicleDto> getUserVehicle(Authentication authentication){
        return vehicleService.getUserVehicle(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('customer', 'staff')")
    public VehicleDto addVehicle(@Valid @RequestBody VehicleDto dto, Authentication authentication ){
        return vehicleService.addVehicle(authentication.getName(), dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer')")
    public void deleteVehicle(@PathVariable @Positive(message = "id phải > 0") Integer id, Authentication authentication ) {
        vehicleService.deleteVehicle(authentication.getName(), id);
    }

//    @GetMapping("/serviced")
//    public List<VehicleRespone> getServicedVehiclesByCurrentCustomer(Authentication authentication) {
//        return vehicleService.getVeicleResponeByCurrentCustomer(authentication);
//    }
//
    @GetMapping(value = "/{vehicleId}/appointments/latest_time", produces = "text/plain")
    public String getLastestAppointment(@PathVariable @Positive(message = "id phải > 0") Integer vehicleId, Authentication authentication){
        Instant time = vehicleService.getLastestAppointmentDate(authentication.getName(), vehicleId);
        return time.toString();
    }


    @GetMapping("/maintained")
    public List<VehicleRespone> getVehicleMaintained(){
        return vehicleService.getVehicleCompletedMantances("completed");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    public List<VehicleRespone> getAllVehicle(){
        return vehicleService.getAllVehicle();
    }

}
