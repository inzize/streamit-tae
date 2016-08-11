package backend.common.buffers;

import static ast.GeneratesCode.writer;
import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.estimators.CommunicationModel;
import backend.common.estimators.BufferEstimator;
import hierarchicalGraph.FilterInstance;
import streamit2.NoEstimationDataException;

public class FileWriterMergeBuffer extends AbstractMergeSplitBuffer {

    public FileWriterMergeBuffer(FilterInstance src, FilterInstance dst) {
        super(src, dst);
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

    @Override
    public void GenerateInitDoneWriting() {
        assert BufferType.SPLIT == bufferType;
    }

  
    @Override
    public void GenerateSteadyWaitRead() {
    }

    /**
     * Generate merge pattern!
     */
    @Override
    public void GenerateSteadyWaitWrite() {
        assert BufferType.MERGE == bufferType;
        AbstractChannel output = dst.FileWriterChannel;
        long iterations = (dst.netSteady() / dst.join.size());
        writer.startBlock("for (int _i=0; _i<" + iterations + ";_i++)");
        for (FilterInstance f : dst.join) {
            writer.write("");
            output.Push(dst.inputs.get(f).PopString(true), true);
        }
        writer.endBlock();
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
    
    @Override
    public int getDoneWriteCost(Chip chip) {
        return 0;
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
