package edu.umassmed.mousedb.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.umassmed.mousedb.data.Animal;
import edu.umassmed.mousedb.data.CellsPreparation;
import edu.umassmed.mousedb.data.CellsSelection;
import edu.umassmed.mousedb.data.Cohort;
import edu.umassmed.mousedb.data.Engraftment;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.Organoid;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;
import edu.umassmed.mousedb.data.Study;

public class FileImporter {

	public static final String COHORT = "Cohort\t";
	public static final String STUDY = "Study\t";
	public static final String EXPERIMENTER = "Experimenter\t";
	public static final String ANIMAL = "Animal\t";
	public static final String ENGRAFTMENT = "Engraftment\t";
	public static final String PREPARATION = "Preparation\t";

	private final File file;
	private final boolean dayFirst;

	private final List<Cohort> cohorts;
	private final List<Study> studies;
	private final List<Person> persons;

	public FileImporter(final File file, final boolean dayFirst) {
		this.file = file;
		this.dayFirst = dayFirst;

		this.cohorts = new ArrayList<Cohort>();
		this.studies = new ArrayList<Study>();
		this.persons = new ArrayList<Person>();
	}

	private Study findStudy(final String studyName) {
		for (final Study study : this.studies) {
			final String name = study.getName();
			if (name.equals(studyName))
				return study;
		}
		return null;
	}

	private Person findPerson(final String personName) {
		for (final Person person : this.persons) {
			final String name = this.persons.toString();
			if (name.equals(personName))
				return person;
		}
		return null;
	}

	public void importFile() throws IOException {
		final FileReader fr = new FileReader(this.file);
		final BufferedReader br = new BufferedReader(fr);

		Cohort currentCohort = null;
		Engraftment currentEngraf = null;
		Organoid currentO = null;
		CellsPreparation currentCP = null;

		String line = br.readLine();
		boolean error = false;
		while (line != null) {
			if (!line.isEmpty()) {
				if (line.contains(FileImporter.COHORT)) {
					line.replace(FileImporter.COHORT, "");
					currentCohort = this.importCohort(line);
					this.cohorts.add(currentCohort);
				} else if (line.contains(FileImporter.STUDY)) {
					line.replace(FileImporter.STUDY, "");
					final Study study = this.importStudy(line);
					this.studies.add(study);
				} else if (line.contains(FileImporter.EXPERIMENTER)) {
					line.replace(FileImporter.EXPERIMENTER, "");
					final Experimenter exp = this.importExperimenter(line);
					this.persons.add(exp);
				} else if (line.contains(FileImporter.ANIMAL)) {
					line.replace(FileImporter.ANIMAL, "");
					final Animal animal = this
					        .importAnimal(line, currentCohort);
					if (animal == null) {
						error = true;
						break;
					}
				} else if (line.contains(FileImporter.ENGRAFTMENT)) {
					line.replace(FileImporter.ENGRAFTMENT, "");
					currentEngraf = this.importEngraftment(line, currentCohort);
					if (currentEngraf == null) {
						error = true;
						break;
					}
					currentO = null;
					currentCP = null;
				} else if (line.contains(FileImporter.PREPARATION)) {
					line.replace(FileImporter.PREPARATION, "");
					final Preparation prep = this.importPreparation(line,
					        currentEngraf, currentO, currentCP);
					if (prep == null) {
						error = true;
						break;
					}
					if (prep instanceof Organoid) {
						currentO = (Organoid) prep;
					} else if (prep instanceof CellsPreparation) {
						currentCP = (CellsPreparation) prep;
					}
				}
			}
			line = br.readLine();
		}

		if (!error) {
			// COMPLETE
		}
		br.close();
		fr.close();
	}

	// TODO FOUND A WAY TO ADD CELLS PREP + ORGANOID
	private CellsSelection importCellsSelection(final String[] tokens,
	        final Organoid organoid, final CellsPreparation cellsPrep) {
		return null;
	}

	// Preparation Caio Sempronio cells preparation 19.04.2015 psource2 true
	// proto2 desc2 specie1 cName1 cSource1 cnt1
	// TODO FOUND A WAY TO ADD ORGANOID
	private CellsPreparation importCellsPreparation(final String[] tokens,
	        final Organoid organoid) {
		final Experimenter exp = (Experimenter) this.findPerson(tokens[0]);
		if (exp == null) {
			System.out.println("ERROR PERSON NOT FOUND");
			return null;
		}
		final String type = tokens[1];
		final String dateSd = tokens[2];
		final LocalDate dateLd = LocalDate.parse(dateSd);
		final String source = tokens[3];
		final boolean frozen = Boolean.valueOf(tokens[4]);
		final String protocol = tokens[5];
		final String desc = tokens[6];
		final String specie = tokens[7];
		final String cellsName = tokens[8];
		final String cellsSource = tokens[9];
		final String totCount = tokens[10];
		return new CellsPreparation(-1, -1, organoid, cellsName, cellsSource,
		        specie, totCount, type, dateLd, exp, source, frozen, protocol,
		        desc);
	}

