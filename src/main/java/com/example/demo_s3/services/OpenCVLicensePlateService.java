package com.example.demo_s3.services;

import com.example.demo_s3.utils.BaseUtil;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OpenCVLicensePlateService extends OpenCVBase{
    private DaemonThread myThread = null;
    String imageLicensePlates;
    String base64;

    VideoCapture capLicensePlate = null;
    Mat frameLicensePlate =null;
//    MatOfByte memLicensePlate = null;


    public String getImageLicensePlates() {
        return imageLicensePlates;
    }

    public void setImageLicensePlates(String imageLicensePlates) {
        this.imageLicensePlates = imageLicensePlates;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public OpenCVLicensePlateService() {
        startStream();
    }

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (capLicensePlate.grab() ) {
                        try {
                            capLicensePlate.retrieve(frameLicensePlate);
//                            Highgui.imencode(".bmp", frame, mem);
                        } catch (Exception ex) {
                            System.out.println("Error");
                        }
                    }
                }
            }
        }
    }

    private void openRTSPLicensePLate(boolean isOpened, VideoCapture capturedVideo, Mat cameraMat) {
        if (isOpened) {
            boolean tempBool = capturedVideo.read(cameraMat);
            System.out.println("VideoCapture license plate is open " + tempBool);

            if (!cameraMat.empty()) {
                System.out.println("Print image size: " + cameraMat.size());
                //processing image captured in cameraMat object
            } else {
                System.out.println("Mat is empty.");
            }
        } else {
            System.out.println("Camera connection problem. Check addressString");
        }
    }

    public void startStream(){
        capLicensePlate = new VideoCapture();
        boolean isOpened = capLicensePlate.open("rtsp://admin:admin@192.168.43.157:8554/unicast");
        frameLicensePlate = new Mat();
        openRTSPLicensePLate(isOpened, capLicensePlate, frameLicensePlate);



        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
    }
    public void snapShot(){
        imageLicensePlates = "";
        imageLicensePlates = System.currentTimeMillis() + "-license-plates.jpg";
        File fileLicensePlate = new File("src/main/resources/static/images/" + imageLicensePlates);
        try {
            fileLicensePlate.createNewFile();
            Highgui.imwrite(fileLicensePlate.getPath(), frameLicensePlate);

            FileInputStream imageInFile = new FileInputStream(fileLicensePlate);
            byte imageData[] = new byte[(int) fileLicensePlate.length()];
            imageInFile.read(imageData);
            BaseUtil.base64Image = Base64.getEncoder().encodeToString(imageData);

        } catch (IOException ex) {
//            Logger.getLogger(LocatorEx.Snapshot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}