package energyModels.data.rater;

import java.util.List;

import threadedSim.peer.Peer;
import threadedSim.util.ScoreCount;
import threadedSim.util.TimeStampedInformation;

public class User extends Peer{
	private List<TimeStampedInformation<User>> rps;
	private List<TimeStampedInformation<User>> neighbors;
	private ScoreCount<Film> profile;
	private ScoreCount<Film> testProfile;

	public User(int peerId) {
		super(peerId);
		this.rps = null;
		this.neighbors = null;
	}

	public final List<TimeStampedInformation<User>> getRps() {
		return rps;
	}

	public final void setRps(List<TimeStampedInformation<User>> rps) {
		this.rps = rps;
	}

	public final List<TimeStampedInformation<User>> getNeighbors() {
		return this.neighbors;
	}

	public final void setNeighbors(List<TimeStampedInformation<User>> neighbors) {
		this.neighbors = neighbors;
	}
	
	

	public final ScoreCount<Film> getProfile() {
		return this.profile;
	}

	public final void setProfile(ScoreCount<Film> profile) {
		this.profile = profile;
	}

	public ScoreCount<Film> getTestProfile() {
		return this.testProfile;
	}

	public void setTestProfile(ScoreCount<Film> hiddenProfile) {
		if (hiddenProfile == null) {
			System.out.println("!!!");
		}
		this.testProfile = hiddenProfile;
	}
}
