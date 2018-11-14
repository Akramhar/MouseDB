package edu.umassmed.mousedb.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cohort extends Element {

	private final LocalDate date;
	private final List<Engraftment> engraftments;
	private final List<Animal> animals;

	private final String name;
	private final int index;

	public Cohort(final long id, final LocalDate date, final int index,
			final List<Engraftment> engrafments, final List<Animal> animals) {
		super(id);
		this.date = date;
		this.index = index;
		this.engraftments = engrafments;
		this.animals = animals;

		this.name = date.getYear() + "_" + index;
	}

	public Cohort(final long id, final LocalDate date, final int index,
			final List<Engraftment> engrafments) {
		this(id, date, index, engrafments, new ArrayList<Animal>());
	}

	public Cohort(final long id, final LocalDate date, final int index) {
		this(id, date, index, new ArrayList<Engraftment>(),
		        new ArrayList<Animal>());
	}

	public String getName() {
		return this.name;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public int getIndex() {
		return this.index;
	}

	public List<Engraftment> getEngraftments() {
		return this.engraftments;
	}

	public void addEngraftment(final Engraftment engraftment) {
		this.engraftments.add(engraftment);
	}

	public List<Animal> getAnimals() {
		return this.animals;
	}

	public void addAnimal(final Animal animal) {
		this.animals.add(animal);
	}
}
