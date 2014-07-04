import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;


public class Check {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommPortIdentifier USB = null;
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			// System.out.println(portIdentifier.getName());
			//if (portIdentifier.getName().indexOf("tty.usbmodem24061501") > -1) {
				USB = portIdentifier;
			//}
		}
		if (USB != null && !USB.isCurrentlyOwned()) {
			CommPort commPort;
			try {
				commPort = USB.open(USB.getName(), 2000);

				if (commPort instanceof SerialPort) {
					SerialPort serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(19200,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					InputStream in = serialPort.getInputStream();
					OutputStream out = serialPort.getOutputStream();
					int err = 0;
					int [][] ring = new int[2][8];
					System.out.print("start\n");
					long str = System.nanoTime();
					for(int i=0;i<10000/8;i++){
						for(int j=0;j<8;j++){
							int rnd = (int)(Math.round((Math.random()*255)));
							out.write(rnd);
							ring[i%2][j] = rnd;
						}
						out.flush();
						for(int j=0;j<8;j++){
							if(in.read()!=(ring[(i+1)%2][j]&0xFF))
								err++;
						}
					}
					System.out.print("failed "+err+"\n time =" + (System.nanoTime()-str) + "\n");
					in.close();
					out.close();
					
				}
				commPort.close();
			} catch (PortInUseException e) {
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		System.out.print("end\n");
	}
}
