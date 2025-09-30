package com.example.Ev.System.service;

import com.example.Ev.System.entity.Maintenancerecord;
import com.example.Ev.System.entity.Partyusage;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.PartRepository;
import com.example.Ev.System.repository.PartyusageRepository;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceRecordService {
    private final AppointmentRepository appointmentRepository;
    private final PartRepository partRepository;
    private final PartyusageRepository partyusageRepository;

    public MaintenanceRecordService(AppointmentRepository appointmentRepository, PartRepository partRepository, PartyusageRepository partyusageRepository) {
        this.appointmentRepository = appointmentRepository;
        this.partRepository = partRepository;
        this.partyusageRepository = partyusageRepository;
    }


    public void recordMaintenance(Integer appointmentId,
                                  String vehicleCondition,
                                  String partName,
                                  int quantityUsed,
                                  String checklist,
                                  String remarks) {
        ServiceAppointment serviceAppointment = appointmentRepository.findById(appointmentId).orElse(null);
        Maintenancerecord maintenancerecord = new Maintenancerecord();
        
        maintenancerecord.setVehicleCondition(vehicleCondition);

        }
}
