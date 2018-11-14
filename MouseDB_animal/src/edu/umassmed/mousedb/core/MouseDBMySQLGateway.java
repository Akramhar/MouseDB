package edu.umassmed.mousedb.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.mousedb.data.Animal;
import edu.umassmed.mousedb.data.CellsPreparation;
import edu.umassmed.mousedb.data.CellsSelection;
import edu.umassmed.mousedb.data.Cohort;
import edu.umassmed.mousedb.data.Engraftment;
import edu.umassmed.mousedb.data.Experimenter;
import edu.umassmed.mousedb.data.MouseDB;
import edu.umassmed.mousedb.data.Organoid;
import edu.umassmed.mousedb.data.Person;
import edu.umassmed.mousedb.data.Preparation;
import edu.umassmed.mousedb.data.Study;

public class MouseDBMySQLGateway {

	public static String USER = "omega";
	public static String PSW = "1234";
	public static String HOSTNAME = "146.189.76.56";
	public static String PORT = "3306";
	public static String DB_NAME = "omega";

	private Connection connection;
	private ServerInformation serverInfo;
	private LoginCredentials loginCred;

	private final MouseDB mouseDB;

	public MouseDBMySQLGateway(final MouseDB mouseDB) {
		this.connection = null;
		this.serverInfo = null;
		this.loginCred = null;

		this.mouseDB = mouseDB;
	}

	public void setServerInformation(final ServerInformation serverInfo) {
		this.serverInfo = serverInfo;
	}

	public void setLoginCredentials(final LoginCredentials loginCred) {
		this.loginCred = loginCred;
	}

	public boolean isConnected() {
		return this.connection != null;
	}

	public void connect() throws ClassNotFoundException, SQLException {
		if (this.connection != null)
			throw new SQLException("Connection already present");
		if (this.serverInfo == null)
			throw new SQLException("Server information not set");
		if (this.loginCred == null)
			throw new SQLException("Login credentials not set");
		Class.forName("com.mysql.jdbc.Driver");

		this.connection = DriverManager.getConnection("jdbc:mysql://"
		        + this.serverInfo.getHostName() + ":"
		        + this.serverInfo.getPort() + "/" + this.serverInfo.getDBName()
		        + "?" + "user=" + this.loginCred.getUserName() + "&password="
		        + this.loginCred.getPassword());
		this.connection.setAutoCommit(false);
	}

	public void commit() throws SQLException {
		this.connection.commit();
	}

	public void rollback() throws SQLException {
		this.connection.rollback();
	}

	public void disconnect() throws SQLException {
		this.connection.close();
		this.connection = null;
	}

	private int insertAndGetId(final String query) throws SQLException {
		final Statement stat = this.connection.createStatement();
		final int error = stat.executeUpdate(query,
		        Statement.RETURN_GENERATED_KEYS);
		final ResultSet results = stat.getGeneratedKeys();
		if ((error != 1) || !results.next()) {
			results.close();
			// this.rollback();
			throw new SQLException("Any generated keys");
		}
		// this.commit();
		final int dbID = results.getInt(1);
		results.close();
		return dbID;
	}

