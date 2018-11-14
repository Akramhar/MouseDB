package edu.umassmed.mousedb.data;

public class Experimenter extends Person {

	private final long specificId;
	private final String role;

	public Experimenter(final long id, final long specificId,
	        final String firstName, final String lastName, final String role) {
		super(id, firstName, lastName);
		this.specificId = specificId;
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}

	public long getSpecificId() {
		return this.specificId;
	}

	@Override
	public String toString() {
		return super.toString() + " - " + this.role;
	}
}
