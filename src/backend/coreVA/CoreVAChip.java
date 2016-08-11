package backend.coreVA;

import backend.common.Chip;

public interface CoreVAChip extends Chip
{
    public int getRows();
    public int getCols();
    public int getMaxDepth();
}
