package energyModels.graph.buildGraph;

import threadedSim.peer.Peer;
import energyModels.graph.Graph;

public interface IBuildGraph<P extends Peer> {
	public void addPeerToGraph(P peer);
	public Graph getGraph();
	public void clear();
}
