package energyModels.data;

import energyModels.data.rater.Film;
import threadedSim.util.ScoreCount;

public interface DataProvider {
	public UserInfo giveData();

	public void clear();

	public int fetchInformation();

	static public class UserInfo {
		public ScoreCount<Film> cache;
		public ScoreCount<Film> search;
		public int userId;

		public UserInfo(int id, ScoreCount<Film> cache, ScoreCount<Film> search) {
			super();
			this.userId = id;
			this.cache = cache;
			this.search = search;
		}

		public ScoreCount<Film> getCache() {
			return cache;
		}

		public void setCache(ScoreCount<Film> cache) {
			this.cache = cache;
		}

		public ScoreCount<Film> getSearch() {
			return search;
		}

		public void setSearch(ScoreCount<Film> search) {
			this.search = search;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

	}
}
