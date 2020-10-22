package com.example.demo_s3.services;

import org.opencv.core.Core;
import org.springframework.stereotype.Service;

@Service
public class OpenCVBase {
    public OpenCVBase() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_ffmpeg249_64");
    }
}
