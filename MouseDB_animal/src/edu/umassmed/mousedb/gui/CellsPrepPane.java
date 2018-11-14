package edu.umassmed.mousedb.gui;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.CellsPreparation;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.Organoid;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;

public class CellsPrepPane extends PreparationPane implements
        PreparationPaneContainer {

	private OrganoidPrepPane organoidPrepPane;
	private final boolean inner;

	private final TextField specieField, totalCountField;
	private final ComboBox<String> nameCombo, sourceCombo;

	public CellsPrepPane(final MouseDBAnimalApplication app,
	        final PreparationPaneContainer prepPaneContainer,
			final boolean inner) {
		super(app, prepPaneContainer);
		this.inner = inner;
		this.setText("Cell preparation");

		final Label specieLabel = new Label("Specie:");
		specieLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.specieField = new TextField();
		this.specieField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(specieLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.specieField, 1, this.compCount);

		final Label nameLabel = new Label("Name:");
		nameLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.nameCombo = new ComboBox<String>();
		this.populateNameCombo();
		this.nameCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(nameLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.nameCombo, 1, this.compCount);
		final Button addNew1 = new Button("New");
		addNew1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CellsPrepPane.this.handleAddNewPrepName();
			}
		});
		addNew1.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew1, 2, this.compCount);

		final Label sourceLabel = new Label("Source:");
		sourceLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.sourceCombo = new ComboBox<String>();
		this.populateSourceCombo();
		this.sourceCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(sourceLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.sourceCombo, 1, this.compCount);
		final Button addNew2 = new Button("New");
		addNew2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CellsPrepPane.this.handleAddNewPrepSource();
			}
		});
		addNew2.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew2, 2, this.compCount);

		final Label totalCountLabel = new Label("Total count:");
		totalCountLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.totalCountField = new TextField();
		this.totalCountField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(totalCountLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.totalCountField, 1, this.compCount);

		final Button add = new Button("Add organoid");
		add.setPrefWidth(InsertStage.LABEL_WIDTH);
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CellsPrepPane.this.handleAddOrganoidPrep();
			}
		});
		this.mainGridPane.add(add, 2, ++this.compCount);

		if (!inner) {
			this.mainGridPane.add(this.remove, 2, ++this.compCount);
		}
	}

	private void handleAddNewPrepName() {
		this.app.showVocabularyContent(this, 2,
				VocabularyPane.CONTENT_CELLS_PREP_NAME);
	}

	private void handleAddNewPrepSource() {
		this.app.showVocabularyContent(this, 3,
				VocabularyPane.CONTENT_CELLS_PREP_SOURCE);
	}

	private void handleAddOrganoidPrep() {
		if (this.organoidPrepPane != null)
			return;
		if (!this.inner) {
			this.mainGridPane.getChildren().remove(this.remove);
			this.compCount--;
		}
		this.organoidPrepPane = new OrganoidPrepPane(this.app, this);
		this.mainGridPane.add(this.organoidPrepPane, 0, ++this.compCount, 3, 1);
		if (!this.inner) {
			this.mainGridPane.add(this.remove, 2, ++this.compCount);
		}
	}

	@Override
	public void removePreparation(final PreparationPane prepPane) {
		if (this.organoidPrepPane == null)
			return;
		this.mainGridPane.getChildren().remove(this.organoidPrepPane);
		this.organoidPrepPane = null;
	}

	private void populateNameCombo() {
		this.nameCombo.getItems().clear();
		final Map<String, Long> namesMap = this.app.getMouseDB()
		        .getCellsPrepNames();
		for (final String name : namesMap.keySet()) {
			this.nameCombo.getItems().add(name);
		}
		this.nameCombo.setValue(this.nameCombo.getItems().get(0));
	}

	private void updateNameCombo(final String content) {
		this.nameCombo.getItems().add(content);
		this.nameCombo.setValue(content);
	}

	private void populateSourceCombo() {
		this.sourceCombo.getItems().clear();
		final Map<String, Long> sourcesMap = this.app.getMouseDB()
		        .getCellsPrepSources();
		for (final String source : sourcesMap.keySet()) {
			this.sourceCombo.getItems().add(source);
		}
		this.sourceCombo.setValue(this.sourceCombo.getItems().get(0));
	}

	private void updateSourceCombo(final String content) {
		this.sourceCombo.getItems().add(content);
		this.sourceCombo.setValue(content);
	}

	@Override
	public void updateVocabulary(final int index, final String content) {
		super.updateVocabulary(index, content);
		if (index == 2) {
			this.updateNameCombo(content);
		} else if (index == 3) {
			this.updateSourceCombo(content);
		}
	}

	@Override
	public Preparation getPreparation() {
		final Person person = this.app.getMouseDB().getPerson(
				this.experimenterCombo.getValue());
		if (person == null) {
			System.out.println("ERROR: person not saved correctly");
			return null;
		}
		Organoid org = null;
		if (this.organoidPrepPane != null) {
			org = (Organoid) this.organoidPrepPane.getPreparation();
			if (org == null) {
				System.out.println("ERROR: organoid not saved correctly");
				return null;
			}
		}
		final CellsPreparation prep = new CellsPreparation(-1, -1, org,
		        this.nameCombo.getValue(), this.sourceCombo.getValue(),
		        this.specieField.getText(), this.totalCountField.getText(),
				this.typeCombo.getValue(), this.harvestDate.getValue(),
		        (Experimenter) person, super.sourceCombo.getValue(),
		        this.frozenCheck.isSelected(), this.protocolField.getText(),
		        this.descriptionArea.getText());
		return prep;
	}
}
