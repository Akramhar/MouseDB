package edu.umassmed.mousedb.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;

public class VocabularyPane extends GridPane {

	public static final String CONTENT_PREP_TYPE = "preparation type";
	public static final String CONTENT_PREP_SOURCE = "preparation source";
	public static final String CONTENT_ROLE = "role";
	public static final String CONTENT_CELLS_PREP_NAME = "cells preparation name";
	public static final String CONTENT_CELLS_PREP_SOURCE = "cells preparation source";
	public static final String CONTENT_MARKER_NAME = "marker name";
	public static final String CONTENT_CELL_SEL_TYPE = "cells selection type";
	public static final String CONTENT_ORGAN = "organ";
	public static final String CONTENT_QUESTION = "question";

	private final MouseDBAnimalApplication app;

	private VocabularyContainer currentContainer;
	private int currentIndex;

	private final Label contentLabel;
	private final TextField contentField;

	private String contentType;

	public VocabularyPane(final MouseDBAnimalApplication app) {
		this.app = app;
		this.currentContainer = null;
		this.currentIndex = -1;
		// this.setText("Add new experimenter");
		// this.setCollapsible(false);

		this.setPadding(new Insets(InsertStage.PADDING));
		this.setVgap(InsertStage.GAP);
		this.setHgap(InsertStage.GAP);

		this.contentLabel = new Label("Content :");
		this.contentLabel.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		this.add(this.contentLabel, 0, 0);

		this.contentField = new TextField();
		this.contentField.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		this.add(this.contentField, 0, 1);

		final GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);

		final Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				VocabularyPane.this.handleCloseVocabulary(true);
			}
		});
		save.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(save, 1, 0);

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				VocabularyPane.this.handleCloseVocabulary(false);
			}
		});
		cancel.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(cancel, 2, 0);

		this.add(buttonPane, 0, 3, 2, 1);

		// this.setContent(gridPane);
	}

	public void setContent(final VocabularyContainer container,
	        final int index, final String contentType) {
		this.currentContainer = container;
		this.currentIndex = index;
		this.contentType = contentType;
		this.contentLabel.setText("Insert new " + contentType);
		this.contentField.clear();
	}

	private void handleCloseVocabulary(final boolean save) {
		if (save) {
			final String content = this.contentField.getText();
			if (this.contentType == VocabularyPane.CONTENT_ROLE) {
				this.app.saveRole(content);
			} else if (this.contentType == VocabularyPane.CONTENT_PREP_TYPE) {
				this.app.savePreparationType(content);
			} else if (this.contentType == VocabularyPane.CONTENT_PREP_SOURCE) {
				this.app.savePreparationSource(content);
			} else if (this.contentType == VocabularyPane.CONTENT_CELLS_PREP_NAME) {
				this.app.saveCellsPreparationName(content);
			} else if (this.contentType == VocabularyPane.CONTENT_CELLS_PREP_SOURCE) {
				this.app.saveCellsPreparationSource(content);
			} else if (this.contentType == VocabularyPane.CONTENT_MARKER_NAME) {
				this.app.saveMarkerName(content);
			} else if (this.contentType == VocabularyPane.CONTENT_ORGAN) {
				this.app.saveOrgan(content);
			} else if (this.contentType == VocabularyPane.CONTENT_QUESTION) {
				this.app.saveQuestion(content);
			} else if (this.contentType == VocabularyPane.CONTENT_CELL_SEL_TYPE) {
				this.app.saveCellSelType(content);
			}
			this.currentContainer.updateVocabulary(this.currentIndex, content);
		}
		this.app.closeVocabularyStage();
	}
}
