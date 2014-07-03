
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;


public class HEXIO {

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
					serialPort.setSerialPortParams(9600,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);

					InputStream in = serialPort.getInputStream();
					OutputStream out = serialPort.getOutputStream();

					(new Thread(new SerialReader(in))).start();
					(new Thread(new SerialWriter(out))).start();

				}
			} catch (PortInUseException e) {
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	/** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(bytesToHex(buffer,len));
                    //System.out.print(bytesToStringUTFCustom(buffer));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    public static String bytesToStringUTFCustom(byte[] bytes) {
    	 char[] buffer = new char[bytes.length >> 1];
    	 for(int i = 0; i < buffer.length; i++) {

    		 char c = (char)(bytes[i]) ;
    		 buffer[i] = c;
    	 }
    	 return new String(buffer);
    }

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes,int len) {
        char[] hexChars = new char[len * 3];
        int v;
        for ( int j = 0; j < len; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ',';
        }
        return new String(hexChars);
    }
    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
}
