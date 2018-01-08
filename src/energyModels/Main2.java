package energyModels;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import threadedSim.ISimulatorTask;
import threadedSim.Simulator;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import energyModels.coord.CartesianCoordinate;
import energyModels.data.voter.Voter;
import energyModels.data.voter.VoterDataProvider;
import energyModels.force.apply.ForceApply;
import energyModels.force.compute.LinLogForce;
import energyModels.force.compute.SignedFruchForce;
import energyModels.force.compute.SignedLinLogForce;
import energyModels.force.compute.SignedNewForce1;
import energyModels.force.compute.SignedNewForce2;
import energyModels.force.compute.SignedNewForce3;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.GraphVisualizer;
import energyModels.graph.VertexType;
import energyModels.graph.buildGraph.BuildGraphFromVoters;
import energyModels.graph.buildGraph.IBuildGraph;
import energyModels.peer.CoordinatesPeer;
import energyModels.peerInit.FromGraph;
import energyModels.peerInit.FromGraphSigned;
import energyModels.tree.BHTree;
import energyModels.tree.Quad;

@SuppressWarnings("unused")
public class Main2 {
	public static int scale = 550;
	public static double posEdgeScale = 2;
	public static double negEdgeScale = 9;
	public static double neutralEdgeScale = 1;
	public static int nbCycles = 200;
	public static int nbUsers = 132000;
	public static int k = 20;
	//number of dimensions is read from main.java of energy models.
	
