package energyModels.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;

public class GraphVisualizer {
	private static double zoom;
	private static final int xOffset = 350;
	private static final int yOffset = 300;
	private energyModels.graph.Graph graph;
	private List<CoordinatesPeer> coordinateList;
	private List<ClusterProperties> clusters;
	private Map<Integer, String> tribes;
		
	public GraphVisualizer(energyModels.graph.Graph graph,
			List<CoordinatesPeer> coordinatesList, List<ClusterProperties> clusters, double zoom) {
		super();
		this.graph = graph;
		this.coordinateList = coordinatesList;
		this.clusters = clusters;
		GraphVisualizer.zoom = zoom;
		this.tribes = new HashMap<Integer, String>();
	}
	
	private void giveNodeNames(String fileName)
	{
		try {
			BufferedReader bf = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = bf.readLine())!= null)
			{
				Scanner scanner = new Scanner(line);
				int vertex = scanner.nextInt();
				String name = scanner.next();
				tribes.put(vertex, name);
				scanner.close();
			}
			bf.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + fileName + " was not found. Vertex names are not read from the file.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void giveTribeNames(){
		tribes.put(1, "Alikadzuha");
		tribes.put(2, "Asarodzuha");
		tribes.put(3, "Gahuka");
		tribes.put(4, "Gama");
		tribes.put(5, "Gaveve");
		tribes.put(6, "Gehamo");
		tribes.put(7, "Kohika");
		tribes.put(8, "Kotuni");
		tribes.put(9, "Masilakidzuha");
		tribes.put(10, "Nagamidzuha");
		tribes.put(11, "Nagamiza");
		tribes.put(12, "Notohana");
		tribes.put(13, "Ove");
		tribes.put(14, "Seu've");
		tribes.put(15, "Uheto");
		tribes.put(16, "Ukudzuha");
	}
	
	public JPanel visualize(int fileSuffix, int level){
		Layout<VertexType,EdgeType> layout = new LayoutGenerator(graph, coordinateList);
		layout.setSize(new Dimension(1500,1500));
		/*BasicVisualizationServer<Integer, EdgeType> bvs = 
			new BasicVisualizationServer<Integer, EdgeType>(layout);*/
		BasicVisualizationServer<VertexType, EdgeType> bvs = 
			new VisualizationImageServer<VertexType, EdgeType>(layout, new Dimension(700,700));
		bvs.setPreferredSize(new Dimension(1500,1500));
		bvs.setBackground(Color.WHITE);
		
		Transformer<VertexType, Paint> vertexClusterColorTransformer = new Transformer<VertexType, Paint>(){
			private List<Color> colors = new ArrayList<Color>();
			@Override
			public Paint transform(VertexType vertex) {
				colors.add(Color.BLACK);
				colors.add(Color.GRAY);
				colors.add(Color.BLUE);
				colors.add(Color.GREEN);
				colors.add(Color.WHITE);
				colors.add(Color.YELLOW);
				colors.add(Color.ORANGE);
				colors.add(Color.CYAN);
				colors.add(Color.MAGENTA);
				colors.add(Color.RED);
				//int nbOfClusters = clusters.size();
				Iterator<Color> iterator = this.colors.iterator();
				for(ClusterProperties cluster: clusters){
					final Color cl = iterator.next();
					if(vertex.getVertexNum()>=cluster.getFirstIndex()&&vertex.getVertexNum()<=cluster.getLastIndex()){
						//final Color cl= new Color(cluster.getClusterID()*(255/nbOfClusters),0,0); 
						return cl;
					}
				}
				return null;
			}
		};
		
		Transformer<VertexType, Paint> vertexColorTransformer = new Transformer<VertexType, Paint>() {
			@Override
			public Paint transform(VertexType vertex){
				return vertex.getColor();
				//return Color.BLACK.darker();
			}
		};
		Transformer<EdgeType,Paint> edgeColorTransformer = new Transformer<EdgeType, Paint>(){
			@Override
			public Paint transform(EdgeType edgeType) {
				return edgeType.getColor();
			}
		};
		
		Transformer<EdgeType, Stroke> edgeDashStrokeTransformer = new Transformer<EdgeType, Stroke>(){
			@Override
			public Stroke transform(EdgeType edgeType) {
				if(edgeType.isEdgeType()){
					Stroke posEdgeStroke = new BasicStroke(1.5f);
					return posEdgeStroke;
				}else{
					//float dash[] = {10.0f};
					//Stroke negEdgeStroke = new BasicStroke(0.1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10.0f, dash, 0.4f);
					Stroke negEdgeStroke = new BasicStroke(1.5f);
					return negEdgeStroke;
				}
			}
			
		};
		
		Transformer<EdgeType, Stroke> edgeStrokeTransformer = new Transformer<EdgeType, Stroke>(){
			@Override
			public Stroke transform(EdgeType edgeType) {
				return new BasicStroke(1.0f);
			}
			
		};
		
		Transformer<VertexType, Shape> vertexShape = new Transformer<VertexType, Shape>() {

			@Override
			public Shape transform(VertexType vertex) {
				Shape res = new Ellipse2D.Double(0,0,5.0,5.0);
				return res;
			}
		};
		
		Transformer<Context<Graph<VertexType, EdgeType>, EdgeType>, Shape> edgeShape = 
			new Transformer<Context<Graph<VertexType,EdgeType>,EdgeType>, Shape>() {
			@Override
			public Shape transform(Context<Graph<VertexType, EdgeType>, EdgeType> arg0) {
				return new EdgeShape.Line<VertexType, EdgeType>().transform(arg0);
			}
		};
		
		Transformer<VertexType, Stroke> vertexStrokeTransformer = new Transformer<VertexType, Stroke>() {

			@Override
			public Stroke transform(VertexType vertex) {
				return null;
			}
		};
		
		Transformer<VertexType, String> edgeLabelTransformer = new Transformer<VertexType, String>() {
			@Override
			public String transform(VertexType v) {
				return tribes.get(v.getVertexNum());
			}
		};
		if(clusters != null){
			bvs.getRenderContext().setVertexShapeTransformer(vertexShape);
			bvs.getRenderContext().setVertexStrokeTransformer(vertexStrokeTransformer);
			//bvs.getRenderer().setVertexRenderer(new vertexRenderer());
			bvs.getRenderContext().setVertexFillPaintTransformer(vertexClusterColorTransformer);
			bvs.getRenderContext().setEdgeShapeTransformer(edgeShape);
			bvs.getRenderContext().setEdgeDrawPaintTransformer(edgeColorTransformer);
			bvs.getRenderContext().setEdgeStrokeTransformer(edgeDashStrokeTransformer);
		}else{
			bvs.getRenderContext().setVertexShapeTransformer(vertexShape);
			//bvs.getRenderer().setVertexRenderer(new vertexRenderer());
			bvs.getRenderContext().setVertexFillPaintTransformer(vertexColorTransformer);
			//this.giveTribeNames();
			this.giveNodeNames("nodenames.txt");
			bvs.getRenderContext().setVertexLabelTransformer(edgeLabelTransformer);
			bvs.getRenderContext().setEdgeShapeTransformer(edgeShape);
			bvs.getRenderContext().setEdgeDrawPaintTransformer(edgeColorTransformer);
			bvs.getRenderContext().setEdgeStrokeTransformer(edgeDashStrokeTransformer);
		}
			
		/*JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(bvs);
		writeToImageFile("testImage.png", frame.getContentPane());
		frame.pack();*/
		JPanel panel = new JPanel();
		//panel.setDefaultCloseOperation();
		panel.add(bvs);
		writeToImageFile("image" + fileSuffix+ "level" + level + ".png", panel.getComponent(0));
		//panel.pack();
		panel.setVisible(true);
		return panel;
		
	}
	
	private void writeToImageFile(String imageFileName, Component jframe) {

		   BufferedImage bufImage = ScreenImage.createImage((JComponent) jframe);
		   try {
		       File outFile = new File(imageFileName);
		       ImageIO.write(bufImage, "png", outFile);
		       System.out.println("wrote image to " + imageFileName);
		   } catch (Exception e) {
		       System.out.println("writeToImageFile(): " + e.getMessage());
		   }
		}

	
	public static class LayoutGenerator extends AbstractLayout<VertexType, EdgeType>{
		private List<CoordinatesPeer> peerList;
		private energyModels.graph.Graph g;
		protected LayoutGenerator(energyModels.graph.Graph graph, List<CoordinatesPeer> peersList) {
			super(graph.getGraph());
			g = graph;
			peerList= peersList;
		}
			
		@Override
		public void initialize() {
			if(CartesianCoordinate.nbDimension == 2){
				locations.clear();
				for(CoordinatesPeer coPeer: peerList){
					double [] position = coPeer.getCoordinate().getPosition();
					Point2D.Double point= new Point2D.Double(position[0]*zoom+xOffset,position[1]*zoom+yOffset);
					locations.put(this.g.getVertex(coPeer.getPeerId()), point);
				}
				size = new Dimension(300,300);
				initialized = true;
			}else
			{
				throw new RuntimeException(this.getClass().getSimpleName()+ "\n"+
						"Layout could not be generated: " +
						"Number of dimensions is not equal to 2: "); 
			}
				
		}
			
			
		@Override
		public void reset() {
				
		}
	}
	
	static class vertexRenderer implements Renderer.Vertex<Integer, EdgeType>{

		@Override
		public void paintVertex(RenderContext<Integer, EdgeType> arg0,
				Layout<Integer, EdgeType> arg1, Integer arg2) {
			Shape shape = new Ellipse2D.Double(0,0,5.0,5.0);
			RenderContext<Integer, EdgeType> rc = arg0;
			GraphicsDecorator gd = rc.getGraphicsContext();
			gd.fill(shape);
		}

		
	}
	
	
}
