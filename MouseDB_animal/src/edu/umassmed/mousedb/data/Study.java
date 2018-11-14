package edu.umassmed.mousedb.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Study extends Element {

	private final LocalDate start, end;
	private final String question, name, description, goal;

	private final List<Animal> animals;

	public Study(final long id, final LocalDate start, final LocalDate end,
	        final String name, final String question, final String description,
	        final String goal, final List<Animal> animals) {
		super(id);
		this.start = start;
		this.end = end;
		this.name = name;
		this.question = question;
		this.description = description;
		this.goal = goal;
		this.animals = animals;
	}

	public Study(final long id, final LocalDate start, final LocalDate end,
	        final String name, final String question, final String description,
	        final String goal) {
		this(id, start, end, name, question, description, goal,
		        new ArrayList<Animal>());
	}

	public List<Animal> getAnimals() {
		return this.animals;
	}

	public void addAnimal(final Animal animal) {
		this.animals.add(animal);
	}

	public String getName() {
		return this.name;
	}

	public LocalDate getStart() {
		return this.start;
	}

	public LocalDate getEnd() {
		return this.end;
	}

	public String getDescription() {
		return this.description;
	}

	public String getGoal() {
		return this.goal;
	}

	public String getQuestion() {
		return this.question;
	}
}
