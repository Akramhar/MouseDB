package edu.umassmed.mousedb.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import edu.umassmed.mousedb.data.Animal;

public class AnimalTablePane extends ScrollPane {

	private final TableView<Animal> table;

	public AnimalTablePane() {
		this.table = new TableView<Animal>();

		final int tableWidth = (InsertStage.LABEL_WIDTH
		        + InsertStage.FIELD_WIDTH + InsertStage.BUTTON_WIDTH
		        + (InsertStage.PADDING * 10) + (InsertStage.GAP * 3)) * 2;
		this.table.setPrefWidth(tableWidth);

		this.table.setEditable(false);

		final TableColumn<Animal, Long> idCol = new TableColumn<Animal, Long>(
		        "ID");
		idCol.setCellValueFactory(new Callback<CellDataFeatures<Animal, Long>, ObservableValue<Long>>() {
			@Override
			public ObservableValue call(final CellDataFeatures<Animal, Long> p) {
				return new SimpleLongProperty(p.getValue().getId());
			}
		});
		final TableColumn<Animal, Long> idCohortCol = new TableColumn<Animal, Long>(
		        "Cohort ID");
		idCohortCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, Long>, ObservableValue<Long>>() {
			        @Override
			        public ObservableValue call(
			                final CellDataFeatures<Animal, Long> p) {
				        return new SimpleLongProperty(p.getValue().getCohort()
				                .getId());
			        }
		        });
		final TableColumn<Animal, String> specieCol = new TableColumn<Animal, String>(
		        "Specie");
		specieCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			        @Override
			        public ObservableValue<String> call(
			                final CellDataFeatures<Animal, String> p) {
				        return new SimpleStringProperty(p.getValue()
				                .getSpecie());
			        }
		        });
		final TableColumn<Animal, String> strainCol = new TableColumn<Animal, String>(
		        "Strain");
		strainCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			        @Override
			        public ObservableValue<String> call(
			                final CellDataFeatures<Animal, String> p) {
				        return new SimpleStringProperty(p.getValue()
				                .getStrain());
			        }
		        });
		final TableColumn<Animal, String> genotypeCol = new TableColumn<Animal, String>(
		        "Genotype");
		genotypeCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			        @Override
			        public ObservableValue<String> call(
			                final CellDataFeatures<Animal, String> p) {
				        return new SimpleStringProperty(p.getValue()
				                .getGenotype());
			        }
		        });
		final TableColumn<Animal, Boolean> irradiatedCol = new TableColumn<Animal, Boolean>(
		        "Irradiated");
		irradiatedCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, Boolean>, ObservableValue<Boolean>>() {
			        @Override
			        public ObservableValue<Boolean> call(
			                final CellDataFeatures<Animal, Boolean> p) {
				        return new SimpleBooleanProperty(p.getValue()
				                .isIrradiated());
			        }
		        });
		final TableColumn<Animal, String> sourceCol = new TableColumn<Animal, String>(
		        "Source");
		sourceCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			        @Override
			        public ObservableValue<String> call(
			                final CellDataFeatures<Animal, String> p) {
				        return new SimpleStringProperty(p.getValue()
				                .getSource());
			        }
		        });
		final TableColumn<Animal, String> dobCol = new TableColumn<Animal, String>(
		        "Birth date");
		dobCol.setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
			        final CellDataFeatures<Animal, String> p) {
				return new SimpleStringProperty(p.getValue().getDob()
				        .toString());
			}
		});
		final TableColumn<Animal, String> dosCol = new TableColumn<Animal, String>(
		        "Sacrifice date");
		dosCol.setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
			        final CellDataFeatures<Animal, String> p) {
				if (p.getValue().getDos() != null)
					return new SimpleStringProperty(p.getValue().getDos()
					        .toString());
				return null;
			}
		});
		final TableColumn<Animal, String> checkDateCol = new TableColumn<Animal, String>(
		        "Check date");
		checkDateCol
		        .setCellValueFactory(new Callback<CellDataFeatures<Animal, String>, ObservableValue<String>>() {
			        @Override
			        public ObservableValue<String> call(
			                final CellDataFeatures<Animal, String> p) {
				        return new SimpleStringProperty(p.getValue()
				                .getCheckDate().toString());
			        }
		        });

		this.table.getColumns().addAll(idCol, idCohortCol, specieCol,
		        strainCol, genotypeCol, irradiatedCol, sourceCol, dobCol,
		        dosCol, checkDateCol);

		this.setContent(this.table);
	}

	public void setAnimals(final ObservableList<Animal> animals) {
		this.table.setItems(animals);
	}
}
