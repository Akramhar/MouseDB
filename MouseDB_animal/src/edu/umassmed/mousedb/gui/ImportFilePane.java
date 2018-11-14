package edu.umassmed.mousedb.gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;

public class ImportFilePane extends GridPane {

	private final MouseDBAnimalApplication app;

	private final CheckBox dayFirstCheck;
	private final TextField selectedFileField;
	private final FileChooser fileChooser;

	private File selectedFile;

	public ImportFilePane(final MouseDBAnimalApplication app) {
		this.app = app;
		this.selectedFile = null;

		this.setPadding(new Insets(InsertStage.PADDING));
		this.setVgap(InsertStage.GAP);
		this.setHgap(InsertStage.GAP);

		final Label dayFirstLabel = new Label("Date start with day :");
		dayFirstLabel.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		this.add(dayFirstLabel, 0, 0);

		this.dayFirstCheck = new CheckBox();
		this.dayFirstCheck.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		this.add(this.dayFirstCheck, 1, 0);

		final Label chooseFileLabel = new Label("Select file");
		chooseFileLabel.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		this.add(chooseFileLabel, 0, 1);

		this.selectedFileField = new TextField();
		this.selectedFileField.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		this.add(this.selectedFileField, 1, 1);

		final Button fileChooser = new Button();
		fileChooser.setPrefWidth(InsertStage.LABEL_WIDTH * 2);
		fileChooser.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				ImportFilePane.this.handleFileChooser();
			}
		});
		fileChooser.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.add(fileChooser, 1, 2);

		final GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);

		final Button importFile = new Button("Start import");
		importFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				ImportFilePane.this.handleClose(true);
			}
		});
		importFile.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(importFile, 0, 0);

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				ImportFilePane.this.handleClose(false);
			}
		});
		cancel.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(cancel, 1, 0);

		this.add(buttonPane, 0, 3, 2, 1);

		this.fileChooser = new FileChooser();
		this.fileChooser.setTitle("Select file");
		this.fileChooser.setInitialDirectory(new File(System
		        .getProperty("user.home")));
		this.fileChooser.getExtensionFilters().addAll(
		        new FileChooser.ExtensionFilter("All files", "*.*"),
		        new FileChooser.ExtensionFilter("Excel old", "*.xls"),
		        new FileChooser.ExtensionFilter("Excel new", "*.xlsx"),
		        new FileChooser.ExtensionFilter("Test", "*.txt"));
	}

	private void handleFileChooser() {
		final FileChooser fc = new FileChooser();
		this.selectedFile = fc.showOpenDialog(this.app.getPrimaryStage());
		this.selectedFileField.setText(this.selectedFile.getAbsolutePath());
	}

	private void handleClose(final boolean importFile) {
		if (importFile) {
			if (!this.selectedFile.exists()) {
				System.out.println("IMPORT FILE: NOT EXISTS");
				return;
			}
			final String fileName = this.selectedFile.getName();
			final String ext = fileName.substring(
			        fileName.lastIndexOf(".") + 1, fileName.length());
			if (!ext.equals("txt") || !ext.equals("xls") || !ext.equals("xlsx")) {
				System.out.println("IMPORT FILE: CANNOT PROCESS");
				return;
			}
			this.app.importFile(this.selectedFile,
			        this.dayFirstCheck.isSelected());
		}
		this.app.closeImportFileStage();
	}
}
