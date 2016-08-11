package partitionerGUI.icons;

import backend.common.partitioner.GUIManualPartitioner;
import hierarchicalGraph.FilterInstance;
import java.awt.Color;
import java.awt.Graphics;
import partitionerGUI.GUIDrawer;

public class GUIFilter {
    public FilterInstance filter;
    public int filter_id;
    public GUIProcessor parent;
    public boolean selected = false;
    
    public int x;
    public int y;
    public int height;
    public boolean dragged = false;
    public boolean showStatus = false;
    
    public GUIFilter(FilterInstance filter, GUIProcessor parent){
        this.filter = filter;
        this.parent = parent;
    }
    
    public void draw(Graphics g){
        double factor = (double)GUIManualPartitioner.evaluator.getFiltersRank().get(filter)/GUIDrawer.SmallestCycle;
        if(!dragged){
            x = GUIDrawer.filter_left_margin + parent.x;
            //y = filter_id * (GUIDrawer.filter_top_margin + GUIDrawer.filter_radius * 2) + GUIDrawer.filter_top_margin + parent.y;
            int top = 0;
            for(int i = 0; i < filter_id; i++){
                GUIFilter gf = parent.children.get(i);
                top += gf.height;
                top += GUIDrawer.filter_top_margin;
            }
            y = GUIDrawer.filter_top_margin + top + GUIDrawer.filter_top_margin + parent.y;
        }
        if(selected)
            g.setColor(Color.red);
        else
            g.setColor(Color.black);
        //int r = (int)(GUIDrawer.filter_radius - 5 * (1-per));
        height = (int)(GUIDrawer.filter_width * factor);
        //g.fillOval(x, y, r , r);
        g.fillRect(x, y, GUIDrawer.filter_width, height);
        g.setColor(Color.BLACK);
        g.drawString(filter.filter.id, x + GUIDrawer.filter_width + 3, y + GUIDrawer.filter_width);
    }
}
