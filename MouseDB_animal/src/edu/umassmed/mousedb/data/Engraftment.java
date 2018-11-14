package edu.umassmed.mousedb.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Engraftment extends Element {

	private final Experimenter experimenter;
	private final LocalDate date;
	private final String route, method;
	private final List<Preparation> preparations;

	public Engraftment(final long id, final LocalDate date, final String route,
			final String method, final Experimenter experimenter,
			final List<Preparation> preparations) {
		super(id);
		this.date = date;
		this.route = route;
		this.method = method;
		this.experimenter = experimenter;
		this.preparations = preparations;
	}

	public Engraftment(final long id, final LocalDate date, final String route,
			final String method, final Experimenter experimenter) {
		this(id, date, route, method, experimenter,
				new ArrayList<Preparation>());
	}

	public Experimenter getExperimenter() {
		return this.experimenter;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public String getRoute() {
		return this.route;
	}

	public String getMethod() {
		return this.method;
	}

	public List<Preparation> getPreparations() {
		return this.preparations;
	}

	public void addPreparation(final Preparation preparation) {
		this.preparations.add(preparation);
	}
}
