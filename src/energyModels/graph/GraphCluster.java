package energyModels.graph;

import java.util.List;
import java.util.Random;

public class GraphCluster {
	private int graphSize;
	private List<ClusterProperties> clusters;
	private List<InterClusterProperties> interCluster;
	private int nbClusters;
	
	public GraphCluster(List<ClusterProperties> properties,
			List<InterClusterProperties> interProperties){
		/*int nbAllVertices=0;
		for(ClusterProperties cluster: properties){
			nbAllVertices += cluster.getClusterSize();
		}*/
		
		this.nbClusters = properties.size();
		this.clusters = properties;
		this.interCluster = interProperties;
		
		int firstIndex=0;
		int clusterID=0;
		for(ClusterProperties cluster: clusters){
			cluster.setClusterID(clusterID);
			cluster.setFirstIndex(firstIndex);
			cluster.setLastIndex(firstIndex+cluster.getClusterSize()-1);
			firstIndex += cluster.getClusterSize();
			clusterID++;
		}
		this.graphSize = firstIndex;
	}
	
	public Graph cluster(){
		Graph graph = new Graph();
		Random random = new Random();
		
		//adding vertex of all clusters to the graph
		for(ClusterProperties cluster: clusters){
			for(int i=cluster.getFirstIndex();i<cluster.getLastIndex()+1;i++){
				graph.addVertex(i);
			}
		}
		
		//adding intra-edges within clusters
		for(ClusterProperties cluster: clusters){
			for(int i=cluster.getFirstIndex();i<cluster.getLastIndex()+1;i++){
				for(int j=i+1;j<cluster.getLastIndex()+1;j++){
					double aRandomDouble = random.nextDouble();
					if(aRandomDouble<cluster.getPosEdgeProb()){
						EdgeType edge = new EdgeType(true);
						graph.addEdge(edge, i, j);
					}else if(aRandomDouble>cluster.getPosEdgeProb() && 
							aRandomDouble <(cluster.getPosEdgeProb()+cluster.getNegEdgeProb())){
						EdgeType edge = new EdgeType(false);
						graph.addEdge(edge, i, j);
					}
				}
			}
		}
		
		//adding inter-edges between clusters
		for(InterClusterProperties inter : interCluster){
			ClusterProperties clusterFirst = inter.getCluster1();
			ClusterProperties clusterSecond = inter.getCluster2();
			for(int i=clusterFirst.getFirstIndex();i<clusterFirst.getLastIndex()+1;i++){
				for(int j = clusterSecond.getFirstIndex();j<clusterSecond.getLastIndex()+1;j++){
					double aRandomDouble = random.nextDouble();
					if(aRandomDouble< inter.getPosEdgeProb()){
						EdgeType edge = new EdgeType(true);
						graph.addEdge(edge, i, j);
					}else if(aRandomDouble>inter.getPosEdgeProb() && 
							aRandomDouble <(inter.getPosEdgeProb()+inter.getNegEdgeProb())){
						EdgeType edge = new EdgeType(false);
						graph.addEdge(edge, i, j);
					} 
				}
			}
		}
		
		return graph;
		
	}
	
	public int getGraphSize() {
		return graphSize;
	}

	public int getNbClusters() {
		return nbClusters;
	}

	
}
