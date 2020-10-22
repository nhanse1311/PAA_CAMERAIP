package com.example.demo_s3.services;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class OpenCVFaceService extends OpenCVBase{
    private DaemonThread myThread = null;
    String imageFace;
    VideoCapture capFace = null;
    Mat frameFace= null;
    public String getImageFace() {
        return imageFace;
    }

    public void setImageFace(String imageFace) {
        this.imageFace = imageFace;
    }

    public OpenCVFaceService() {
        startStream();
    }

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (capFace.grab()) {
                        try {
                            capFace.retrieve(frameFace);
                        } catch (Exception ex) {
                            System.out.println("Error");
                        }
                    }
                }
            }
        }
    }

    private void openRTSPFaceCamera(boolean isOpened, VideoCapture capturedVideo, Mat cameraMat) {
        if (isOpened) {
            boolean tempBool = capturedVideo.read(cameraMat);
            System.out.println("VideoCapture face is open " + tempBool);

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
        capFace = new VideoCapture();
        boolean isOpenedFace = capFace.open("rtsp://admin:admin@192.168.43.33:8554/unicast");
        frameFace = new Mat();
        openRTSPFaceCamera(isOpenedFace,capFace,frameFace);

        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
    }

    public void snapShot(){
        imageFace = "";
        imageFace = System.currentTimeMillis()+"-face.jpg";
        File fileFace = new File("src/main/resources/static/images/" + imageFace);
        try {
            fileFace.createNewFile();
        } catch (IOException ex) {
//            Logger.getLogger(LocatorEx.Snapshot.class.getName()).log(Level.SEVERE, null, ex);
        }
        Highgui.imwrite(fileFace.getPath(),frameFace);
    }
}
