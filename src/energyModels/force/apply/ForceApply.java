package energyModels.force.apply;

import java.util.concurrent.Semaphore;

import energyModels.peer.CoordinatesPeer;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class ForceApply extends AActionTaskFactory<CoordinatesPeer> {

	@Override
	public final Runnable createTask(CoordinatesPeer peer) {
		return new ForceApplyTask(peer, this.getSem());
	}

	@Override
	public final Object getResult() {
		return null;
	}

	public static class ForceApplyTask extends
			ALocalActionTask<CoordinatesPeer> {
		public ForceApplyTask(CoordinatesPeer peer, Semaphore sem) {
			super(peer, sem);
		}

		@Override
		protected void doAction() {
			this.peer.getCoordinate().applyForce();
		}
	}

}
