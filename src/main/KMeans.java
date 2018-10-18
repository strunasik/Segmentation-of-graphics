package main; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage; 
import java.io.File; 
import java.util.Arrays; 
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer; 


public class KMeans { 
    BufferedImage original; 
    BufferedImage result; 
    Cluster[] clusters; 
    public static final int MODE_CONTINUOUS = 1; 
    public static final int MODE_ITERATIVE = 2;
    Timer t;
     
     
    public KMeans() {    } 
     
    public BufferedImage calculate(BufferedImage image, int k, int mode, JPanel screen) { 
    	
        long start = System.currentTimeMillis(); 
        int w = image.getWidth(); 
        int h = image.getHeight(); 
        clusters = createClusters(image,k);         // create clusters 
        int[] lut = new int[w*h];         // create cluster lookup table 
        Arrays.fill(lut, -1); 
       
         
        // at first loop all pixels will move their clusters 

        result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); 
        
        t = new Timer(0, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
	            int pixelChangedCluster = 0; 
	            for (int y=0;y<h;y++) { 
	                for (int x=0;x<w;x++) { 
	                    int pixel = image.getRGB(x, y); 
	                    Cluster cluster = findMinimalCluster(pixel); 
	                    if (lut[w*y+x]!=cluster.getId()) { 
	                        // cluster changed 
	                        if (mode==MODE_CONTINUOUS) { 
	                            if (lut[w*y+x]!=-1)                         
	                                clusters[lut[w*y+x]].removePixel(pixel); // remove from possible previous cluster 
	                            cluster.addPixel(pixel); // add pixel to cluster 
	                        } 
	                        
	                        pixelChangedCluster++; // continue looping   
	                        lut[w*y+x] = cluster.getId(); // update lut 
	                    } 
	                } 
	            } 
	            if (mode==MODE_ITERATIVE) { 
	                for (int i=0;i<clusters.length;i++)  // update clusters 
	                    clusters[i].clear(); 
	         
	                for (int y=0;y<h;y++) { 
	                    for (int x=0;x<w;x++) { 
	                        int clusterId = lut[w*y+x]; 
	                        clusters[clusterId].addPixel(image.getRGB(x, y)); // add pixels to cluster 
	                    } 
	                } 
	            } 

	            for (int y=0;y<h;y++) { 
	            	for (int x=0;x<w;x++) { 
	            		int clusterId = lut[w*y+x]; 
	            		result.setRGB(x, y, clusters[clusterId].getRGB()); 
	            	} 
	            } 
	            System.out.println("repainted!");
				screen.repaint();
				if(pixelChangedCluster < 20)t.stop();
			}
		});

        t.start();
     
        
        return result; 
    } 
     
    public Cluster[] createClusters(BufferedImage image, int k) { 
        // Here the clusters are taken with specific steps, 
        // so the result looks always same with same image. 
        // You can randomize the cluster centers, if you like. 
        Cluster[] result = new Cluster[k]; 
        int x = 0; int y = 0; 
        int dx = image.getWidth()/k; 
        int dy = image.getHeight()/k; 
        for (int i=0;i<k;i++) { 
            result[i] = new Cluster(i,image.getRGB(x, y)); 
            x+=dx; y+=dy; 
        } 
        return result; 
    } 
     
    public Cluster findMinimalCluster(int rgb) { 
        Cluster cluster = null; 
        int min = Integer.MAX_VALUE; 
        for (int i=0;i<clusters.length;i++) { 
            int distance = clusters[i].distance(rgb); 
            if (distance<min) { 
                min = distance; 
                cluster = clusters[i]; 
            } 
        } 
        return cluster; 
    } 
     
    public static void saveImage(File file, String format,BufferedImage image) { 
        try { 
            ImageIO.write(image, format, file); 
        } catch (Exception e) { 
            System.out.println(e.toString()+" Image '"+file.getAbsolutePath() 
                                +"' saving failed."); 
        } 
    } 
     
    public static BufferedImage loadImage(File file) { 
        BufferedImage result = null; 
        try { 
            result = ImageIO.read(file); 
        } catch (Exception e) {} 
        return result; 
    } 
     

     
} 