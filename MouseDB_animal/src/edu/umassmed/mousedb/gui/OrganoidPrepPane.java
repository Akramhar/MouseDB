package edu.umassmed.mousedb.gui;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.Organoid;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;

public class OrganoidPrepPane extends PreparationPane {

	private final ComboBox<String> organCombo;
	private final TextField sizeField, qualityField;

	public OrganoidPrepPane(final MouseDBAnimalApplication app,
	        final PreparationPaneContainer prepPaneContainer) {
		super(app, prepPaneContainer);

		this.setText("Organoid preparation");

		final Label organLabel = new Label("Organ:");
		organLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.organCombo = new ComboBox<String>();
		this.populateOrganCombo();
		this.organCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(organLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.organCombo, 1, this.compCount);
		final Button addNew1 = new Button("New");
		addNew1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				OrganoidPrepPane.this.handleAddNewOrgan();
			}
		});
		addNew1.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew1, 2, this.compCount);

		final Label sizeLabel = new Label("Size/Amount:");
		sizeLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.sizeField = new TextField();
		this.sizeField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(sizeLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.sizeField, 1, this.compCount);

		final Label qualityLabel = new Label("Quality:");
		qualityLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.qualityField = new TextField();
		this.qualityField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(qualityLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.qualityField, 1, this.compCount);

		this.mainGridPane.add(this.remove, 2, ++this.compCount);
	}

	private void handleAddNewOrgan() {
		this.app.showVocabularyContent(this, 2, VocabularyPane.CONTENT_ORGAN);
	}

	private void populateOrganCombo() {
		this.organCombo.getItems().clear();
		final Map<String, Long> organMap = this.app.getMouseDB().getOrgans();
		for (final String organ : organMap.keySet()) {
			this.organCombo.getItems().add(organ);
		}
		this.organCombo.setValue(this.organCombo.getItems().get(0));
	}

	private void updateOrganCombo(final String content) {
		this.organCombo.getItems().add(content);
		this.organCombo.setValue(content);
	}

	@Override
	public void updateVocabulary(final int index, final String content) {
		super.updateVocabulary(index, content);
		if (index == 2) {
			this.updateOrganCombo(content);
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
		final Organoid prep = new Organoid(-1, -1, this.organCombo.getValue(),
		        this.sizeField.getText(), this.qualityField.getText(),
		        this.typeCombo.getValue(), this.harvestDate.getValue(),
				(Experimenter) person, this.sourceCombo.getValue(),
				this.frozenCheck.isSelected(), this.protocolField.getText(),
				this.descriptionArea.getText());
		return prep;
	}
}
