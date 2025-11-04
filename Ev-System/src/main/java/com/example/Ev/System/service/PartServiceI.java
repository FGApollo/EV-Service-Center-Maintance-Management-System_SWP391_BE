package com.example.Ev.System.service;

import com.example.Ev.System.entity.Part;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PartServiceI {
    public List<Part> getAll();
    public Part getById(Integer id);
    public Part createPart(Part part);
    public Part updatePart(Integer id, Part part);
    public void deletePart(Integer id);
}
