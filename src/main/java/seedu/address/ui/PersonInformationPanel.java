package seedu.address.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.model.person.ReadOnlyPerson;

/**
 * The person information panel of the app.
 */
public class PersonInformationPanel extends UiPart<Region> {

    private static final String FXML = "PersonInformationPanel.fxml";
    private static String[] colors = {"red", "yellow", "blue", "orange", "brown", "green", "pink", "black", "grey"};
    private static HashMap<String, String> tagColors = new HashMap<String, String>();
    private static Random random = new Random();

    protected List<String> optionalPhoneDisplayList = new ArrayList<String>();
    protected ListProperty<String> listProperty = new SimpleListProperty<>();

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private ReadOnlyPerson person;

    @FXML
    private VBox informationPane;
    @FXML
    private FlowPane tags;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label customFields;
    @FXML
    private ImageView photoContainer;
    @FXML
    private ListView optionalPhoneList;

    public PersonInformationPanel() {
        super(FXML);
        loadDefaultScreen();
        registerAsAnEventHandler(this);
    }

    private static String getColorForTag(String tagValue) {
        if (!tagColors.containsKey(tagValue)) {
            tagColors.put(tagValue, colors[random.nextInt(colors.length)]);
        }

        return tagColors.get(tagValue);
    }

    private void loadDefaultScreen() {
        this.person = null; }

    /**
     * binds the person's information to that of the person card.
     * @param person
     * @param personid
     */
    private void bindListeners(ReadOnlyPerson person, int personid) {
        person.tagProperty().addListener((observable, oldValue, newValue) -> {
            tags.getChildren().clear();
            initTags();
        });
    }

    /**
     * loads the selected person's information to be displayed.
     * @param person
     * @param personId
     */
    private void loadPersonInformation(ReadOnlyPerson person, int personId) {
        this.person = person;
        tags.getChildren().clear();
        initTags();
        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        phone.textProperty().bind(Bindings.convert(person.phoneProperty()));
        address.textProperty().bind(Bindings.convert(person.addressProperty()));
        email.textProperty().bind(Bindings.convert(person.emailProperty()));
        customFields.textProperty().bind(Bindings.convert(person.customFieldProperty()));
        id.setText(Integer.toString(personId));
        optionalPhoneDisplayList.clear();
        initOptionalPhones(person);
        initPhoto(person);
    }

    //@@author LuLechuan
    /**
     *  Initialises icon photo
     */
    private void initPhoto(ReadOnlyPerson person) {
        String pathName = person.getPhoto().pathName;

        File photoImage = new File(pathName);
        Image photo = null;
        try {
            photo = new Image(photoImage.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        photoContainer.setImage(photo);
    }
    //@@author

    /**
     * Sets a background color for each tag.
     * @param
     */
    private void initTags() {
        person.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            tagLabel.setStyle("-fx-background-color: " + getColorForTag(tag.tagName));
            tags.getChildren().add(tagLabel);
        });
    }

    //@@author LuLechuan
    /**
     *  Initialise optional phone display list
     */
    public void initOptionalPhones(ReadOnlyPerson person) {
        final int[] index = {1};
        person.getPhoneList().forEach(optionalPhone -> {
            optionalPhoneDisplayList.add("Other phone " + index[0] + " : " + optionalPhone.value);
            index[0]++;
        });

        optionalPhoneList.itemsProperty().bind(listProperty);

        listProperty.set(FXCollections.observableArrayList(optionalPhoneDisplayList));
    }
    //@@author

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPersonInformation(event.getNewSelection().person, event.getNewSelection().stringid);
        bindListeners(event.getNewSelection().person, event.getNewSelection().stringid);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonInformationPanel)) {
            return false;
        }

        // state check
        PersonInformationPanel card = (PersonInformationPanel) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }
}