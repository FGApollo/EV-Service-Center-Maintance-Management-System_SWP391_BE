package com.example.Ev.System.service;

import com.example.Ev.System.entity.Part;
import com.example.Ev.System.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PartService implements PartServiceI{
    @Autowired
    private PartRepository partRepository;

    @Override
    public List<Part> getAll() {
        return partRepository.findAll();
    }

    @Override
    public Part getById(Integer id) {
        return partRepository.findById(id).get();
    }

    @Override
    public Part createPart(Part part) {
        part.setCreatedAt(Instant.now());
        return partRepository.save(part);
    }

    @Override
    public Part updatePart(Integer id, Part part) {
        return partRepository.findById(id)
                .map(existingPart -> {
                    existingPart.setName(part.getName());
                    existingPart.setDescription(part.getDescription());
                    existingPart.setUnitPrice(part.getUnitPrice());
                    existingPart.setMinStockLevel(part.getMinStockLevel());
            return partRepository.save(existingPart);
        })
                .orElseThrow(() -> new RuntimeException("Part not found with id " + id));
    }

    @Override
    public void deletePart(Integer id) {
        if(!partRepository.existsById(id)) {
            throw new RuntimeException("Part not found with id " + id);
        }
        partRepository.deleteById(id);
    }
}
