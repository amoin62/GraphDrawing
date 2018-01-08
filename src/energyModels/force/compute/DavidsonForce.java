package energyModels.force.compute;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;
import energyModels.tree.BHTree;
import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class DavidsonForce extends AActionTaskFactory<CoordinatesPeer>{
	private final double posEdgeScaling;
	private final double neutralContScaling;
	private static BHTree bhTree = null;
	public static boolean BHMode = false;
	
	public static void setBhTree(BHTree bhTree) {
		DavidsonForce.bhTree = bhTree;
	}

	public DavidsonForce(double posEdgeScaling,
			double neutralContScaling) {
		super();
		this.posEdgeScaling = posEdgeScaling;
		this.neutralContScaling = neutralContScaling;
	}
	
	@Override
	public Runnable createTask(CoordinatesPeer peer) {
		return new DavidsonForceTask(peer,this.getSem());
	}
	
	public class DavidsonForceTask extends ALocalActionTask<CoordinatesPeer>{

		public DavidsonForceTask(CoordinatesPeer peer, Semaphore sem) {
			super(peer, sem);
		}

		@Override
		protected void doAction() {
			// attract
			CartesianCoordinate thisPos = this.peer.getCoordinate();
			for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
					.getAttractNeighbors().entrySet()) {
				CartesianCoordinate logicalNeighborPosition = logicalNeighbor.getKey()
						.getCoordinate();
				double d = thisPos.distance(logicalNeighborPosition);
				d = Math.max(d, 0.0000001);
				thisPos.storeBodyForce(logicalNeighbor.getKey(), this.peer, 2.0*d*posEdgeScaling*logicalNeighbor.getValue());
			}
			//baseline force
			for(CoordinatesPeer neutralNeighbor: this.peer.getNeutralNeighbors()){
				if(!this.peer.equals(neutralNeighbor)){
					CartesianCoordinate neutralNeighborPosition = neutralNeighbor.getCoordinate();
					double d = thisPos.distance(neutralNeighborPosition);
					d = Math.max(d, 0.0000001);
					thisPos.storeBodyForce(neutralNeighbor, this.peer, (-2 / Math.pow(d, 3.0))*neutralContScaling);
				}
			}
			if(BHMode){
				bhTree.updateForceDavidson(this.peer, neutralContScaling);
			}
		}
	}

}
