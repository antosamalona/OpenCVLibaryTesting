/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camera;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import java.awt.Graphics;
import javax.swing.JFrame;

/**
 *
 * @author thread009
 */
public class WebCam extends JFrame{
    
    public WebCam(){
        initComponent();
    }
    
    private void initComponent(){
          canvas = new CanvasFrame("testing");
          canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
          frameGrabber = new OpenCVFrameGrabber("");
          
          try {    
              
              frameGrabber.start();
              IplImage img;
              
              while (true) {
                  
                  img = frameGrabber.grab();
                  canvas.setCanvasSize(frameGrabber.getImageWidth(), frameGrabber.getImageHeight()); 
                  
                  
                  if (img != null) {
                      cvFlip(img, img, 1);
                      canvas.showImage(img);
                  }
              }
          }catch (Exception e) {}
          setSize(frameGrabber.getImageWidth() +10, frameGrabber.getImageHeight() +10);
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          add(canvas);
    }
    
    
    
    public static void main(String[] xx){
        WebCam webCam = new WebCam();
        
      
    }
    
    private CanvasFrame canvas;
    private FrameGrabber frameGrabber;
    
}
