package backend.common.channels;

public interface AbstractInputChannel {
    public void generateWaitRead();
    public void generateDoneReading();
    public void reconfigureForSteadyState();
}
