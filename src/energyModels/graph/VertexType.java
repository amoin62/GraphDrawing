package energyModels.graph;

import java.awt.Color;

public class VertexType {
	private static int idFactory = 0;
	private int identifier;
	private int vertexNum;
	private float mass;
	private boolean matched;
	private double degree;
	private boolean mivs;
	private Color color;
	

	public VertexType(int vertexNum, float mass) {
		super();
		this.vertexNum = vertexNum;
		this.mass = mass;
		this.matched = false;
		this.degree = 0.0;
		this.identifier = idFactory;
		this.mivs = false;
		this.color = Color.BLACK.darker();
		idFactory++;
	}
	
	public VertexType(int vertexNum) {
		super();
		this.vertexNum = vertexNum;
		this.mass = 1.0f;
		this.matched = false;
		this.degree = 0.0;
		this.identifier = idFactory;
		this.mivs = false;
		this.color = Color.BLACK.darker();
		idFactory++;
	}
	
	public boolean isMivs() {
		return mivs;
	}

	public void setMivs(boolean mivs) {
		this.mivs = mivs;
	}

	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	public int getVertexNum() {
		return vertexNum;
	}
	
	public float getMass() {
		return mass;
	}
	
	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@Override
	public final boolean equals(Object obj){
		if(obj instanceof VertexType){
			return this.identifier == ((VertexType)obj).identifier;
		}
		return false;
	}
	
	@Override
	public final int hashCode(){
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.identifier;
		return result;
	}
	
	@Override
	public String toString(){
		return "" + this.vertexNum;
	}
}
