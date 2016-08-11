package backend.common.buffers;

import static ast.GeneratesCode.writer;
import backend.common.Chip;
import backend.common.channels.AbstractChannel;
import backend.common.channels.AbstractInputChannel;
import backend.common.estimators.BufferEstimator;
import backend.common.estimators.CommunicationModel;
import hierarchicalGraph.FilterInstance;
import streamit2.NoEstimationDataException;

public class FileReaderSplitBuffer extends AbstractMergeSplitBuffer {
    //TODO: remove these values
    private static final int loopOverhead = 9;
    private static final int mergingOverhead = 3;
    private static final int assignOverhead = 1;
    private static final int tempOverhead = 2;
    public FileReaderSplitBuffer(FilterInstance src) {
        super(src, null);
    }
  
    @Override
    public void ReadDecls() {
    }    
    
    @Override
    public void GenerateInitWaitRead() {
    }

    @Override
    public void GenerateInitWaitWrite() {
    }

    @Override
    public void GenerateInitDoneReading() {
    }

    /**
     * Note:
     * We assume that there was an empty generateInitDoneWrite for the filereader
     */
    @Override
    public void GenerateInitDoneWriting() {
        //Check that we are used in a split construct after the FileReader
        assert BufferType.SPLIT == bufferType;
        assert src.FileReaderChannel instanceof AbstractInputChannel;
        AbstractInputChannel input = (AbstractInputChannel)src.FileReaderChannel;
        
        //Exit early if there is nothing to do in init 
        if (src.initMult==0) {
            writer.writeLine("/*  FileReader SplitBuffer inlined -> nothing to do */");
            return;
        }
        writer.writeLine("/*  FileReader SplitBuffer inlined */");
        
        String var = "_tmp";
        // declare _tmp variable only when needed
        for (FilterInstance[] list : src.split) {
            if (list.length > 1) {
                writer.write("");
                src.filter.type.output.PrintCType();
                writer.println(" " + var + ";");
                break;
            }
        }
        long counter = src.initMult * src.push;
        long counter2 = src.netSteady();
        writer.writeLine("//netInit:"+counter+" netsteady: "+counter2);
        input.generateWaitRead(); 
        writer.writeLine("//Split"+src.split.size());
        for (FilterInstance[] list : src.split) {
            writer.writeLine("//list:"+list.length);
            if (counter-- == 0) { //cklarhor: ???
                break;
            }
            if (list.length > 1) {
                writer.write(var + " = ");
                writer.write(src.FileReaderChannel.PopString());
                writer.writeLine(";");
                for (FilterInstance f : list) {
                    writer.write("");
                    src.outputs.get(f).Push(var, true, true);
               }
            } else {
                if (src.split.size()==1)
                    writer.startBlock("for (unsigned int _i=0;_i<"+src.initMult * src.push+";_i++)");
                writer.write("");
                src.outputs.get(list[0]).Push(src.FileReaderChannel.PopString(),true,true);
                if (src.split.size()==1)
                    writer.endBlock();
            }
        }
        //input.generateDoneReading();
        input.reconfigureForSteadyState();
        writer.writeLine("/*  FileReader SplitBuffer done */");
    }

  
    @Override
    public void GenerateSteadyWaitRead() {
    }

    @Override
    public void GenerateSteadyWaitWrite() {
    }

    @Override
    public void GenerateSteadyDoneReading() {
        assert false; //you have to add code to CommunicationEvaluator if you want to use this function
    }

    /**
     * Generate split pattern
     */
    @Override
    public void GenerateSteadyDoneWriting() {
        writer.writeLine("// FileReader SplitBuffer inlined");
        long iterations = (src.netSteady() * src.push / src.split.size());
        if (iterations > 0) {
            writer.startBlock("for (unsigned int _i=0;_i<" + iterations + ";_i++)");
        }
        int len = src.split.size();
        String var = "_tmp";
        if (len > 0) {
            writer.write("");
            src.filter.type.output.PrintCType();
            writer.writeLine(" " + var + ";");
        }
        // wrap merging pattern around if init already merged
        for (int j = 0; j < len; j++) {
            FilterInstance[] list = src.split.get((j + (int) src.initMult) % len);
            if (list.length > 1) {
                writer.write(var + " = ");
                writer.write(src.FileReaderChannel.PopString());
                writer.println(";");
                for (FilterInstance f : list) {
                    writer.write("");
                    src.outputs.get(f).Push(var, true, true);
                }
            } else {
                writer.write("");
                src.outputs.get(list[0]).Push(src.FileReaderChannel.PopString(), true, true);
            }
        }
        if (iterations > 0) {
            writer.endBlock();
        }        
    }

    @Override
    public int getWaitReadCost(Chip chip) {
        return 0;
    }

    @Override
    public int getWaitWriteCost(Chip chip) {
        int cost = BufferEstimator.getInstance().getBufferCost(this);
        if (cost>0)
            return cost;
        else
            if (streamit2.Compiler.abortOnUnknownEstimation) {
                throw new NoEstimationDataException("no estimation data");
            } else {
                return 0;
            }
    }

    @Override
    public int getDoneReadCost(Chip chip) {
        return 0;
    }
    
    /**
     * This is a copy of SimpleMergeSplitBuffer
     * @param chip
     * @return 
     */
    @Override
    public int getDoneWriteCost(Chip chip) {
        int extra_overhead = 0;
        int len = super.src.split.size();
        int body_cost = 0;
        BufferEstimator mbe = BufferEstimator.getInstance();
        int cachedResult = mbe.getBufferCost(this);
        if (cachedResult != -1) {
            return cachedResult;
        }
        long iterations = (super.src.netSteady() * super.src.push / super.src.split.size());
        long numLines = super.src.split.size();
        return 1000; //TODO: FIXME: MERGE!
        // return (int) (getLoopCost(iterations, numLines)*1.25); //TODO: LoopCost values are old!!!
    }

    @Override
    public long getMemUsedSrc() {
        return 0;
    }

    @Override
    public long getMemUsedDst() {
        return 0;
    }
    
    @Override
    public long TransmissionTime(CommunicationModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
