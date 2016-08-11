package backend.coreVA;

import backend.common.Processor;
import backend.coreVA.xmlModel.MpsocChip;
import backend.coreVA.xmlModel.Node;

public class CoreVAProcessor extends Processor
{
    private final int row;
    private final int col;
    private final int id;
    public MpsocChip chip;
    public int memSize = 0;
    public int memSizeShared = 0;
    private int clusterId;
    private Node node;
    
    public CoreVAProcessor(int col, int row, int id, int gid, MpsocChip chip, Node node)
    {
        super(gid);
        this.row = row;
        this.col = col;
        this.id = id;
        this.chip = chip;
        this.clusterId = col + row * chip.maxWidth;
        this.node = node;
    }
    
    public CoreVAProcessor(int col, int row, int id, int gid, MpsocChip chip)
    {
        super(gid);
        this.row = row;
        this.col = col;
        this.id = id;
        this.chip = chip;
        this.clusterId = col + row * chip.maxWidth;
        this.node = null;
    }

    @Override
    public String toString()
    {
        return "cpu_" + col + "_" + row + "_" + id;
    }    
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getId() {
        return id;
    }
    
    public int getClusterId() {
       return clusterId;
    }
    
    public Node getNode() {
        return node;
    }

    @Override
    public long getStaticEnergyConsumption() {
        return 2;//dummy
    }
}
