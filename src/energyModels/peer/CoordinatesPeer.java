package energyModels.peer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import energyModels.coord.CartesianCoordinate;
import energyModels.coord.IProvidesPosition;
import threadedSim.peer.Peer;

public class CoordinatesPeer extends Peer implements
		IProvidesPosition<CartesianCoordinate> {
	
	private CartesianCoordinate coord;
	public double mass;	
	private double indegree;
	private double posIndegree;
	private double negIndegree;
	//public double stepLength;
			
	private Map<CoordinatesPeer,Double> repulseNeighbors;
	private Map<CoordinatesPeer,Double> attractNeighbors;
	private List<CoordinatesPeer> neutralNeighbors;

	public CoordinatesPeer(int peerId) {
		super(peerId);
		this.coord = new CartesianCoordinate();
		this.repulseNeighbors = new HashMap<CoordinatesPeer, Double>();
		this.attractNeighbors = new HashMap<CoordinatesPeer, Double>();
		this.neutralNeighbors = new ArrayList<CoordinatesPeer>();
		this.indegree = 1.0;
		this.posIndegree = 1.0;
		this.negIndegree = 1.0;
		this.mass = 1.0;
		//this.stepLength = initialStepLength;
	}
	
	public final CartesianCoordinate getCoordinate() {
		return this.coord;
	}

	public final void setCoordinate(CartesianCoordinate c) {
		this.coord = c;
	}
	
	public Map<CoordinatesPeer, Double> getRepulseNeighbors() {
		return repulseNeighbors;
	}

	public Map<CoordinatesPeer, Double> getAttractNeighbors() {
		return attractNeighbors;
	}

	public double getIndegree() {
		return indegree;
	}

	public List<CoordinatesPeer> getNeutralNeighbors() {
		return neutralNeighbors;
	}

	public void setNeutralNeighbors(List<CoordinatesPeer> neutralNeighbors) {
		this.neutralNeighbors = neutralNeighbors;
	}

	public void setIndegree(double indegree) {
		this.indegree = indegree;
	}
	
	public double getPosIndegree() {
		return posIndegree;
	}

	public void setPosIndegree(double posIndegree) {
		this.posIndegree = posIndegree;
	}

	public double getNegIndegree() {
		return negIndegree;
	}

	public void setNegIndegree(double negIndegree) {
		this.negIndegree = negIndegree;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	/*public List<CoordinatesPeer> getRepulseNeighbors() {
		return repulseNeighbors;
	}

	public void setRepulseNeighbors(List<CoordinatesPeer> repulseNeighbors) {
		this.repulseNeighbors = repulseNeighbors;
	}

	public List<CoordinatesPeer> getAttractNeighbors() {
		return attractNeighbors;
	}

	public void setAttractNeighbors(List<CoordinatesPeer> attractNeighbors) {
		this.attractNeighbors = attractNeighbors;
	}

	public List<CoordinatesPeer> getNeutralNeighbors() {
		return neutralNeighbors;
	}

	public void setNeutralNeighbors(List<CoordinatesPeer> neutralNeighbors) {
		this.neutralNeighbors = neutralNeighbors;
	}
	
	public List<Double> getRepulseNeighborsweight() {
		return repulseNeighborsweight;
	}

	public void setRepulseNeighborsweight(List<Double> repulseNeighborsweight) {
		this.repulseNeighborsweight = repulseNeighborsweight;
	}

	public List<Double> getAttractNeighborsweight() {
		return attractNeighborsweight;
	}

	public void setAttractNeighborsweight(List<Double> attractNeighborsweight) {
		this.attractNeighborsweight = attractNeighborsweight;
	}

	public List<Double> getNeutralNeighborsweight() {
		return neutralNeighborsweight;
	}

	public void setNeutralNeighborsweight(List<Double> neutralNeighborsweight) {
		this.neutralNeighborsweight = neutralNeighborsweight;
	}*/
}
