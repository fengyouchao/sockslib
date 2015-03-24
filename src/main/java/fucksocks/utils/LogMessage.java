package fucksocks.utils;

/**
 * <code>LogMessage</code> a tool class to generate some debug message.
 * 
 * @author Youchao Feng
 * @date  Mar 24, 2015 5:41:56 PM 
 * @version 1.0
 */
public class LogMessage {
	
	public static String create(byte[] buffer, MsgType type){
		return create(buffer, buffer.length, type);
	}
	
	public static String create(byte[] buffer,final int size,  MsgType type){
		StringBuffer debugMsg = new StringBuffer();
		
		switch (type) {
		case RECEIVE:
			debugMsg.append("Received: ");
			break;
		case SEND:
			debugMsg.append("Sent: ");
			break;
		default:
			break;

		}

		for(int i=0; i < size; i++){
			int x = UnsignedByte.toInt(buffer[i]);
			debugMsg.append(Integer.toHexString(x)+" ");
		}
		return debugMsg.toString();
	}
	
	public enum MsgType{
		SEND, RECEIVE
	}

}
