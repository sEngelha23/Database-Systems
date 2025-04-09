package edu.hsog.db;

import org.assertj.swing.core.NameMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Pause;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;


// Unfortunately  In Java 9 and beyond, the module system (introduced in Jigsaw) restricts
// reflection-based access to private fields and methods unless explicitly allowed
// To prevent warning msg do add in VM config: --add-opens java.base/java.util=ALL-UNNAMED
// Go to: Run|Edit config -> Left bottom -> EQdit config setting

public class GuiTests extends AssertJSwingJUnitTestCase {

    private final static int WIDTH = 800; // Not required if set as preffered size by MasterPanel
    private final static int HIGHT = 600; // Not required if set as preffered size by MasterPanel

    private final static String NUM_OF_BOOKS = "Count: 7";


    private final static  EDbType DATABASE_TYPE =  EDbType.ORACLE;

    private FrameFixture window; // The window fixture

    private static Connection DB_CONNECTION;

    private final static String DEFAULT_STATUS = "nicht verbunden";
    private final static String CONNECTED_STATUS = "verbunden";
    private final static String LOGGED_IN = "logged in";
    private final static String NOT_LOGGED_IN = "not logged in";

    public String filepath0 = "test/resources/test0.png";
    public String filepath1 = "test/resources/test1.png";
    public String filepath2 = "test/resources/test2.png";


    @BeforeClass
    public static void createTestState() {
        try {
            switch (DATABASE_TYPE) {
                case ORACLE:
                    DB_CONNECTION = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/FREE", "Chris", "xyz");
                    break;
                case MYSQL:
                    DB_CONNECTION = DriverManager.getConnection("jdbc:mysql://localhost/chris?serverTimezone=Europe/Berlin", "Chris", "xyz");
                    break;
                case MARIADB:
                    DB_CONNECTION = DriverManager.getConnection("jdbc:mariadb://localhost:1521/chris", "chris", "xyz");
                    break;
                default:
                    throw new RuntimeException("DB connection failed");
            }

        } catch (SQLException ex) {
            throw new RuntimeException("DB connection failed", ex);
        }
    }

