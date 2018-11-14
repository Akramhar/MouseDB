package edu.umassmed.mousedb.gui;

import java.time.LocalDate;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Engraftment;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.Person;

public class EngraftmentPane extends TitledPane implements
        ExperimenterContainer {

	private final MouseDBAnimalApplication app;
	private final InsertStack mainStack;

	private final GridPane mainGridPane;
	private final DatePicker date;
	private final TextField routeField, methodField;
	private final ComboBox<String> experimenterCombo;

	public EngraftmentPane(final MouseDBAnimalApplication app,
	        final InsertStack mainStack) {
		this.app = app;
		this.mainStack = mainStack;
		this.setText("Engraftment");

		this.mainGridPane = new GridPane();
		this.mainGridPane.setPadding(new Insets(InsertStage.PADDING));
		this.mainGridPane.setVgap(InsertStage.GAP);
		this.mainGridPane.setHgap(InsertStage.GAP);

		final Label dateLabel = new Label("Date:");
		dateLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.date = new DatePicker(LocalDate.now());
		this.date.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(dateLabel, 0, 0);
		this.mainGridPane.add(this.date, 1, 0);

		final Label experimenterLabel = new Label("Experimenter:");
		experimenterLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.experimenterCombo = new ComboBox<String>();
		this.populateExperimenterCombo();
		this.experimenterCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(experimenterLabel, 0, 1);
		this.mainGridPane.add(this.experimenterCombo, 1, 1);
		final Button addNew = new Button("New");
		addNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				EngraftmentPane.this.handleAddNewExperimenter();
			}
		});
		addNew.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew, 2, 1);

		final Label routeLabel = new Label("Route:");
		routeLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.routeField = new TextField();
		this.routeField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(routeLabel, 0, 2);
		this.mainGridPane.add(this.routeField, 1, 2);

		final Label methodLabel = new Label("Method:");
		methodLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.methodField = new TextField();
		this.methodField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(methodLabel, 0, 3);
		this.mainGridPane.add(this.methodField, 1, 3);

		final GridPane buttonPane = new GridPane();
		buttonPane.setPadding(new Insets(InsertStage.PADDING));
		buttonPane.setVgap(InsertStage.GAP);
		buttonPane.setHgap(InsertStage.GAP);

		final Button addOrganoid = new Button("Add organoid");
		addOrganoid.setPrefWidth(InsertStage.LABEL_WIDTH);
		addOrganoid.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				EngraftmentPane.this.handleAddOrganoid();
			}
		});
		buttonPane.add(addOrganoid, 0, 0);

		final Button addCellPrep = new Button("Add cell prep");
		addCellPrep.setPrefWidth(InsertStage.LABEL_WIDTH);
		addCellPrep.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				EngraftmentPane.this.handleAddCellPrep();
			}
		});
		buttonPane.add(addCellPrep, 1, 0);

		final Button addCellSel = new Button("Add cell sel");
		addCellSel.setPrefWidth(InsertStage.LABEL_WIDTH);
		addCellSel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				EngraftmentPane.this.handleAddCellSel();
			}
		});
		buttonPane.add(addCellSel, 2, 0);

		final Button remove = new Button("Remove");
		remove.setPrefWidth(InsertStage.BUTTON_WIDTH);
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				EngraftmentPane.this.handleRemoveEngraftment();
			}
		});
		buttonPane.add(remove, 3, 0);

		this.mainGridPane.add(buttonPane, 0, 5, 3, 1);

		this.setContent(this.mainGridPane);
	}

	private void handleRemoveEngraftment() {
		this.mainStack.removeEngraftment(this);
	}

	private void handleAddNewExperimenter() {
		this.app.showExperimenterContent(this, 0);
	}

	private void handleAddOrganoid() {
		this.mainStack.addOrganoidPrep(this);
	}

	private void handleAddCellPrep() {
		this.mainStack.addCellPrep(this);
	}

	private void handleAddCellSel() {
		this.mainStack.addCellSelPrep(this);
	}

	private void populateExperimenterCombo() {
		this.experimenterCombo.getItems().clear();
		final List<Person> persons = this.app.getMouseDB().getPersons();
		for (final Person person : persons) {
			if (person instanceof Experimenter) {
				this.experimenterCombo.getItems().add(person.toString());
			}
		}
		if (this.experimenterCombo.getItems().size() > 0) {
			this.experimenterCombo.setValue(this.experimenterCombo.getItems()
					.get(0));
		}
	}

	private void updateExperimenterCombo(final Experimenter exp) {
		final String selection = exp.toString();
		this.experimenterCombo.getItems().add(selection);
		this.experimenterCombo.setValue(selection);
	}

	@Override
	public void updateExperimenter(final int index, final Experimenter exp) {
		if (index == 0) {
			this.updateExperimenterCombo(exp);
		}
	}

	public Engraftment getEngraftment() {
		final Person person = this.app.getMouseDB().getPerson(
		        this.experimenterCombo.getValue());
		if (person == null) {
			System.out.println("ERROR: person not saved correctly");
			return null;
		}
		final Engraftment engraf = new Engraftment(-1, this.date.getValue(),
		        this.routeField.getText(), this.methodField.getText(),
		        (Experimenter) person);
		return engraf;
	}
}
