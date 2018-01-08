package energyModels;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import no.uib.cipr.matrix.Matrix;

import spectralGD.Spec;
import threadedSim.ISimulatorTask;
import threadedSim.Simulator;
import energyModels.coord.CartesianCoordinate;
import energyModels.data.voter.Voter;
import energyModels.data.voter.VoterDataProvider;
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
import energyModels.graph.GraphVisualizer;
import energyModels.graph.VertexType;
import energyModels.graph.buildGraph.BuildGraphFromVoters;
import energyModels.graph.buildGraph.IBuildGraph;
import energyModels.peer.CoordinatesPeer;
import energyModels.peerInit.FromGraphSigned;
import energyModels.tree.BHTree;
import energyModels.tree.MultiLevelGraph;
import energyModels.tree.Quad;

public class MultiLevelMain2 {

	public static double scale = 150;
	public static double posEdgeScale = 3;
	public static double negEdgeScale = 3;
	public static double neutralEdgeScale = 1;
	public static int nbUsers = 132000;
	public static double tolerance = 1.01;
	//static double def = 15.0;
		
	//number of dimensions is read from main.java of energy models.
	public static void main(String[] args) {
		posEdgeScale = Double.parseDouble(args[0]); 
		negEdgeScale = Double.parseDouble(args[1]);
		String fileName = args[2];
		scale = Integer.parseInt(args[3]);
		tolerance = Double.parseDouble(args[4]);
		
		//String fileName = "coater1.mtx";
		//String fileName = "s1rmq4m1.mtx";
		//String fileName = "G49.mtx";
		//String fileName = "flower_8_1.mtx";
		//String fileName = "mesh.mtx";
		//String fileName = "1138_bus.mtx";
		//String fileName = "wiki-Vote.mtx";
		//String fileName = "jagmesh8.mtx";
		//String fileName = "jagmesh1.mtx";
		//String fileName = "cavity04.mtx";
		//String fileName = "finance256.mtx";
		//String fileName = "pkustk01.mtx";
		//String fileName = "blckhole.mtx";
		//String fileName = "plskz362.mtx";
		//String fileName = "poli3.mtx";
		//String fileName = "bcsstm07.mtx";
		//String fileName = "dwt_1005.mtx";
		//String fileName = "Franz3.mtx";
		//String fileName = "Harvard500.mtx";
		//String fileName = "California.mtx";
		//String fileName = "EVA.mtx";
		//String fileName = "rdist3a.mtx";
		//String fileName = "rajat22.mtx";
		//String fileName = "utm1700b.mtx";
		//String fileName = "tube2.mtx";
		//String fileName = "utm3060.mtx";
		//String fileName = "bbmat.mtx";
		//String fileName = "shyy41.mtx";
		//String fileName = "3D_28984_Tetra.mtx";
		//String fileName = "mesh2e1.mtx";
		//String fileName = "G12.mtx";
		//String fileName = "can_229.mtx";
		//String fileName = "can_61.mtx";
		//String fileName = "can_838.mtx";
		//String fileName = "cegb3024.mtx";
		//String fileName = "nasa1824.mtx";
		//String fileName = "kaufhold.mtx";
		//String fileName = "crack.mtx";
		//String fileName = "Alemdar.mtx";
		//String fileName = "nnc666.mtx";
		//String fileName = "can_292.mtx";
		//String fileName = "bcsstk24.mtx";
		//String fileName = "nasa2146.mtx";
		//String fileName = "shuttle_eddy.mtx";
		//String fileName = "skirt.mtx";
		//String fileName = "cavity17.mtx";
		//String fileName = "cavity24.mtx";
		//String fileName = "cavity06.mtx";
		//String fileName = "cavity01.mtx";
		//String fileName = "tsyl201.mtx";
		//String fileName = "thermal.mtx";
		//String fileName = "nasa4704.mtx";
		//String fileName = "nasa2910.mtx";
		//String fileName = "crystk01.mtx";
		//String fileName = "rbsa480.mtx";
		//String fileName = "bfwb398.mtx";
		//String fileName = "L.mtx";
		//String fileName = "raefsky5.mtx";
		//String fileName = "bcsstk31.mtx";
		//String fileName = "vibrobox.mtx";
		//String fileName = "uk.mtx";
		//String fileName = "3elt.mtx";
		//String fileName = "airfoil1.mtx";
		//String fileName = "stufe.mtx";
		//String fileName = "ukerbe1.mtx";
		//String fileName = "fxm3_6.mtx";
		//String fileName = "barth5.mtx";
		//String fileName = "lock1074.mtx";
		//String fileName = "dw256A.mtx";
		//String fileName = "bcsstm36.mtx";
		//String fileName = "pkustk12.mtx";
		//String fileName = "pkustk09.mtx";
		//String fileName = "fe_4elt2.mtx";
		//String fileName = "fe_ocean.mtx";
		//String fileName = "G34.mtx";
				
		VoterDataProvider dataProvider = new VoterDataProvider(nbUsers);
		dataProvider.fetchVotersFromFile(fileName);
		
		//Collection<Voter> voters = dataProvider.getVoters().values();
		List<Voter> voters = new ArrayList<Voter>(Voter.factory.values());
		
		//omitting conflicting votes.
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
		
		/*Set<VertexType> dis = new HashSet<VertexType>();
		for(VertexType v: graph.getGraph().getVertices()){
			if(graph.getGraph().getNeighborCount(v) == 0){
				dis.add(v);
			}
		}
		for(VertexType v : dis){
			graph.getGraph().removeVertex(v);
		}*/
		
		System.out.println("I added " + counter + " user profiles to the graph");
		dataProvider.clear();
		
		
		/*posEdgeScale = (graph.getGraph().getVertexCount() * graph.getGraph().getVertexCount()) 
		/ (graph.getGraph().getEdgeCount());*/
		
		//Grid graph example.
		/*GridGraph grid = new GridGraph();
		graph = grid.buildGridGraph(10,10);*/
		//end of grid graph example.
		
		
		//ISimulatorTask<CoordinatesPeer> task = new DualLinLogForce();
		//ISimulatorTask<CoordinatesPeer> task = new HCForce(posEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new DavidsonForce(posEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new LinLogForce(posEdgeScale,neutralEdgeScale);
		ISimulatorTask<CoordinatesPeer> task = new FruchForce(posEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedLinLogForce(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce1(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedNewForce3(posEdgeScale,negEdgeScale,neutralEdgeScale);
		//ISimulatorTask<CoordinatesPeer> task = new SignedHCForce(posEdgeScale,negEdgeScale, neutralEdgeScale);
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
		
		/*Spec spec = new Spec();
		System.out.println("number of edges = " + graph.getGraph().getEdgeCount());
		Matrix m = spec.buildMatrixFromGraph(graph);
		graph = spec.buildGraphFromMatrix(m);
		System.out.println("number of edges = " + graph.getGraph().getEdgeCount());*/
		
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
				multi.HEC();
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
		
		/*double vertexCount = coarsest.getGraph().getVertexCount();
		double edgeCount = coarsest.getGraph().getEdgeCount();
		double step = (vertexCount * vertexCount) 
		/ (((posEdgeScale * edgeCount ) + (vertexCount * vertexCount))*posEdgeScale);*/
		
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
			
			/*vertexCount = currentMulti.getFineGraph().getGraph().getVertexCount();
			edgeCount = currentMulti.getFineGraph().getGraph().getEdgeCount();
			step = (vertexCount * vertexCount) 
			/ (((posEdgeScale * edgeCount ) + (vertexCount * vertexCount))*posEdgeScale);*/
			
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
			draw(simulator, currentMulti.getFineGraph(), task, fileName+(multiList.size()-j - 1)+"-",bg, multiList.size()-level);
			level++;
			coordinatesMap.clear();
			for(CoordinatesPeer peer : simulator.getPeers()){
				coordinatesMap.put(currentMulti.getFineGraph().getVertex(peer.getPeerId()), peer.getCoordinate());
			}
		}
		long endTime = (long) (System.currentTimeMillis()*0.001);
		System.out.println("Total Execution Time = " + (endTime - startTime));
		bg.clear();
		simulator.clear();
	}
	
	public static void draw(Simulator<CoordinatesPeer> simulator, Graph currentGraph, 
			ISimulatorTask<CoordinatesPeer> task, String fileName, IBuildGraph<Voter> bg, int level){
		//parameter estimation
		double vertexCount = currentGraph.getGraph().getVertexCount();
		double edgeCount = currentGraph.getGraph().getEdgeCount();
		double edgeWeight = 0.0;
		for(EdgeType e : currentGraph.getGraph().getEdges()){
			edgeWeight += e.getWeight();
		}
		//signedLinLog param
	/*	double param = (vertexCount * vertexCount) 
		/ (((posEdgeScale * edgeCount ) + (vertexCount * vertexCount))*posEdgeScale * edgeCount * 0.5);
		*/
		
		//linlog param
		/*double param = (vertexCount * vertexCount) 
		/ (edgeCount * edgeCount * 0.5);*/
				
		
		/*double param = (vertexCount * vertexCount) 
		/ (((edgeCount ) + (vertexCount * vertexCount - edgeCount))*posEdgeScale * edgeCount * 0.5);*/
		
		/*CartesianCoordinate.minMoveDistance = param;
		CartesianCoordinate.maxMoveDistance = edgeCount * 0.5 * posEdgeScale * param;
		CartesianCoordinate.defaultMoveDist = edgeCount * 0.5 * param;*/
		
		/*CartesianCoordinate.defaultMoveDist = def;
		CartesianCoordinate.maxMoveDistance = 2*def;
		CartesianCoordinate.minMoveDistance = 0.1*def;
		def *= 1.0;*/
		
		long bhStart;
		long bhEnd;
		long cycleEnd;
		long bhTime = 0;
		long cycleTime = 0;
		long total = 0;
				
		double ratio = 2.0;
		double prevTotalDis = 0.00000001;
		double maxDis = Double.POSITIVE_INFINITY;
		int counter = 0;
		long startExec = (long) (System.currentTimeMillis()*0.001);
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
			prevTotalDis = totalDis;
			//totalDis /= simulator.getPeers().size();
			counter++;
			cycleEnd = System.currentTimeMillis();
			cycleTime += (cycleEnd - bhEnd);
			total += (cycleEnd - bhStart);
			
			GraphVisualizer gv = new GraphVisualizer(currentGraph, simulator.getPeers(),null,scale);
			if((counter % 100 == 0) && (level < 3)){
				gv.visualize(counter,level);
			}
		}
		long endExec = (long) (System.currentTimeMillis()*0.001);
		try {
			BufferedWriter out = new BufferedWriter (new FileWriter(fileName + counter+".txt", false));
			Date todaysDate = new java.util.Date();
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
			String formattedDate = formatter.format(todaysDate);
			
			out.write("# Execution date = " + formattedDate);
			out.write("\n# Execution Time = " + (endExec - startExec));
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
		}
		//if(level < 3){
			GraphVisualizer gv = new GraphVisualizer(currentGraph, simulator.getPeers(),null, scale);
			gv.visualize(counter,level);
		//}
	}
}
