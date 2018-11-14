package edu.umassmed.mousedb.gui;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.CellsPreparation;
import edu.umassmed.mousedb.data.CellsSelection;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;

public class CellsSelPrep extends PreparationPane {

	private final CellsPrepPane cellsPrepPane;
	private final ComboBox<String> markerCombo, selTypeCombo;

	public CellsSelPrep(final MouseDBAnimalApplication app,
	        final PreparationPaneContainer prepPaneContainer) {
		super(app, prepPaneContainer);
		this.setText("Cell selection preparation");

		final Label markerLabel = new Label("Marker:");
		markerLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.markerCombo = new ComboBox<String>();
		this.populateMarkerCombo();
		this.markerCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(markerLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.markerCombo, 1, this.compCount);
		final Button addNew1 = new Button("New");
		addNew1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CellsSelPrep.this.handleAddNewMarkerName();
			}
		});
		addNew1.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew1, 2, this.compCount);

		final Label selTypeLabel = new Label("Selection type:");
		selTypeLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.selTypeCombo = new ComboBox<String>();
		this.populateSelTypeCombo();
		this.selTypeCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.mainGridPane.add(selTypeLabel, 0, ++this.compCount);
		this.mainGridPane.add(this.selTypeCombo, 1, this.compCount);
		final Button addNew2 = new Button("New");
		addNew2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				CellsSelPrep.this.handleAddNewCellSelType();
			}
		});
		addNew2.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.mainGridPane.add(addNew2, 2, this.compCount);

		this.cellsPrepPane = new CellsPrepPane(app, prepPaneContainer, true);
		this.mainGridPane.add(this.cellsPrepPane, 0, ++this.compCount, 3, 1);

		this.mainGridPane.add(this.remove, 2, ++this.compCount);
	}

	private void handleAddNewMarkerName() {
		this.app.showVocabularyContent(this, 2,
				VocabularyPane.CONTENT_MARKER_NAME);
	}

	private void handleAddNewCellSelType() {
		this.app.showVocabularyContent(this, 3,
				VocabularyPane.CONTENT_CELL_SEL_TYPE);
	}

	private void populateMarkerCombo() {
		this.markerCombo.getItems().clear();
		final Map<String, Long> markerMap = this.app.getMouseDB()
				.getMarkerNames();
		for (final String marker : markerMap.keySet()) {
			this.markerCombo.getItems().add(marker);
		}
		this.markerCombo.setValue(this.markerCombo.getItems().get(0));
	}

	private void updateMarkerCombo(final String content) {
		this.markerCombo.getItems().add(content);
		this.markerCombo.setValue(content);
	}

	private void populateSelTypeCombo() {
		this.selTypeCombo.getItems().clear();
		final Map<String, Long> cellsSelTypeMap = this.app.getMouseDB()
				.getCellSelTypes();
		for (final String cellsSelType : cellsSelTypeMap.keySet()) {
			this.selTypeCombo.getItems().add(cellsSelType);
		}
		this.selTypeCombo.setValue(this.selTypeCombo.getItems().get(0));
	}

	private void updateSelTypeCombo(final String content) {
		this.selTypeCombo.getItems().add(content);
		this.selTypeCombo.setValue(content);
	}

	@Override
	public void updateVocabulary(final int index, final String content) {
		super.updateVocabulary(index, content);
		if (index == 2) {
			this.updateMarkerCombo(content);
		} else if (index == 3) {
			this.updateSelTypeCombo(content);
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
		final CellsPreparation cellsPrep = (CellsPreparation) this.cellsPrepPane
		        .getPreparation();
		if (cellsPrep == null) {
			System.out.println("ERROR: cells preparation not saved correctly");
			return null;
		}
		final CellsSelection prep = new CellsSelection(-1, -1, cellsPrep,
		        this.markerCombo.getValue(), this.selTypeCombo.getValue(),
		        this.typeCombo.getValue(), this.harvestDate.getValue(),
		        (Experimenter) person, this.sourceCombo.getValue(),
		        this.frozenCheck.isSelected(), this.protocolField.getText(),
		        this.descriptionArea.getText());
		return prep;
	}
}
