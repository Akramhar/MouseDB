package edu.umassmed.mousedb.data;

import java.time.LocalDate;

public class CellsPreparation extends Preparation {

	private final Organoid org;

	private final long specificId;
	private final String name, source, specie, totalCount;

	public CellsPreparation(final long id, final long specificId,
	        final Organoid org, final String name, final String source,
	        final String specie, final String totalCount, final String type,
			final LocalDate harvestDate, final Experimenter experimenter,
			final String prepSource, final boolean frozen,
			final String protocol, final String description) {
		super(id, type, harvestDate, experimenter, prepSource, frozen,
				protocol, description);
		this.org = org;

		this.specificId = specificId;
		this.name = name;
		this.source = source;
		this.specie = specie;
		this.totalCount = totalCount;
	}

	public Organoid getOrganoid() {
		return this.org;
	}

	public long getSpecificId() {
		return this.specificId;
	}

	public String getName() {
		return this.name;
	}

	public String getCellsPrepSource() {
		return this.source;
	}

	public String getSpecie() {
		return this.specie;
	}

	public String getTotalCount() {
		return this.totalCount;
	}
}
