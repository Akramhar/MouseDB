package edu.umassmed.mousedb.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Animal;
import edu.umassmed.mousedb.data.Cohort;
import edu.umassmed.mousedb.data.Engraftment;
import edu.umassmed.mousedb.data.Preparation;

public class InsertStack extends BorderPane implements PreparationPaneContainer {

	private final MouseDBAnimalApplication app;

	private final CohortPane cohortPane;
	private final Map<EngraftmentPane, GridPane> engraftmentPanes;
	private final Map<EngraftmentPane, ScrollPane> engraftmentScrollPanes;
	private final Map<EngraftmentPane, List<PreparationPane>> preparationPanes;
	private final List<AnimalPane> animalPanes;
	private final GridPane mainGridPane;

	public InsertStack(final MouseDBAnimalApplication app) {
		this.app = app;

		this.engraftmentPanes = new LinkedHashMap<EngraftmentPane, GridPane>();
		this.engraftmentScrollPanes = new LinkedHashMap<EngraftmentPane, ScrollPane>();
		this.preparationPanes = new LinkedHashMap<EngraftmentPane, List<PreparationPane>>();
		this.animalPanes = new ArrayList<AnimalPane>();

		// this.setAlignment(Pos.TOP_LEFT);
		this.setPadding(new Insets(InsertStage.PADDING));
		// this.setHgap(10);
		// this.setVgap(10);

		final int width = (InsertStage.LABEL_WIDTH + InsertStage.FIELD_WIDTH
		        + InsertStage.BUTTON_WIDTH + (InsertStage.PADDING * 12) + (InsertStage.GAP * 3));

		this.cohortPane = new CohortPane(app, this);
		this.mainGridPane = new GridPane();
		this.mainGridPane.setAlignment(Pos.TOP_LEFT);
		this.mainGridPane.setPadding(new Insets(InsertStage.PADDING));
		this.mainGridPane.setHgap(10);
		this.mainGridPane.setVgap(10);
		this.mainGridPane.add(this.cohortPane, 0, 0);
		final ScrollPane scrollPane = new ScrollPane(this.mainGridPane);
		scrollPane.setPrefSize(width, 600);
		this.setCenter(scrollPane);

		final GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);

