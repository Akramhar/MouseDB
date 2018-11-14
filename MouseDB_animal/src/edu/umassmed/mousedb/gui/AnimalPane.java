package edu.umassmed.mousedb.gui;

import java.time.LocalDate;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Animal;
import edu.umassmed.mousedb.data.Cohort;
import edu.umassmed.mousedb.data.Study;

public class AnimalPane extends TitledPane {

	private final MouseDBAnimalApplication app;
	private final InsertStack mainStack;

	private final GridPane mainGridPane;

	private final TextField specieField, strainField, genotypeField,
	        sourceField;
	private final CheckBox irradiationCheck;
	final DatePicker bDate, sDate, checkDate;
	private final ComboBox<String> studyCombo;

	public AnimalPane(final MouseDBAnimalApplication app,
			final InsertStack mainStack) {
		this.app = app;
		this.mainStack = mainStack;
		this.setText("Animal");

		this.mainGridPane = new GridPane();
		this.mainGridPane.setPadding(new Insets(InsertStage.PADDING));
		this.mainGridPane.setVgap(InsertStage.GAP);
		this.mainGridPane.setHgap(InsertStage.GAP);

		final Label specieLabel = new Label("Specie:");
		specieLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.specieField = new TextField();
		this.specieField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(specieLabel, 0, 0);
		this.mainGridPane.add(this.specieField, 1, 0);

		final Label strainLabel = new Label("Strain:");
		strainLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.strainField = new TextField();
		this.strainField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(strainLabel, 0, 1);
		this.mainGridPane.add(this.strainField, 1, 1);

		final Label genotypeLabel = new Label("Genotype:");
		genotypeLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.genotypeField = new TextField();
		this.genotypeField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(genotypeLabel, 0, 2);
		this.mainGridPane.add(this.genotypeField, 1, 2);

		final Label irradiationLabel = new Label("Irradiation:");
		irradiationLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.irradiationCheck = new CheckBox();
		this.irradiationCheck.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(irradiationLabel, 0, 3);
		this.mainGridPane.add(this.irradiationCheck, 1, 3);

		final Label sourceLabel = new Label("Source:");
		sourceLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.sourceField = new TextField();
		this.sourceField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(sourceLabel, 0, 4);
		this.mainGridPane.add(this.sourceField, 1, 4);

		final Label bDateLabel = new Label("Date of birth:");
		bDateLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.bDate = new DatePicker(LocalDate.now());
		this.bDate.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(bDateLabel, 0, 5);
		this.mainGridPane.add(this.bDate, 1, 5);

		final Label sDateLabel = new Label("Date of sacrifice:");
		sDateLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.sDate = new DatePicker();
		this.sDate.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(sDateLabel, 0, 6);
		this.mainGridPane.add(this.sDate, 1, 6);

		final Label checkDateLabel = new Label("Check date:");
		checkDateLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.checkDate = new DatePicker(LocalDate.now());
		this.checkDate.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(checkDateLabel, 0, 7);
		this.mainGridPane.add(this.checkDate, 1, 7);

		final Label studyLabel = new Label("Study:");
		studyLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.studyCombo = new ComboBox<String>();
		this.populateStudyCombo();
		this.studyCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(studyLabel, 0, 8);
		this.mainGridPane.add(this.studyCombo, 1, 8);
		final Button addNew = new Button("New");
		addNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				AnimalPane.this.handleAddNewStudy();
			}
		});
		addNew.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew, 2, 8);

		final Button remove = new Button("Remove");
		remove.setPrefWidth(InsertStage.BUTTON_WIDTH);
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				AnimalPane.this.handleRemoveAnimal();
			}
		});
		this.mainGridPane.add(remove, 2, 9);

		this.setContent(this.mainGridPane);
	}

	public void populateStudyCombo() {
		this.studyCombo.getItems().clear();
		final List<Study> studies = this.app.getMouseDB().getStudies();
		for (final Study study : studies) {
			this.studyCombo.getItems().add(study.getName());
		}
		if (this.studyCombo.getItems().size() > 0) {
			this.studyCombo.setValue(this.studyCombo.getItems().get(0));
		}
	}

	public void updateStudyCombo(final Study study) {
		final String selection = study.getName();
		this.studyCombo.getItems().add(selection);
		this.studyCombo.setValue(selection);
	}

	public void handleAddNewStudy() {
		this.app.showStudyContent(this);
	}

	private void handleRemoveAnimal() {
		this.mainStack.removeAnimal(this);
	}

	public Animal getAnimal(final Cohort cohort) {
		final Study study = this.app.getMouseDB().getStudy(
				this.studyCombo.getValue());
		if (study == null) {
			System.out.println("ERROR: study not saved correctly");
			return null;
		}
		final Animal animal = new Animal(-1, cohort, study,
		        this.specieField.getText(), this.strainField.getText(),
		        this.genotypeField.getText(), this.sourceField.getText(),
		        this.bDate.getValue(), this.sDate.getValue(),
		        this.irradiationCheck.isSelected(), this.checkDate.getValue());
		study.addAnimal(animal);
		return animal;
	}

	public void reset() {

	}
}
