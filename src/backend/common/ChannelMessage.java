package backend.common;

public class ChannelMessage {
    public long size;
    public long origin;
    public long destTime;
    
    public ChannelMessage(long size, long origin, long destTime) {
        this.size = size;
        this.origin = origin;
        this.destTime = destTime;
    }
}
