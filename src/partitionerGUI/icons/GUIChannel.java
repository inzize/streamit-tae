package partitionerGUI.icons;

import backend.common.channels.AbstractChannel;
import java.awt.Color;
import java.awt.Graphics;
import partitionerGUI.GUIDrawer;


public class GUIChannel {
    public AbstractChannel channel;
    public GUIFilter src_filter;
    public GUIFilter dst_filter;
    public boolean selected = false;
    
    public GUIChannel(AbstractChannel channel){
        this.channel = channel;
        src_filter = GUIDrawer.GUIFilters.get(channel.src);
        dst_filter = GUIDrawer.GUIFilters.get(channel.dst);
    }
    
    public void draw(Graphics g){
        if(selected)
            g.setColor(Color.red);
        g.drawLine(src_filter.x + GUIDrawer.filter_width/2, src_filter.y + GUIDrawer.filter_width/2, dst_filter.x + GUIDrawer.filter_width/2, dst_filter.y + GUIDrawer.filter_width/2);
        g.setColor(Color.black);
    }
}