    @AfterClass
    public static void destroyState() {
        try {
            DB_CONNECTION.close();
        } catch (SQLException ex) {
            Logger.getLogger(GuiTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void onSetUp() {
        // Keep DB in initial State for every single test
        RestoreDB.restoreDB();

        robot().settings().delayBetweenEvents(100); // Optional, adjust as needed

        AtomicReference<JFrame> jf = new AtomicReference<>();

        // Ensure the frame is visible and in normal state
        GuiActionRunner.execute(() -> {
            jf.set(Main.generateJFrame());
            jf.get().setState(JFrame.NORMAL);
            jf.get().setExtendedState(JFrame.NORMAL);
            jf.get().toFront();
            jf.get().requestFocus();
            jf.get().setVisible(true);
            System.out.println("Frame Size1: " + jf.get().getSize()); // Log the size of the frame after setting it
        });

        // Wrap the JFrame in a FrameFixture on the EDT
        window = new FrameFixture(robot(),  jf.get() );

        // Show the frame to interact with it
        // window.show(new Dimension(WIDTH, HIGHT)); // Ensure it is visible and has a proper size
        window.show();
    }

    @Override
    protected void onTearDown() {
        if (window != null) window.cleanUp();
    }

    // Helper functions
    public void login(String user, String passwd) {
        GuiTestNames.getUSER_TEXTFIELD(window).setText(user);
        GuiTestNames.getPASSWORD_TEXTFIELD(window).setText(passwd);
        GuiTestNames.getLOGIN_BUTTON(window).click();
    }

    public void insertGadget(String gadgetUrl, String keywords, String description) {
        GuiTestNames.getGADGET_TEXTFIELD(window).setText(gadgetUrl);
        GuiTestNames.getKEYWORD_TEXTFIELD(window).setText(keywords);
        GuiTestNames.getDESCRIPTION_TEXTAREA(window).setText(description);
        GuiTestNames.getSAVE_BUTTON(window).click();
    }

    public void loadImage(String filepath) {
        GuiActionRunner.execute(() -> {
            JFileChooser fileChooser = (JFileChooser) robot().finder().find(new NameMatcher("imageJFch", JFileChooser.class));
            System.out.println(filepath);
            fileChooser.setSelectedFile(new File(filepath));
            fileChooser.approveSelection();
        });
    }

    public static boolean compareIcons(Icon icon1, String filePathIcon2) {
        try {
            // Step 1: Check if the Icon is null
            if (icon1 == null) {
                return false; // Can't compare a null icon with a file
            }

            // Step 2: Convert the Icon to a BufferedImage
            BufferedImage img1 = Converter.iconToBufferedImage(icon1);

            // Step 3: Load the second image from the file path
            Icon icon2 = Converter.loadIconFromFile(filePathIcon2);
            BufferedImage img2 = Converter.iconToBufferedImage(icon2);
            if (img2 == null) {
                return false; // Invalid or unsupported image format
            }

            // Step 4: Compare dimensions
            if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
                return false; // Images have different dimensions
            }

            // Step 5: Compare pixel data
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                        return false; // Pixels are different
                    }
                }
            }

            // Images are identical
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs (e.g., file not found)
        }
    }

    public void loadImageToLabel(String filepath) {
        GuiTestNames.getCONNECTION_BUTTON(window).click();
        login("abc@web.de", "abc");
        GuiTestNames.getCLEAR_BUTTON(window).click();
        GuiTestNames.getLOADIMAGE_BUTTON(window).click();
        loadImage(filepath);
        Pause.pause(2000);
    }
    // Here come the tests ---------------------------------------------------------

    @Test
    public void STATUS_SHOULD_BE_DEFAULT_WHEN_NOT_CONNECTED() {
        GuiTestNames.getSTATUS_LABEL(window).requireVisible().requireEnabled().requireText(DEFAULT_STATUS);
    }


    @Test
    public void STATUS_SHOULD_BE_CONNECTED_WHEN_CONNECTION_BUTTON_PRESSED() {
        GuiTestNames.getSTATUS_LABEL(window).requireVisible().requireEnabled().requireText(DEFAULT_STATUS);

        GuiTestNames.getCONNECTION_BUTTON(window).requireVisible().requireEnabled().click();

        GuiTestNames.getSTATUS_LABEL(window).requireVisible().requireEnabled().requireText(CONNECTED_STATUS);

    }

    @Test
    public void COUNT_SHOULD_RETURN_CORRECT_NUMBER_WHEN_DATA_IS_PRESENT() {
        GuiTestNames.getCONNECTION_BUTTON(window).click();

        GuiTestNames.getCOUNT_BUTTON(window).requireEnabled().requireVisible().click();

        // Assert.assertNotEquals("Bitte verwendet Sie für die Anzahl der Bücher ein Integer und kein Double", GuiTestNames.getCOUNT_LABEL(window).text(), Integer.valueOf(GuiTestNames.getCOUNT_LABEL(window).text()) + "");
        GuiTestNames.getCOUNT_LABEL(window).requireText(NUM_OF_BOOKS);

    }

    @Test
    public void LOGIN_SHOULD_FAIL_WHEN_EMAIL_IS_NOT_FOUND() {
        GuiTestNames.getCONNECTION_BUTTON(window).click();
        login("Fred", "wrongPassword");

        GuiTestNames.getSTATUS_LABEL(window).requireText(NOT_LOGGED_IN);
    }


    @Test
    public void LOGIN_SHOULD_FAIL_WHEN_PASSWORD_IS_WRONG() {
        GuiTestNames.getCONNECTION_BUTTON(window).click();
        login("abc@web.de", "wrongPassword");

        GuiTestNames.getSTATUS_LABEL(window).requireText(NOT_LOGGED_IN);
    }


    @Test
    public void LOGIN_SHOULD_WORK_WHEN_EMAIL_AND_PASSWORD_ARE_CORRECT() {
        GuiTestNames.getCONNECTION_BUTTON(window).click();
        login("abc@web.de", "abc");

        GuiTestNames.getSTATUS_LABEL(window).requireText(LOGGED_IN);
    }


    @Test
    public void CLEAR_BUTTON_SHOULD_EMPTY_GUI() {
        GuiTestNames.getCONNECTION_BUTTON(window).click();
        login("abc@web.de", "abc");
        GuiTestNames.getFIRST_BUTTON(window).click();

        GuiTestNames.getCLEAR_BUTTON(window).click();

        GuiTestNames.getCOMMENTS_TEXTAREA(window).requireText("");
        GuiTestNames.getCOMMENT_TEXTFIELD(window).requireText("");
        GuiTestNames.getDESCRIPTION_TEXTAREA(window).requireText("");
        GuiTestNames.getKEYWORD_TEXTFIELD(window).requireText("");
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("");
        String labelText = GuiTestNames.getIMAGE_LABEL(window).target().getText();
        Assert.assertTrue("Label text is neither null nor empty nor 'image'", labelText == null || labelText.isEmpty() || labelText.equals("image"));
        GuiTestNames.getOWNER_LABEL(window).requireText("");
    }


    // --------------------------------
    @Test
    public void TEST_IF_LOADED_IMAGE0_IS_CORRECT() {
        loadImageToLabel(filepath0);
        // Retrieve the JLabel
        JLabel label = GuiTestNames.getIMAGE_LABEL(window).target();
        Icon actualIcon = label.getIcon();
        // Compare the actualIcon with the expected image file
        boolean isImageCorrect = compareIcons(actualIcon, filepath0);
        // Assert the result
        Assert.assertTrue("This is not the expected image!", isImageCorrect);
    }

    @Test
    public void FIRST_NEXT_PREVIOUS_LAST_WITHOUT_IMAGES() {

        GuiTestNames.getCONNECTION_BUTTON(window).click();

        login("abc@web.de", "abc");

        GuiTestNames.getCLEAR_BUTTON(window).click();
        GuiTestNames.getFIRST_BUTTON(window).click();
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("at https://www.china-gadgets.de/kuehljacke-fuer-getraenke/");
        GuiTestNames.getPREVIOUS_BUTTON(window).click();
        GuiTestNames.getPREVIOUS_BUTTON(window).click();
        GuiTestNames.getPREVIOUS_BUTTON(window).click();
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("at https://www.china-gadgets.de/kuehljacke-fuer-getraenke/");
        GuiTestNames.getNEXT_BUTTON(window).click();
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("https://eu.ember.com/de/products/ember-mug-2?variant=41371672969368&utm_source=google&campaign_id=21589997996&ad_id=&utm_medium=cpc&utm_campaign=DE-PMAX&utm_content=&utm_term=&gclid=Cj0KCQiAgdC6BhCgARIsAPWNWH34yA7bPpSKHd1vGptrjPJV19Vaeiupu33wB45nh2wwLuoqt6U8DYwaAj8UEALw_wcB&gad_source=1");

        GuiTestNames.getLAST_BUTTON(window).click();
        GuiTestNames.getNEXT_BUTTON(window).click();
        GuiTestNames.getNEXT_BUTTON(window).click();
        GuiTestNames.getPREVIOUS_BUTTON(window).click();
        GuiTestNames.getNEXT_BUTTON(window).click();

        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("https://www.manufactum.de/rakete-a43809/?gad_source=1&gclid=Cj0KCQiAgdC6BhCgARIsAPWNWH1H47M9dVm_7uizPhKwQedPW6PuUKmLDrJ5SXZufNNuyyidt1nwOn0aAhbnEALw_wcB");

    }


    @Test
    public void INSERT_NEW_ITEM_WHICH_IS_NOT_IN_DB() {

        GuiTestNames.getCONNECTION_BUTTON(window).click();

        login("abc@web.de", "abc");

        GuiTestNames.getLOADIMAGE_BUTTON(window).click();
        loadImage(filepath0);
        insertGadget("za http://first-test.de", "Key1, Key2, Key3", "This is the test description1!");

        GuiTestNames.getLOADIMAGE_BUTTON(window).click();
        loadImage(filepath1);
        insertGadget("zb http://second-test.de", "word1, word2, word3", "This is the test description2!");
        GuiTestNames.getCLEAR_BUTTON(window).click();

        GuiTestNames.getFIRST_BUTTON(window).click();
        GuiTestNames.getLAST_BUTTON(window).click();
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("zb http://second-test.de");
        JLabel label = GuiTestNames.getIMAGE_LABEL(window).target();
        Icon actualIcon = label.getIcon();
        boolean isImageCorrect = compareIcons(actualIcon, filepath1);
        Assert.assertTrue("This is not the expected image1!", isImageCorrect);

        GuiTestNames.getPREVIOUS_BUTTON(window).click();
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("za http://first-test.de");
        label = GuiTestNames.getIMAGE_LABEL(window).target();
        actualIcon = label.getIcon();
        isImageCorrect = compareIcons(actualIcon, filepath0);
        Assert.assertTrue("This is not the expected image0!", isImageCorrect);
    }


    @Test
    public void MODIFY_ITEM_WHICH_IS_IN_DB() {

        GuiTestNames.getCONNECTION_BUTTON(window).click();

        login("abc@web.de", "abc");
        GuiTestNames.getFIRST_BUTTON(window).click();
        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("at https://www.china-gadgets.de/kuehljacke-fuer-getraenke/");

        GuiTestNames.getLOADIMAGE_BUTTON(window).click();
        loadImage(filepath0);
        insertGadget("at https://www.china-gadgets.de/kuehljacke-fuer-getraenke/", "Key1, Key2, Key3", "This is the test description1!");

        GuiTestNames.getCLEAR_BUTTON(window).click();
        GuiTestNames.getFIRST_BUTTON(window).click();

        GuiTestNames.getGADGET_TEXTFIELD(window).requireText("at https://www.china-gadgets.de/kuehljacke-fuer-getraenke/");
        GuiTestNames.getKEYWORD_TEXTFIELD(window).requireText("Key1, Key2, Key3");
        JLabel label = GuiTestNames.getIMAGE_LABEL(window).target();
        Icon actualIcon = label.getIcon();
        boolean isImageCorrect = compareIcons(actualIcon, filepath0);
        Assert.assertTrue("This is not the expected image0!", isImageCorrect);
    }

    // Further test will follow here :-) -----
}