		final Button importFile = new Button("Import file");
		importFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				InsertStack.this.handleImportFile();
			}
		});
		importFile.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(importFile, 1, 0);

		final Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				InsertStack.this.handleClose(true);
			}
		});
		save.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(save, 2, 0);

		final Button close = new Button("Close");
		close.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				InsertStack.this.handleClose(false);
			}
		});
		close.setPrefWidth(InsertStage.BUTTON_WIDTH);
		buttonPane.add(close, 3, 0);
		this.setBottom(buttonPane);

		// this.engraftmentPane = new EngraftmentPane(app, this);
		// final ScrollPane scrollPane1 = new ScrollPane(this.engraftmentPane);
		// scrollPane1.setPrefSize(width, 600);
		// this.add(scrollPane1, 1, 0);
	}

	public void addOrganoidPrep(final EngraftmentPane engraftmentPane) {
		final OrganoidPrepPane prepPane = new OrganoidPrepPane(this.app, this);
		List<PreparationPane> prepList;
		if (this.preparationPanes.containsKey(engraftmentPane)) {
			prepList = this.preparationPanes.get(engraftmentPane);
		} else {
			prepList = new ArrayList<PreparationPane>();
		}
		final GridPane gridPane = this.engraftmentPanes.get(engraftmentPane);
		gridPane.add(prepPane, 0, prepList.size() + 1);
		prepList.add(prepPane);
		this.preparationPanes.put(engraftmentPane, prepList);
	}

	public void addCellPrep(final EngraftmentPane engraftmentPane) {
		final CellsPrepPane prepPane = new CellsPrepPane(this.app, this, false);
		List<PreparationPane> prepList;
		if (this.preparationPanes.containsKey(engraftmentPane)) {
			prepList = this.preparationPanes.get(engraftmentPane);
		} else {
			prepList = new ArrayList<PreparationPane>();
		}
		final GridPane gridPane = this.engraftmentPanes.get(engraftmentPane);
		gridPane.add(prepPane, 0, prepList.size() + 1);
		prepList.add(prepPane);
		this.preparationPanes.put(engraftmentPane, prepList);
	}

	public void addCellSelPrep(final EngraftmentPane engraftmentPane) {
		final CellsSelPrep prepPane = new CellsSelPrep(this.app, this);
		List<PreparationPane> prepList;
		if (this.preparationPanes.containsKey(engraftmentPane)) {
			prepList = this.preparationPanes.get(engraftmentPane);
		} else {
			prepList = new ArrayList<PreparationPane>();
		}
		final GridPane gridPane = this.engraftmentPanes.get(engraftmentPane);
		gridPane.add(prepPane, 0, prepList.size() + 1);
		prepList.add(prepPane);
		this.preparationPanes.put(engraftmentPane, prepList);
	}

	@Override
	public void removePreparation(final PreparationPane prepPane) {
		for (final EngraftmentPane engraftmentPane : this.preparationPanes
				.keySet()) {
			final List<PreparationPane> prepList = this.preparationPanes
					.get(engraftmentPane);
			if (prepList.contains(prepPane)) {
				this.preparationPanes.remove(prepPane);
				final GridPane gridPane = this.engraftmentPanes
						.get(engraftmentPane);
				gridPane.getChildren().remove(prepPane);
			}
		}
	}

	public void addEngraftment() {
		final EngraftmentPane engraftmentPane = new EngraftmentPane(this.app,
				this);
		final GridPane gridPane = new GridPane();
		this.engraftmentPanes.put(engraftmentPane, gridPane);
		gridPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setPadding(new Insets(InsertStage.PADDING));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.add(engraftmentPane, 0, 0);
		final ScrollPane scrollPane = new ScrollPane(gridPane);
		final int width = (InsertStage.LABEL_WIDTH + InsertStage.FIELD_WIDTH
				+ InsertStage.BUTTON_WIDTH + (InsertStage.PADDING * 12) + (InsertStage.GAP * 3));
		scrollPane.setPrefSize(width, 600);
		this.engraftmentScrollPanes.put(engraftmentPane, scrollPane);
		this.mainGridPane.add(scrollPane, this.engraftmentPanes.size() + 1, 0);
	}

	public void removeEngraftment(final EngraftmentPane engraftmentPane) {
		if (this.engraftmentScrollPanes.containsKey(engraftmentPane)) {
			final ScrollPane sp = this.engraftmentScrollPanes
					.get(engraftmentPane);
			this.getChildren().remove(sp);
			this.engraftmentScrollPanes.remove(engraftmentPane);
			this.engraftmentPanes.remove(engraftmentPane);
		}
	}

	public void addAnimal() {
		final AnimalPane animalPane = new AnimalPane(this.app, this);
		this.mainGridPane.add(animalPane, 0, this.animalPanes.size() + 1);
		this.animalPanes.add(animalPane);
	}

	public void removeAnimal(final AnimalPane animalPane) {
		if (this.animalPanes.contains(animalPane)) {
			this.mainGridPane.getChildren().remove(animalPane);
			this.animalPanes.remove(animalPane);
		}
	}

	private void handleImportFile() {
		this.app.showImportContent();
	}

	private void handleClose(final boolean save) {
		if (save) {
			final Cohort cohort = this.cohortPane.getCohort();
			if (cohort == null) {
				System.out.println("ERROR: cohort not saved correctly");
				return;
			}
			for (final AnimalPane animalPane : this.animalPanes) {
				final Animal animal = animalPane.getAnimal(cohort);
				if (animal == null) {
					System.out.println("ERROR: animal not saved correctly");
					return;
				}
				cohort.addAnimal(animal);
			}
			for (final EngraftmentPane engrafPane : this.engraftmentPanes
					.keySet()) {
				final Engraftment engraf = engrafPane.getEngraftment();
				if (engraf == null) {
					System.out
					.println("ERROR: engraftment not saved correctly");
					return;
				}
				for (final PreparationPane prepPane : this.preparationPanes
				        .get(engrafPane)) {
					final Preparation prep = prepPane.getPreparation();
					if (prep == null) {
						System.out
						.println("ERROR: preparation not saved correctly");
						return;
					}
					engraf.addPreparation(prep);
				}
				cohort.addEngraftment(engraf);
			}
			this.app.saveCohort(cohort);
		}
		try {
			this.app.stop();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