	// Preparation Caio Sempronio organoid 20.04.2015 psource1 false proto1
	// desc1 org1 size1 quali1
	private Organoid importOrganoid(final String[] tokens) {
		final Experimenter exp = (Experimenter) this.findPerson(tokens[0]);
		if (exp == null) {
			System.out.println("ERROR PERSON NOT FOUND");
			return null;
		}
		final String type = tokens[1];
		final String dateSd = tokens[2];
		final LocalDate dateLd = LocalDate.parse(dateSd);
		final String source = tokens[3];
		final boolean frozen = Boolean.valueOf(tokens[4]);
		final String protocol = tokens[5];
		final String desc = tokens[6];
		final String organ = tokens[7];
		final String size = tokens[8];
		final String quality = tokens[9];
		return new Organoid(-1, -1, organ, size, quality, type, dateLd, exp,
		        source, frozen, protocol, desc);
	}

	private Preparation importPreparation(final String line,
	        final Engraftment engraf, final Organoid organoid,
	        final CellsPreparation cellsPrep) {
		line.replaceFirst("\t", "");
		line.replaceFirst("\t", "");
		final String[] tokens = line.split("\t");
		final String prepType = tokens[1];
		Preparation prep = null;
		if (prepType == "organoid") {
			prep = this.importOrganoid(tokens);
		} else if (prepType == "cells preparation") {
			prep = this.importCellsPreparation(tokens, organoid);
		} else if (prepType == "cells selection") {
			prep = this.importCellsSelection(tokens, organoid, cellsPrep);
		}
		engraf.addPreparation(prep);
		return null;
	}

	// Engraftment Pinco Palla 22.04.2015 route1 method1
	private Engraftment importEngraftment(final String line, final Cohort cohort) {
		line.replaceFirst("\t", "");
		line.replaceFirst("\t", "");
		final String[] tokens = line.split("\t");
		final Experimenter exp = (Experimenter) this.findPerson(tokens[0]);
		if (exp == null) {
			System.out.println("ERROR PERSON NOT FOUND");
			return null;
		}
		final String dateSd = tokens[1];
		final LocalDate dateLd = LocalDate.parse(dateSd);
		final String route = tokens[2];
		final String method = tokens[3];
		final Engraftment engraf = new Engraftment(-1, dateLd, route, method,
		        exp);
		cohort.addEngraftment(engraf);
		return engraf;
	}

	// Animal Cohort20150424 Study1 sp1 st1 geno1 false source1 14.03.2015
	// 18.04.2015
	private Animal importAnimal(final String line, final Cohort cohort) {
		line.replaceFirst("\t", "");
		line.replaceFirst("\t", "");
		final String[] tokens = line.split("\t");
		final String studyName = tokens[0];
		final String specie = tokens[1];
		final String strain = tokens[2];
		final String genotype = tokens[3];
		final boolean irradiated = Boolean.valueOf(tokens[4]);
		final String source = tokens[5];
		final String bDateSd = this.transformDate(tokens[6]);
		final LocalDate bDateLd = LocalDate.parse(bDateSd);
		LocalDate sDateLd = null;
		if (tokens[7] != null) {
			final String sDateSd = this.transformDate(tokens[7]);
			sDateLd = LocalDate.parse(sDateSd);
		}
		final String cDateSd = this.transformDate(tokens[8]);
		final LocalDate cDateLd = LocalDate.parse(cDateSd);
		final Study study = this.findStudy(studyName);
		if (study == null) {
			System.out.println("ERROR STUDY NOT FOUND");
			return null;
		}
		final Animal animal = new Animal(-1, cohort, study, specie, strain,
		        genotype, source, bDateLd, sDateLd, irradiated, cDateLd);
		cohort.addAnimal(animal);
		study.addAnimal(animal);
		return animal;
	}

	// Experimenter Pinco Palla role1
	private Experimenter importExperimenter(final String line) {
		line.replaceFirst("\t", "");
		line.replaceFirst("\t", "");
		final String[] tokens = line.split("\t");
		final String fName = tokens[0];
		final String lName = tokens[1];
		final String role = tokens[2];
		final Experimenter exp = new Experimenter(-1, -1, fName, lName, role);
		return exp;
	}

	// Study Study1 question1 24.04.2015 24.05.2015 study1 desc study1 goal
	private Study importStudy(final String line) {
		line.replaceFirst("\t", "");
		line.replaceFirst("\t", "");
		final String[] tokens = line.split("\t");
		final String name = tokens[0];
		final String question = tokens[1];
		final String startSd = this.transformDate(tokens[2]);
		final LocalDate startLd = LocalDate.parse(startSd);
		this.transformDate(tokens[3]);
		final LocalDate endLd = LocalDate.parse(startSd);
		final String desc = tokens[4];
		final String goal = tokens[5];
		final Study study = new Study(-1, startLd, endLd, name, question, desc,
		        goal);
		return study;
	}

	// Cohort Cohort20150424 24.04.2015
	private Cohort importCohort(final String line) {
		line.replaceFirst("\t", "");
		line.replaceFirst("\t", "");
		final String[] tokens = line.split("\t");
		final String sd = this.transformDate(tokens[1]);
		final LocalDate ld = LocalDate.parse(sd);
		final Cohort cohort = new Cohort(-1, ld, 0);
		return cohort;
	}

	private String transformDate(final String date) {
		final String[] dateTokens = date.split(".");
		final StringBuffer buf = new StringBuffer();
		buf.append(dateTokens[3]);
		buf.append("-");
		if (this.dayFirst) {
			buf.append(dateTokens[2]);
			buf.append("-");
			buf.append(dateTokens[1]);
		} else {
			buf.append(dateTokens[1]);
			buf.append("-");
			buf.append(dateTokens[2]);
		}
		return buf.toString();
	}
}
