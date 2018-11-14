package edu.umassmed.mousedb.data;

import java.time.LocalDate;

public class Animal extends Element {

	private final Cohort cohort;
	private final Study study;
	private final String specie, strain, genotype, source;
	private final boolean irradiated;
	private final LocalDate checkDate, dob, dos;

	public Animal(final long id, final Cohort cohort, final Study study,
			final String specie, final String strain, final String genotype,
			final String source, final LocalDate dob, final LocalDate dos,
			final boolean irradiated, final LocalDate checkDate) {
		super(id);
		this.cohort = cohort;
		this.study = study;
		this.specie = specie;
		this.strain = strain;
		this.genotype = genotype;
		this.source = source;
		this.dob = dob;
		this.dos = dos;
		this.irradiated = irradiated;
		this.checkDate = checkDate;
	}

	public Study getStudy() {
		return this.study;
	}

	public Cohort getCohort() {
		return this.cohort;
	}

	public String getSpecie() {
		return this.specie;
	}

	public String getStrain() {
		return this.strain;
	}

	public String getGenotype() {
		return this.genotype;
	}

	public String getSource() {
		return this.source;
	}

	public LocalDate getDob() {
		return this.dob;
	}

	public LocalDate getDos() {
		return this.dos;
	}

	public boolean isIrradiated() {
		return this.irradiated;
	}

	public LocalDate getCheckDate() {
		return this.checkDate;
	}
}
