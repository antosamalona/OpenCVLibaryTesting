/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camera;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;      
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMinAreaRect2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author thread009
 */
public class WebCam extends CanvasFrame {
    
    public WebCam(String title) throws FrameGrabber.Exception{
        super(title);
        initComponent();
        setLocationRelativeTo(null);
        //stAction();
    }
    
    private void initComponent() throws FrameGrabber.Exception{
          
          frameGrabber = new OpenCVFrameGrabber("");
          panel = new javax.swing.JPanel();
          recordVideo = new javax.swing.JButton();
          motionDetectorActive = new javax.swing.JCheckBox();
          
          motionDetectorActive.setFont(new java.awt.Font("Times New Roman", 1, 12)); 
          motionDetectorActive.setText("Motion Detector");
          
          recordVideo.setFont(new java.awt.Font("Times New Roman", 0, 12)); 
          recordVideo.setText("Record Video");
          
          setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
          setPreferredSize(new Dimension(744, 597));
          setResizable(false);
          
          panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
          
          javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
          panel.setLayout(panelLayout);
          panelLayout.setHorizontalGroup(
                  panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                          .addGroup(panelLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(motionDetectorActive)
                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(recordVideo, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                  .addContainerGap(458, Short.MAX_VALUE))
          );
          panelLayout.setVerticalGroup(
                  panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                          .addGroup(panelLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                          .addComponent(motionDetectorActive)
                                          .addComponent(recordVideo))
                                  .addContainerGap(139, Short.MAX_VALUE))
          );
          
          javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
          getContentPane().setLayout(layout);
          layout.setHorizontalGroup(
                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                          .addGroup(layout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addContainerGap())
          );
          layout.setVerticalGroup(
                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                  .addContainerGap(209, Short.MAX_VALUE)
                                  .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                  .addContainerGap())
          );
          
          frameGrabber.start();
          
          IplImage img = frameGrabber.grab();
          IplImage image = null;
          IplImage prevImage = null;          
          IplImage diff = null;
          
          opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

          while (isVisible() && (img = frameGrabber.grab()) != null) {
              cvClearMemStorage(storage);
              
              if(!motionDetectorActive.isSelected()){
                  img = frameGrabber.grab();            
              //System.out.println("width :"+getWidth()+" "+"height: "+getHeight());
              setCanvasSize(getWidth(), getHeight()-230); 
              
              if (img != null) {
                  cvFlip(img, img, 1);
                  showImage(img);
              }
              }else{
                  cvSmooth(img, img, CV_GAUSSIAN, 9, 9, 2, 2);
                  if (image == null) {
                      image = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                      cvCvtColor(img, image, CV_RGB2GRAY);
                  } else {
                      prevImage = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                      prevImage = image;
                      image = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                      cvCvtColor(img, image, CV_RGB2GRAY);
                  }
                  
                  if (diff == null) {
                      diff = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                  }
                  
                  if (prevImage != null) {
                      cvAbsDiff(image, prevImage, diff);
                      
                      cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);
                      cvFlip(diff, diff, 1);
                      showImage(diff);
                      
                      opencv_core.CvSeq contour = new opencv_core.CvSeq(null);
                      cvFindContours(diff, storage, contour, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
                      
                      while (contour != null && !contour.isNull()) {
                          if (contour.elem_size() > 0) {
                              opencv_core.CvBox2D box = cvMinAreaRect2(contour, storage);
                              
                              if (box != null) {
                                  opencv_core.CvPoint2D32f center = box.center();
                                  opencv_core.CvSize2D32f size = box.size();
                              }
                          }
                          contour = contour.h_next();
                      }
                  }
              }
          }
          frameGrabber.stop();
    }
    
    /*private void stAction(){
        recordVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }*/
    
    
    
    public static void main(String[] xx) throws FrameGrabber.Exception{
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        WebCam webCam = new WebCam("Web Camera");
        
      
    }
                
    //private CanvasFrame canvas;
    private FrameGrabber frameGrabber;
    private JButton recordVideo;
    private JPanel panel;
    private JCheckBox motionDetectorActive;
    
}
