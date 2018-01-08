package energyModels.force.compute;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;
import energyModels.tree.BHTree;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class HCForce extends AActionTaskFactory<CoordinatesPeer> {
	private final double posEdgeScaling;
	private final double neutralContScaling;
	private static BHTree bhTree = null;
	public static boolean BHMode = false;
	
	public HCForce(double posEdgeScaling, double neutralContScaling) {
		super();
		this.posEdgeScaling = posEdgeScaling;
		this.neutralContScaling = neutralContScaling;
	}

	@Override
	public Runnable createTask(CoordinatesPeer peer) {
		return new HCForceTask(peer, this.getSem());
	}

	@Override
	public Object getResult() {
		return null;
	}

	public class HCForceTask extends ALocalActionTask<CoordinatesPeer> {

		public HCForceTask(CoordinatesPeer peer, Semaphore sem) {
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
				d = Math.max(d, 0.0001);
				//thisPos.storeForce(logicalNeighborPosition, d);
				thisPos.storeBodyForce(logicalNeighbor.getKey(), this.peer, d* posEdgeScaling*logicalNeighbor.getValue());
			}
			// repulse
			for (CoordinatesPeer pushNeighbor : this.peer.getNeutralNeighbors()) {
				if (!this.peer.equals(pushNeighbor)) {
					CartesianCoordinate pushNeighborPosition = pushNeighbor
							.getCoordinate();
					double d = thisPos.distance(pushNeighborPosition);
					d = Math.max(d, 0.0001);
					//thisPos.storeForce(pushNeighborPosition, -1 / (d * d));
					thisPos.storeBodyForce(pushNeighbor, this.peer, -1 / (d * d));
				}
			}
			
			if(BHMode){
				bhTree.updateForceHC(this.peer, neutralContScaling);
			}
		}
	}

	public static void setBhTree(BHTree bhTree) {
		HCForce.bhTree = bhTree;
	}
}
