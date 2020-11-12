package main;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

public class Main {

	  public static void main(String[] args) { 
		  EventQueue.invokeLater(new Runnable() {
		        public void run() {
		            try {
		                new GUI();
		                
		            }
		            catch (Exception e) {
		                e.printStackTrace();
		            }
		        }
		    }); 
	        
	    } 
}
