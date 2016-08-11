package partitionerGUI;

import backend.common.channels.AbstractChannel;
import backend.common.Chip;
import backend.common.Processor;
import backend.common.partitioner.GUIManualPartitioner;
import backend.coreVA.CoreVAProcessor;
import backend.coreVA.xmlModel.MpsocChip;
import hierarchicalGraph.FilterInstance;
import hierarchicalGraph.StreamNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import partitionerGUI.icons.*;

public class GUIDrawer extends JComponent implements MouseListener, MouseMotionListener {
    public static JFrame window;
    public static GUIDrawer gd;
    public static JScrollPane jsp;
    private static Object lock = new Object();
    
    private static FilterInstance selectedFilter = null;
    public static int SmallestCycle = 0;
    
    public static int panel_height;
    public static int panel_width;
    
    public static int noc_top_margin = 20;
    public static int noc_left_margin = 20;
    public static int noc_height = 320;
    public static int noc_width = 600;
    
    public static int processor_top_margin = 10;
    public static int processor_left_margin = 10;
    public static int processor_height = 280;
    public static int processor_width = 120;
    
    public static int filter_top_margin = 3;
    public static int filter_left_margin = 10;
    public static int filter_width = 15;
    
    public static List<GUINocProcessor> GUINocs;
    public static List<GUIChannel> GUIChannels;
    public static Map<CoreVAProcessor, GUIProcessor> GUIProcessors;
    public static Map<FilterInstance, GUIFilter> GUIFilters;
    
    private static String result = null;
    private static boolean validPartition = true;
    
    private static void updateDisplayConfiguration(MpsocChip mpc){
        int num_pro = mpc.maxDepth;
        int num_col = mpc.maxWidth;
        int num_row = mpc.maxHeight;
        noc_width = num_pro * (processor_width + processor_left_margin) + processor_left_margin;
        
        panel_width = num_row * (noc_width + noc_left_margin) + noc_left_margin;
        panel_height = num_col * (noc_height + noc_top_margin) + noc_top_margin;
    }
    
    private static CoreVAProcessor[] castToCoreVA(Processor[] pros){
        CoreVAProcessor[] coreva_pros = new CoreVAProcessor[pros.length];
        for(int i = 0; i < pros.length; i++)
            coreva_pros[i] = (CoreVAProcessor)pros[i];
        return coreva_pros;
    }
    
    private static int findSmallestFilterCycle(){
        Map<FilterInstance, Integer> rank = GUIManualPartitioner.evaluator.getFiltersRank();
        return rank.get(rank.keySet().toArray()[0]);
    }
    
    private static void setBusiestProcessor(){
        List<CoreVAProcessor> busiest = new ArrayList<>();
        int maxLoad = 0;
        for(CoreVAProcessor processor: GUIProcessors.keySet()){
            int newLoad = 0;
            if(GUIManualPartitioner.evaluator.getProcessorsRank().containsKey(processor))
                newLoad = GUIManualPartitioner.evaluator.getProcessorsRank().get(processor);
            if(newLoad > maxLoad){
                maxLoad = newLoad;
                busiest.clear();
                busiest.add(processor);
            }else if(newLoad == maxLoad){
                busiest.add(processor);
            }   
        }
        
        for(CoreVAProcessor processor: busiest)
            GUIProcessors.get(processor).isBusiestProcessor = true;
    }
    
    public static void setChip(Chip chip){
        CoreVAProcessor[] processors = castToCoreVA(chip.getProcessors());
        GUINocs = new ArrayList<>();
        GUIChannels = new ArrayList<>();
        GUIProcessors = new LinkedHashMap<>();
        GUIFilters = new LinkedHashMap<>();
        createProcessorGUI(processors);
    }
    
