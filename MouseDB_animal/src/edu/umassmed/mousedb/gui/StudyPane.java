package edu.umassmed.mousedb.gui;

import java.time.LocalDate;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Study;

public class StudyPane extends GridPane implements VocabularyContainer {

	private final MouseDBAnimalApplication app;

	private final TextField nameField, goalField;
	private final TextArea descriptionArea;
	private final DatePicker startDate, endDate;
	private final ComboBox<String> questionCombo;

	private AnimalPane currentAnimalPane;

	public StudyPane(final MouseDBAnimalApplication app) {
		this.app = app;
		this.currentAnimalPane = null;
		// this.setText("Add new experimenter");
		// this.setCollapsible(false);

		this.setPadding(new Insets(InsertStage.PADDING));
		this.setVgap(InsertStage.GAP);
		this.setHgap(InsertStage.GAP);

		final Label nameLabel = new Label("Name:");
		nameLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.nameField = new TextField();
		this.nameField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(nameLabel, 0, 0);
		this.add(this.nameField, 1, 0);

		final Label roleLabel = new Label("Question:");
		roleLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.questionCombo = new ComboBox<String>();
		this.createQuestionCombo();
		this.questionCombo.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(roleLabel, 0, 1);
		this.add(this.questionCombo, 1, 1);
		final Button addNew = new Button("New");
		addNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				StudyPane.this.handleAddNewQuestion();
			}
		});
		addNew.setPrefWidth(InsertStage.BUTTON_WIDTH);
		this.add(addNew, 2, 1);

		final Label startLabel = new Label("Start date:");
		startLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.startDate = new DatePicker(LocalDate.now());
		this.startDate.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(startLabel, 0, 3);
		this.add(this.startDate, 1, 3);

		final Label endLabel = new Label("End date:");
		endLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.endDate = new DatePicker(LocalDate.now());
		this.endDate.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(endLabel, 0, 4);
		this.add(this.endDate, 1, 4);

		final Label descriptionLabel = new Label("Description:");
		descriptionLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.descriptionArea = new TextArea();
		this.descriptionArea.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(descriptionLabel, 0, 5);
		this.add(this.descriptionArea, 1, 5, 1, 2);

		final Label goalLabel = new Label("Goal:");
		goalLabel.setPrefWidth(InsertStage.LABEL_WIDTH);
		this.goalField = new TextField();
		this.goalField.setPrefWidth(InsertStage.FIELD_WIDTH);
		this.add(goalLabel, 0, 7);
		this.add(this.goalField, 1, 7);

		final GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);

		final Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				StudyPane.this.handleCloseStudy(true);
			}
		});
		save.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(save, 1, 0);

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				StudyPane.this.handleCloseStudy(false);
			}
		});
		cancel.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(cancel, 2, 0);

		this.add(buttonPane, 0, 8, 3, 1);

		// this.setContent(gridPane);
	}

	private void createQuestionCombo() {
		this.questionCombo.getItems().clear();
		final Map<String, Long> questionMap = this.app.getMouseDB()
		        .getQuestions();
		for (final String question : questionMap.keySet()) {
			this.questionCombo.getItems().add(question);
		}
		this.questionCombo.setValue(this.questionCombo.getItems().get(0));
	}

	private void updateQuestionCombo(final String content) {
		this.questionCombo.getItems().add(content);
		this.questionCombo.setValue(content);
	}

	private void handleAddNewQuestion() {
		this.app.showVocabularyContent(this, 0, VocabularyPane.CONTENT_QUESTION);
	}

	private void handleCloseStudy(final boolean save) {
		if (save) {
			final Study study = new Study(-1, this.startDate.getValue(),
			        this.endDate.getValue(), this.nameField.getText(),
			        this.questionCombo.getValue(),
			        this.descriptionArea.getText(), this.goalField.getText());
			this.app.saveStudy(study);
			this.currentAnimalPane.updateStudyCombo(study);
		}
		this.app.closeStudyStage();
	}

	@Override
	public void updateVocabulary(final int index, final String content) {
		if (index == 0) {
			this.updateQuestionCombo(content);
		}
	}

	public void setCurrentAnimalPane(final AnimalPane animalPane) {
		this.currentAnimalPane = animalPane;
	}
}
