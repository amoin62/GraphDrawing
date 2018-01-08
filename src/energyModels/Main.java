package energyModels;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import threadedSim.ISimulatorTask;
import threadedSim.Simulator;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import energyModels.coord.CartesianCoordinate;
import energyModels.data.DataProvider;
import energyModels.data.DataProvider.UserInfo;
import energyModels.data.rater.MovielensDataProvider;
import energyModels.data.rater.User;
import energyModels.data.voter.Voter;
import energyModels.data.voter.VoterDataProvider;
import energyModels.force.apply.ForceApply;
import energyModels.force.compute.DualLinLogForce;
import energyModels.force.compute.HCForce;
import energyModels.force.compute.LinLogForce;
import energyModels.force.compute.SignedDavidsonForce;
import energyModels.force.compute.SignedFruchForce;
import energyModels.force.compute.SignedLinLogForce;
import energyModels.force.compute.SignedNewForce1;
import energyModels.force.compute.SignedNewForce2;
import energyModels.force.compute.SignedNewForce3;
import energyModels.graph.ClusterProperties;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.GraphCluster;
import energyModels.graph.GraphStatistics;
import energyModels.graph.GraphVisualizer;
import energyModels.graph.InterClusterProperties;
import energyModels.graph.VertexType;
import energyModels.graph.buildGraph.BuildGraphFromRaters;
import energyModels.graph.buildGraph.BuildGraphFromVoters;
import energyModels.graph.buildGraph.IBuildGraph;
import energyModels.peer.CoordinatesPeer;
import energyModels.peerInit.FromGraph;
import energyModels.peerInit.FromGraphDual;
import energyModels.peerInit.FromGraphSigned;
import energyModels.tree.BHTree;
import energyModels.tree.Quad;

//Green edges are attractive edges and red edges are repulsive ones.
//Nothing should be deleted from this main class.
@SuppressWarnings("unused")
public class Main {
	public static final int nbPeer = 1000;
	public static double scale = 150.0;
	public static double posEdgeScale = 3.0;
	public static double negEdgeScale = 3.0;
	public static final double neutralEdgeScale = 1.0;
	public static final int dimension = 2;
	public static int nbCycles = 200;
	
