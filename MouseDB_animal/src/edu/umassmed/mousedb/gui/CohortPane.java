package edu.umassmed.mousedb.gui;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Cohort;

public class CohortPane extends TitledPane {

	private MouseDBAnimalApplication app;
	private final InsertStack mainStack;
	private final GridPane mainGridPane;
	final DatePicker date;

	public CohortPane(final MouseDBAnimalApplication app,
	        final InsertStack mainStack) {
		this.mainStack = mainStack;
		this.setText("Cohort");

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

		final GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		buttonPane.setPadding(new Insets(InsertStage.PADDING));
		buttonPane.setVgap(InsertStage.GAP);
		buttonPane.setHgap(InsertStage.GAP);

		final Button addAnimal = new Button("Add animal");
		addAnimal.setPrefWidth(InsertStage.LABEL_WIDTH);
		addAnimal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CohortPane.this.handleAddAnimal();
			}
		});
		buttonPane.add(addAnimal, 1, 0);

		final Button addEngraftment = new Button("Add engraftment");
		addEngraftment.setPrefWidth(InsertStage.BUTTON_WIDTH * 2);
		addEngraftment.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CohortPane.this.handleAddEngraftment();
			}
		});
		buttonPane.add(addEngraftment, 2, 0);

		this.mainGridPane.add(buttonPane, 0, 1, 2, 1);

		this.setContent(this.mainGridPane);
	}

	private void handleAddAnimal() {
		this.mainStack.addAnimal();
	}

	private void handleAddEngraftment() {
		this.mainStack.addEngraftment();
	}

	public Cohort getCohort() {
		final LocalDate date = this.date.getValue();
		int index = this.app.getCohortForYear(date.getYear());
		if (index == -1) {
			System.out.println("ERROR: COHORT INDEX NOT CORRECT");
			return null;
		}
		return new Cohort(-1, date, ++index);
	}

	public void reset() {
		this.date.setValue(LocalDate.now());
	}
}
