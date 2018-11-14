package edu.umassmed.mousedb.gui;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;

public class PreparationPane extends TitledPane implements
        ExperimenterContainer, VocabularyContainer {

	protected final MouseDBAnimalApplication app;
	protected final PreparationPaneContainer prepPaneContainer;

	protected GridPane mainGridPane;
	protected Button remove;
	protected int compCount;

	protected final DatePicker harvestDate;
	protected final TextField protocolField;
	protected final CheckBox frozenCheck;
	protected final TextArea descriptionArea;
	protected final ComboBox<String> typeCombo, experimenterCombo, sourceCombo;

	public PreparationPane(final MouseDBAnimalApplication app,
	        final PreparationPaneContainer prepPaneContainer) {
		this.app = app;
		this.prepPaneContainer = prepPaneContainer;

		this.mainGridPane = new GridPane();
		this.mainGridPane.setPadding(new Insets(InsertStage.PADDING));
		this.mainGridPane.setVgap(InsertStage.GAP);
		this.mainGridPane.setHgap(InsertStage.GAP);

		final Label typeLabel = new Label("Type:");
		typeLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.typeCombo = new ComboBox<String>();
		this.populateTypeCombo();
		this.typeCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(typeLabel, 0, 0);
		this.mainGridPane.add(this.typeCombo, 1, 0);
		final Button addNew1 = new Button("New");
		addNew1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				PreparationPane.this.handleAddNewPrepType();
			}
		});
		addNew1.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew1, 2, 3);

		final Label harvestDateLabel = new Label("Harvest date:");
		harvestDateLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.harvestDate = new DatePicker();
		this.harvestDate.setValue(LocalDate.now());
		this.harvestDate.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(harvestDateLabel, 0, 1);
		this.mainGridPane.add(this.harvestDate, 1, 1);

		final Label experimenterLabel = new Label("Experimenter:");
		experimenterLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.experimenterCombo = new ComboBox<String>();
		this.populateExperimenterCombo();
		this.experimenterCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(experimenterLabel, 0, 2);
		this.mainGridPane.add(this.experimenterCombo, 1, 2);
		final Button addNew2 = new Button("New");
		addNew2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				PreparationPane.this.handleAddNewExperimenter();
			}
		});
		addNew2.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew2, 2, 2);

		final Label sourceLabel = new Label("Source:");
		sourceLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.sourceCombo = new ComboBox<String>();
		this.populateSourceCombo();
		this.sourceCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(sourceLabel, 0, 3);
		this.mainGridPane.add(this.sourceCombo, 1, 3);
		final Button addNew3 = new Button("New");
		addNew3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				PreparationPane.this.handleAddNewPrepSource();
			}
		});
		addNew3.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew3, 2, 3);

		final Label frozenLabel = new Label("Frozen:");
		sourceLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.frozenCheck = new CheckBox();
		this.frozenCheck.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(frozenLabel, 0, 4);
		this.mainGridPane.add(this.frozenCheck, 1, 4);

		final Label protocolLabel = new Label("Protocol:");
		protocolLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.protocolField = new TextField("");
		this.protocolField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(protocolLabel, 0, 5);
		this.mainGridPane.add(this.protocolField, 1, 5);

		final Label descriptionLabel = new Label("Description:");
		descriptionLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.descriptionArea = new TextArea("");
		this.descriptionArea.setWrapText(true);
		this.descriptionArea.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.descriptionArea.setPrefHeight(InsertStage.AREA_HEIGHT);
		this.mainGridPane.add(descriptionLabel, 0, 6);
		this.mainGridPane.add(this.descriptionArea, 1, 6);

		this.remove = new Button("Remove");
		this.remove.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				PreparationPane.this.handleRemovePreparation();
			}
		});

		this.compCount = 6;

		this.setContent(this.mainGridPane);
	}

	private void handleAddNewPrepType() {
		this.app.showVocabularyContent(this, 0,
				VocabularyPane.CONTENT_PREP_TYPE);
	}

	private void handleAddNewPrepSource() {
		this.app.showVocabularyContent(this, 1,
				VocabularyPane.CONTENT_PREP_SOURCE);
	}

	private void handleAddNewExperimenter() {
		this.app.showExperimenterContent(this, 0);
	}

	private void handleRemovePreparation() {
		this.prepPaneContainer.removePreparation(this);
	}

	public void populateExperimenterCombo() {
		this.experimenterCombo.getItems().clear();
		final List<Person> persons = this.app.getMouseDB().getPersons();
		for (final Person person : persons) {
			if (person instanceof Experimenter) {
				this.experimenterCombo.getItems().add(person.toString());
			}
		}
		this.experimenterCombo.setValue(this.experimenterCombo.getItems()
				.get(0));
	}

	public void updateExperimenterCombo(final Experimenter exp) {
		final String selection = exp.toString();
		this.experimenterCombo.getItems().add(selection);
		this.experimenterCombo.setValue(selection);
	}

	private void populateTypeCombo() {
		this.typeCombo.getItems().clear();
		final Map<String, Long> prepTypeMap = this.app.getMouseDB()
		        .getPrepTypes();
		for (final String prepType : prepTypeMap.keySet()) {
			this.typeCombo.getItems().add(prepType);
		}
		this.typeCombo.setValue(this.typeCombo.getItems().get(0));
	}

	private void updateTypeCombo(final String content) {
		this.typeCombo.getItems().add(content);
		this.typeCombo.setValue(content);
	}

	private void populateSourceCombo() {
		this.sourceCombo.getItems().clear();
		final Map<String, Long> prepSourceMap = this.app.getMouseDB()
		        .getPrepSources();
		for (final String prepSource : prepSourceMap.keySet()) {
			this.sourceCombo.getItems().add(prepSource);
		}
		this.sourceCombo.setValue(this.sourceCombo.getItems().get(0));
	}

	private void updateSourceCombo(final String content) {
		this.sourceCombo.getItems().add(content);
		this.sourceCombo.setValue(content);
	}

	@Override
	public void updateExperimenter(final int index, final Experimenter exp) {
		if (index == 0) {
			this.updateExperimenterCombo(exp);
		}
	}

	@Override
	public void updateVocabulary(final int index, final String content) {
		if (index == 0) {
			this.updateTypeCombo(content);
		} else if (index == 1) {
			this.updateSourceCombo(content);
		}
	}

	public Preparation getPreparation() {
		final Person person = this.app.getMouseDB().getPerson(
				this.experimenterCombo.getValue());
		if (person == null) {
			System.out.println("ERROR: person not saved correctly");
			return null;
		}
		final Preparation prep = new Preparation(-1, this.typeCombo.getValue(),
				this.harvestDate.getValue(), (Experimenter) person,
				this.sourceCombo.getValue(), this.frozenCheck.isSelected(),
				this.protocolField.getText(), this.descriptionArea.getText());
		return prep;
	}
}
