package backend.coreVA.xmlModel;

import backend.common.channels.AbstractChannel;
import backend.common.Processor;
import backend.coreVA.CoreVAChip;
import backend.coreVA.channels.CoreVANocChannel;
import backend.coreVA.CoreVAProcessor;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public class MpsocChip implements CoreVAChip {

    private final static Logger logger = Logger.getLogger("compiler");
    private final static String XML_SCHEMA_FILE = "last-mpsoc-config.xsd";
    private final static String XML_SCHEMA_LANG = "http://www.w3.org/2001/XMLSchema";
    private Noc noc;
    private Processor[] processors;
    public int maxWidth;
    public int maxHeight;
    public int maxDepth;
    
    // memory of a cluster
    public int[][] sharedMemCluster;

    // shared memory used for file IO
    public int fioSharedMem=0;
    
    public int[][] next_flow_id;
    public int[][] next_ack_id;
    public int[][][][] channels;

    public MpsocChip(String filePath, boolean generateSchema) throws IOException {
        try {
          parseXML(filePath, generateSchema);
        } catch (UnmarshalException e) {
            logger.log(Level.SEVERE, e.getLinkedException().getLocalizedMessage());
            throw new IOException();
        } catch (Exception e) {
            throw new IOException(e);
        }
        initStructures();
    }

    public MpsocChip(int ProcCols, int ProcRows, int ProcIds) {
        processors = new CoreVAProcessor[ProcRows*ProcCols*ProcIds];
        int gId = 0;
        next_flow_id = new int[ProcCols][ProcRows];
        next_ack_id = new int[ProcCols][ProcRows];
        channels = new int[ProcCols][ProcRows][ProcIds][ProcIds];
        for (int row = 0; row < ProcRows; row++) 
            for (int col = 0; col < ProcCols; col++) 
                for (int depth = 0; depth < ProcIds; depth++) {
                    processors[gId] = new CoreVAProcessor(col, row, depth, gId, this, null);
                    gId++;
                }
        this.maxWidth = ProcCols;
        this.maxHeight = ProcRows;
        this.maxDepth = ProcIds;
    }

    private void parseXML(String filePath, boolean generateSchema) throws Exception {
        JAXBContext jaxB = JAXBContext.newInstance(Noc.class);
        Unmarshaller unmarschaler = jaxB.createUnmarshaller();
        if (generateSchema) {
            Schema xmlSchema = generateSchema(jaxB);
            unmarschaler.setSchema(xmlSchema);
        }
        noc = (Noc) unmarschaler.unmarshal(new File(filePath));
    }

    private Schema generateSchema(JAXBContext jaxB) throws IOException, SAXException {
        jaxB.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                return new StreamResult(new File(XML_SCHEMA_FILE));
            }
        });
        return SchemaFactory.newInstance(XML_SCHEMA_LANG).newSchema(new File(XML_SCHEMA_FILE));
    }

    private void initStructures() {
        processors = new Processor[noc.countProcessors()];
        int gid = 0;
        int numMem = 0;
        for (Node node : noc.node) {
            if (node.x >= maxWidth) {
                maxWidth = node.x+1;
            }
            if (node.y >= maxHeight) {
                maxHeight = node.y+1;
            }
            for (Core core : node.core) {
                processors[gid] = new CoreVAProcessor(node.x, node.y, core.id, gid, this, node);
                ((CoreVAProcessor)processors[gid]).memSize = core.data_mem_size;
                gid++;
                if (core.id >= maxDepth ) {
                    maxDepth = core.id+1;
                }
            }
            if ((node.memory != null) && (numMem < node.memory.size())) {
                numMem = node.memory.size();
            }
        }                       
        sharedMemCluster = new int[maxWidth*maxHeight][numMem];
        if (numMem > 0) {
            for (Node node : noc.node) {
                int clusterId = node.x + node.y * maxWidth;
                for (Memory mem : node.memory) {
                    switch (mem.type) {
                            case tcdm:
                                sharedMemCluster[clusterId][mem.id] = mem.size * mem.banks;
                                break;
                            default:
                                sharedMemCluster[clusterId][mem.id] = mem.size;
                                break;
                    }
                }
            }
        }
        next_flow_id = new int[maxWidth][maxHeight];
        next_ack_id = new int[maxWidth][maxHeight];
        channels = new int[maxWidth][maxHeight][maxDepth][maxDepth];
    }

    @Override
    public Processor[] getProcessors() {
        return processors;
    }
    
    @Override
    public Processor[] getFirstClusterProcessors() {
        Processor[] procs = new CoreVAProcessor[maxDepth];
        System.arraycopy(processors, 0, procs, 0, maxDepth);
        return procs;
    }
    
    @Override
    public AbstractMap.SimpleEntry<Integer,Integer> getCommunciationCost(Processor pSrc, Processor pDst) {
        if (noc==null) //No Model? -> No costs 
	        return new AbstractMap.SimpleEntry<>(0,0);
        CoreVAProcessor src = (CoreVAProcessor)pSrc;
        CoreVAProcessor dst = (CoreVAProcessor)pDst;
        if (src==dst) //Proc internal costs;
            return new AbstractMap.SimpleEntry<>(0,0);
        if (src.getCol()==dst.getCol() && src.getRow()==dst.getRow()) { //Cluster costs -> xml
            Node node = noc.getNode(src);
            return new AbstractMap.SimpleEntry<>(
                    node.interconnect.get(0).getCommunicationCostSender(),
                    node.interconnect.get(0).getCommunicationCostReceiver());
        }
        try {
        return new AbstractMap.SimpleEntry<>(
                    noc.comm_lib_send_wait+noc.comm_lib_send_done,
                    noc.comm_lib_recv_wait+noc.comm_lib_recv_done);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "You don't set one of (comm_lib_wait, comm_lib_send) in your xml");
            System.exit(-1);
            return null;
        }
    }
    
    /**
     * Get link width from XML model
     * @param lnk link
     * @return link width depending on type
     */
    @Override
    public double getLinkWidth(Link lnk) {
        double lnkWidth = 0;
        Node node = noc.getNode(lnk.x, lnk.y);
        try {
            switch (lnk.linkType) {
                case LNK_BUS:   // default:  4 byte/cycle
                    lnkWidth = node.interconnect.get(0).width;
                    break;
                case LNK_NCI:   // 2.66 byte/cycle - TODO: Get from model
                    lnkWidth = 2.66;
                    break;
                case LNK_NORTH:
                case LNK_EAST:
                case LNK_SOUTH:
                case LNK_WEST:  // default: 8 byte/cycle
                    lnkWidth = noc.link_data_width;
                    break;
            }
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "You don't set one of (width, link_data_width) in your xml");
            System.exit(-1);
        }
        return lnkWidth;
    }
    
    @Override
    public int getCommunicationLatency(Processor pSrc, Processor pDst) {
        if (noc==null) return 0; //no model no costs
        CoreVAProcessor src = (CoreVAProcessor)pSrc;
        CoreVAProcessor dst = (CoreVAProcessor)pDst;
        if (src==dst) 
            return 0;
        if (src.getCol()==dst.getCol() && src.getRow()==dst.getRow()) {
            Node node = noc.getNode(src);
            return node.interconnect.get(0).latency;
        }
        return noc.getLatency(src, dst); 
    }
    
    @Override
    public boolean isValidBufferSize(Processor pSrc, Processor pDst, AbstractChannel channel) {
        CoreVAProcessor src = (CoreVAProcessor)pSrc;
        CoreVAProcessor dst = (CoreVAProcessor)pDst;        
        // validate NoC parameters
        if (channel instanceof CoreVANocChannel) {           
            final long bufferSize = ((CoreVANocChannel) channel).getBuffersizeBytes();
            // max buffersize
            if (bufferSize >= noc.getNode(dst).nci.max_buffersize) {
                logger.log(Level.FINE, "NoC: channel buffersize exceeded: {0} >= {1} ({2} on {3})", new Object[]{bufferSize, noc.getNode(dst).nci.max_buffersize, channel.getIdent(), dst.toString()});
                return false;
            }
            if (bufferSize >= noc.getNode(src).nci.max_buffersize) {
                logger.log(Level.FINE, "NoC: channel buffersize exceeded: {0} >= {1} ({2} on {3})", new Object[]{bufferSize, noc.getNode(src).nci.max_buffersize, channel.getIdent(), src.toString()});
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isValidChannel(Processor pSrc, Processor pDst, AbstractChannel channel) {
        CoreVAProcessor src = (CoreVAProcessor)pSrc;
        CoreVAProcessor dst = (CoreVAProcessor)pDst;        
        // validate NoC parameters
        if (channel instanceof CoreVANocChannel) {           
            // max number of channels
            if (src.chip.next_flow_id[dst.getCol()][dst.getRow()] >= noc.getNode(dst).nci.max_channel_recv) {
                logger.log(Level.FINE, "NoC: max number of channels exceeded(flow): {0} >= {1} ({2} on {3})", new Object[]{dst.chip.next_flow_id[dst.getCol()][dst.getRow()], noc.getNode(dst).nci.max_channel_recv, channel.getIdent(), dst.toString()});
                return false;
            }
            if (src.chip.next_ack_id[src.getCol()][src.getRow()] >= noc.getNode(src).nci.max_channel_send) {
                logger.log(Level.FINE, "NoC: max number of channels exceeded(ack): {0} >= {1} ({2} on {3})", new Object[]{src.chip.next_ack_id[src.getCol()][src.getRow()], noc.getNode(src).nci.max_channel_send, channel.getIdent(), src.toString()});                
                return false;
            }
        }
        return true;
    }

    public void reset() {
        fioSharedMem = 0;
        for (int x = 0; x < maxWidth; x++) {
            for (int y = 0; y < maxHeight; y++) {
                next_flow_id[x][y] = 0;
                next_ack_id[x][y] = 0;
                for (int zSrc = 0; zSrc < maxDepth; zSrc++) {
                    for (int zDst = 0; zDst < maxDepth; zDst++) {
                        channels[x][y][zSrc][zDst] = 0;
                    }
                }
            }
        }
    }
    
    @Override
    public int getRows() {
        return maxHeight;
    }

    @Override
    public int getCols() {
        return maxWidth;
    }

    @Override
    public int getMaxDepth() {
        return maxDepth;
    }
    
    @Override
    public String toString() {
        return "mpsoc_" + getRows() + "x" + getCols() + "x" + getMaxDepth();
    }
    
    
    public int getMaxChannelSend(CoreVAProcessor proc) {
        if (proc.getNode().nci.max_channel_send != null) {
            return proc.getNode().nci.max_channel_send;
        } else {
            return 0;
        }
    }

    public int getMaxChannelRecv(CoreVAProcessor proc) {
        if (proc.getNode().nci.max_channel_recv != null) {
            return proc.getNode().nci.max_channel_recv;
        } else {
            return 0;
        }
    }

    public int getClusterChannelWidth(CoreVAProcessor proc) {
        if (proc.getNode().interconnect.get(0).width != null) {
            return proc.getNode().interconnect.get(0).width;
        } else {
            return 0;
        }
    }
    
    public boolean sharedMemAvail(CoreVAProcessor proc) {
        return (proc.getNode().memory != null);
    }
}
