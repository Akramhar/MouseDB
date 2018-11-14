package edu.umassmed.mousedb.gui;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Experimenter;

public class ExperimenterPane extends GridPane implements VocabularyContainer {

	private final MouseDBAnimalApplication app;

	private ExperimenterContainer currentContainer;
	private int currentIndex;

	private final TextField firstNameField, lastNameField;
	private final ComboBox<String> roleCombo;

	public ExperimenterPane(final MouseDBAnimalApplication app) {
		this.app = app;
		this.currentContainer = null;
		this.currentIndex = -1;
		// this.setText("Add new experimenter");
		// this.setCollapsible(false);

		this.setPadding(new Insets(InsertStage.PADDING));
		this.setVgap(InsertStage.GAP);
		this.setHgap(InsertStage.GAP);

		final Label firstNameLabel = new Label("First name:");
		firstNameLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.firstNameField = new TextField();
		this.firstNameField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(firstNameLabel, 0, 0);
		this.add(this.firstNameField, 1, 0);

		final Label lastNameLabel = new Label("Last name:");
		lastNameLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.lastNameField = new TextField();
		this.lastNameField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(lastNameLabel, 0, 1);
		this.add(this.lastNameField, 1, 1);

		final Label roleLabel = new Label("Role:");
		roleLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.roleCombo = new ComboBox<String>();
		this.populateRoleCombo();
		this.roleCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(roleLabel, 0, 2);
		this.add(this.roleCombo, 1, 2);
		final Button addNew = new Button("New");
		addNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				ExperimenterPane.this.handleAddNewRole();
			}
		});
		addNew.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.add(addNew, 2, 2);

		final GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);

		final Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				ExperimenterPane.this.handleCloseExperimenter(true);
			}
		});
		save.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(save, 1, 0);

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				ExperimenterPane.this.handleCloseExperimenter(false);
			}
		});
		cancel.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(cancel, 2, 0);

		this.add(buttonPane, 0, 3, 2, 1);

		// this.setContent(gridPane);
	}

	private void populateRoleCombo() {
		this.roleCombo.getItems().clear();
		final Map<String, Long> rolesMap = this.app.getMouseDB().getRoles();
		for (final String role : rolesMap.keySet()) {
			this.roleCombo.getItems().add(role);
		}
		this.roleCombo.setValue(this.roleCombo.getItems().get(0));
	}

	private void updateRoleCombo(final String content) {
		this.roleCombo.getItems().add(content);
		this.roleCombo.setValue(content);
	}

	private void handleAddNewRole() {
		this.app.showVocabularyContent(this, 0, VocabularyPane.CONTENT_ROLE);
	}

	private void handleCloseExperimenter(final boolean save) {
		if (save) {
			final Experimenter exp = new Experimenter(-1, -1,
					this.firstNameField.getText(),
					this.lastNameField.getText(), this.roleCombo.getValue());
			this.app.savePerson(exp);
			this.currentContainer.updateExperimenter(this.currentIndex, exp);
		}
		this.app.closeExperimenterStage();
	}

	@Override
	public void updateVocabulary(final int index, final String content) {
		if (index == 0) {
			this.updateRoleCombo(content);
		}
	}

	public void setCurrentContainer(final ExperimenterContainer container,
	        final int index) {
		this.currentContainer = container;
		this.currentIndex = index;
		this.firstNameField.clear();
		this.lastNameField.clear();
	}
}
