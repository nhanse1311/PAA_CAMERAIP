package com.example.demo_s3.controllers;

import com.example.demo_s3.services.OpenCVLicensePlateService;
import com.example.demo_s3.utils.BaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/detect/")
public class DetectController {
    private OpenCVLicensePlateService openCVLicensePlateService = new OpenCVLicensePlateService();
    @Autowired
    public DetectController() {

    }

    @PostMapping("/license-plate/{name_image}")
    public ResponseEntity<String> getLicensePlate(@PathVariable("name_image") String nameImage) {
        ResponseEntity<String> response = null;
        try {
            String base64Image = "";
            File file = new File("src/main/resources/static/images/"+nameImage+".jpg");
            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            base64Image = Base64.getEncoder().encodeToString(imageData);

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://127.0.0.1:5000/findlp";
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap();
            map.put("Content-Type", "application/json");

            headers.setAll(map);

            Map req_payload = new HashMap();
            req_payload.put("license-img", "data:image/jpeg;base64,"+base64Image);
            HttpEntity<?> request = new HttpEntity<>(req_payload, headers);

            response = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    @PostMapping("/license-plate")
    public ResponseEntity<String> getLicensePlate() {
        ResponseEntity<String> response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://127.0.0.1:5000/findlp";
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap();
            map.put("Content-Type", "application/json");

            headers.setAll(map);

            Map req_payload = new HashMap();
            req_payload.put("license-img", "data:image/jpeg;base64,"+ BaseUtil.base64Image);
            HttpEntity<?> request = new HttpEntity<>(req_payload, headers);

            response = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