    public static void setPartition(SortedMap<FilterInstance,Processor> partition){
        for(FilterInstance filter: partition.keySet()){
            GUIProcessor parent = GUIProcessors.get((CoreVAProcessor)partition.get(filter));
            GUIFilter gf = new GUIFilter(filter, parent);
            gf.filter_id = parent.children.size();
            parent.children.add(gf);
            GUIFilters.put(filter, gf);
        }
        for(FilterInstance filter : GUIFilters.keySet()){
            for(AbstractChannel channel : filter.outputs.values()){
                GUIChannel gc = new GUIChannel(channel);
                GUIChannels.add(gc);
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        String throughput = " Estimated Throughput = " + GUIManualPartitioner.getThroughput();
        if(!validPartition)
            throughput = " Invalid Patition!";

        if(result == null)
            g.drawString("Global Multiplicity = " + StreamNode.getGlobalMult() + throughput, 0, g.getFont().getSize());
        else
            g.drawString("Global Multiplicity = " + StreamNode.getGlobalMult() + throughput + " " + result, 0, g.getFont().getSize());
        SmallestCycle = findSmallestFilterCycle();
        setBusiestProcessor();
        for(GUINocProcessor gn: GUINocs)
            gn.draw(g);
        for(GUIChannel gc: GUIChannels)
            gc.draw(g);
        for(FilterInstance filter: GUIFilters.keySet()){
            GUIFilter gf = GUIFilters.get(filter);
            if(gf.showStatus){
                String text = "work=" + GUIManualPartitioner.getFilterWork(filter) + " ,mult=" + filter.netSteady();
                g.setColor(Color.WHITE);
                g.fillRect(gf.x + filter_width, gf.y - filter_width - 4, text.length() * 8, filter_width + 6);
                g.setColor(Color.BLACK);
                g.drawRect(gf.x + filter_width, gf.y - filter_width - 4, text.length() * 8, filter_width + 6);
                g.drawString(text, gf.x + filter_width + 3, gf.y);
            }
        }   

    }
    
    public static void initialFrame(MpsocChip mpc){
        updateDisplayConfiguration(mpc);
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(new FileNameExtensionFilter("*.par", "par"));
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openButton = new JMenuItem("Open Partition");
        JMenuItem saveButton = new JMenuItem("Save Partition");
        menuBar.add(fileMenu);
        fileMenu.add(openButton);
        fileMenu.add(saveButton);
        
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File location = fc.getSelectedFile();
                    if(GUIManualPartitioner.openPartition(location))
                        JOptionPane.showMessageDialog(window, "Partition loaded", "Open", JOptionPane.PLAIN_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(window, "Failed to load partition. Damaged or wrong par file for this graph and platform!", "Open", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showSaveDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File location = fc.getSelectedFile();
                    if(GUIManualPartitioner.savePartition(location))
                        JOptionPane.showMessageDialog(window, "Partition saved", "Save", JOptionPane.PLAIN_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(window, "Failed to save partition...", "Save", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized(lock) {
                    lock.notifyAll();
                }
                window.dispose();
            }          
        });
        
        JButton setMultButton = new JButton("Set Root Multiplier");
        setMultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int newMult = getNewMult();
                validPartition = GUIManualPartitioner.alterRootMult(newMult);
            }          
        });
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIManualPartitioner.restore();
            }          
        });
        
        JButton testButton = new JButton("Test Run");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mfname = GUIManualPartitioner.generateCode();
                result = GUIManualPartitioner.run(mfname);
                gd.repaint();
            }
        });
        
        JPanel buttons = new JPanel();
        buttons.add(okButton);
        buttons.add(setMultButton);
        buttons.add(backButton);
        buttons.add(testButton);
        
        window = new JFrame();
        window.setJMenuBar(menuBar);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 40);
        gd = new GUIDrawer();
        gd.setPreferredSize(new Dimension(panel_width, panel_height));
        jsp = new JScrollPane(gd);
        jsp.addMouseListener(gd);
        jsp.addMouseMotionListener(gd);
        window.getContentPane().add(jsp, BorderLayout.CENTER);
        window.getContentPane().add(buttons, BorderLayout.PAGE_END);
        window.getContentPane().setBackground(Color.white);
        window.setVisible(true);
        
        GUIManualPartitioner.drawGraph();
        
        synchronized(lock) {
            try {
                lock.wait();
            } catch(InterruptedException e) {
                // nothing
            }
        }
    }
    
    public static void draw(){
        clearSelectedFilter();
        gd.repaint();
    }
    
    private static void createProcessorGUI(CoreVAProcessor[] processors){
        int current_row = -1;
        int current_col = -1;
        GUINocProcessor current_GUINocProcessor = null;
        for(CoreVAProcessor p: processors){
            GUIProcessor gp;
            if(p.getRow() == current_row && p.getCol() == current_col){
                gp = new GUIProcessor(p, current_GUINocProcessor);
                current_GUINocProcessor.children.add(gp);
            }else{
                current_GUINocProcessor = new GUINocProcessor(p.getCol(), p.getRow());
                current_row = p.getRow();
                current_col = p.getCol();
                gp = new GUIProcessor(p, current_GUINocProcessor);
                current_GUINocProcessor.children.add(gp);
                GUINocs.add(current_GUINocProcessor);
            }
            GUIProcessors.put(p, gp);
        }
    }

    private static FilterInstance getFilter(int x, int y){
        clearSelectedFilter();
        for(FilterInstance filter : GUIFilters.keySet()){
            GUIFilter gf = GUIFilters.get(filter);
            if(x > gf.x && y > gf.y)
                if(x < gf.x + filter_width && y < gf.y + gf.height){
                    gf.selected = true;
                    for(GUIChannel gc: GUIChannels)
                        if(gc.src_filter == gf || gc.dst_filter == gf)
                            gc.selected = true;
                    return filter;
                }
        }
        return null;
    }
    
    private static void clearSelectedFilter(){
        selectedFilter = null;
        for(FilterInstance filter : GUIFilters.keySet()){
            GUIFilter gf = GUIFilters.get(filter);
            gf.selected = false;
            gf.dragged = false;
            gf.showStatus = false;
        }
        for (GUIChannel gc: GUIChannels)
            gc.selected = false;
    }
    
    private static CoreVAProcessor getProcessor(int x, int y){
        for(CoreVAProcessor processor : GUIProcessors.keySet()){
            GUIProcessor gp = GUIProcessors.get(processor);
            if(x > gp.x && y > gp.y)
                if(x < gp.x + processor_width && y < gp.y + processor_height)
                    return processor;
        }
        return null;
    }
    
    private static int getFissionCopies(){
        int copies = 0;
        while(copies < 1){
            String value = JOptionPane.showInputDialog(window, "Please enter number of copies", "Filter Fission",  JOptionPane.QUESTION_MESSAGE);
            if(value != null && !"".equals(value))
                copies = Integer.parseInt(value);
            else
                copies = 1;
        }
        return copies;
    }
    
    private static int getNewMult(){
        int newMult = 0;
        while(newMult < 1){
            String value = JOptionPane.showInputDialog(window, "Please enter a new root multiplier", "Change Multiplier",  JOptionPane.QUESTION_MESSAGE);
            if(value != null && !"".equals(value))
                newMult = Integer.parseInt(value);
            else
                newMult = StreamNode.getRootMult();
        }
        return newMult;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        int mouse_x = getAbsoluteX(me);
        int mouse_y = getAbsoluteY(me);
        if(getFilter(mouse_x, mouse_y) != null)
            selectedFilter = getFilter(mouse_x, mouse_y);
        else selectedFilter = null;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        int mouse_x = getAbsoluteX(me);
        int mouse_y = getAbsoluteY(me);
        if(selectedFilter != null){
            CoreVAProcessor processor = getProcessor(mouse_x, mouse_y);
            if(processor == null)
                draw();
            else{
                if(me.getButton() == MouseEvent.BUTTON3){
                    int copies = getFissionCopies();
                    validPartition = GUIManualPartitioner.fissFilter(selectedFilter, copies);
                    result = null;
                }else{
                    validPartition = GUIManualPartitioner.alterPartition(selectedFilter, processor);
                    result = null;
                }
            }
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        int mouse_x = getAbsoluteX(me);
        int mouse_y = getAbsoluteY(me);
        if(selectedFilter != null){
            GUIFilter gf = GUIFilters.get(selectedFilter);
            gf.x = mouse_x - filter_width/2;
            gf.y = mouse_y - filter_width/2;
            gf.dragged = true;
            gd.repaint();
        }
        int x = jsp.getHorizontalScrollBar().getValue();
        int y = jsp.getVerticalScrollBar().getValue();
        if(me.getX() > window.getSize().width - 50)
            x++;
        else if(me.getX() < 50)
            x--;
        else if(me.getY() < 50)
            y--;
        else if(me.getY() > window.getSize().height - 50)
            y++;
        jsp.getHorizontalScrollBar().setValue(x);
        jsp.getVerticalScrollBar().setValue(y);
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        int mouse_x = getAbsoluteX(me);
        int mouse_y = getAbsoluteY(me);
        
        FilterInstance filter = getFilter(mouse_x, mouse_y);
        if(filter != null){
            GUIFilters.get(filter).showStatus = true;
            gd.repaint();
        }else{
            draw();
        }
    }
    
    private int getAbsoluteX(MouseEvent me){
        return me.getX() + jsp.getHorizontalScrollBar().getValue();
    }
    
    private int getAbsoluteY(MouseEvent me){
        return me.getY() + jsp.getVerticalScrollBar().getValue();
    }
}
