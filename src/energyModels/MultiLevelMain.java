package energyModels;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import threadedSim.ISimulatorTask;
import threadedSim.Simulator;
import energyModels.coord.CartesianCoordinate;
import energyModels.force.apply.ForceApply;
import energyModels.force.compute.DavidsonForce;
import energyModels.force.compute.FruchForce;
import energyModels.force.compute.HCForce;
import energyModels.force.compute.LinLogForce;
import energyModels.force.compute.SignedDavidsonForce;
import energyModels.force.compute.SignedFruchForce;
import energyModels.force.compute.SignedHCForce;
import energyModels.force.compute.SignedLinLogForce;
import energyModels.force.compute.SignedNewForce1;
import energyModels.force.compute.SignedNewForce3;
import energyModels.graph.ClusterProperties;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.GraphCluster;
import energyModels.graph.GraphVisualizer;
import energyModels.graph.InterClusterProperties;
import energyModels.graph.VertexType;
import energyModels.peer.CoordinatesPeer;
import energyModels.peerInit.FromGraph;
import energyModels.peerInit.FromGraphSigned;
import energyModels.tree.BHTree;
import energyModels.tree.MultiLevelGraph;
import energyModels.tree.Quad;

public class MultiLevelMain {
	public static final double scale = 200 ;
	public static double posEdgeScale =100;
	public static double negEdgeScale = 1;
	public static double neutralEdgeScale = 1;
	public static final int dimension = 2;
	//public static int nbCycles = 250;
	public static String fileName = "image";
	public static double tolerance = 1.001;
	public static final int nbPeer = 6050;
	
