package edu.umassmed.mousedb.core;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import edu.umassmed.mousedb.data.Animal;
import edu.umassmed.mousedb.data.Cohort;
import edu.umassmed.mousedb.data.Engraftment;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.MouseDB;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;
import edu.umassmed.mousedb.data.Study;
import edu.umassmed.mousedb.gui.AnimalPane;
import edu.umassmed.mousedb.gui.BrowserStage;
import edu.umassmed.mousedb.gui.ExperimenterContainer;
import edu.umassmed.mousedb.gui.ExperimenterPane;
import edu.umassmed.mousedb.gui.ImportFilePane;
import edu.umassmed.mousedb.gui.InsertStage;
import edu.umassmed.mousedb.gui.StudyPane;
import edu.umassmed.mousedb.gui.VocabularyContainer;
import edu.umassmed.mousedb.gui.VocabularyPane;

public class MouseDBAnimalApplication extends Application {

	private final MouseDBMySQLGateway sqlGateway;
	private final MouseDB db;

	private final InsertStage insertStage;
	private final BrowserStage browserStage;
	private Stage expStage, studyStage, vocabularyStage, importFileStage;

	private MenuBar menu;

	private final ExperimenterPane experimenterPane;
	private final StudyPane studyPane;
	private final VocabularyPane vocabularyPane;
	private final ImportFilePane importFilePane;

	private Stage primaryStage;
	private boolean started;

