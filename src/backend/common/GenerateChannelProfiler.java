package backend.common;

import backend.common.channels.AbstractChannel;
import ast.Filter;
import ast.GeneratesCode;
import static ast.GeneratesCode.writer;
import ast.streamNodes.AnonFilter;
import ast.types.NamedType;
import ast.types.streamIT.StreamType;
import backend.common.Processor;
import backend.common.SourceFileWriter;
import backend.common.estimators.ASTEstimator;
import backend.common.evaluators.ThroughputEvaluator;
import backend.coreVA.CoreVABackend;
import static backend.coreVA.CoreVACodeGenerator.project_dir;
import backend.coreVA.CoreVAProcessor;
import backend.coreVA.xmlModel.MpsocChip;
import hierarchicalGraph.FilterInstance;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenerateChannelProfiler extends GeneratesCode {

    void IncludeHeaders() {
        writer.println("#include <mpsoc_com-lib.h>");
    }

    List<FilterInstance> filters = new ArrayList<>();
    Map<FilterInstance, Processor> partitioning = new LinkedHashMap<>();
    Path src_dir;

    public void Generate(String path, int pop_push) throws IOException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        MpsocChip chip = new MpsocChip(2, 2, 2);
        CoreVABackend.chip = chip;
        CoreVAProcessor p000 = new CoreVAProcessor(0, 0, 0, 0, chip, null);
        CoreVAProcessor p010 = new CoreVAProcessor(0, 1, 0, 1, chip, null);
        CoreVAProcessor p001 = new CoreVAProcessor(0, 0, 1, 2, chip, null);
        CoreVAProcessor p011 = new CoreVAProcessor(0, 1, 1, 3, chip, null);
        CoreVAProcessor p100 = new CoreVAProcessor(1, 0, 0, 4, chip, null);
        CoreVAProcessor p110 = new CoreVAProcessor(1, 1, 0, 5, chip, null);
        CoreVAProcessor p101 = new CoreVAProcessor(1, 0, 1, 6, chip, null);
        CoreVAProcessor p111 = new CoreVAProcessor(1, 1, 1, 7, chip, null);

        Path generated_dir = FileManager.CreateSrcDir(path);
        project_dir = FileManager.CreateProjectDir(generated_dir.toAbsolutePath(), "channelProfiler");
        Path coreva_dir = FileManager.CreateCDir("coreva", project_dir);
        src_dir = FileManager.CreateProcessorDir(coreva_dir, "cores");

        AddPair(p000, p010, backend.coreVA.channels.CoreVANocChannel.class, pop_push);
        AddPair(p100, p101, backend.coreVA.channels.CoreVAClusterChannel.class, pop_push);
        AddPair(p110, p110, backend.common.channels.MemoryChannel2.class, pop_push);
        //Empty(p001);
        //Empty(p011);
        //Empty(p111);

        ThroughputEvaluator evaluator2 = new ThroughputEvaluator(new ASTEstimator());
        evaluator2.getEstimate(new PrintStream(project_dir + "/PerfEstimate.txt"), partitioning);
    }

    void Empty(CoreVAProcessor srcProc) throws IOException {
        writer = new SourceFileWriter(new FileWriter(src_dir + File.separator + srcProc.toString() + ".c"));
        writer.println();
        writer.startBlock("void " + srcProc.toString() + "()");
        writer.endBlock();
        writer.close();
    }

    void AddPair(CoreVAProcessor srcProc, CoreVAProcessor dstProc, Class channelClass, int pop_push) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Filter f1 = new AnonFilter(new StreamType(NamedType.Void, NamedType.Int32_t), false);
        Filter f2 = new AnonFilter(new StreamType(NamedType.Int32_t, NamedType.Void), false);

        FilterInstance src = new FilterInstance(f1.id, 0, 0, pop_push, 0, 0, 0);
        FilterInstance dst = new FilterInstance(f1.id, pop_push, 0, 0, 0, 0, 0);

        filters.add(src);
        filters.add(dst);

        partitioning.put(src, srcProc);
        partitioning.put(dst, dstProc);

        src.split.add(new FilterInstance[]{dst});
        dst.join.add(src);

        AbstractChannel channel = null;
        for (Constructor<AbstractChannel> ctor : channelClass.getConstructors()) {
            int args = ctor.getParameterTypes().length;
            if (args == 4) {
                channel = ctor.newInstance(src, srcProc, dst, dstProc);
            } else if (args == 2) {
                channel = ctor.newInstance(src, dst);
            }
        }

        src.outputs = new LinkedHashMap<>();
        src.outputs.put(dst, channel);

        dst.inputs = new LinkedHashMap<>();
        dst.inputs.put(src, channel);

        if (srcProc != dstProc) {
            writer = new SourceFileWriter(new FileWriter(src_dir + File.separator + srcProc.toString() + ".c"));
            IncludeHeaders();
            channel.WriteDecls();
            writer.println();
            writer.startBlock("void " + srcProc.toString() + "()");
            channel.InitWrite();
            writer.startBlock("for (int i=0; i<10; i++)");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(10);");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(2);");
            channel.GenerateSteadyWaitWrite();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(4);");
            writer.writeLine("// do work here ...");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(5);");
            channel.GenerateSteadyDoneWriting();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(7);");
            writer.endBlock();
            writer.endBlock();
            writer.close();

            writer = new SourceFileWriter(new FileWriter(src_dir + File.separator + dstProc.toString() + ".c"));
            IncludeHeaders();
            channel.ReadDecls();
            writer.println();
            writer.startBlock("void " + dstProc.toString() + "()");
            channel.InitRead();
            writer.startBlock("for (int i=0; i<10; i++)");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(10);");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(2);");
            channel.GenerateSteadyWaitRead();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
            writer.writeLine("// do work here ...");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(5);");
            channel.GenerateSteadyDoneReading();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(6);");
            writer.endBlock();
            writer.endBlock();
            writer.close();
        } else {
            writer = new SourceFileWriter(new FileWriter(src_dir + File.separator + srcProc.toString() + ".c"));
            IncludeHeaders();
            channel.WriteDecls();
            channel.ReadDecls();
            writer.println();
            writer.startBlock("void " + srcProc.toString() + "()");
            channel.InitWrite();
            channel.InitRead();
            writer.startBlock("for (int i=0; i<10; i++)");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(10);");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(2);");
            channel.GenerateSteadyWaitWrite();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(4);");
            writer.writeLine("// do work here ...");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(5);");
            channel.GenerateSteadyDoneWriting();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(7);");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(11);");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(2);");
            channel.GenerateSteadyWaitRead();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(3);");
            writer.writeLine("// do work here ...");
            writer.writeLine("COREVA_HWACC_MISC_TRACE(5);");
            channel.GenerateSteadyDoneReading();
            writer.writeLine("COREVA_HWACC_MISC_TRACE(6);");
            writer.endBlock();
            writer.endBlock();
            writer.close();
        }
    }
}
