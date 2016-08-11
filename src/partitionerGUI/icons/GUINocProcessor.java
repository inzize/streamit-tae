package partitionerGUI.icons;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import partitionerGUI.GUIDrawer;

public class GUINocProcessor {
    public int row;
    public int col;
    public List<GUIProcessor> children;
    
    public int x;
    public int y;
    
    public GUINocProcessor(int row, int col){
        this.row = row;
        this.col = col;
        children = new ArrayList<>();
    }
    
    public void draw(Graphics g){
        x = row * (GUIDrawer.noc_left_margin + GUIDrawer.noc_width) + GUIDrawer.noc_left_margin;
        y = col * (GUIDrawer.noc_top_margin + GUIDrawer.noc_height) + GUIDrawer.noc_top_margin;
        g.drawRect(x, y, GUIDrawer.noc_width, GUIDrawer.noc_height);
        for(GUIProcessor gp: children)
            gp.draw(g);
    }
}
