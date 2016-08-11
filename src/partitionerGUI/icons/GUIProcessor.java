package partitionerGUI.icons;

import backend.common.partitioner.GUIManualPartitioner;
import backend.coreVA.CoreVAProcessor;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import partitionerGUI.GUIDrawer;

public class GUIProcessor {
    public CoreVAProcessor processor;
    public int processor_id;
    public GUINocProcessor parent;
    public List<GUIFilter> children;
    public boolean isBusiestProcessor = false;
    
    public int x;
    public int y;
    public int height = 0;
    
    public GUIProcessor(CoreVAProcessor p, GUINocProcessor parent){
        processor = p;
        processor_id = processor.getId();
        this.parent = parent;
        children = new ArrayList<>();
    }
    
    public void draw(Graphics g){
        x = processor_id * (GUIDrawer.processor_left_margin + GUIDrawer.processor_width) + GUIDrawer.processor_left_margin + parent.x;
        y = GUIDrawer.processor_top_margin + parent.y;
        if(isBusiestProcessor)
            g.setColor(Color.red);
        //for(GUIFilter gf: children)
        //    height += gf.height + GUIDrawer.filter_top_margin;
        //height = Math.max(height, GUIDrawer.processor_height);
        height = GUIDrawer.processor_height;
        g.drawRect(x, y, GUIDrawer.processor_width, height);
        int load = 0;
        if(GUIManualPartitioner.evaluator.getProcessorsRank().containsKey(processor))
            load = GUIManualPartitioner.evaluator.getProcessorsRank().get(processor);
        g.drawString(processor.toString() + " (" + load + ")", x, y + height + g.getFont().getSize());
        g.setColor(Color.BLACK);
        for(GUIFilter gf: children)
            gf.draw(g);
    }
}
