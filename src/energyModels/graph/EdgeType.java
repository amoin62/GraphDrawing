package energyModels.graph;

import java.awt.Color;

public class EdgeType {
	//true stands for positive edges and false for negative edges.
	private final static String NO_NAME = "NO_NAME";
	//private final static Color posEdgeColor = Color.GREEN.darker().darker();
	private final static Color posEdgeColor = Color.GRAY.darker();
	private final static Color negEdgeColor = Color.RED.brighter();
	private boolean edgeType;
	private Color color;
	private String edgeString;
	private double weight;
	
	public EdgeType(){
		super();
		this.edgeType = true;
		this.color = posEdgeColor;
		this.edgeString = NO_NAME;
		this.weight = 1;
	}
	public EdgeType(boolean edgeType) {
		super();
		this.edgeString = NO_NAME;
		this.edgeType = edgeType;
		this.weight = 1;
		if(edgeType){
			this.color = posEdgeColor;
		}else{
			this.color = negEdgeColor;
		}
	}
	public EdgeType(double weight) {
		this();
		this.weight = weight;
	}
	
	public EdgeType(boolean edgeType, double weight) {
		super();
		this.edgeString = NO_NAME;
		this.edgeType = edgeType;
		this.weight = weight;
		if(edgeType){
			this.color = posEdgeColor;
		}else{
			this.color = negEdgeColor;
		}
	}

	public String getEdgeString() {
		return edgeString;
	}
	public void setEdgeString(String edgeString) {
		this.edgeString = edgeString;
	}
	
	public boolean isEdgeType() {
		return edgeType;
	}

	public void setEdgeType(boolean edgeType) {
		this.edgeType = edgeType;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color edgeColor) {
		this.color = edgeColor;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
