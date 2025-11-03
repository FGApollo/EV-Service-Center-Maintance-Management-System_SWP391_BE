package com.example.Ev.System.controller;

import com.example.Ev.System.dto.VehicleDto;
import com.example.Ev.System.dto.VehicleRespone;
import com.example.Ev.System.service.VehicleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService){
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<VehicleDto> getUserVehicle(Authentication authentication){
        return vehicleService.getUserVehicle(authentication.getName());
    }

    @PostMapping
    public VehicleDto addVehicle(@RequestBody VehicleDto dto, Authentication authentication ){
        return vehicleService.addVehicle(authentication.getName(), dto);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Integer id, Authentication authentication ) {
        vehicleService.deleteVehicle(authentication.getName(), id);
    }

//    @GetMapping("/serviced")
//    public List<VehicleRespone> getServicedVehiclesByCurrentCustomer(Authentication authentication) {
//        return vehicleService.getVeicleResponeByCurrentCustomer(authentication);
//    }
//
    @GetMapping(value = "/{vehicleId}/appointments/latest_time", produces = "text/plain")
    public String getLastestAppointment(@PathVariable Integer vehicleId, Authentication authentication){
        Instant time = vehicleService.getLastestAppointmentDate(authentication.getName(), vehicleId);
        return time.toString();
    }

    @GetMapping("/maintained")
    public List<VehicleRespone> getVehicleMaintained(){
        return vehicleService.getVehicleCompletedMantances("completed");
    }

}
