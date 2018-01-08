package energyModels.data.rater;

import java.util.HashMap;
import java.util.Map;

public class Film {

	private static Map<Film, Film> factory = new HashMap<Film, Film>();

	public static String noName = "noFilmName";

	private int num;

	private String name;

	private int popularity;

	static synchronized public Film getFilm(int n, String val) {
		Film t = new Film(n, val);
		Film ret = factory.get(t);
		if (ret == null) {
			factory.put(t, t);
			return t;
		} else {
			return ret;
		}
	}

	static synchronized public Film getFilm(int n) {
		return getFilm(n, noName);
	}

	static synchronized public Film getTempItem(int n, String val) {
		Film t = new Film(n, val);
		Film ret = factory.get(t);
		if (ret == null) {
			return t;
		} else {
			return ret;
		}
	}

	static synchronized public Film getTempItem(int n) {
		return getTempItem(n, "" + n);
	}

	private Film(int n, String val) {
		this.num = n;
		this.name = val;
		this.popularity = 0;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Film) {
			return this.num == ((Film) obj).num;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + num;
		return result;
	}

	@Override
	public final String toString() {
		if (this.name == Film.noName) {
			return "" + this.num;
		} else {
			return this.name;
		}
	}

	public final int getPopularity() {
		return popularity;
	}

	public synchronized final void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public synchronized final void incPopularity() {
		this.popularity++;
	}

	public final int getNum() {
		return this.num;
	}
}