	// ##########################################################
	// UTILITIES
	// ##########################################################
	public void loadCohorts() throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT id FROM cohort");
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		while (results1.next()) {
			final long cohortID = results1.getInt(1);
			final Cohort cohort = this.loadCohort(cohortID);
			final List<Long> engrafIDs = this.getEngraftmentIDs(cohortID);
			for (final long engrafID : engrafIDs) {
				final Engraftment engraf = this.loadEngraftment(engrafID);
				final Map<Long, String> prepIDs = this
				        .getPreparationIDs(engrafID);
				for (final long prepID : prepIDs.keySet()) {
					Preparation prep = null;
					final String prepType = prepIDs.get(prepID);
					if (prepType.equals("organoid")) {
						prep = this.loadPrepOrganoid(prepID);
					} else if (prepType.equals("cells preparation")) {
						prep = this.loadPrepCellsPrep(prepID);
					} else {
						prep = this.loadPrepCellsSel(prepID);
					}
					engraf.addPreparation(prep);
				}
				cohort.addEngraftment(engraf);
			}

			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT id,study_id FROM animal WHERE cohort_id=");
			query2.append(cohortID);
			final PreparedStatement stat2 = this.connection
			        .prepareStatement(query2.toString());
			final ResultSet results2 = stat2.executeQuery();
			while (results2.next()) {
				Study animalStudy = null;
				for (final Study study : this.mouseDB.getStudies()) {
					if (study.getId() == results2.getInt(2)) {
						animalStudy = study;
						break;
					}
				}
				// final Study study = this.loadStudy(results2.getInt(2));
				final Animal animal = this.loadAnimal(results2.getInt(1),
				        cohort, animalStudy);
				animalStudy.addAnimal(animal);
				cohort.addAnimal(animal);
				this.mouseDB.addAnimal(animal);
			}
			results2.close();
			this.mouseDB.addCohort(cohort);
		}
		results1.close();
	}

	// ##########################################################
	// COHORT ANIMAL STUDY
	// ##########################################################

	public void loadQuestions() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM question");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		while (results.next()) {
			final long id = results.getInt(1);
			final String question = results.getString(2);
			this.mouseDB.addQuestion(id, question);
		}
		results.close();
		// return roles;
	}

	public long saveQuestion(final String question) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO question (question) VALUES ('");
		query.append(question);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadStudies() throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT id FROM study");
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		while (results1.next()) {
			final long studyID = results1.getInt(1);
			final Study study = this.loadStudy(studyID);
			this.mouseDB.addStudy(study);
		}
	}

	public Study loadStudy(final long studyID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM study WHERE id=");
		query1.append(studyID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		final long questionID = results1.getInt(2);
		String question = null;
		final Map<String, Long> questions = this.mouseDB.getQuestions();
		for (final String quest : questions.keySet()) {
			final long questIdDb = questions.get(quest);
			if (questIdDb == questionID) {
				question = quest;
				break;
			}
		}
		final String name = results1.getString(3);
		final LocalDate startDate = results1.getDate(4).toLocalDate();
		final LocalDate endDate = results1.getDate(5).toLocalDate();
		// final String questionType = results1.getString(6);
		final String description = results1.getString(7);
		final String goal = results1.getString(8);
		final Study study = new Study(studyID, startDate, endDate, name,
				question, description, goal);
		return study;
	}

	public long saveStudy(final Study study) throws SQLException {
		if (!this.mouseDB.getQuestions().containsKey(study.getQuestion())) {
			System.out.println("ERROR: question not present");
			return -1;
		}
		final long questionID = this.mouseDB.getQuestions().get(
		        study.getQuestion());
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO study (name, question_id, startDate, endDate, description, goal) VALUES ('");
		query.append(study.getName());
		query.append("',");
		query.append(questionID);
		query.append(",'");
		query.append(Date.valueOf(study.getStart()));
		query.append("','");
		query.append(Date.valueOf(study.getEnd()));
		query.append("','");
		query.append(study.getDescription());
		query.append("','");
		query.append(study.getGoal());
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public long saveAnimal(final Animal animal) throws SQLException {
		if (animal.getCohort().getId() == -1) {
			System.out.println("ERROR: cohort not save correctly");
			return -1;
		}
		if (animal.getStudy().getId() == -1) {
			System.out.println("ERROR: study not save correctly");
			return -1;
		}
		final StringBuffer query = new StringBuffer();
		if (animal.getDos() != null) {
			query.append("INSERT INTO animal (cohort_id, study_id, specie, strain, genotype, irradiation, source, DOB, DOS, checkDate) VALUES (");
		} else {
			query.append("INSERT INTO animal (cohort_id, study_id, specie, strain, genotype, irradiation, source, DOB, checkDate) VALUES (");
		}
		query.append(animal.getCohort().getId());
		query.append(",");
		query.append(animal.getStudy().getId());
		query.append(",'");
		query.append(animal.getSpecie());
		query.append("','");
		query.append(animal.getStrain());
		query.append("','");
		query.append(animal.getGenotype());
		query.append("',");
		query.append(animal.isIrradiated());
		query.append(",'");
		query.append(animal.getSource());
		query.append("','");
		query.append(Date.valueOf(animal.getDob()));
		if (animal.getDos() != null) {
			query.append("','");
			query.append(Date.valueOf(animal.getDos()));
		}
		query.append("','");
		query.append(Date.valueOf(animal.getCheckDate()));
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public Animal loadAnimal(final long animalID, final Cohort cohort,
	        final Study study) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM animal WHERE id=");
		query1.append(animalID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		// long cohortID = results1.getInt(3);
		final String specie = results1.getString(4);
		final String strain = results1.getString(5);
		final String genotype = results1.getString(6);
		final boolean irradiated = results1.getBoolean(7);
		final String source = results1.getString(8);
		final LocalDate dob = results1.getDate(9).toLocalDate();
		final Date sDate = results1.getDate(10);
		LocalDate dos = null;
		if (sDate != null) {
			dos = sDate.toLocalDate();
		}
		final LocalDate checkDate = results1.getDate(11).toLocalDate();
		final Animal animal = new Animal(animalID, cohort, study, specie,
		        strain, genotype, source, dob, dos, irradiated, checkDate);
		return animal;
	}

	public long saveCohort(final Cohort cohort) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO cohort (date, index) VALUES ('");
		query.append(Date.valueOf(cohort.getDate()));
		query.append("',");
		query.append(cohort.getIndex());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public Cohort loadCohort(final long cohortID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM cohort WHERE id=");
		query1.append(cohortID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		final LocalDate date = results1.getDate(2).toLocalDate();
		final int index = results1.getInt(3);
		final Cohort cohort = new Cohort(cohortID, date, index);
		return cohort;
	}

	public int getCohortsForYear(final int year) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM cohort WHERE year(date)=");
		query1.append(year);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		int index = 0;
		while (results1.next()) {
			index++;
		}
		return index;
	}

	public List<Long> getEngraftmentIDs(final long cohortID)
	        throws SQLException {
		final List<Long> engrafIDs = new ArrayList<Long>();
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM cohortEngrafMap WHERE cohort_id=");
		query1.append(cohortID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		while (results1.next()) {
			final long prepID = results1.getInt(2);
			engrafIDs.add(prepID);
		}
		results1.close();
		return engrafIDs;
	}

	// ##########################################################
	// ENGRAFTMENT
	// ##########################################################

	public long saveEngraftment(final Engraftment engraftment)
	        throws SQLException {
		long personID = engraftment.getExperimenter().getId();
		if (personID == -1) {
			personID = this.savePerson(engraftment.getExperimenter());
		}
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO engraftment (experimenter_id, date, route, method) VALUES (");
		query.append(personID);
		query.append(",'");
		query.append(Date.valueOf(engraftment.getDate()));
		query.append("','");
		query.append(engraftment.getRoute());
		query.append("','");
		query.append(engraftment.getMethod());
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public Engraftment loadEngraftment(final long engraftmentID)
	        throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM engraftment WHERE id=");
		query1.append(engraftmentID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		final long expID = results1.getInt(2);
		Experimenter exp = null;
		final List<Person> persons = this.mouseDB.getPersons();
		for (final Person person : persons) {
			if (!(person instanceof Experimenter)) {
				continue;
			}
			final Experimenter e = (Experimenter) person;
			if (e.getSpecificId() == expID) {
				exp = e;
				break;
			}
		}
		final LocalDate date = results1.getDate(3).toLocalDate();
		final String route = results1.getString(4);
		final String method = results1.getString(5);
		results1.close();
		final Engraftment engraftment = new Engraftment(engraftmentID, date,
		        route, method, exp);
		return engraftment;
	}

	public void saveEngraftmentPrep(final long engraftmentID, final long prepID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO engraftmentPreparationMap (engraftment_id, preparation_id) VALUES (");
		query.append(engraftmentID);
		query.append(",");
		query.append(prepID);
		query.append(")");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		stat.executeUpdate();
		stat.close();
	}

	public Map<Long, String> getPreparationIDs(final long engraftmentID)
	        throws SQLException {
		final Map<Long, String> prepIDs = new LinkedHashMap<Long, String>();
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM engraftmentPreparationMap WHERE engraftment_id=");
		query1.append(engraftmentID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		while (results1.next()) {
			final long prepID = results1.getInt(2);
			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT preparationType_id FROM preparation WHERE id=");
			query2.append(prepID);
			final PreparedStatement stat2 = this.connection
			        .prepareStatement(query2.toString());
			final ResultSet results2 = stat2.executeQuery();
			if (!results2.next()) {
				results2.close();
				results1.close();
				return null;
			}
			String specPrepType = null;
			final Map<String, Long> prepTypes = this.mouseDB.getPrepTypes();
			for (final String prepType : prepTypes.keySet()) {
				final long prepTypeIdDb = prepTypes.get(prepType);
				if (prepTypeIdDb == results2.getInt(1)) {
					specPrepType = prepType;
					break;
				}
			}
			results2.close();
			prepIDs.put(prepID, specPrepType);
		}
		results1.close();
		return prepIDs;
	}

	// ##########################################################
	// PREPARATION
	// ##########################################################

	public long savePrepCellsSel(final CellsSelection cellsSel)
	        throws SQLException {
		if (!this.mouseDB.getMarkerNames().containsKey(cellsSel.getMarker())) {
			System.out.println("ERROR: marker not present");
			return -1;
		}
		if (!this.mouseDB.getCellSelTypes().containsKey(cellsSel.getSelType())) {
			System.out.println("ERROR: sel type not present");
			return -1;
		}
		final long prepID = this.savePreparation(cellsSel);
		final long cellsPrepID = this.savePrepCellsPrep(cellsSel
		        .getCellsPreparation());
		final long markerID = this.mouseDB.getMarkerNames().get(
		        cellsSel.getMarker());
		final long selTypeID = this.mouseDB.getCellSelTypes().get(
		        cellsSel.getSelType());
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO preparation (preparation_id, cellsPrep_id, markers_id, selectionType_id) VALUES (");
		query.append(prepID);
		query.append(",");
		query.append(cellsPrepID);
		query.append(",");
		query.append(markerID);
		query.append(",");
		query.append(selTypeID);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public long savePrepCellsPrep(final CellsPreparation cellsPrep)
	        throws SQLException {
		if (!this.mouseDB.getCellsPrepNames().containsKey(cellsPrep.getName())) {
			System.out.println("ERROR: cells prep name not present");
			return -1;
		}
		if (!this.mouseDB.getCellsPrepSources().containsKey(
		        cellsPrep.getSource())) {
			System.out.println("ERROR: cells prep source not present");
			return -1;
		}
		final long prepID = this.savePreparation(cellsPrep);
		Long orgID = null;
		if (cellsPrep.getOrganoid() != null) {
			orgID = cellsPrep.getOrganoid().getSpecificId();
		}
		final long cellsPrepNameID = this.mouseDB.getCellsPrepNames().get(
		        cellsPrep.getName());
		final long cellsPrepSourceID = this.mouseDB.getCellsPrepSources().get(
		        cellsPrep.getCellsPrepSource());
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO preparation (organoidPrep_id, preparation_id, specie, cellsName_id, cellsSource_id, totCount) VALUES (");
		query.append(orgID);
		query.append(",");
		query.append(prepID);
		query.append(",");
		query.append(cellsPrep.getSpecie());
		query.append(",");
		query.append(cellsPrepNameID);
		query.append(",");
		query.append(cellsPrepSourceID);
		query.append(",'");
		query.append(String.valueOf(cellsPrep.getTotalCount()));
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public long savePrepOrganoid(final Organoid org) throws SQLException {
		if (!this.mouseDB.getOrgans().containsKey(org.getOrgan())) {
			System.out.println("ERROR: organ name not present");
			return -1;
		}
		final long prepID = this.savePreparation(org);
		final long organID = this.mouseDB.getOrgans().get(org.getOrgan());
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO preparation (preparation_id, organ_id, size, quality) VALUES (");
		query.append(prepID);
		query.append(",");
		query.append(organID);
		query.append(",'");
		query.append(String.valueOf(org.getSize()));
		query.append("','");
		query.append(String.valueOf(org.getQuality()));
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public CellsSelection loadPrepCellsSel(final long prepID)
	        throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM cellsSel WHERE preparation_id=");
		query1.append(prepID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		final long cellsSelID = results1.getInt(1);
		final long cellsPrepID = results1.getInt(3);
		CellsPreparation cellsPrep = null;
		if (cellsPrepID != 0) {
			cellsPrep = this.loadPrepCellsPrep(cellsPrepID);
		}
		final long markerID = results1.getInt(5);
		String prepMarker = null;
		final Map<String, Long> markers = this.mouseDB.getMarkerNames();
		for (final String marker : markers.keySet()) {
			final long markerIdDb = markers.get(marker);
			if (markerIdDb == markerID) {
				prepMarker = marker;
				break;
			}
		}
		final long selectionTypeID = results1.getInt(6);
		String prepSelType = null;
		final Map<String, Long> selTypes = this.mouseDB.getCellSelTypes();
		for (final String selType : selTypes.keySet()) {
			final long selTypeIdDb = selTypes.get(selType);
			if (selectionTypeID == selTypeIdDb) {
				prepSelType = selType;
				break;
			}
		}
		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM preparation WHERE id=");
		query2.append(prepID);
		final PreparedStatement stat2 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results2 = stat2.executeQuery();
		if (!results2.next()) {
			results1.close();
			results2.close();
			return null;
		}
		final long prepTypeID = results2.getInt(2);
		String prepCellsPrepType = null;
		final Map<String, Long> prepTypes = this.mouseDB.getPrepTypes();
		for (final String prepType : prepTypes.keySet()) {
			final long prepTypeIdDb = prepTypes.get(prepType);
			if (prepTypeIdDb == prepTypeID) {
				prepCellsPrepType = prepType;
				break;
			}
		}
		final long expID = results2.getInt(3);
		Experimenter exp = null;
		final List<Person> persons = this.mouseDB.getPersons();
		for (final Person person : persons) {
			if (!(person instanceof Experimenter)) {
				continue;
			}
			final Experimenter e = (Experimenter) person;
			if (e.getSpecificId() == expID) {
				exp = e;
				break;
			}
		}
		final LocalDate harvestDate = results2.getDate(4).toLocalDate();
		final long prepSourceID = results2.getInt(5);
		String prepCellsPrepSource = null;
		final Map<String, Long> prepSources = this.mouseDB.getPrepSources();
		for (final String prepSource : prepSources.keySet()) {
			final long prepSourceIdDb = prepSources.get(prepSource);
			if (prepSourceIdDb == prepSourceID) {
				prepCellsPrepSource = prepSource;
				break;
			}
		}
		final boolean isFrozen = results2.getBoolean(6);
		final String protocol = results2.getString(7);
		final String description = results2.getString(8);
		final CellsSelection cellsSel = new CellsSelection(prepID, cellsSelID,
		        cellsPrep, prepMarker, prepSelType, prepCellsPrepType,
				harvestDate, exp, prepCellsPrepSource, isFrozen, protocol,
				description);
		results2.close();
		results1.close();
		return cellsSel;
	}

	public CellsPreparation loadPrepCellsPrep(final long prepID)
	        throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM cellsPrep WHERE preparation_id=");
		query1.append(prepID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		final long cellsPrepID = results1.getInt(1);
		final long organoidPrepID = results1.getInt(3);
		Organoid org = null;
		if (organoidPrepID != 0) {
			org = this.loadPrepOrganoid(organoidPrepID);
		}
		final String specie = results1.getString(4);
		final long cellsNameID = results1.getInt(5);
		String prepCellsName = null;
		final Map<String, Long> cellsNames = this.mouseDB.getCellsPrepNames();
		for (final String cellName : cellsNames.keySet()) {
			final long cellsNameIdDb = cellsNames.get(cellName);
			if (cellsNameIdDb == cellsNameID) {
				prepCellsName = cellName;
				break;
			}
		}
		final long cellsSourceID = results1.getInt(6);
		String prepCellsSource = null;
		final Map<String, Long> cellsSources = this.mouseDB
		        .getCellsPrepSources();
		for (final String cellsSource : cellsSources.keySet()) {
			final long cellsSourceIdDb = cellsSources.get(cellsSource);
			if (cellsSourceIdDb == cellsSourceID) {
				prepCellsSource = cellsSource;
				break;
			}
		}
		final String totCount = results1.getString(7);
		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM preparation WHERE id=");
		query2.append(prepID);
		final PreparedStatement stat2 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results2 = stat2.executeQuery();
		if (!results2.next()) {
			results1.close();
			results2.close();
			return null;
		}
		final long prepTypeID = results2.getInt(2);
		String prepCellsPrepType = null;
		final Map<String, Long> prepTypes = this.mouseDB.getPrepTypes();
		for (final String prepType : prepTypes.keySet()) {
			final long prepTypeIdDb = prepTypes.get(prepType);
			if (prepTypeIdDb == prepTypeID) {
				prepCellsPrepType = prepType;
				break;
			}
		}
		final long expID = results2.getInt(3);
		Experimenter exp = null;
		final List<Person> persons = this.mouseDB.getPersons();
		for (final Person person : persons) {
			if (!(person instanceof Experimenter)) {
				continue;
			}
			final Experimenter e = (Experimenter) person;
			if (e.getSpecificId() == expID) {
				exp = e;
				break;
			}
		}
		final LocalDate harvestDate = results2.getDate(4).toLocalDate();
		final long prepSourceID = results2.getInt(5);
		String prepCellsPrepSource = null;
		final Map<String, Long> prepSources = this.mouseDB.getPrepSources();
		for (final String prepSource : prepSources.keySet()) {
			final long prepSourceIdDb = prepSources.get(prepSource);
			if (prepSourceIdDb == prepSourceID) {
				prepCellsPrepSource = prepSource;
				break;
			}
		}
		final boolean isFrozen = results2.getBoolean(6);
		final String protocol = results2.getString(7);
		final String description = results2.getString(8);
		final CellsPreparation cellsPrep = new CellsPreparation(prepID,
		        cellsPrepID, org, prepCellsName, prepCellsSource, specie,
		        totCount, prepCellsPrepType, harvestDate, exp,
		        prepCellsPrepSource, isFrozen, protocol, description);
		results2.close();
		results1.close();
		return cellsPrep;
	}

	public Organoid loadPrepOrganoid(final long prepID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM organoidPrep WHERE preparation_id=");
		query1.append(prepID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		// final List<String> roles = new ArrayList<String>();
		if (!results1.next()) {
			results1.close();
			return null;
		}
		final long organoidID = results1.getInt(1);
		final long organID = results1.getInt(3);
		String prepOrgan = null;
		final Map<String, Long> organs = this.mouseDB.getOrgans();
		for (final String organ : organs.keySet()) {
			final long organIdDb = organs.get(organ);
			if (organIdDb == organID) {
				prepOrgan = organ;
				break;
			}
		}
		final String size = results1.getString(4);
		final String quality = results1.getString(5);
		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM preparation WHERE id=");
		query2.append(prepID);
		final PreparedStatement stat2 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results2 = stat2.executeQuery();
		if (!results2.next()) {
			results2.close();
			return null;
		}
		final long prepTypeID = results2.getInt(2);
		String prepOrgType = null;
		final Map<String, Long> prepTypes = this.mouseDB.getPrepTypes();
		for (final String prepType : prepTypes.keySet()) {
			final long prepTypeIdDb = prepTypes.get(prepType);
			if (prepTypeIdDb == prepTypeID) {
				prepOrgType = prepType;
				break;
			}
		}
		final long expID = results2.getInt(3);
		Experimenter exp = null;
		final List<Person> persons = this.mouseDB.getPersons();
		for (final Person person : persons) {
			if (!(person instanceof Experimenter)) {
				continue;
			}
			final Experimenter e = (Experimenter) person;
			if (e.getSpecificId() == expID) {
				exp = e;
				break;
			}
		}
		final LocalDate harvestDate = results2.getDate(4).toLocalDate();
		final long prepSourceID = results2.getInt(5);
		String prepOrgSource = null;
		final Map<String, Long> prepSources = this.mouseDB.getPrepSources();
		for (final String prepSource : prepSources.keySet()) {
			final long prepSourceIdDb = prepSources.get(prepSource);
			if (prepSourceIdDb == prepSourceID) {
				prepOrgSource = prepSource;
				break;
			}
		}
		final boolean isFrozen = results2.getBoolean(6);
		final String protocol = results2.getString(7);
		final String description = results2.getString(8);
		final Organoid org = new Organoid(prepID, organoidID, prepOrgan, size,
		        quality, prepOrgType, harvestDate, exp, prepOrgSource,
				isFrozen, protocol, description);
		results2.close();
		results1.close();
		return org;
	}

	public long savePreparation(final Preparation prep) throws SQLException {
		if (!this.mouseDB.getPrepTypes().containsKey(prep.getType())) {
			System.out.println("ERROR: type not present");
			return -1;
		}
		if (!this.mouseDB.getPrepSources().containsKey(prep.getSource())) {
			System.out.println("ERROR: source not present");
			return -1;
		}
		long personID = prep.getExperimenter().getId();
		if (personID == -1) {
			personID = this.savePerson(prep.getExperimenter());
		}
		final long prepTypeID = this.mouseDB.getPrepTypes().get(prep.getType());
		final long prepSourceID = this.mouseDB.getPrepSources().get(
		        prep.getSource());
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO preparation (preparationType_id, experimenter_id, harvestDate, prepSource_id, frozen, protocol, description) VALUES (");
		query.append(prepTypeID);
		query.append(",");
		query.append(personID);
		query.append(",'");
		query.append(Date.valueOf(prep.getHarvestDate()));
		query.append("',");
		query.append(prepSourceID);
		query.append(",");
		query.append(prep.isFrozen());
		query.append(",'");
		query.append(prep.getProtocol());
		query.append("','");
		query.append(prep.getDescription());
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadCellsSelType() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM selectionType");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String cellSelType = results.getString(2);
			this.mouseDB.addCellSelType(id, cellSelType);
		}
		results.close();
	}

	public long saveCellSelType(final String cellSelType) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO selectionType (type) VALUES ('");
		query.append(cellSelType);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadMarkers() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM marker");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String marker = results.getString(2);
			this.mouseDB.addMarkerName(id, marker);
		}
		results.close();
	}

	public long saveMarkerName(final String marker) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO marker (name) VALUES ('");
		query.append(marker);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadCellsSources() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM cellsSource");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String cellsSource = results.getString(2);
			this.mouseDB.addCellsPrepSource(id, cellsSource);
		}
		results.close();
	}

	public long saveCellsPrepSource(final String cellsPrepSource)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO cellsSource (name) VALUES ('");
		query.append(cellsPrepSource);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadCellsNames() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM cellsName");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String cellsName = results.getString(2);
			this.mouseDB.addCellsPrepName(id, cellsName);
		}
		results.close();
	}

	public long saveCellsPrepName(final String cellsPrepName)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO cellsName (name) VALUES ('");
		query.append(cellsPrepName);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadOrgans() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM organ");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String organ = results.getString(2);
			this.mouseDB.addOrgan(id, organ);
		}
		results.close();
	}

	public long saveOrgan(final String organ) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO organ (name) VALUES ('");
		query.append(organ);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadPreparationSources() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM preparationSource");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String prepSource = results.getString(2);
			this.mouseDB.addPrepSource(id, prepSource);
		}
		results.close();
	}

	public long savePreparationSource(final String prepSource)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO preparationSource (name) VALUES ('");
		query.append(prepSource);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadPreparationTypes() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM preparationType");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String prepType = results.getString(2);
			this.mouseDB.addPrepType(id, prepType);
		}
		results.close();
	}

	public long savePreparationType(final String prepType) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO preparationType (name) VALUES ('");
		query.append(prepType);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	// ##########################################################
	// PERSON - EXPERIMENTER - ROLE
	// ##########################################################

	public void loadRoles() throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM role");
		final PreparedStatement stat = this.connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		while (results.next()) {
			final long id = results.getInt(1);
			final String role = results.getString(2);
			this.mouseDB.addRole(id, role);
		}
		results.close();
	}

	public long saveRole(final String role) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO role (name) VALUES ('");
		query.append(role);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public void loadExperimenters() throws SQLException, ParseException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM experimenter");
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
		        .toString());
		final ResultSet results1 = stat1.executeQuery();
		while (results1.next()) {
			final long id = results1.getInt(1);
			final long personId = results1.getInt(2);
			final long roleId = results1.getInt(3);
			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT * FROM person WHERE id=");
			query2.append(personId);
			final PreparedStatement stat2 = this.connection
			        .prepareStatement(query2.toString());
			final ResultSet results2 = stat2.executeQuery();
			if (!results2.next()) {
				System.out.println("Person not found for experimenter: " + id);
				results2.close();
				break;
			}
			final String firstName = results2.getString(2);
			final String lastName = results2.getString(3);
			results2.close();
			String expRole = null;
			final Map<String, Long> roles = this.mouseDB.getRoles();
			for (final String role : roles.keySet()) {
				final long roleIdDb = roles.get(role);
				if (roleIdDb == roleId) {
					expRole = role;
					break;
				}
			}
			final Experimenter exp = new Experimenter(personId, id, firstName,
			        lastName, expRole);
			this.mouseDB.addPerson(exp);
		}
		results1.close();
	}

	public long saveExperimenter(final Experimenter experimenter)
	        throws SQLException {
		if (!this.mouseDB.getRoles().containsKey(experimenter.getRole())) {
			System.out.println("ERROR: role not present");
			return -1;
		}
		final long personID = this.savePerson(experimenter);
		final long roleID = this.mouseDB.getRoles().get(experimenter.getRole());
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO experimenter (person_id, role_id) VALUES (");
		query.append(personID);
		query.append(",");
		query.append(roleID);
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	public long savePerson(final Person person) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO person (firstName, lastName) VALUES ('");
		query.append(person.getFirstName());
		query.append("','");
		query.append(person.getLastName());
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}
}
