package edu.umassmed.mousedb.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MouseDB {
	private final Map<String, Long> prepTypes, prepSources, roles,
	cellsPrepNames, cellsPrepSources, markerNames, cellSelTypes,
	organs, questions;
	private final List<Person> persons;
	private final List<Study> studies;
	private final List<Cohort> cohorts;
	private final List<Animal> animals;

	public MouseDB() {
		this.persons = new ArrayList<Person>();
		this.studies = new ArrayList<Study>();
		this.prepTypes = new LinkedHashMap<String, Long>();
		this.prepSources = new LinkedHashMap<String, Long>();
		this.roles = new LinkedHashMap<String, Long>();
		this.cellsPrepNames = new LinkedHashMap<String, Long>();
		this.cellsPrepSources = new LinkedHashMap<String, Long>();
		this.markerNames = new LinkedHashMap<String, Long>();
		this.cellSelTypes = new LinkedHashMap<String, Long>();
		this.organs = new LinkedHashMap<String, Long>();
		this.questions = new LinkedHashMap<String, Long>();
		this.cohorts = new ArrayList<Cohort>();
		this.animals = new ArrayList<Animal>();
	}

	public void addPrepType(final long id, final String prepType) {
		this.prepTypes.put(prepType, id);
	}

	public Map<String, Long> getPrepTypes() {
		return this.prepTypes;
	}

	public void addPrepSource(final long id, final String source) {
		this.prepSources.put(source, id);
	}

	public Map<String, Long> getPrepSources() {
		return this.prepSources;
	}

	public void addRole(final long id, final String role) {
		this.roles.put(role, id);
	}

	public Map<String, Long> getRoles() {
		return this.roles;
	}

	public void addCellsPrepName(final long id, final String cellsPrepName) {
		this.cellsPrepNames.put(cellsPrepName, id);
	}

	public Map<String, Long> getCellsPrepNames() {
		return this.cellsPrepNames;
	}

	public void addCellsPrepSource(final long id, final String cellsPrepSource) {
		this.cellsPrepSources.put(cellsPrepSource, id);
	}

	public Map<String, Long> getCellsPrepSources() {
		return this.cellsPrepSources;
	}

	public void addMarkerName(final long id, final String markerName) {
		this.markerNames.put(markerName, id);
	}

	public Map<String, Long> getMarkerNames() {
		return this.markerNames;
	}

	public void addCellSelType(final long id, final String cellSelType) {
		this.cellSelTypes.put(cellSelType, id);
	}

	public Map<String, Long> getCellSelTypes() {
		return this.cellSelTypes;
	}

	public void addOrgan(final long id, final String organ) {
		this.organs.put(organ, id);
	}

	public Map<String, Long> getOrgans() {
		return this.organs;
	}

	public void addQuestion(final long id, final String question) {
		this.questions.put(question, id);
	}

	public Map<String, Long> getQuestions() {
		return this.questions;
	}

	public void addCohort(final Cohort cohort) {
		this.cohorts.add(cohort);
	}

	public List<Cohort> getCohorts() {
		return this.cohorts;
	}

	public void addAnimal(final Animal animal) {
		this.animals.add(animal);
	}

	public List<Animal> getAnimals() {
		return this.animals;
	}

	public void addPerson(final Person person) {
		this.persons.add(person);
	}

	public List<Person> getPersons() {
		return this.persons;
	}

	public Person getPerson(final String name) {
		for (final Person person : this.persons) {
			if (person.toString().equals(name))
				return person;
		}
		return null;
	}

	public void addStudy(final Study study) {
		this.studies.add(study);
	}

	public List<Study> getStudies() {
		return this.studies;
	}

	public Study getStudy(final String name) {
		for (final Study study : this.studies) {
			if (study.getName().equals(name))
				return study;
		}
		return null;
	}
}
