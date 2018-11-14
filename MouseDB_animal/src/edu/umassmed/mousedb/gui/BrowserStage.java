package edu.umassmed.mousedb.gui;

import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import edu.umassmed.mousedb.core.MouseDBAnimalApplication;

public class BrowserStage extends Stage {

	public static final int PADDING = 5;
	public static final int GAP = 10;

	public static final int LABEL_WIDTH = 100;
	public static final int FIELD_WIDTH = 200;
	public static final int BUTTON_WIDTH = 80;
	public static final int AREA_HEIGHT = 80;

	private final BorderPane bp;
	private final BrowserStack browserStack;

	public BrowserStage(final MouseDBAnimalApplication app) {

		this.setTitle("MouseDB animal interface");

		this.browserStack = new BrowserStack(app);

		this.bp = new BorderPane();
		// this.bp.setTop(menu);
		this.bp.setCenter(this.browserStack);
		final int width = (BrowserStage.LABEL_WIDTH + BrowserStage.FIELD_WIDTH
				+ BrowserStage.BUTTON_WIDTH + (BrowserStage.PADDING * 12) + (BrowserStage.GAP * 3)) * 2;

		this.browserStack.setPrefWidth(width);
		this.setScene(new Scene(this.bp));
	}

	public void removeMenu() {
		if (this.bp.getTop() != null) {
			this.bp.getChildren().remove(this.bp.getTop());
		}
	}

	public void setMenu(final MenuBar menu) {
		this.bp.setTop(menu);
	}
}
