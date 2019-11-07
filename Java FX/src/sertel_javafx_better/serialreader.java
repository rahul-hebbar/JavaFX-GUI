package sertel_javafx_better;

import java.util.Arrays;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public class serialreader {
	static SerialPort serialPort;
	static byte[] packet;
	public static void main(String[] args) {
	    try {
	    	serialPort = new SerialPort("COM8"); 
	        serialPort.openPort();//Open port
	        serialPort.setParams(19200, 8, 1, 0);//Set params
	        serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
	    }
	    catch (SerialPortException ex) {
	        System.out.println(ex);
	    }
	}
	static class SerialPortReader implements SerialPortEventListener {

	    public void serialEvent(SerialPortEvent event) {
	        if(event.isRXCHAR() && event.getEventValue()>0){
	                try {
	                    packet = serialPort.readBytes(110);
	                    String par = new String(packet);
	                    String[] params = par.split(",");
	                    System.out.println(event.getEventValue());
	                    System.out.println(par);
	                }
	                catch (SerialPortException ex) {
	                    System.out.println(ex);
	                }
	        }
	       
	    }
	}
}
