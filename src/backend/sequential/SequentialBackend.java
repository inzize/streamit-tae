package backend.sequential;

import ast.Program;
import backend.common.channels.AbstractChannel;
import backend.common.channels.MemoryChannel2;
import backend.common.buffers.FileReaderSplitBuffer;
import hierarchicalGraph.FilterInstance;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class SequentialBackend {

    public static void Compile(String filename, Program program, List<FilterInstance> filters) throws FileNotFoundException, IOException {
        BuildChannels(filters);
        new SequentialCodeGenerator().Generate(filename, program, filters);
    }

    public static void BuildChannels(List<FilterInstance> filters) {
        for (FilterInstance filter : filters) {
            filter.inputs = new LinkedHashMap<>();
            filter.outputs = new LinkedHashMap<>();
        }

        for (FilterInstance src : filters) {
            if (src.push > 0 && src.split.isEmpty()) {
                src.FileWriterChannel = new SequentialFileOutputChannel(src);
            }

            if (src.push > 0 && src.join.isEmpty() && src.filter.id.equals("FileReader")) {
                src.FileReaderChannel = new SequentialFileInputChannel(src);
            }

            for (FilterInstance[] list : src.split) {
                for (FilterInstance dst : list) {
                    if (!src.outputs.containsKey(dst)) {
                        AbstractChannel channel = SelectChannel(src, dst);
                        src.outputs.put(dst, channel);
                        dst.inputs.put(src, channel);
                    }
                }
            }
            if (src.inputs.size() > 1)
            {
                src.input_buffer = new backend.common.buffers.SimpleMergeSplitBuffer(null, src);
            } else {
                src.input_buffer = null;
            }

            if ("FileReader".equals(src.filter.id)) {
                src.output_buffer = new FileReaderSplitBuffer(src);
            } else if (src.outputs.size() > 1) {
                src.output_buffer = new backend.common.buffers.SimpleMergeSplitBuffer(src, null);
            } else {
                src.output_buffer = null;
            }
        }

    }

    private static AbstractChannel SelectChannel(FilterInstance src, FilterInstance dst) {
        return new MemoryChannel2(src, dst);
    }
    
    
}
