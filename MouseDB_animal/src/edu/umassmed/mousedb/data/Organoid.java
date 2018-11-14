package edu.umassmed.mousedb.data;

import java.time.LocalDate;

public class Organoid extends Preparation {

	private final long specificId;
	private final String organ, size, quality;

	public Organoid(final long id, final long specificId, final String organ,
			final String size, final String quality, final String type,
	        final LocalDate harvestDate, final Experimenter experimenter,
	        final String source, final boolean frozen, final String protocol,
	        final String description) {
		super(id, type, harvestDate, experimenter, source, frozen, protocol,
				description);
		this.specificId = specificId;
		this.organ = organ;
		this.size = size;
		this.quality = quality;
	}

	public long getSpecificId() {
		return this.specificId;
	}

	public String getOrgan() {
		return this.organ;
	}

	public String getSize() {
		return this.size;
	}

	public String getQuality() {
		return this.quality;
	}
}
