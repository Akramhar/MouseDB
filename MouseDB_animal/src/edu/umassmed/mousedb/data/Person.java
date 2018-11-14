package edu.umassmed.mousedb.data;

public class Person extends Element {

	private final String firstName, lastName;

	public Person(final long id, final String firstName, final String lastName) {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	@Override
	public String toString() {
		return this.firstName + " " + this.lastName;
	}
}
