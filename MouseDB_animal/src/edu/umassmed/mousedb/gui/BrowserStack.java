package edu.umassmed.mousedb.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;
import edu.umassmed.mousedb.data.Animal;

public class BrowserStack extends GridPane {

	private final MouseDBAnimalApplication app;

	private final AnimalTablePane animalTablePane;

	public BrowserStack(final MouseDBAnimalApplication app) {
		this.app = app;

		this.setAlignment(Pos.TOP_LEFT);
		this.setPadding(new Insets(InsertStage.PADDING));
		this.setHgap(10);
		this.setVgap(10);

		this.animalTablePane = new AnimalTablePane();
		this.add(this.animalTablePane, 0, 0);

		final Button load = new Button("Load cohorts");
		load.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				BrowserStack.this.handleLoadCohorts();
			}
		});

		this.add(load, 0, 1);
	}

	private void handleLoadCohorts() {
		this.app.loadCohorts();
		final ObservableList<Animal> list = FXCollections.observableArrayList();
		for (final Animal animal : this.app.getMouseDB().getAnimals()) {
			list.add(animal);
		}
		this.animalTablePane.setAnimals(list);
	}
}
