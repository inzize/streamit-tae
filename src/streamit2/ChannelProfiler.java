package streamit2;

import backend.common.GenerateChannelProfiler;

public class ChannelProfiler {

    public static void main(String[] args) throws Exception {
        GenerateChannelProfiler profiler = new GenerateChannelProfiler();
        profiler.Generate(args[0], Integer.parseInt(args[1]));
    }
}
