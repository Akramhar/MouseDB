package edu.umassmed.mousedb.data;

import java.time.LocalDate;

public class Preparation extends Element {

	private final LocalDate harvestDate;
	private final Experimenter experimenter;
	private final boolean frozen;
	private final String type, source, protocol, description;

	public Preparation(final long id, final String type,
	        final LocalDate harvestDate, final Experimenter experimenter,
	        final String source, final boolean frozen, final String protocol,
	        final String description) {
		super(id);
		this.type = type;
		this.harvestDate = harvestDate;
		this.experimenter = experimenter;
		this.source = source;
		this.frozen = frozen;
		this.protocol = protocol;
		this.description = description;
	}

	public LocalDate getHarvestDate() {
		return this.harvestDate;
	}

	public Experimenter getExperimenter() {
		return this.experimenter;
	}

	public String getSource() {
		return this.source;
	}

	public boolean isFrozen() {
		return this.frozen;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public String getDescription() {
		return this.description;
	}

	public String getType() {
		return this.type;
	}

}
