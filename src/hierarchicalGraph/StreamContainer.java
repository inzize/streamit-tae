package hierarchicalGraph;

import java.util.ArrayList;
import java.util.List;

public abstract class StreamContainer extends StreamNode {

    public static int next_fused_container_id = 0;
    public boolean isFusedContainer = false;
    private final String container_name = "container";
    public int fused_container_id = -1;
    public List<StreamNode> children = new ArrayList<>();

    @Override
    public abstract StreamContainer clone();

    @Override
    public String StreamNodeName() {
        if (fused_container_id > -1) {
            return container_name + fused_container_id;
        } else {
            return container_name;
        }
    }

    @Override
    protected void getFissables(List<StreamNode> list) {
        if (isFusedContainer) {
            list.add(this);
        } else {
            for (StreamNode child : children) {
                child.getFissables(list);
            }
        }

    }

    public void fuseAllStateless() {
        List<Pipeline> fusedFilters = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            StreamNode previous = children.get(i);
            if (previous instanceof Pipeline) {
                ((Pipeline) previous).fuseAllStateless();
                continue;
            }
            if (!previous.isStateless()) {
                continue;
            }
            Pipeline fusedFilter = new Pipeline();
            fusedFilter.children.add(previous);
            int j;
            for (j = i + 1; j < children.size(); j++) {
                StreamNode next = children.get(j);
                if (next.isStateless() && !next.isPeeking()) {
                    fusedFilter.children.add(next);
                } else {
                    break;
                }
            }
            if (fusedFilter.children.size() > 1) {
                fusedFilters.add(fusedFilter);
                fusedFilter.isFusedContainer = true;
                fusedFilter.fused_container_id = next_fused_container_id++;
                i = j - 1;
            }
        }
        for (Pipeline fusedFilter : fusedFilters) {
            int index = children.indexOf(fusedFilter.children.get(0));
            for (StreamNode child : fusedFilter.children) {
                children.remove(child);
            }
            children.add(index, fusedFilter);
        }
    }

    @Override
    public boolean isPeeking() {
        for (StreamNode node : children) {
            if (node.isPeeking()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isStateless() {
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).isStateless() || (children.get(i).isPeeking() && (i > 0))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isFissable() {
        if (isFissedNode) {
            return false;
        }
        for (StreamNode node : children) {
            if (!node.isFissable()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveNetSteady() {
        super.saveNetSteady();
        for (StreamNode node : children) {
            node.saveNetSteady();
        }
    }

    public void clone(StreamContainer newCopy) {
        super.clone(newCopy);
        for (StreamNode node : children) {
            if (node instanceof FilterInstance) {
                newCopy.children.add(((FilterInstance) node).clone());
            } else {
                newCopy.children.add(((StreamContainer) node).clone());
            }
        }

    }

    @Override
    public List<StreamNode> InitFissedNodes(Integer[] ratio) {
        List<StreamNode> fissedNodes = new ArrayList<>();
        for (Integer ratio1 : ratio) {
            StreamNode fissedStreamNode = clone();
            fissedNodes.add(fissedStreamNode);
            fissedStreamNode.isFissedNode = true;
        }
        return fissedNodes;
    }
}