	public static void main(String[] args) {
		/*nbCycles = Integer.parseInt(args[0]);
		neutralEdgeScale = Double.parseDouble(args[1]);
		posEdgeScale = Double.parseDouble(args[2]);
		negEdgeScale = Double.parseDouble(args[3]);*/
		//int nbPeer = 50;
		
		//LinLog Model
		/*UndirectedSparseGraph<Integer, EdgeType> graph = new UndirectedSparseGraph<Integer, EdgeType>();
		for (int i = 0; i < nbPeer; i++) {
			graph.addEdge(new EdgeType(), i, (i + 1) % nbPeer);
		}
		simulator.addPeerInit(new FromGraph(graph));
		for (int i = 0; i < nbPeer; i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}
		for (CoordinatesPeer p : simulator.getPeers()) {
			p.setRepulseNeighbors(simulator.getPeers());
			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
		}*/
		//End of LinLog model
		
		//DualLinLog model
		/*UndirectedSparseGraph<Integer, EdgeType> graph = new UndirectedSparseGraph<Integer, EdgeType>();
		for (int i=0; i<nbPeer;i++){
			graph.addVertex(i);
		}
		Random rand = new Random();
		for (int i = 0; i < nbPeer/2; i++) {
			for(int j=(nbPeer/2); j<nbPeer; j++){
				if(rand.nextDouble()>0.1){
					graph.addEdge(new EdgeType(), i, j);
				}
			}
		}
		simulator.addPeerInit(new FromGraphDual(graph));
		for (int i = 0; i < nbPeer; i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}
		for (CoordinatesPeer p : simulator.getPeers()) {
			p.setAttractNeighbors(simulator.getPeers());
			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
		}*/
		//End of DualLinLog Block
		
		//Neutral Model
		/*UndirectedSparseGraph<Integer, EdgeType> graph = new UndirectedSparseGraph<Integer, EdgeType>();
		for (int i=0; i<nbPeer;i++){
			graph.addVertex(i);
		}
		simulator.addPeerInit(new FromGraphSigned(graph));
		for (int i = 0; i < nbPeer; i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}
		for (CoordinatesPeer p : simulator.getPeers()) {
			p.setRepulseNeighbors(simulator.getPeers());
			p.setAttractNeighbors(simulator.getPeers());
		}*/
		//End of Neutral Model
		
		
		//Grid graph example.
		/*GridGraph grid = new GridGraph();
		Graph graph = grid.buildGridGraph(100,100);
		System.out.println("Vertex count = " + graph.getGraph().getVertexCount());
		System.out.println("Edge count = " + graph.getGraph().getEdgeCount());*/
		//end of grid graph example.
		
		//caveman graph example
		/*Graph graph = new Graph();
		int clusterSize = 5;
		int nbClusters = 5;
		for(int i = 0; i < nbClusters * clusterSize; i++){
			graph.addVertex(i);
		}
		for(int i = 0; i < nbClusters; i++){
			for(int j = 0; j < clusterSize; j++){
				for(int k = j + 1; k < clusterSize; k++){
					graph.addEdge(new EdgeType(), i * clusterSize + j, i * clusterSize + k);
				}
				graph.getGraph().removeEdge(graph.getGraph().findEdge(graph.getVertex(i*clusterSize), graph.getVertex(i*clusterSize+1)));
			}
		}
		for(int i = 0; i < nbClusters - 1; i++){
			graph.addEdge(new EdgeType(), i*clusterSize + clusterSize - 1, (i + 1)*clusterSize);
		}
		graph.addEdge(new EdgeType(), clusterSize * nbClusters - 1, 0);
		*/
		//end of caveman graph example
			
		//line graph
		/*Graph graph = new Graph();
		int lineLength = 2000;
		for(int i = 0; i < lineLength; i++){
			graph.addVertex(i);
		}
		for(int i = 0; i < lineLength - 1; i++){
			graph.getGraph().addEdge(new EdgeType(), graph.getVertex(i), graph.getVertex(i+1));
		}*/
		//end of line graph
		
		//disconnected graph
		/*Graph graph = new Graph();
		int lineLength = 1000;
		for(int i = 0; i < lineLength; i++){
			graph.addVertex(i);
		}*/
		//end of disconnected graph.
		
		//circle graph
		/*Graph graph = new Graph();
		int circleLength = 11;
		int nbCircles = 5;
		for(int i = 0; i < nbCircles * circleLength; i++){
			graph.addVertex(i);
		}
		
		for(int j = 0; j < nbCircles; j++){
			for(int i =0; i < circleLength - 1; i++){
				graph.getGraph().addEdge(new EdgeType(), graph.getVertex((j*circleLength) + i), graph.getVertex((j*circleLength)+i+1));
			}
			graph.getGraph().addEdge(new EdgeType(),graph.getVertex(j*circleLength),graph.getVertex((j*circleLength) + circleLength-1));
		}
		
		int lineLength = 10;
		for(int i = nbCircles * circleLength; i < nbCircles * circleLength + lineLength; i++){
			graph.addVertex(i);
		}
		for(int i = nbCircles * circleLength; i < nbCircles * circleLength + lineLength - 1; i++){
			graph.getGraph().addEdge(new EdgeType(), graph.getVertex(i), graph.getVertex(i+1));
		}*/
		//end of circle graph
		
		//flower graph
		/*Graph graph = new Graph();
		int circleLength = 50;
		int nbCircles = 6;
		for(int i = 0; i < nbCircles * circleLength; i++){
			graph.addVertex(i);
		}
		
		for(int j = 0; j < nbCircles; j++){
			for(int i =0; i < circleLength - 1; i++){
				graph.getGraph().addEdge(new EdgeType(), graph.getVertex((j*circleLength) + i), graph.getVertex((j*circleLength)+i+1));
			}
			graph.getGraph().addEdge(new EdgeType(),graph.getVertex(j*circleLength),graph.getVertex((j*circleLength) + circleLength-1));
		}
		
		graph.addVertex((nbCircles * circleLength));
		VertexType centralVertex = graph.getVertex(nbCircles * circleLength); 
		for(int i = 0; i < nbCircles; i++){
			graph.getGraph().addEdge(new EdgeType(), centralVertex, graph.getVertex(i*circleLength));
		}*/
		//end of flower graph
		
		//mesh graph
		/*Graph graph = new Graph();
		int graphSize = 30;
		for(int i = 0; i < graphSize; i++){
			graph.addVertex(i);
		}
		for(int i = 0; i < graphSize; i++){
			for(int j = i+1; j < graphSize; j++){
				EdgeType edge = new EdgeType();
				graph.addEdge(edge, i, j);
			}
		}*/
		//end of mesh graph

		
		
		//user-item movieLens graph builder example
	/*	Set<Integer> searchSlice = new HashSet<Integer>();
		Set<Integer> profileSlice = new HashSet<Integer>();
		for (int i = 1; i <= 20; i++) {
			profileSlice.add(i);
		}
		DataProvider dataProvider = new MovielensDataProvider(nbPeer,
				profileSlice, searchSlice);
		int nbPeer = dataProvider.fetchInformation();
		BuildGraphFromRaters g = new BuildGraphFromRaters(0.0);
		for (int i = 0; i < nbPeer; i++) {
			UserInfo uInfo = dataProvider.giveData();
			User p = new User(uInfo.userId);
			p.setProfile(uInfo.cache);
			p.setTestProfile(uInfo.search);
			g.addPeerToGraph(p);
		}
		dataProvider.clear();
		UndirectedSparseGraph<Integer, EdgeType> graph = g.getGraph();
		
		//removing non positively connected vertices from the graph. Only For Signed Model 3.
		List<Integer> vertices = new ArrayList<Integer>(graph.getVertices());
		for(Integer node : vertices){
			int nbPosIncidentEdges = 0;
			for(EdgeType edge : graph.getIncidentEdges(node)){
				if(edge.isEdgeType()){
					nbPosIncidentEdges++;
				}
			}
			if (nbPosIncidentEdges < 1){
				graph.removeVertex(node);
			}
		}
		//end of removing non positively connected vertices.
		
		simulator.addPeerInit(new FromGraphSigned(graph));
		List<CoordinatesPeer> allItems = new ArrayList<CoordinatesPeer>();
		List<CoordinatesPeer> allUsers = new ArrayList<CoordinatesPeer>();
		for(Integer i: graph.getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(i);
			if(i>0){
				allUsers.add(coPeer);
			}else{
				allItems.add(coPeer);
			}
			simulator.addPeer(coPeer);
		}
		
		for (CoordinatesPeer p : simulator.getPeers()) {
			p.setNeutralNeighbors(simulator.getPeers());
			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		}
		
//		for(CoordinatesPeer p: allUsers){
//			p.setNeutralNeighbors(allUsers);
//			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
//			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
//			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
//		} 
//		for(CoordinatesPeer p: allItems){
//				p.setNeutralNeighbors(allItems);
//				System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
//				System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
//				System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
//				
//		}			
*/		// end of user-item movieLens graph builder example
		
		//friend/foe example of African tribes
		/*UndirectedSparseGraph<Integer, EdgeType> graph = new UndirectedSparseGraph<Integer, EdgeType>();
		for(int i=1; i <= 16; i++){
			graph.addVertex(i);
		}
		graph.addEdge(new EdgeType(false), 1, 5);
		graph.addEdge(new EdgeType(true), 1, 13);
		graph.addEdge(new EdgeType(true), 1, 16);
		graph.addEdge(new EdgeType(true), 2, 3);
		graph.addEdge(new EdgeType(false), 2, 4);
		graph.addEdge(new EdgeType(false), 2, 5);
		graph.addEdge(new EdgeType(true), 2, 6);
		graph.addEdge(new EdgeType(true), 2, 9);
		graph.addEdge(new EdgeType(false), 2, 10);
		graph.addEdge(new EdgeType(false), 2, 14);
		graph.addEdge(new EdgeType(true), 2, 16);
		graph.addEdge(new EdgeType(false), 3, 4);
		graph.addEdge(new EdgeType(false), 3, 5);
		graph.addEdge(new EdgeType(true), 3, 6);
		graph.addEdge(new EdgeType(false), 3, 8);
		graph.addEdge(new EdgeType(true), 3, 9);
		graph.addEdge(new EdgeType(false), 3, 12);
		graph.addEdge(new EdgeType(true), 3, 13);
		graph.addEdge(new EdgeType(false), 3, 15);
		graph.addEdge(new EdgeType(true), 3, 16);
		graph.addEdge(new EdgeType(true), 4, 5);
		graph.addEdge(new EdgeType(false), 4, 6);
		graph.addEdge(new EdgeType(true), 4, 8);
		graph.addEdge(new EdgeType(true), 4, 10);
		graph.addEdge(new EdgeType(false), 4, 11);
		graph.addEdge(new EdgeType(false), 4, 14);
		graph.addEdge(new EdgeType(false), 4, 15);
		graph.addEdge(new EdgeType(true), 5, 8);
		graph.addEdge(new EdgeType(false), 5, 10);
		graph.addEdge(new EdgeType(false), 5, 11);
		graph.addEdge(new EdgeType(false), 5, 13);
		graph.addEdge(new EdgeType(false), 6, 7);
		graph.addEdge(new EdgeType(false), 6, 10);
		graph.addEdge(new EdgeType(false), 6, 12);
		graph.addEdge(new EdgeType(true), 6, 9);
		graph.addEdge(new EdgeType(false), 6, 15);
		graph.addEdge(new EdgeType(true), 6, 16);
		graph.addEdge(new EdgeType(false), 7, 8);
		graph.addEdge(new EdgeType(false), 7, 10);
		graph.addEdge(new EdgeType(true), 7, 12);
		graph.addEdge(new EdgeType(true), 7, 15);
		graph.addEdge(new EdgeType(true),8, 10);
		graph.addEdge(new EdgeType(false), 8, 11);
		graph.addEdge(new EdgeType(false), 8, 12);
		graph.addEdge(new EdgeType(false), 8, 13);
		graph.addEdge(new EdgeType(true), 9, 11);
		graph.addEdge(new EdgeType(true), 9, 13);
		graph.addEdge(new EdgeType(true), 9, 15);
		graph.addEdge(new EdgeType(true), 9, 16);
		graph.addEdge(new EdgeType(false), 10, 11);
		graph.addEdge(new EdgeType(false), 10, 12);
		graph.addEdge(new EdgeType(false), 10, 15);
		graph.addEdge(new EdgeType(true), 11, 12);
		graph.addEdge(new EdgeType(true), 11, 14);
		graph.addEdge(new EdgeType(true), 12, 15);
		graph.addEdge(new EdgeType(true), 13, 16);
		graph.addEdge(new EdgeType(true), 14, 15);
		graph.addEdge(new EdgeType(false), 14, 16);
		
		simulator.addPeerInit(new FromGraphSigned(graph));
		
		List<CoordinatesPeer> tribes = new ArrayList<CoordinatesPeer>();
		for(Integer i : graph.getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(i);
			tribes.add(coPeer);
			simulator.addPeer(coPeer);
		}
		//for(CoordinatesPeer p: tribes){
			//p.setNeutralNeighbors(tribes);
			//System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			//System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			//System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		//}
	*/	//end of the example of African tribes.
		
		//user/item signed unipartite example
		/*List<ClusterProperties> clusterList = new ArrayList<ClusterProperties>();
		List<InterClusterProperties> interClusterList = new ArrayList<InterClusterProperties>();
		ClusterProperties cluster1 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster1);
		ClusterProperties cluster2 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster2);
		ClusterProperties cluster3 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster3);
		ClusterProperties cluster4 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster4);
		
		InterClusterProperties cluster12 = new InterClusterProperties(cluster1,cluster2,0.0,0.0);
		interClusterList.add(cluster12);
		InterClusterProperties cluster13 = new InterClusterProperties(cluster1,cluster3,0.7,0.0);
		interClusterList.add(cluster13);
		InterClusterProperties cluster14 = new InterClusterProperties(cluster1,cluster4,0.0,0.0);
		interClusterList.add(cluster14);
		InterClusterProperties cluster23 = new InterClusterProperties(cluster2,cluster3,0.0,0.0);
		interClusterList.add(cluster23);
		InterClusterProperties cluster24 = new InterClusterProperties(cluster2,cluster4,0.7,0.0);
		interClusterList.add(cluster24);
		InterClusterProperties cluster34 = new InterClusterProperties(cluster3,cluster4,0.0,0.0);
		interClusterList.add(cluster34);
		
		GraphCluster graphCluster = new GraphCluster(clusterList, interClusterList);
		Graph graph = graphCluster.cluster();
		
		simulator.addPeerInit(new FromGraphSigned(graph));
		for (int i = 0; i < graph.getVertexCount(); i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}	
		for (CoordinatesPeer p : simulator.getPeers()) {
			p.setNeutralNeighbors(simulator.getPeers());
			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		}*/
		//end of user/item signed unipartite example
		
		
		//user/item signed bipartite
		/*List<ClusterProperties> clusterList = new ArrayList<ClusterProperties>();
		List<InterClusterProperties> interClusterList = new ArrayList<InterClusterProperties>();
		ClusterProperties cluster1 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster1);
		ClusterProperties cluster2 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster2);
		ClusterProperties cluster3 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster3);
		ClusterProperties cluster4 = new ClusterProperties(0.0,0.0,10);
		clusterList.add(cluster4);
		
		InterClusterProperties cluster12 = new InterClusterProperties(cluster1,cluster2,0.0,0.0);
		interClusterList.add(cluster12);
		InterClusterProperties cluster13 = new InterClusterProperties(cluster1,cluster3,0.7,0.0);
		interClusterList.add(cluster13);
		InterClusterProperties cluster14 = new InterClusterProperties(cluster1,cluster4,0.0,0.0);
		interClusterList.add(cluster14);
		InterClusterProperties cluster23 = new InterClusterProperties(cluster2,cluster3,0.0,0.0);
		interClusterList.add(cluster23);
		InterClusterProperties cluster24 = new InterClusterProperties(cluster2,cluster4,0.7,0.0);
		interClusterList.add(cluster24);
		InterClusterProperties cluster34 = new InterClusterProperties(cluster3,cluster4,0.0,0.0);
		interClusterList.add(cluster34);
		
		GraphCluster graphCluster = new GraphCluster(clusterList, interClusterList);
		UndirectedSparseGraph<Integer , EdgeType> graph = 
			(UndirectedSparseGraph<Integer, EdgeType>) graphCluster.cluster();
		simulator.addPeerInit(new FromGraphSigned(graph));
		List<CoordinatesPeer> users = new ArrayList<CoordinatesPeer>();
		List<CoordinatesPeer> items = new ArrayList<CoordinatesPeer>();
		for (int i = cluster1.getFirstIndex(); i < cluster2.getLastIndex()+1; i++) {
			CoordinatesPeer coPeer = new CoordinatesPeer(i);
			simulator.addPeer(coPeer);
			users.add(coPeer);
		}	
		for (int i = cluster3.getFirstIndex(); i < cluster4.getLastIndex()+1; i++) {
			CoordinatesPeer coPeer = new CoordinatesPeer(i);
			simulator.addPeer(coPeer);
			items.add(coPeer);
		}	
		for (CoordinatesPeer p : users) {
			p.setNeutralNeighbors(users);
			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		}
		for (CoordinatesPeer p : items) {
			p.setNeutralNeighbors(items);
			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		}*/
		//end of user/item signed bipartite
		
		Graph graph = new Graph();
		int clusterSize = 10;
		int nbClusters = 2;
		for(int i = 0; i < nbClusters; i++){
			for(int j = 0; j < clusterSize; j++){
				graph.addVertex((i * clusterSize) + j);
			}
		}
		for(int i = 0; i < nbClusters; i++){
			for(int j = 0; j < clusterSize; j++){
				for(int k = j + 1; k < clusterSize; k += 2){
					graph.addEdge(new EdgeType(), (i * clusterSize) + j, (i * clusterSize) + k);
				}
			}
		}
		
		for(int i = 0; i < nbClusters; i++){
			for(int j = i+1; j < nbClusters; j++){
				for(int k = 0; k < clusterSize; k++){
					graph.addEdge(new EdgeType(), (i * clusterSize) + k, (j * clusterSize) + k);
				}
			}
		}
		
		//4 clusters signed linlog model example
		/*List<ClusterProperties> clusterList = new ArrayList<ClusterProperties>();
		List<InterClusterProperties> interClusterList = new ArrayList<InterClusterProperties>();
		ClusterProperties cluster1 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster1);
		ClusterProperties cluster2 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster2);
		ClusterProperties cluster3 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster3);
		ClusterProperties cluster4 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster4);
		ClusterProperties cluster5 = new ClusterProperties(0.0,0.0,50);
		clusterList.add(cluster5);*/
		
		
		/*InterClusterProperties cluster12 = new InterClusterProperties(cluster1,cluster2,0.1,0.0);
		interClusterList.add(cluster12);
		InterClusterProperties cluster13 = new InterClusterProperties(cluster1,cluster3,0.02,0.0);
		interClusterList.add(cluster13);
		InterClusterProperties cluster14 = new InterClusterProperties(cluster1,cluster4,0.00,0.0);
		interClusterList.add(cluster14);
		InterClusterProperties cluster23 = new InterClusterProperties(cluster2,cluster3,0.00,0.0);
		interClusterList.add(cluster23);
		InterClusterProperties cluster24 = new InterClusterProperties(cluster2,cluster4,0.00,0.0);
		interClusterList.add(cluster24);
		InterClusterProperties cluster34 = new InterClusterProperties(cluster3,cluster4,0.02,0.0);
		interClusterList.add(cluster34);*/
		
		/*GraphCluster graphCluster = new GraphCluster(clusterList, interClusterList);
		Graph graph = graphCluster.cluster();*/
		
				
		//for (CoordinatesPeer p : simulator.getPeers()) {
			//p.setNeutralNeighbors(simulator.getPeers());
			//p.setRepulseNeighbors(simulator.getPeers());
			//p.setAttractNeighbors(simulator.getPeers());
			//System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			//System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			//System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		//}
		//End of 4 cluster signed linlog model example
		
		//calculating harmonic mean for cluster example only.
		/*List<CoordinatesPeer> subgraph1 = new ArrayList<CoordinatesPeer>();
		List<CoordinatesPeer> subgraph2 = new ArrayList<CoordinatesPeer>();
		for(int i = 0; i < cluster1.getLastIndex()+1; i++){
			subgraph1.add(simulator.getPeers().get(i));
		}
		for(int i = cluster1.getLastIndex() + 1; i < simulator.getPeers().size();i++){
			subgraph2.add(simulator.getPeers().get(i));
		}
		double[] vector = GraphStatistics.harmMeanNeg(subgraph1, subgraph2, posEdgeScale, negEdgeScale, neutralEdgeScale);
		double harmNeg = vector[0];
		double nbPosEdges = vector[1];
		double nbNegEdges = vector[2];
		double posDensity = (nbPosEdges/(subgraph1.size()*subgraph2.size()));
		double negDensity = (nbNegEdges/(subgraph1.size()*subgraph2.size()));
		System.out.println("size of the first subgraph = " + subgraph1.size());
		System.out.println("size of the second subgraph = " + subgraph2.size());
		System.out.println("Positive density = " + posDensity);
		System.out.println("Negative density = " + negDensity);
		System.out.println("Negative Harmonic distance = " + harmNeg);
		System.out.println("(1 + (k2 *density-)) / (1 + (k1 *density+)) =  " + ((1.0 + (negEdgeScale * negDensity))
				/ (1.0 + (posEdgeScale * posDensity))));*/
		//end of calculating harmonic mean for cluster example only.
		
		//writing cluster coordinate results into the file.
		/*try {
			BufferedWriter out = new BufferedWriter
			(new FileWriter("coordinatesCluster"+".txt", false));
			for (CoordinatesPeer p : simulator.getPeers()) {
				//System.out.println(p.getCoordinate());
				int num = 0;
				for(ClusterProperties c: clusterList){
					if(p.getPeerId()>= c.getFirstIndex()&& p.getPeerId() <= c.getLastIndex()){
						num  = c.getClusterID();
					}
				}
				out.write("\n"+p.getPeerId() + "\t" +p.getCoordinate().toString() + "\t" + num );
				out.flush();
			}
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		//end of writing cluster coordinate results into the file.
		
		//writing user/item coordinate results into the file.
		/*try {
			BufferedWriter out = new BufferedWriter (new FileWriter("coordinates"+".txt", false));
			Date todaysDate = new java.util.Date();
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
			String formattedDate = formatter.format(todaysDate);
		
			out.write("# Execution time = " + formattedDate);
			out.write("\n# BH tree formation mean time = " + (bhTime * 0.001)/ nbCycles);
			out.write("\n# Execution cycle mean time = " + (cycleTime * 0.001)/ nbCycles);
			out.write("\n# Total execution mean time = " + (total * 0.001)/ nbCycles);
			//out.write("\n# Data type = " + bg.getClass().getSimpleName());
			out.write("\n# Force Model = " + task.getClass().getSimpleName());
			out.write("\n# Number of Coordinates = " + CartesianCoordinate.nbDimension);
			out.write("\n# Number of users = " + simulator.getPeers().size());
			out.write("\n# Number of cycles = " + nbCycles);
			out.write("\n# PosEdgeScale = " + posEdgeScale);
			out.write("\n# NegEdgeScale = " + negEdgeScale);
			out.write("\n# NeutralEdgeScale = " + neutralEdgeScale);
			for (CoordinatesPeer p : allUsers){
				System.out.println(p.getCoordinate());
					out.write("\n"+p.getPeerId() + "\t" +p.getCoordinate().toString() + "\t1");
					out.flush();
			}
			out.write("\n\n");
			out.flush();
			for(CoordinatesPeer p:allItems){
					out.write("\n"+p.getPeerId() + "\t" +p.getCoordinate().toString() + "\t-1");
					out.flush();
			}
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		//end of writing user/item coordinates into the file.
		
		/*
		System.out.println("Arithmatic Mean : " + GraphStatistics.arithMean(simulator.getPeers()));
		System.out.println("Geometrical Mean : " + GraphStatistics.geomMean(simulator.getPeers()));
		
		double meanOfArithMeans=0.0;
		for(InterClusterProperties interProp: interClusterList ){
			meanOfArithMeans += GraphStatistics.arithMeanOf2Clusters(simulator.getPeers(), interProp.getCluster1(), interProp.getCluster2());
			System.out.println("\nPositive cut between cluster " + interProp.getCluster1().getClusterID()+
					" and cluster " + interProp.getCluster2().getClusterID()+ " is :" 
					+ GraphStatistics.cutPositive(graph, interProp.getCluster1(), interProp.getCluster2()));
			System.out.println("Arithmatic Mean between cluster " +interProp.getCluster1().getClusterID()+
					" and cluster " + interProp.getCluster2().getClusterID()+ " is :"
					+ GraphStatistics.arithMeanOf2Clusters(simulator.getPeers(), interProp.getCluster1(), interProp.getCluster2()));
			System.out.println("Geometrical Mean between cluster " +interProp.getCluster1().getClusterID()+
					" and cluster " + interProp.getCluster2().getClusterID()+ " is :"+ 
					GraphStatistics.geomMeanOf2Clusters(simulator.getPeers(), interProp.getCluster1(), interProp.getCluster2()));
		}
		System.out.println("\nMean of the arithmatic mean between all clusters : " + meanOfArithMeans/interClusterList.size());
		for(ClusterProperties cluster : clusterList){
			System.out.println("\nArithmatic mean of cluster " + cluster.getClusterID() + " is: "
					+ GraphStatistics.arithMeanOf1Cluster(simulator.getPeers(), cluster));
			System.out.println("Geometrical mean of cluster " + cluster.getClusterID() + " is: "
					+ GraphStatistics.geomMeanOf1Cluster(simulator.getPeers(), cluster));
			System.out.println("landa is :" + (GraphStatistics.geomMeanOf1Cluster(simulator.getPeers(), cluster)/
					GraphStatistics.arithMeanOf1Cluster(simulator.getPeers(), cluster)));
			}
		double vertexPairs = (((double)graph.getVertexCount()*(double)(graph.getVertexCount()-1))/2);
		double arithTerm = GraphStatistics.posEdgeCount(graph)*GraphStatistics.arithMeanPosEdges(graph, simulator.getPeers())
		+ (((double)graph.getVertexCount()*(double)(graph.getVertexCount()-1))/2)*GraphStatistics.arithMean(simulator.getPeers());
		double arithEquivalentTerm = GraphStatistics.negEdgeCount(graph) + 
		(((double)graph.getVertexCount()*(double)(graph.getVertexCount()-1))/2);
		
		System.out.println("\nnegative edges to vertex pairs ratio : " + (double)GraphStatistics.negEdgeCount(graph)/vertexPairs);
		System.out.println("positive edges to vertex pairs ratio : " + (double)GraphStatistics.posEdgeCount(graph)/vertexPairs);
		System.out.println("arithmTerm : " + arithTerm + "\n arithEquivalentTerm : " + arithEquivalentTerm);
		*/
		
		/*posEdgeScale = (graph.getGraph().getVertexCount() * graph.getGraph().getVertexCount()) 
			/ (graph.getGraph().getEdgeCount());*/
		
		//posEdgeScale = Math.sqrt(graph.getGraph().getVertexCount())/3;
		
		
		//ISimulatorTask<CoordinatesPeer> task = new DualLinLogForce();
		//ISimulatorTask<CoordinatesPeer> task = new HCForce(posEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedHCForce(posEdgeScale,negEdgeScale, neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new DavidsonForce(posEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new LinLogForce(posEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new FruchForce(posEdgeScale,neutralEdgeScale);
		ISimulatorTask<CoordinatesPeer> task = new SignedLinLogForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce1(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce3(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedFruchForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedDavidsonForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		
		

		int nbDisconnectedFine = 0;
		for(VertexType v : graph.getGraph().getVertices()){
			if(graph.getGraph().getNeighborCount(v) == 0){
				nbDisconnectedFine++;
			}
		}
		System.out.println("Number of disconnected vertices = " + nbDisconnectedFine);
		
		//direct graph drawing with no coarsening
		/*Simulator<CoordinatesPeer> simulatorDirect = new Simulator<CoordinatesPeer>();
		simulatorDirect.addTask(task);
		simulatorDirect.addTask(new ForceApply());
		draw(simulatorDirect, graph, task, fileName+"direct",null,-1);*/
		//end of direct graph drawing with no coarsening
		
		
		long startTime = (long) (System.currentTimeMillis()*0.001);
		//coarsening phase.
		List<MultiLevelGraph> multiList = new ArrayList<MultiLevelGraph>();
		MultiLevelGraph multiLevelObj = new MultiLevelGraph(graph);
		multiLevelObj.HEC();
		int levelCounter = 1;
		double gamma = ((double)multiLevelObj.getCoarseGraph().getGraph().getVertexCount() - nbDisconnectedFine) 
		/ (multiLevelObj.getFineGraph().getGraph().getVertexCount()- nbDisconnectedFine);
		System.out.println("gamma = " + gamma);
		int minSize = 10;
		while((gamma < 0.9) && (multiLevelObj.getCoarseGraph().getGraph().getVertexCount() > (nbDisconnectedFine + minSize)) && (levelCounter < 12)){
			
			nbDisconnectedFine = 0;
			for(VertexType v : multiLevelObj.getCoarseGraph().getGraph().getVertices()){
				if(multiLevelObj.getCoarseGraph().getGraph().getNeighborCount(v) == 0){
					nbDisconnectedFine++;
				}
			}
			System.out.println("Number of disconnected vertices = " + nbDisconnectedFine);
			multiList.add(multiLevelObj);
			levelCounter++;
			
			if(gamma < 0.85){
				MultiLevelGraph multi = new MultiLevelGraph(multiLevelObj.getCoarseGraph());
				multi.HEC();
				gamma = ((double)multi.getCoarseGraph().getGraph().getVertexCount() - nbDisconnectedFine) 
				/ (multi.getFineGraph().getGraph().getVertexCount()- nbDisconnectedFine);
				System.out.println("gamma = " + gamma);
				multiLevelObj = multi;
			}else{
				MultiLevelGraph multi = new MultiLevelGraph(multiLevelObj.getCoarseGraph());
				multi.MIVS();
				gamma = ((double)multi.getCoarseGraph().getGraph().getVertexCount() - nbDisconnectedFine) 
				/ (multi.getFineGraph().getGraph().getVertexCount()- nbDisconnectedFine);
				System.out.println("gamma = " + gamma);
				multiLevelObj = multi;
			}
		}
		multiList.add(multiLevelObj);
		//end of coarsening phase.
		
		//initialization of the coarsest graph.
		Simulator<CoordinatesPeer> simulator = new Simulator<CoordinatesPeer>();
		simulator.addTask(task);
		simulator.addTask(new ForceApply());
		Map<VertexType, CartesianCoordinate> coordinatesMap = new HashMap<VertexType, CartesianCoordinate>();
		Graph coarsest = multiList.get(multiList.size() - 1).getCoarseGraph();
		simulator.addPeerInit(new FromGraphSigned(coarsest));
		for(VertexType vertex: coarsest.getGraph().getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(vertex.getVertexNum());
			coPeer.setMass(vertex.getMass());
			simulator.addPeer(coPeer);
		}
		int level = 0;
		draw(simulator, coarsest, task, fileName+multiList.size()+"-",null,multiList.size()-level);
		level++;
		for(CoordinatesPeer peer : simulator.getPeers()){
			coordinatesMap.put(coarsest.getVertex(peer.getPeerId()), peer.getCoordinate());
		}
		
		//algo
		for(int j = 0; j < multiList.size(); j++){
			simulator.clear();
			simulator = new Simulator<CoordinatesPeer>();
			simulator.addTask(task);
			simulator.addTask(new ForceApply());
			
			MultiLevelGraph currentMulti = multiList.get(multiList.size() - j - 1);
			simulator.addPeerInit(new FromGraphSigned(currentMulti.getFineGraph()));
			if(currentMulti.EC){
				for(VertexType vertex: currentMulti.getFineGraph().getGraph().getVertices()){
					VertexType v = currentMulti.getPro().get(vertex);
					CartesianCoordinate coorMap = coordinatesMap.get(v);
					CartesianCoordinate coor = new CartesianCoordinate(coorMap);
					CartesianCoordinate random = new CartesianCoordinate();
					random.times(CartesianCoordinate.minMoveDistance/2.0);
					coor.plus(random);
					CoordinatesPeer coPeer = new CoordinatesPeer(vertex.getVertexNum());
					coPeer.setMass(vertex.getMass());
					coPeer.setCoordinate(coor);
					simulator.addPeer(coPeer);
				}
			}else if(currentMulti.MIVS){
				for(VertexType vertex: currentMulti.getFineGraph().getGraph().getVertices()){
					Set<VertexType> coarseVertices = currentMulti.prolongateMIVS(vertex);
					CartesianCoordinate coor = new CartesianCoordinate(new double[]{0.0,0.0});
					for(VertexType coarseVertex : coarseVertices){
						CartesianCoordinate c = coordinatesMap.get(coarseVertex);
						coor.plus(c);
					}
					coor.times(1.0 / coarseVertices.size());
					CartesianCoordinate random = new CartesianCoordinate();
					random.times(CartesianCoordinate.minMoveDistance/2.0);
					coor.plus(random);
					CoordinatesPeer coPeer = new CoordinatesPeer(vertex.getVertexNum());
					coPeer.setMass(vertex.getMass());
					coPeer.setCoordinate(coor);
					simulator.addPeer(coPeer);
				}
			}else{
				throw new RuntimeException("The MultiLevel object has no type!");
			}
			draw(simulator, currentMulti.getFineGraph(), task, fileName+(multiList.size()-j - 1)+"-",null, multiList.size()-level);
			level++;
			coordinatesMap.clear();
			for(CoordinatesPeer peer : simulator.getPeers()){
				coordinatesMap.put(currentMulti.getFineGraph().getVertex(peer.getPeerId()), peer.getCoordinate());
			}
			if(simulator.getPeers().size() == 2*clusterSize){
				//computing the harmonic distance between two clusters.
				double harm = 0;
				int counter = 0;
				Set<Integer> first = new HashSet<Integer>(clusterSize);
				Set<Integer> second = new HashSet<Integer>(clusterSize);
				for(int i = 0; i < clusterSize; i++){
					first.add(i);
					second.add(i + clusterSize);
				}
				for(CoordinatesPeer coor : simulator.getPeers()){
					if(first.contains(coor.getPeerId())){
						CartesianCoordinate firstCoor = coor.getCoordinate();
						System.out.println("vertex number = " + coor.getPeerId());
						for(CoordinatesPeer coor2 : simulator.getPeers()){
							if(second.contains(coor2.getPeerId())){
								CartesianCoordinate secondCoor = coor2.getCoordinate();
								double distance = firstCoor.distance(secondCoor);
								counter++;
								harm += (1.0 / distance);
							}
						}
					}				
				}
				harm = (double)(counter / harm);
				DecimalFormat df = new DecimalFormat("#.###");
				//computing the density
				double density = 1.0 / clusterSize;
				System.out.println("harmonic mean = " + df.format(harm));
				System.out.println("one by k times density = " + df.format((1.0 / (1.0 + (posEdgeScale * density)))));
			}
		}
	//	dataProvider.clear();
		long endTime = (long) (System.currentTimeMillis()*0.001);
		System.out.println("Total Execution Time = " + (endTime - startTime));
		
		System.out.println("number of vertices in the simulator = " + simulator.getPeers().size());
		simulator.clear();
	}
	
	public static void draw(Simulator<CoordinatesPeer> simulator, Graph currentGraph, 
			ISimulatorTask<CoordinatesPeer> task, String fileName, List<ClusterProperties> clusterList, int level){
		//parameter estimation
		double vertexCount = currentGraph.getGraph().getVertexCount();
		double edgeCount = currentGraph.getGraph().getEdgeCount();
		double param = (vertexCount * vertexCount) 
		/ (((posEdgeScale * edgeCount) + (vertexCount * vertexCount))*posEdgeScale * edgeCount * 0.5);
		/*double param = (vertexCount * vertexCount) 
		/ ((posEdgeScale * edgeCount * Math.sqrt(vertexCount)));*/
		//param = 0.00005;
	
		/*CartesianCoordinate.maxMoveDistance = 200.0 * param;
		CartesianCoordinate.defaultMoveDist = 100.0 * param;*/
		CartesianCoordinate.minMoveDistance = param;
		CartesianCoordinate.maxMoveDistance = edgeCount * 0.5 * posEdgeScale * param;
		CartesianCoordinate.defaultMoveDist = edgeCount * 0.5 * param;
		//end of parameter estimation
		
		long bhStart;
		long bhEnd;
		long cycleEnd;
		long bhTime = 0;
		long cycleTime = 0;
		long total = 0;
		/*double tolerance = 0.0001;
		double toleranceMax = 0.0002;*/
		//double tolerance = param * 10.0;
		double ratio = 2.0;
		double prevTotalDis = 0.00000001;
		double maxDis = Double.POSITIVE_INFINITY;
		int counter = 0;
		long startExec = (long) (System.currentTimeMillis() * 0.001);
		while((ratio > tolerance) && (counter < 3999)) {
			double totalDis = 0.0;
			maxDis = 0.0;
			bhStart = System.currentTimeMillis();
			double[] margins = BHTree.getMargins(simulator.getPeers());
			Quad initialQuad = new Quad(margins[0], margins[1], margins[2], margins[3]);
			BHTree.NODE_ID = 0;
			BHTree bhTree = new BHTree(initialQuad);
			for(CoordinatesPeer p : simulator.getPeers()){
				bhTree.insert(p);
			}
			SignedLinLogForce.BHMode = true;
			SignedLinLogForce.setBhTree(bhTree);
			SignedFruchForce.BHMode = true;
			SignedFruchForce.setBhTree(bhTree);
			SignedLinLogForce.setBhTree(bhTree);
			LinLogForce.BHMode = true;
			LinLogForce.setBhTree(bhTree);
			FruchForce.BHMode = true;
			FruchForce.setBhTree(bhTree);
			HCForce.BHMode = true;
			HCForce.setBhTree(bhTree);
			SignedHCForce.BHMode = true;
			SignedHCForce.setBhTree(bhTree);
			DavidsonForce.BHMode = true;
			DavidsonForce.setBhTree(bhTree);
			SignedDavidsonForce.BHMode = true;
			SignedDavidsonForce.setBhTree(bhTree);
			SignedNewForce1.BHMode = true;
			SignedNewForce1.setBhTree(bhTree);
			SignedNewForce3.BHMode = true;
			SignedNewForce3.setBhTree(bhTree);
			bhEnd = System.currentTimeMillis();
			bhTime += (bhEnd - bhStart);
			List<CartesianCoordinate> previousCoor = new ArrayList<CartesianCoordinate>();
			for(CoordinatesPeer peer : simulator.getPeers()){
				previousCoor.add(new CartesianCoordinate(peer.getCoordinate()));
			}
			simulator.cycle();
			for(int i = 0; i < simulator.getPeers().size();i++){
				double dist = simulator.getPeers().get(i).getCoordinate().distance(previousCoor.get(i));
				totalDis += dist;
				if(dist > maxDis){
					maxDis = dist;
				}
			}
			ratio = Math.max(totalDis / prevTotalDis, prevTotalDis / totalDis);
			//System.out.println("totalDis = " + totalDis + " prevTotalDis = " + prevTotalDis);
			//System.out.println("ratio= " + ratio);
			/*if(ratio < 1.00001){
				confidance++;
			}else{
				confidance = 0;
			}*/
			prevTotalDis = totalDis;
			//totalDis /= simulator.getPeers().size();
			counter++;
			cycleEnd = System.currentTimeMillis();
			cycleTime += (cycleEnd - bhEnd);
			total += (cycleEnd - bhStart);
			
			GraphVisualizer gv = new GraphVisualizer(currentGraph, simulator.getPeers(),clusterList,scale);
			if((counter % 100 == 0) && (level < 3)){
				gv.visualize(counter,level);
			}
		}
		long endExec = (long) (System.currentTimeMillis() * 0.001);
	/*	try {
			BufferedWriter out = new BufferedWriter (new FileWriter(fileName + counter+".txt", false));
			Date todaysDate = new java.util.Date();
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
			String formattedDate = formatter.format(todaysDate);
	
			out.write("# Execution date = " + formattedDate);
			out.write("# Execution time = " + (endExec - startExec));
			out.write("\n# BH tree formation mean time = " + (bhTime * 0.001)/ (counter));
			out.write("\n# Execution cycle mean time = " + (cycleTime * 0.001)/ (counter));
			out.write("\n# Total execution mean time = " + (total * 0.001)/ (counter));
			out.write("\n# Force Model = " + task.getClass().getSimpleName());
			out.write("\n# Number of Coordinates = " + CartesianCoordinate.nbDimension);
			out.write("\n# Number of users = " + simulator.getPeers().size());
			out.write("\n# Number of cycles = " + (counter));
			out.write("\n# PosEdgeScale = " + posEdgeScale);
			out.write("\n# NegEdgeScale = " + negEdgeScale);
			out.write("\n# NeutralEdgeScale = " + neutralEdgeScale);
			out.write("\n# Scale = " + scale);
				
			for (CoordinatesPeer p : simulator.getPeers()){
				out.write("\n"+p.getPeerId() + " " +p.getCoordinate().toString());
				out.flush();
			}
			out.write("\n\n");
			out.flush();
			out.close();
			System.out.println("Number of vertices in the simulator = " + simulator.getPeers().size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
			GraphVisualizer gv = new GraphVisualizer(currentGraph, simulator.getPeers(),clusterList, scale);
			gv.visualize(counter,level);
	}
}


	