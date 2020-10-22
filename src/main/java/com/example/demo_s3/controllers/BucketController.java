package com.example.demo_s3.controllers;
import com.example.demo_s3.services.AmazonClient;
import com.example.demo_s3.services.OpenCVFaceService;
import com.example.demo_s3.services.OpenCVLicensePlateService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/storage/")
public class BucketController {

    private AmazonClient amazonClient;
    private OpenCVLicensePlateService openCVLicensePlateService;
    private OpenCVFaceService openCVFaceService;

    @Autowired
    BucketController(AmazonClient amazonClient,OpenCVFaceService openCVFaceService ,OpenCVLicensePlateService openCVLicensePlateService) {
        this.amazonClient = amazonClient;
        this.openCVFaceService = openCVFaceService;
        this.openCVLicensePlateService = openCVLicensePlateService;
    }

    @PostMapping("/snapshot")
    public List<String> snapshot(){
        List<String> images = new ArrayList<>();

        openCVFaceService.snapShot();
        openCVLicensePlateService.snapShot();

        images.add(openCVFaceService.getImageFace());
        images.add(openCVLicensePlateService.getImageLicensePlates());
        return images;
    }

    @PostMapping("/uploadFile/{name_image}")
    public String uploadFile(@PathVariable("name_image") String nameImage) throws FileNotFoundException {
//        openCVService.snapShot();
        Path path = Paths.get("src/main/resources/static/images/" + nameImage+".jpg");
        String name = nameImage+".jpg";
        String originalFileName = nameImage+".jpg";
        String contentType = "image/jpg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);
        return this.amazonClient.uploadFile(result);
    }

    @PostMapping("/uploadFiles")
    public ResponseEntity<?> uploadFiles(@RequestBody List<String> nameImages) throws FileNotFoundException {
        for (int i = 0; i < nameImages.size(); i++) {
            Path path = Paths.get("src/main/resources/static/images/" + nameImages.get(i));
            String name = nameImages.get(i);
            String originalFileName = nameImages.get(i);
            String contentType = "image/jpg";
            byte[] content = null;
            try {
                content = Files.readAllBytes(path);
            } catch (final IOException e) {
            }
            MultipartFile result = new MockMultipartFile(name,
                    originalFileName, contentType, content);
            this.amazonClient.uploadFile(result);
        }
        return ResponseEntity.ok(nameImages);

    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }
    @GetMapping("/images/{name_image}")
    public ResponseEntity<byte[]> getImage(@PathVariable("name_image") String nameImage) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(AmazonClient.CONTENT_TYPE_IMAGE))
                .body(amazonClient.getImage(nameImage));
    }

    @RequestMapping(value = "/beforesave/{name_image}", method = RequestMethod.GET,  produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImageAsByteArray(@PathVariable("name_image") String nameImage) throws IOException {
        File file = new File("src/main/resources/static/images/"+nameImage+".jpg");
        InputStream in = new FileInputStream(file);
        return IOUtils.toByteArray(in);
    }
}
