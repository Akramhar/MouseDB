package edu.umassmed.mousedb.data;

import java.time.LocalDate;

public class CellsSelection extends Preparation {

	private final long specificId;
	private final String marker, selType;
	private final CellsPreparation cellsPreparation;

	public CellsSelection(final long id, final long specificId,
	        final CellsPreparation cellsPreparation, final String marker,
			final String selType, final String type,
			final LocalDate harvestDate, final Experimenter experimenter,
			final String source, final boolean frozen, final String protocol,
			final String description) {
		super(id, type, harvestDate, experimenter, source, frozen, protocol,
				description);
		this.specificId = specificId;
		this.cellsPreparation = cellsPreparation;
		this.marker = marker;
		this.selType = selType;
	}

	public long getSpecificId() {
		return this.specificId;
	}

	public String getMarker() {
		return this.marker;
	}

	public String getSelType() {
		return this.selType;
	}

	public CellsPreparation getCellsPreparation() {
		return this.cellsPreparation;
	}
}
