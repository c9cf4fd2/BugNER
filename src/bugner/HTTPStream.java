/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugner;

import java.util.LinkedList;
import java.util.List;
import jpcap.packet.TCPPacket;

/**
 *
 * @author c9cf4fd2NTUT
 */
public class HTTPStream {

    private boolean host; // true if this stream represent host rx data
    private String streamName;
    private TCPStreamRecordBuffer oooBuffer;
    private List<Byte> ioStream;
    private boolean isHeader;
    private int contentLen;
    private int msgCount;

    public HTTPStream(boolean host) {
        this.host = host;
        if (host) {
            streamName = "REQ";
        } else {
            streamName = "RES";
        }
        oooBuffer = new TCPStreamRecordBuffer();
        isHeader = true;
        contentLen = -1;
        msgCount = 0;
        ioStream = new LinkedList<Byte>();
    }

    public void put(TCPPacket tp) {
        if (host) {
            if (tp.dst_port == 80) { // to host
                if (tp.data.length > 0) {
                    oooBuffer.put(tp);
                }
            } else {
                if (tp.ack) {
                    oooBuffer.setAckedSeq(tp.ack_num);
                }
            }
        } else {
            if (tp.dst_port != 80) { // to client
                if (tp.data.length > 0) {
                    oooBuffer.put(tp);
                }
            } else {
                if (tp.ack) {
                    oooBuffer.setAckedSeq(tp.ack_num);
                }
            }
        }
        if (oooBuffer.getOOOBufLen() > 8192) {
            update();
        }
    }

    public int getIOStreamLen() {
        return ioStream.size();
    }

    public List<Byte> getIOStream() {
        return ioStream;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setContentLen(int contentLen) {
        this.contentLen = contentLen;
    }

    public int getContentLen() {
        return contentLen;
    }

    public void addMsgCount() {
        msgCount++;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public String getStreamName() {
        return streamName;
    }

    public void flushBuffer() {
        if (oooBuffer.getOOOBufLen() > 0) {
            update();
        }
    }

    private void update() {
        byte[] BA = oooBuffer.getIOData();
        for (int i = 0; i < BA.length; i++) {
            ioStream.add(BA[i]);
        }
    }
}