	public static void main(String[] args){
		scale = Integer.parseInt(args[1]);
		posEdgeScale = Double.parseDouble(args[2]);
		negEdgeScale = Double.parseDouble(args[3]);
		nbCycles = Integer.parseInt(args[4]);
		/*posEdgeScale = Double.parseDouble(args[0]);
		negEdgeScale = Double.parseDouble(args[1]);*/
		//int nbPeer = 50;
		Simulator<CoordinatesPeer> simulator = new Simulator<CoordinatesPeer>();
		//ISimulatorTask<CoordinatesPeer> task = new DualLinLogForce();
		//ISimulatorTask<CoordinatesPeer> task = new HCForce();
		//ISimulatorTask<CoordinatesPeer> task = new LinLogForce(posEdgeScale,neutralEdgeScale);
		ISimulatorTask<CoordinatesPeer> task = null;
		if (args[0].equals("SignedLinLog"))
			task = new SignedLinLogForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		else if(args[0].equals("SignedLinLog2"))
			task = new SignedNewForce1(posEdgeScale,negEdgeScale,neutralEdgeScale);
		else if(args[0].equals("SignedLinLog3"))
			task = new SignedNewForce3(posEdgeScale,negEdgeScale,neutralEdgeScale);
		else if(args[0].equals("SignedFruch"))
			task = new SignedFruchForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		else if(args[0].equals("SignedDavidson"))
			task = new SignedDavidsonForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce1(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce3(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedFruchForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedDavidsonForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		else throw new RuntimeException("The Signed model is unknown.");
		
		simulator.addTask(task);
		simulator.addTask(new ForceApply());
		
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
		UndirectedSparseGraph<Integer, EdgeType> graph = grid.buildGridGraph(32,32);
		simulator.addPeerInit(new FromGraphSigned(graph));
		//simulator.addPeerInit(new FromGraph(graph));
		//simulator.addPeerInit(new FromGraphDual(graph));
		for (int i = 0; i < graph.getVertexCount(); i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}
		
		//setting neutral neighbors for brute force.
		//for (CoordinatesPeer p : simulator.getPeers()) {
			//p.setNeutralNeighbors(simulator.getPeers());
			//p.setRepulseNeighbors(simulator.getPeers());
			//p.setAttractNeighbors(simulator.getPeers());
			//System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			//System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			//System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		//}
*/		//end of grid graph example.
		
		//mesh graph
		/*Graph graph = new Graph();
		int graphSize = 20;
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
		
		//example for the introduction of the thesis.
		/*Graph graph = new Graph();
		int clusterSize = 10;
		
		for(int i = 0; i < (4 * clusterSize); i++){
			System.out.println(i + "\t" + i / clusterSize);
			if (i / clusterSize == 0)
			graph.addVertex(i, Color.BLUE.darker());
			if (i / clusterSize == 1)
				graph.addVertex(i, Color.BLUE.brighter());
			if (i / clusterSize == 2)
				graph.addVertex(i, Color.GREEN.darker());
			if (i / clusterSize == 3)
				graph.addVertex(i, Color.GREEN.brighter());
		}
		
		System.out.println( graph.getGraph().getVertexCount());
		for(int i = 0 ; i < 4; i++){
			for(int j = 0; j < clusterSize; j++){
				for(int k = j + 1; k < clusterSize; k++){
					//System.out.println("haa");
					graph.addEdge(new EdgeType(), (i * clusterSize) + j, (i * clusterSize) + k);
				}
			}
		}
		for(int i = 0 ; i < clusterSize; i = i + 2){
			graph.addEdge(new EdgeType(), i, clusterSize + i);
			graph.addEdge(new EdgeType(), 2 * clusterSize + i, 3 * clusterSize + i);
		}
		for(int i = 0 ; i < 2 * clusterSize; i = i + 5){
			graph.addEdge(new EdgeType(), i, 2 * clusterSize + i);
		}
		System.out.println( graph.getGraph().getEdgeCount());
		simulator.addPeerInit(new FromGraphSigned(graph));
		//simulator.addPeerInit(new FromGraph(graph));
		//simulator.addPeerInit(new FromGraphDual(graph));
		for (int i = 0; i < graph.getGraph().getVertexCount(); i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}*/
		//end of the example for the introduction of the thesis.
		
		//example of signed graph for the thesis.
		/*Graph graph = new Graph();
		for(int i = 0; i < 5; i++){
			graph.addVertex(i, Color.BLACK);
			graph.addVertex(i + 5, Color.GRAY);
			graph.addVertex(i + 10, Color.BLUE);
			graph.addVertex(i + 15, Color.GREEN);
		}
		for(int i = 20 ; i < 35; i++){
			graph.addVertex(i, Color.WHITE);
		}
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 5; j++){
				for(int k = j + 1; k < 5; k++){
					graph.addEdge(new EdgeType(), (i * 5) + j ,(i * 5) + k);
				}
			}
		}
		for(int i = 0; i < 5; i = i + 3){
			graph.addEdge(new EdgeType(), i, i + 5);
		}
		for(int i = 0; i < 5; i = i + 2){
			graph.addEdge(new EdgeType(false), i, i + 10);
			graph.addEdge(new EdgeType(false), i + 5, i + 10);
		}
		System.out.println("edges = " + graph.getGraph().getEdgeCount());
		System.out.println( graph.getGraph().getVertexCount());
		simulator.addPeerInit(new FromGraphSigned(graph));
		//simulator.addPeerInit(new FromGraph(graph));
		//simulator.addPeerInit(new FromGraphDual(graph));
		for (int i = 0; i < graph.getGraph().getVertexCount(); i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}
		System.out.println( "simulator size = " + simulator.getPeers().size());*/
		//end of the example of signed graph for the thesis.
		
		//4 clusters signed linlog model example
	/*	List<ClusterProperties> clusterList = new ArrayList<ClusterProperties>();
		List<InterClusterProperties> interClusterList = new ArrayList<InterClusterProperties>();
		ClusterProperties cluster1 = new ClusterProperties(0.00,0.0,1000);
		clusterList.add(cluster1);
		ClusterProperties cluster2 = new ClusterProperties(0.90,0.0,100);
		clusterList.add(cluster2);
		ClusterProperties cluster3 = new ClusterProperties(0.90,0.0,100);
		clusterList.add(cluster3);
		ClusterProperties cluster4 = new ClusterProperties(0.90,0.0,100);
		clusterList.add(cluster4);
		ClusterProperties cluster5 = new ClusterProperties(0.90,0.0,50);
		clusterList.add(cluster5);
		
		
		InterClusterProperties cluster12 = new InterClusterProperties(cluster1,cluster2,0.00,0.0);
		interClusterList.add(cluster12);
		InterClusterProperties cluster13 = new InterClusterProperties(cluster1,cluster3,0.00,0.0);
		interClusterList.add(cluster13);
		InterClusterProperties cluster14 = new InterClusterProperties(cluster1,cluster4,0.00,0.0);
		interClusterList.add(cluster14);
		InterClusterProperties cluster23 = new InterClusterProperties(cluster2,cluster3,0.00,0.0);
		interClusterList.add(cluster23);
		InterClusterProperties cluster24 = new InterClusterProperties(cluster2,cluster4,0.00,0.0);
		interClusterList.add(cluster24);
		InterClusterProperties cluster34 = new InterClusterProperties(cluster3,cluster4,0.00,0.0);
		interClusterList.add(cluster34);
		
		GraphCluster graphCluster = new GraphCluster(clusterList, interClusterList);
		Graph graph = graphCluster.cluster();
		
		simulator.addPeerInit(new FromGraphSigned(graph));
		//simulator.addPeerInit(new FromGraph(graph));
		//simulator.addPeerInit(new FromGraphDual(graph));
		for (int i = 0; i < graph.getGraph().getVertexCount(); i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}
		
		//for (CoordinatesPeer p : simulator.getPeers()) {
			//p.setNeutralNeighbors(simulator.getPeers());
			//p.setRepulseNeighbors(simulator.getPeers());
			//p.setAttractNeighbors(simulator.getPeers());
			//System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			//System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			//System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		//}
*/		//End of 4 cluster signed linlog model example
		
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
		Graph graph = new Graph();
		BufferedReader buf;
		try {
			buf = new BufferedReader(new FileReader("graph.txt"));
			String line = buf.readLine();
			Scanner scanner = new Scanner(line);
			int nbVertices = scanner.nextInt();
			System.out.println("number of vertices = " + nbVertices );
			for(int i=1; i <= nbVertices; i++){
				graph.addVertex(i);
			}
			
			while((line = buf.readLine()) != null)
			{
				scanner = new Scanner(line);
				int v1 = scanner.nextInt();
				int v2 = scanner.nextInt();
				int weight = scanner.nextInt();
				if(weight < 0) 
				{
					EdgeType edge = new EdgeType(false);
					edge.setColor(Color.RED);
					graph.addEdge(edge , v1, v2);
				}
				else 
				{
					EdgeType edge = new EdgeType(true);
					edge.setColor(Color.GREEN);
					graph.addEdge(edge , v1, v2);
				}
			}
			scanner.close();
			buf.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (IOException e1) {
			e1.printStackTrace();
		}
	
		simulator.addPeerInit(new FromGraphSigned(graph));
		
		List<CoordinatesPeer> tribes = new ArrayList<CoordinatesPeer>();
		for(VertexType v : graph.getGraph().getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(v.getVertexNum());
			tribes.add(coPeer);
			simulator.addPeer(coPeer);
		}
		
		/*Graph graph = new Graph();
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
		for(VertexType v : graph.getGraph().getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(v.getVertexNum());
			tribes.add(coPeer);
			simulator.addPeer(coPeer);
		}
		//for(CoordinatesPeer p: tribes){
			//p.setNeutralNeighbors(tribes);
			//System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			//System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			//System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
		//}
*/		//end of the example of African tribes.
		
		//balance theory example
		/*Graph graph = new Graph();
		for(int i = 0; i < 5; i++){
			graph.addVertex(i, Color.BLACK);
			graph.addVertex(i + 5, Color.BLUE);
			graph.addVertex(i + 10, Color.GREEN);
		}
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 5; j++){
				for(int k = j + 1; k < 5; k++){
					graph.addEdge(new EdgeType(), (i * 5) + j, (i * 5) + k);
				}
			}
		}
		
		// +++
		for(int i = 0; i < 5; i += 3){
			graph.addEdge(new EdgeType(), i, i + 5);
			graph.addEdge(new EdgeType(false), i, i + 10);
			graph.addEdge(new EdgeType(false), i + 5, i + 10);
		}
		
		simulator.addPeerInit(new FromGraphSigned(graph));
		for (int i = 0; i < graph.getGraph().getVertexCount(); i++) {
			simulator.addPeer(new CoordinatesPeer(i));
		}*/
		//end of balance theory example
		
		//user/item signed unipartite example
//		List<ClusterProperties> clusterList = new ArrayList<ClusterProperties>();
//		List<InterClusterProperties> interClusterList = new ArrayList<InterClusterProperties>();
//		ClusterProperties cluster1 = new ClusterProperties(0.0,0.0,10);
//		clusterList.add(cluster1);
//		ClusterProperties cluster2 = new ClusterProperties(0.0,0.0,10);
//		clusterList.add(cluster2);
//		ClusterProperties cluster3 = new ClusterProperties(0.0,0.0,10);
//		clusterList.add(cluster3);
//		ClusterProperties cluster4 = new ClusterProperties(0.0,0.0,10);
//		clusterList.add(cluster4);
//		
//		InterClusterProperties cluster12 = new InterClusterProperties(cluster1,cluster2,0.0,0.0);
//		interClusterList.add(cluster12);
//		InterClusterProperties cluster13 = new InterClusterProperties(cluster1,cluster3,0.7,0.0);
//		interClusterList.add(cluster13);
//		InterClusterProperties cluster14 = new InterClusterProperties(cluster1,cluster4,0.0,0.0);
//		interClusterList.add(cluster14);
//		InterClusterProperties cluster23 = new InterClusterProperties(cluster2,cluster3,0.0,0.0);
//		interClusterList.add(cluster23);
//		InterClusterProperties cluster24 = new InterClusterProperties(cluster2,cluster4,0.7,0.0);
//		interClusterList.add(cluster24);
//		InterClusterProperties cluster34 = new InterClusterProperties(cluster3,cluster4,0.0,0.0);
//		interClusterList.add(cluster34);
//		
//		GraphCluster graphCluster = new GraphCluster(clusterList, interClusterList);
//		UndirectedSparseGraph<Integer , EdgeType> graph = (UndirectedSparseGraph<Integer, EdgeType>) graphCluster.cluster();
//		simulator.addPeerInit(new FromGraphSigned(graph));
//		for (int i = 0; i < graph.getVertexCount(); i++) {
//			simulator.addPeer(new CoordinatesPeer(i));
//		}	
//		for (CoordinatesPeer p : simulator.getPeers()) {
//			p.setNeutralNeighbors(simulator.getPeers());
//			System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
//			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
//			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());
//		}
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
		
		
		
		
		long bhStart;
		long bhEnd;
		long cycleEnd;
		long bhTime = 0;
		long cycleTime = 0;
		long total = 0;
		for (int i = 0; i < nbCycles; i++) {
			bhStart = System.currentTimeMillis();
			//Barnes and Hut algorithm.
			double[] margins = BHTree.getMargins(simulator.getPeers());
			Quad initialQuad = new Quad(margins[0], margins[1], margins[2], margins[3]);
			BHTree.NODE_ID = 0;
			BHTree bhTree = new BHTree(initialQuad);
			for(CoordinatesPeer p : simulator.getPeers()){
				bhTree.insert(p);
			}
			SignedLinLogForce.BHMode = true;
			SignedLinLogForce.setBhTree(bhTree);
			LinLogForce.BHMode = true;
			LinLogForce.setBhTree(bhTree);
			SignedNewForce1.BHMode = true;
			SignedNewForce1.setBhTree(bhTree);
			SignedNewForce3.BHMode = true;
			SignedNewForce3.setBhTree(bhTree);
			SignedFruchForce.BHMode = true;
			SignedFruchForce.setBhTree(bhTree);
			SignedDavidsonForce.BHMode = true;
			SignedDavidsonForce.setBhTree(bhTree);
			//end of Barnes and Hut algorithm.
			
			bhEnd = System.currentTimeMillis();
			bhTime += (bhEnd - bhStart);
			simulator.cycle();
			cycleEnd = System.currentTimeMillis();
			cycleTime += (cycleEnd - bhEnd);
			total += (cycleEnd - bhStart);
			
			/*GraphVisualizer gv = new GraphVisualizer(graph, simulator.getPeers(),null,scale);
			if(i % 200 == 0){
				JPanel frame = gv.visualize(i,0);
			}*/
		}
		
		//balance theory calculation
		/*List<CoordinatesPeer> peers = simulator.getPeers();
		double arithdist12 = 0.0;
		double arithdist13 = 0.0;
		double arithdist23 = 0.0;
		for(int i = 0; i < 5; i++){
			CartesianCoordinate coor1 = peers.get(i).getCoordinate();
			CartesianCoordinate coor2aux = peers.get(i + 5).getCoordinate();
			for(int j = 0; j < 5; j++){
				CartesianCoordinate coor2 = peers.get(j + 5).getCoordinate();
				double distance = coor1.distance(coor2);
				arithdist12 += distance;
				CartesianCoordinate coor3 = peers.get(j + 10).getCoordinate();
				distance = coor1.distance(coor3);
				arithdist13 += distance;
				distance = coor3.distance(coor2aux);
				arithdist23 += distance;
			}
		}
		DecimalFormat decFormat = new DecimalFormat("#.##");
		System.out.println("distance 1 and 2 = " + decFormat.format(arithdist12 / 25.0));
		System.out.println("distance 1 and 3 = " + decFormat.format(arithdist13 / 25.0));
		System.out.println("distance 2 and 3 = " + decFormat.format(arithdist23 / 25.0));
		//end of balance theory calculation.
		int k = 10;
		List<Integer> distribution = new ArrayList<Integer>(k);
		for(int i = 0; i < k; i++){
			distribution.add(0);
		}
		double minDist = Double.MAX_VALUE;
		double maxDist = 0.0;
		List<Double> distList = new ArrayList<Double>();
		for(int i = 0; i < simulator.getPeers().size(); i++){
			for (int j = i + 1; j < simulator.getPeers().size(); j++){
				double dist = simulator.getPeers().get(i).getCoordinate().distance(
						simulator.getPeers().get(j).getCoordinate());
				if(dist < minDist){
					minDist = dist;
				}
				if(dist > maxDist){
					maxDist = dist;
				}
				distList.add(dist);
			}
		}
		double step = (maxDist - minDist)/k;
		System.out.println("size of distList = " + distList.size());
		double mean = 0.0;
		for(Double dist : distList){
			mean += dist;
			int index = (int) ((dist - minDist) / step);
			if(dist == maxDist){
				index -= 1;;
			}
			distribution.set(index, distribution.get(index)+1);
		}
		for(int i = 0; i < distribution.size(); i++){
			//System.out.println(i + "\t" + distribution.get(i));
		}
		//System.out.println("min distance = " + "\t" + minDist);
		//System.out.println("max distance = " + "\t" + maxDist);
		//System.out.println("mean distance = " + "\t" + mean / distList.size());
		DecimalFormat df = new DecimalFormat("#.##");
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter("distribution.txt"));
			for(int i = 0; i < distribution.size(); i++){
				bf.write(df.format((step)*(i)+ minDist + (step/2.0)) + "\t" + distribution.get(i) + "\n");
			}
			bf.flush();
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		
		
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
		
		//visualizing the graph.
		GraphVisualizer gv = new GraphVisualizer(graph, simulator.getPeers(),null,scale);
		gv.visualize(nbCycles,0);
		//end of visualization.
		
		//writing image into the file.
		/*BufferedImage image = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		File file = new File("image.png");

		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			System.out.println("File could not be saved");
			e.printStackTrace();
		}*/
		//end of writing image into the file.
	}
}