	public MouseDBAnimalApplication() {
		this.db = new MouseDB();
		this.sqlGateway = new MouseDBMySQLGateway(this.db);
		final ServerInformation serverInfo = new ServerInformation(
				"146.189.76.56", 3306, "jeremy_mouse");
		final LoginCredentials loginCred = new LoginCredentials("omega", "1234");
		this.sqlGateway.setServerInformation(serverInfo);
		this.sqlGateway.setLoginCredentials(loginCred);
		this.loadVocabularyAndExperimenters();

		this.primaryStage = null;
		this.createMenu();
		this.insertStage = new InsertStage(this);
		this.browserStage = new BrowserStage(this);
		this.started = true;

		this.experimenterPane = new ExperimenterPane(this);
		this.studyPane = new StudyPane(this);
		this.vocabularyPane = new VocabularyPane(this);
		this.importFilePane = new ImportFilePane(this);
	}

	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	private void createMenu() {
		this.menu = new MenuBar();
		final Menu menuFile = new Menu("File");
		final MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				System.exit(0);
			}
		});
		menuFile.getItems().addAll(exit);
		final Menu menuView = new Menu("View");
		final MenuItem insertView = new MenuItem("Insert");
		insertView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				MouseDBAnimalApplication.this.handleShowInsertStage();
			}
		});
		final MenuItem browserView = new MenuItem("Browser");
		browserView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				MouseDBAnimalApplication.this.handleShowBrowserStage();
			}
		});
		menuView.getItems().addAll(insertView, browserView);
		this.menu.getMenus().addAll(menuFile, menuView);
	}

	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		if (this.started) {
			this.started = false;
			this.insertStage.setMenu(this.menu);
			this.primaryStage = this.insertStage;
		} else {
			this.primaryStage.close();
			this.primaryStage = primaryStage;
		}
		this.primaryStage.show();
	}

	public void showImportContent() {
		if (this.importFileStage == null) {
			this.importFileStage = new Stage();
			this.importFileStage.initModality(Modality.APPLICATION_MODAL);
			this.importFileStage.setTitle("Import data from file");
			this.importFileStage.setScene(new Scene(this.importFilePane));
		}
		this.importFileStage.show();
	}

	public void showVocabularyContent(final VocabularyContainer container,
			final int index, final String content) {
		if (this.vocabularyStage == null) {
			this.vocabularyStage = new Stage();
			this.vocabularyStage.initModality(Modality.APPLICATION_MODAL);
			this.vocabularyStage.setScene(new Scene(this.vocabularyPane));
		}
		this.vocabularyStage.setTitle("Add new " + content);
		this.vocabularyPane.setContent(container, index, content);
		this.vocabularyStage.show();
	}

	public void showStudyContent(final AnimalPane animalPane) {
		if (this.studyStage == null) {
			this.studyStage = new Stage();
			this.studyStage.setTitle("Add new study");
			this.studyStage.initModality(Modality.APPLICATION_MODAL);
			this.studyStage.setScene(new Scene(this.studyPane));
		}
		this.studyPane.setCurrentAnimalPane(animalPane);
		this.studyStage.show();
	}

	public void showExperimenterContent(final ExperimenterContainer container,
			final int index) {
		if (this.expStage == null) {
			this.expStage = new Stage();
			this.expStage.setTitle("Add new experimenter");
			this.expStage.initModality(Modality.APPLICATION_MODAL);
			this.expStage.setScene(new Scene(this.experimenterPane));
		}
		this.experimenterPane.setCurrentContainer(container, index);
		this.expStage.show();
	}

	public void closeExperimenterStage() {
		this.expStage.close();
	}

	public void closeStudyStage() {
		this.studyStage.close();
	}

	public void closeVocabularyStage() {
		this.vocabularyStage.close();
	}

	public void closeImportFileStage() {
		this.importFileStage.close();
	}

	public void center(final Stage stage) {
		final Screen screen = Screen.getPrimary();
		final Rectangle2D bounds = screen.getVisualBounds();

		stage.setWidth(600);
		stage.setHeight(400);

		stage.setX((bounds.getWidth() / 2) - (600 / 2));
		stage.setY((bounds.getHeight() / 2) - (400 / 2));
	}

	public MouseDB getMouseDB() {
		return this.db;
	}

	public void handleShowInsertStage() {
		if (this.primaryStage != this.insertStage) {
			this.browserStage.removeMenu();
			this.insertStage.setMenu(this.menu);
			this.start(this.insertStage);
		}
	}

	public void handleShowBrowserStage() {
		if (this.primaryStage != this.browserStage) {
			this.insertStage.removeMenu();
			this.browserStage.setMenu(this.menu);
			this.start(this.browserStage);
		}
	}

	public void savePerson(final Person person) {
		// TODO add controllo gia esistente
		this.connect();
		try {
			long personID;
			if (person instanceof Experimenter) {
				personID = this.sqlGateway
				        .saveExperimenter((Experimenter) person);
			} else {
				personID = this.sqlGateway.savePerson(person);
			}
			person.setId(personID);
			this.db.addPerson(person);

		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveStudy(final Study study) {
		this.connect();
		// TODO add controllo gia esistente
		try {
			final long studyID = this.sqlGateway.saveStudy(study);
			study.setId(studyID);
			this.db.addStudy(study);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void savePreparationType(final String prepType) {
		this.connect();
		try {
			final long prepTypeID = this.sqlGateway
					.savePreparationType(prepType);
			this.db.addPrepType(prepTypeID, prepType);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void savePreparationSource(final String prepSource) {
		this.connect();
		try {
			final long prepSourceID = this.sqlGateway
			        .savePreparationSource(prepSource);
			this.db.addPrepSource(prepSourceID, prepSource);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveRole(final String role) {
		this.connect();
		try {
			final long roleID = this.sqlGateway.saveRole(role);
			this.db.addRole(roleID, role);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveCellsPreparationName(final String cellsPrepName) {
		this.connect();
		try {
			final long cellsPrepNameID = this.sqlGateway
					.saveCellsPrepName(cellsPrepName);
			this.db.addCellsPrepName(cellsPrepNameID, cellsPrepName);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveCellsPreparationSource(final String cellsPrepSource) {
		this.connect();
		try {
			final long cellsPrepSourceID = this.sqlGateway
					.saveCellsPrepSource(cellsPrepSource);
			this.db.addCellsPrepSource(cellsPrepSourceID, cellsPrepSource);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveMarkerName(final String markerName) {
		this.connect();
		try {
			final long markerNameID = this.sqlGateway
					.saveMarkerName(markerName);
			this.db.addMarkerName(markerNameID, markerName);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveCellSelType(final String cellSelType) {
		this.connect();
		try {
			final long cellSelTypeID = this.sqlGateway
					.saveCellSelType(cellSelType);
			this.db.addCellSelType(cellSelTypeID, cellSelType);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveOrgan(final String organ) {
		this.connect();
		try {
			final long organID = this.sqlGateway.saveOrgan(organ);
			this.db.addOrgan(organID, organ);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveQuestion(final String question) {
		this.connect();
		try {
			final long questionID = this.sqlGateway.saveOrgan(question);
			this.db.addQuestion(questionID, question);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public void saveCohort(final Cohort cohort) {
		this.connect();
		try {
			final long cohortID = this.sqlGateway.saveCohort(cohort);
			cohort.setId(cohortID);
			this.db.addCohort(cohort);
		} catch (final SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		final List<Engraftment> engrafs = cohort.getEngraftments();

		for (final Engraftment engraf : engrafs) {
			try {
				final long engrafID = this.sqlGateway.saveEngraftment(engraf);
				engraf.setId(engrafID);
			} catch (final SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			final List<Preparation> preps = engraf.getPreparations();
			for (final Preparation prep : preps) {
				try {
					final long prepID = this.sqlGateway.savePreparation(prep);
					prep.setId(prepID);
				} catch (final SQLException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				if ((engraf.getId() != -1) && (prep.getId() != -1)) {
					try {
						this.sqlGateway.saveEngraftmentPrep(engraf.getId(),
								prep.getId());
					} catch (final SQLException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			}
		}

		final List<Animal> animals = cohort.getAnimals();
		for (final Animal animal : animals) {
			try {
				final long animalID = this.sqlGateway.saveAnimal(animal);
				animal.setId(animalID);
				this.db.addAnimal(animal);
			} catch (final SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		this.commit();
		this.disconnect();
	}

	public void loadCohorts() {
		this.connect();
		try {
			this.sqlGateway.loadCohorts();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.commit();
		this.disconnect();
	}

	public int getCohortForYear(final int year) {
		this.connect();
		int index = -1;
		try {
			index = this.sqlGateway.getCohortsForYear(year);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.disconnect();
		return index;
	}

	public void loadVocabularyAndExperimenters() {
		this.connect();
		try {
			this.sqlGateway.loadRoles();
			this.sqlGateway.loadPreparationTypes();
			this.sqlGateway.loadPreparationSources();
			this.sqlGateway.loadCellsNames();
			this.sqlGateway.loadCellsSources();
			this.sqlGateway.loadCellsSelType();
			this.sqlGateway.loadMarkers();
			this.sqlGateway.loadOrgans();
			this.sqlGateway.loadQuestions();

			this.sqlGateway.loadStudies();
			this.sqlGateway.loadExperimenters();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.disconnect();
	}

	public void commit() {
		try {
			this.sqlGateway.commit();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect() {
		try {
			this.sqlGateway.connect();
		} catch (final ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			this.sqlGateway.disconnect();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void importFile(final File selectedFile, final boolean dayFirst) {
		final FileImporter fi = new FileImporter(selectedFile, dayFirst);
		try {
			fi.importFile();
		} catch (final IOException e) {
			System.out.println("ERROR PROCESSING FILE");
			e.printStackTrace();
		}
		// fi.
	}
}
