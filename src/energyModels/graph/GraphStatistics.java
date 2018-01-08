package energyModels.graph;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;

public class GraphStatistics {
Graph<Integer, EdgeType> graph;

	public GraphStatistics(Graph<Integer,EdgeType> graph) {
		super();
		this.graph = graph;
	}

	public static double arithMean(List<CoordinatesPeer> list){
		double sumOfDistances=0.0;
		int nbDistances=0;
		for(int i=0;i<list.size();i++){
			CartesianCoordinate coordinatei = list.get(i).getCoordinate();
			for(int j=i+1;j<list.size();j++){
				CartesianCoordinate coordinatej = list.get(j).getCoordinate();
				double dist = coordinatei.distance(coordinatej);
				sumOfDistances += dist;
				nbDistances++;
			}
		}
		return sumOfDistances/nbDistances;
		
	}	
	
	public static double geomMean(List<CoordinatesPeer> list){
		double multiDistance=1.0;
		int nbDistances=0;
		/*double maxDistance=0.0;
		double minDistance = 100000.0;*/
		for(int i=0;i<list.size();i++){
			CartesianCoordinate coordinatei = list.get(i).getCoordinate();
			for(int j=i+1;j<list.size();j++){
				CartesianCoordinate coordinatej = list.get(j).getCoordinate();
				double dist = coordinatei.distance(coordinatej);
				multiDistance *= dist;
				nbDistances++;
			/*	if(dist>maxDistance){
					maxDistance = dist;
				}
				if(dist<minDistance){
					minDistance = dist;
				}*/
			}
		}
		/*System.out.println("multiDistance " + multiDistance + " 1/nbOfDistacnes " +  (1.0/nbDistances));
		System.out.println("max distance " + maxDistance + " min distance " + minDistance);*/
		return Math.pow(multiDistance, (double)(1.0/nbDistances));
		
	}
	public static double geomMeanOf2Clusters(List<CoordinatesPeer> peers, ClusterProperties cluster1,
			ClusterProperties cluster2){
		double multiDistance =1.0;
		int nbDistances = 0;
		for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
			CartesianCoordinate coordinatei = peers.get(i).getCoordinate();
			for(int j=cluster2.getFirstIndex();j<cluster2.getLastIndex()+1;j++){
				CartesianCoordinate coordinatej = peers.get(j).getCoordinate();
				multiDistance *= coordinatei.distance(coordinatej);
				nbDistances++;
			}
		}
		return Math.pow(multiDistance, (double)(1.0/nbDistances));
	}	
	
	public static double arithMeanOf2Clusters(List<CoordinatesPeer> peers, ClusterProperties cluster1,
			ClusterProperties cluster2){
		double sumOfDistances =0.0;
		int nbDistances = 0;
		for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
			CartesianCoordinate coordinatei = peers.get(i).getCoordinate();
			for(int j=cluster2.getFirstIndex();j<cluster2.getLastIndex()+1;j++){
				CartesianCoordinate coordinatej = peers.get(j).getCoordinate();
				sumOfDistances += coordinatei.distance(coordinatej);
				nbDistances++;
			}
		}
		return sumOfDistances/nbDistances;
	}	
	
	public static double geomMeanOf1Cluster(List<CoordinatesPeer> peers, ClusterProperties cluster1){
		double multiDistance =1.0;
		int nbDistances = 0;
		for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
			CartesianCoordinate coordinatei = peers.get(i).getCoordinate();
			for(int j=i+1;j<cluster1.getLastIndex()+1;j++){
				CartesianCoordinate coordinatej = peers.get(j).getCoordinate();
				multiDistance *= coordinatei.distance(coordinatej);
				nbDistances++;
			}
		}
		return Math.pow(multiDistance, (double)(1.0/nbDistances));
	}
	
	public static double arithMeanOf1Cluster(List<CoordinatesPeer> peers, ClusterProperties cluster1){
		double sumOfDistances =0.0;
		int nbDistances = 0;
		for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
			CartesianCoordinate coordinatei = peers.get(i).getCoordinate();
			for(int j=i+1;j<cluster1.getLastIndex()+1;j++){
				CartesianCoordinate coordinatej = peers.get(j).getCoordinate();
				sumOfDistances += coordinatei.distance(coordinatej);
				nbDistances++;
			}
		}
		return sumOfDistances/nbDistances;
	}

	public static int cutPositive(Graph<Integer, EdgeType> g, ClusterProperties cluster1, ClusterProperties cluster2){
			int posConnectingEdges=0;
			for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
				for(int j=cluster2.getFirstIndex();j<cluster2.getLastIndex()+1;j++){
					if(g.isNeighbor(i, j)){
						if(g.findEdge(i,j).isEdgeType()){
							posConnectingEdges++;
						}
					}
				}
			}
			return posConnectingEdges;
	}
	
	public static int cutNegative(Graph<Integer, EdgeType> g, ClusterProperties cluster1, ClusterProperties cluster2){
		int negConnectingEdges=0;
		for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
			for(int j=cluster2.getFirstIndex();j<cluster2.getLastIndex()+1;j++){
				if(g.isNeighbor(i, j)){
					if(!(g.findEdge(i,j).isEdgeType())){
						negConnectingEdges++;
					}
				}
			}
		}
		return negConnectingEdges;
	}
	
	//returns [the generalized harmonic distance between two subgraphs, number of positive edges between two subgraphs
	//, number of negative edges between two subgraphs];
	public static double[] harmMeanNeg(List<CoordinatesPeer> subgraph1, List<CoordinatesPeer> 
		subgraph2, double posWeight, double negWeight, double neutralWeight){
		double[] res = new double[3];
		double numerator = 0.0;
		double denominator = 0.0;
		double nbNegEdges = 0;
		double nbPosEdges = 0;
		for(int i = 0; i< subgraph1.size();i++){
			CoordinatesPeer p1 = subgraph1.get(i);
			for(int j= 0 ; j < subgraph2.size() ; j++){
				CoordinatesPeer p2 = subgraph2.get(j);
				if(p1.getRepulseNeighbors().containsKey(p2)){
					//denominator += ((neutralWeight + negWeight) / p1.getCoordinate().distance(p2.getCoordinate()));
					denominator += ((neutralWeight + negWeight)/ p1.getCoordinate().distanceMinkowski(p2.getCoordinate(),10));
					numerator += (neutralWeight+negWeight);
					nbNegEdges++;
				}else{
					//denominator += (neutralWeight / p1.getCoordinate().distance(p2.getCoordinate()));
					denominator += (neutralWeight / p1.getCoordinate().distanceMinkowski(p2.getCoordinate(),10));
					numerator += neutralWeight;
				}
				if(p1.getAttractNeighbors().containsKey(p2)){
					nbPosEdges++;
				}
			}
		}
		res[0] = (numerator / denominator);
		res[1] = nbPosEdges;
		res[2] = nbNegEdges;
		return res;
	}
	
	public static int cut(Graph<Integer, EdgeType> g, ClusterProperties cluster1, ClusterProperties cluster2){
		int allConnectingEdges=0;
		for(int i=cluster1.getFirstIndex();i<cluster1.getLastIndex()+1;i++){
			for(int j=cluster2.getFirstIndex();j<cluster2.getLastIndex()+1;j++){
				if(g.isNeighbor(i, j)){
						allConnectingEdges++;
				}
			}
		}
		return allConnectingEdges;
	}
	
	public static double normalizedPosCut(Graph<Integer, EdgeType> g, ClusterProperties cluster1, ClusterProperties cluster2){
			return cutPositive(g, cluster1, cluster2)/(cluster1.getClusterSize()*cluster2.getClusterSize());
	}
	
	public static double normalizedNegCut(Graph<Integer, EdgeType> g, ClusterProperties cluster1, ClusterProperties cluster2){
		return cutNegative(g, cluster1, cluster2)/(cluster1.getClusterSize()*cluster2.getClusterSize());
	}
	
	public static double normalizedCut(Graph<Integer, EdgeType> g, ClusterProperties cluster1, ClusterProperties cluster2){
		return cut(g, cluster1, cluster2)/(cluster1.getClusterSize()*cluster2.getClusterSize());
	}
	public static int posEdgeCount(Graph<Integer, EdgeType> g){
		int edgeCount =0;
		for(EdgeType edge : g.getEdges()){
			if(edge.isEdgeType()){
				edgeCount++;
			}
		}
		return edgeCount;
	}
	
	public static int negEdgeCount(Graph<Integer, EdgeType> g){
		int edgeCount =0;
		for(EdgeType edge : g.getEdges()){
			if(!edge.isEdgeType()){
				edgeCount++;
			}
		}
		return edgeCount;
	}
	
	public static int edgeCount(Graph<Integer, EdgeType> g){
		return g.getEdgeCount();
	}
	
	public static double arithMeanPosEdges(Graph<Integer, EdgeType> g, List<CoordinatesPeer> list){
		double sumDistance =0.0;
		int nbOfDistances = 0;
		for(EdgeType edge: g.getEdges()){
			if(edge.isEdgeType()){
				Integer firstVertex = g.getEndpoints(edge).getFirst();
				Integer secondVertex = g.getEndpoints(edge).getSecond();
				sumDistance += list.get(firstVertex).getCoordinate().distance(list.get(secondVertex).getCoordinate());
				nbOfDistances++;
			}
		}
		if(nbOfDistances!=0){
			return sumDistance/nbOfDistances;
		}else{
			return 0.0;
			}
	}
	
	public static double arithMeanNegEdges(Graph<Integer, EdgeType> g, List<CoordinatesPeer> list){
		double sumDistance =0.0;
		int nbOfDistances = 0;
		for(EdgeType edge: g.getEdges()){
			if(!edge.isEdgeType()){
				Integer firstVertex = g.getEndpoints(edge).getFirst();
				Integer secondVertex = g.getEndpoints(edge).getSecond();
				sumDistance += list.get(firstVertex).getCoordinate().distance(list.get(secondVertex).getCoordinate());
				nbOfDistances++;
			}
		}
		if(nbOfDistances!=0){
			return sumDistance/nbOfDistances;
		}else{
			return 0.0;
			}
	}
	
	public static double geoMeanPosEdges(Graph<Integer, EdgeType> g, List<CoordinatesPeer> list){
		double multiDistance =1.0;
		int nbOfDistances = 0;
		for(EdgeType edge: g.getEdges()){
			if(edge.isEdgeType()){
				Integer firstVertex = g.getEndpoints(edge).getFirst();
				Integer secondVertex = g.getEndpoints(edge).getSecond();
				multiDistance *= list.get(firstVertex).getCoordinate().distance(list.get(secondVertex).getCoordinate());
				nbOfDistances++;
			}
		}
		if(nbOfDistances!=0){
			return Math.pow(multiDistance, (double)(1.0/nbOfDistances));
		}else{
			return 0.0;
			}
	}
	
	public static double geoMeanNegEdges(Graph<Integer, EdgeType> g, List<CoordinatesPeer> list){
		double multiDistance =1.0;
		int nbOfDistances = 0;
		for(EdgeType edge: g.getEdges()){
			if(!edge.isEdgeType()){
				Integer firstVertex = g.getEndpoints(edge).getFirst();
				Integer secondVertex = g.getEndpoints(edge).getSecond();
				multiDistance *= list.get(firstVertex).getCoordinate().distance(list.get(secondVertex).getCoordinate());
				nbOfDistances++;
			}
		}
		if(nbOfDistances!=0){
			return Math.pow(multiDistance, (double)(1.0/nbOfDistances));
		}else{
			return 0.0;
			}
	}
}
