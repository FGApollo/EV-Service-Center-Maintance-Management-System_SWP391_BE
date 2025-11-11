package com.example.Ev.System.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UploadService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload file to Cloudinary and return the public URL
     */
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName, // e.g. "certificates" or "profiles"
                        "resource_type", "auto"
                ));
        return uploadResult.get("secure_url").toString(); // Return the uploaded image URL
    }
}