	public static void main(String[] args) {
		/*nbCycles = Integer.parseInt(args[0]);
		//int k = Integer.parseInt(args[1]);
		posEdgeScale = Double.parseDouble(args[1]); 
		negEdgeScale = Double.parseDouble(args[2]);
		String fileName = args[3];
		scale = Integer.parseInt(args[4]);*/
		String fileName = "mesh.mtx";
		Simulator<CoordinatesPeer> simulator = new Simulator<CoordinatesPeer>();
		ISimulatorTask<CoordinatesPeer> task = new SignedLinLogForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedFruchForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce1(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce3(2,1,1);
		//ISimulatorTask<CoordinatesPeer> task = new LinLogForce();
		simulator.addTask(task);
		simulator.addTask(new ForceApply());
		
		//nbUsers = Integer.parseInt(args[0]);
		VoterDataProvider dataProvider = new VoterDataProvider(nbUsers);
		//dataProvider.fetchInformation();
		dataProvider.fetchVotersFromFile(fileName);
		
		//Collection<Voter> voters = dataProvider.getVoters().values();
		List<Voter> voters = new ArrayList<Voter>(Voter.factory.values());
		
		//omitting conflicting votes.
		//dataProvider.resolveConflicts();
		dataProvider.resolveConflicts();
				
		
		Graph graph = new Graph();
		for(Voter v: voters){
			graph.addVertex(v.getPeerId());
		}
		
		//creating the initial empty graph.
		IBuildGraph<Voter> bg = new BuildGraphFromVoters(graph);
		
		//adding vertices to the graph.
		int counter = 0;
		for(Voter v: voters){
			bg.addPeerToGraph(v);
			counter++;
			if(counter % 10000 == 0){
				System.out.println("I added " + counter + " user profiles to the graph");
			}
		}
		System.out.println("I added " + counter + " user profiles to the graph");
		
		//Removing vertices with few positive incident edges.
		/*List<Integer> vertices = new ArrayList<Integer>(graph.getVertices());
		int removed = 0;
		for(Integer node : vertices){
			int nbPosIncidentEdges = 0;
			for(EdgeType edge : graph.getIncidentEdges(node)){
				if(edge.isEdgeType()){
					nbPosIncidentEdges++;
				}
			}
			if (nbPosIncidentEdges < k){
				graph.removeVertex(node);
				removed++;
			}
		}
		System.out.println("I removed " + removed + " users from the graph");*/
		//end of removing vertices with few positive incident edges.
		
		//checking graph disconnectivity.
		/*UnweightedShortestPath<Integer, EdgeType> path = new UnweightedShortestPath<Integer, EdgeType>(graph);
		for(Integer vertex : graph.getVertices()){
			for(Integer v: graph.getVertices()){
				if(path.getDistance(vertex, v) == null){
					throw new RuntimeException("The graph is disconnected!");
				}
			}
		}
		System.out.println("The graph is connected!");*/
		//end of checking graph disconnectivity.
		
		simulator.addPeerInit(new FromGraphSigned(graph));
		for(VertexType voter: graph.getGraph().getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(voter.getVertexNum());
			simulator.addPeer(coPeer);
		}
				
		//for (CoordinatesPeer p : simulator.getPeers()) {
			//p.setNeutralNeighbors(simulator.getPeers());
			/*System.out.println("attract neighbors: "+p.getAttractNeighbors().size());
			System.out.println("repulse neighbors: "+p.getRepulseNeighbors().size());
			System.out.println("neutral neighbors: "+p.getNeutralNeighbors().size());*/
		//}
		System.out.println("Number of positive edges = " + BuildGraphFromVoters.nbPosEdges);
		System.out.println("Number of negative edges = " + BuildGraphFromVoters.nbNegEdges);
		long bhStart;
		long bhEnd;
		long cycleEnd;
		long bhTime = 0;
		long cycleTime = 0;
		long total = 0;
		for (int i = 0; i < nbCycles ; i++) {
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
			//LinLogForce.BHMode = true;
			//LinLogForce.setBhTree(bhTree);
			/*SignedNewForce1.BHMode = true;
			SignedNewForce1.setBhTree(bhTree);
			SignedNewForce3.BHMode = true;
			SignedNewForce3.setBhTree(bhTree);*/
			bhEnd = System.currentTimeMillis();
			bhTime += (bhEnd - bhStart);
			simulator.cycle();
			cycleEnd = System.currentTimeMillis();
			cycleTime += (cycleEnd - bhEnd);
			total += (cycleEnd - bhStart);
			
			GraphVisualizer gv = new GraphVisualizer(graph, simulator.getPeers(),null,scale);
			if(i % 200 == 0){
				JPanel frame = gv.visualize(i,0);
			//}
		//}
		
		//mixing two models for the multi-scale model.
		/*Simulator<CoordinatesPeer> simulator2 = new Simulator<CoordinatesPeer>();
		//ISimulatorTask<CoordinatesPeer> task2 = new SignedLinLogForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task2 = new SignedNewForce1(posEdgeScale,negEdgeScale,neutralEdgeScale);
		ISimulatorTask<CoordinatesPeer> task2 = new SignedNewForce3(2,1,1);
		//ISimulatorTask<CoordinatesPeer> task2 = new LinLogForce();
		simulator2.addTask(task2);
		simulator2.addTask(new ForceApply());
		//No peer Initializer like FromGraphSigned() must be added since the peers are already initialized by the previous simulator.
		for(CoordinatesPeer coPeer: simulator.getPeers()){
			simulator2.addPeer(coPeer);
		}
		int nbCycles2 = 10;
		for(int i=0; i< nbCycles2; i++){
			simulator2.cycle();
		}*/
		//end of mixing two models for the multi-scale model.
		
		try {
			BufferedWriter out = new BufferedWriter (new FileWriter(fileName + i+".txt", false));
			Date todaysDate = new java.util.Date();
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
			String formattedDate = formatter.format(todaysDate);
			
			out.write("# Execution time = " + formattedDate);
			out.write("\n# BH tree formation mean time = " + (bhTime * 0.001)/ (i+1));
			out.write("\n# Execution cycle mean time = " + (cycleTime * 0.001)/ (i+1));
			out.write("\n# Total execution mean time = " + (total * 0.001)/ (i+1));
			out.write("\n# Data type = " + bg.getClass().getSimpleName());
			out.write("\n# Force Model = " + task.getClass().getSimpleName());
			out.write("\n# Number of Coordinates = " + CartesianCoordinate.nbDimension);
			out.write("\n# Number of users = " + simulator.getPeers().size());
			out.write("\n# Number of cycles = " + (i+1));
			out.write("\n# PosEdgeScale = " + posEdgeScale);
			out.write("\n# NegEdgeScale = " + negEdgeScale);
			out.write("\n# NeutralEdgeScale = " + neutralEdgeScale);
			out.write("\n# Scale = " + scale);
						
			for (CoordinatesPeer p : simulator.getPeers()){
				//System.out.println(p.getCoordinate());
					out.write("\n"+p.getPeerId() + " " +p.getCoordinate().toString());
					out.flush();
			}
			out.write("\n\n");
			out.flush();
			System.out.println("Number of vertices in the simulator = " + simulator.getPeers().size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	}
		dataProvider.clear();
		bg.clear();
		//visualizing the graph.
		GraphVisualizer gv = new GraphVisualizer(graph, simulator.getPeers(),null, scale);
		JPanel frame = gv.visualize(nbCycles,0);
		
		//writing the graph image into the file.
		/*BufferedImage image = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		frame.paint(g2);
		File file = new File("C:\\Documents and Settings\\amoin\\Mes documents\\Energy models\\signedEnergyModels\\pictures\\image" + posEdgeScale+negEdgeScale+neutralEdgeScale+"." + "jpg");
		try {
			ImageIO.write(image, "jpg", file);
		} catch (IOException e) {
			System.out.println("File could not be saved");
			e.printStackTrace();
		}*/
		//end of visualization.
	}

}
