import javax.rmi.CORBA.Util;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by nikmal on 2016-12-28.
 */
public class GraphicalInterface extends UI {

    private final Integer FRAME_WIDTH = 800;
    private final Integer FRAME_HEIGHT = 600;
    private final Integer MAIN_PANE_HEIGHT = FRAME_HEIGHT; //(int)Math.floor(FRAME_HEIGHT*0.5);
    private final Integer LOG_PANE_HEIGHT = FRAME_HEIGHT-MAIN_PANE_HEIGHT;

    private final Integer left_margin = 10;
    private final Integer top_margin = 20;
    private final Integer col1_width = 150;
    private final Integer row_space = 35;
    private final Integer component_height = row_space-10;

    JFrame frame = new JFrame("DataExtractor");
    private String logText = "";
    private JPanel container = new JPanel();
    private JPanel mainPanel = new JPanel();
    private JPanel logPanel = new JPanel();
    private JTextArea logWindow = new JTextArea(13, 40);
    JTextField usrDataExtId = new JTextField(20);
    String[] processModes = new String[] {
            Utils.ProcessMode.EXTRACT.toString(),
            Utils.ProcessMode.DELETE.toString()
    };
    JComboBox<String> usrProcessMode = new JComboBox<>(processModes);
    JTextField usrMappingDataDir = new JTextField(20);
    JTextField usrGenotypeDataDir = new JTextField(20);
    JTextField usrParticipantListFilePath = new JTextField(20);

    @Override
    public void initialize(String[] args) {
        addComponents();
        loadParameters();
    }

    private void addComponents() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setLayout(new GridLayout(2,1)); //
        mainPanel.setBorder(new TitledBorder( new EtchedBorder(), "Configuration" ));
        logPanel.setBorder(new TitledBorder( new EtchedBorder(), "Log window" ));
        logPanel.setBounds(left_margin,FRAME_HEIGHT-LOG_PANE_HEIGHT, FRAME_WIDTH-left_margin, LOG_PANE_HEIGHT-left_margin);
        logWindow.setEditable(false);
        logPanel.add(logWindow);

        JScrollPane scroll = new JScrollPane(logWindow);
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        logPanel.add(scroll);

        container.add(mainPanel);
        container.add(logPanel);
        frame.add(container);

        mainPanel.setLayout(null);

        // 1st row
        JLabel dataExtIdLabel = new JLabel("Data Extraction ID");
        dataExtIdLabel.setBounds(left_margin, top_margin,130,component_height);
        mainPanel.add(dataExtIdLabel);


        usrDataExtId.setBounds(left_margin + col1_width,top_margin,165,component_height);
        mainPanel.add(usrDataExtId);

        // 2nd row
        JLabel processModeLabel = new JLabel("Process mode");
        processModeLabel.setBounds(left_margin,top_margin + row_space,130,25);
        mainPanel.add(processModeLabel);
        usrProcessMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usrProcessMode.getSelectedItem().toString().equals("DELETE")) {
                    usrDataExtId.setEnabled(false);
                    usrDataExtId.setText("");
                } else {
                    usrDataExtId.setEnabled(true);
                }
                //printMessage("Changed to: " + usrProcessMode.getSelectedItem().toString());

            }
        });

        usrProcessMode.setBounds(left_margin + col1_width, top_margin + row_space, 100, component_height);
        mainPanel.add(usrProcessMode);

        // 3rd row
        JLabel mappingDataDirLabel = new JLabel("Mapping data directory");
        mappingDataDirLabel.setBounds(left_margin, top_margin + row_space * 2, 130, component_height);
        mainPanel.add(mappingDataDirLabel);

        usrMappingDataDir.setBounds(left_margin + col1_width, top_margin + row_space * 2, 200, component_height);
        mainPanel.add(usrMappingDataDir);

        JButton mappingDirBrowseBtn = new JButton("Browse...");
        mappingDirBrowseBtn.setBounds(left_margin + col1_width + 200, top_margin + row_space * 2, 100, component_height);
        mainPanel.add(mappingDirBrowseBtn);
        mappingDirBrowseBtn.addActionListener(getFilebrowserListener(usrMappingDataDir, "D"));

        // 4th row
        JLabel genotypeDataDirLabel = new JLabel("Genotype data directory");
        genotypeDataDirLabel.setBounds(left_margin, top_margin + row_space * 3, 140, component_height);
        mainPanel.add(genotypeDataDirLabel);

        usrGenotypeDataDir.setBounds(left_margin + col1_width, top_margin + row_space * 3, 200, component_height);
        mainPanel.add(usrGenotypeDataDir);

        JButton genotypeDataDirBrowseBtn = new JButton("Browse...");
        genotypeDataDirBrowseBtn.setBounds(left_margin + col1_width + 200, top_margin + row_space * 3, 100, component_height);
        mainPanel.add(genotypeDataDirBrowseBtn);
        genotypeDataDirBrowseBtn.addActionListener(getFilebrowserListener(usrGenotypeDataDir, "D"));

        // 5th row
        JLabel participantListFilePathLabel = new JLabel("Participant Id list");
        participantListFilePathLabel.setBounds(left_margin, top_margin + row_space * 4, 130, component_height);
        mainPanel.add(participantListFilePathLabel);

        usrParticipantListFilePath.setBounds(left_margin + col1_width, top_margin + row_space * 4, 200, component_height);
        mainPanel.add(usrParticipantListFilePath);

        JButton participantListFilePathBrowseBtn = new JButton("Browse...");
        participantListFilePathBrowseBtn.setBounds(left_margin + col1_width + 200, top_margin + row_space * 4, 100, component_height);
        mainPanel.add(participantListFilePathBrowseBtn);
        participantListFilePathBrowseBtn.addActionListener(getFilebrowserListener(usrParticipantListFilePath, ""));

        // last row
        JButton goButton = new JButton("GO");
        goButton.setBounds(left_margin + col1_width, top_margin + 5 + row_space * 5, 80, component_height);
        mainPanel.add(goButton);
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setParameters();
                saveParameters();
                start();
            }
        });

        JButton clearButton = new JButton("Clear log");
        clearButton.setBounds(left_margin + col1_width + 100, top_margin + 5 + row_space * 5, 85, component_height);
        mainPanel.add(clearButton);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logText = "";
                logWindow.setText(logText);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private ActionListener getFilebrowserListener(JTextField textField, String mode) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (mode.equals("D"))
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    textField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        };
    }

    private Boolean validateInput() {

        java.util.List<String> fields = new ArrayList<String>();
        fields.add(mappingDataDirectory);
        fields.add(dataDirectory);
        fields.add(participantListFilePath);
        fields.add(processMode.toString());

        if (processMode.toString().equals("EXTRACT"))
            fields.add(dataExtractionId);

        for (String field : fields) {
            if (field.isEmpty())
                return false;
        }

        return true;

        /*
        return !dataExtractionId.isEmpty() && !mappingDataDirectory.isEmpty()
                && !dataDirectory.isEmpty() && !participantListFilePath.isEmpty() && !processMode.toString().isEmpty();
                */
    }

    private void setParameters() {
        dataExtractionId = usrDataExtId.getText();
        mappingDataDirectory = usrMappingDataDir.getText();
        dataDirectory = usrGenotypeDataDir.getText();
        participantListFilePath = usrParticipantListFilePath.getText();
        processMode = Utils.getProcessMode(usrProcessMode.getSelectedItem().toString());
    }

    private void saveParameters() {
        try {
            PrintWriter writer = new PrintWriter(".lastParameters", "UTF-8");
            writer.println("MAPPING_DIR;" + mappingDataDirectory);
            writer.println("DATA_DIR;" + dataDirectory);
            writer.println("PARTICIPANTLIST_DIR;" + participantListFilePath);
            writer.close();
        } catch (IOException e) {
            printMessage(String.format("Unable to save parameters to disk, here's the error: %s", e.getMessage()));
        }
    }

    private void loadParameters() {
        try (Stream<String> stream = Files.lines(Paths.get(".lastParameters"))) {

            stream.forEach((line) -> {
                final String[] lineParts = line.split(";");
                switch (lineParts[0]) {
                    case "MAPPING_DIR":
                        usrMappingDataDir.setText(lineParts[1]);
                        break;
                    case "DATA_DIR":
                        usrGenotypeDataDir.setText(lineParts[1]);
                        break;
                    case "PARTICIPANTLIST_DIR":
                        usrParticipantListFilePath.setText(lineParts[1]);
                        break;
                }
            });

        } catch (IOException e) {
            printMessage("(No previously saved parameters found)");
        }
    }

    @Override
    public void start() {
        if (validateInput()) {
            if (processMode.equals(Utils.ProcessMode.DELETE)) {
                int dialogResult = JOptionPane.showConfirmDialog (frame, "Really go ahead and delete files?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.NO_OPTION){
                    return;
                }
            }

            this.extractor = new Extractor(this, dataExtractionId, mappingDataDirectory, dataDirectory,
                    participantListFilePath);
            printMessage("------------------------------------");
            extractor.run(processMode);
            printMessage("------------------------------------");

        } else {
            printMessage("Missing inputs!");
        }
    }

    @Override
    public void printMessage(String msg) {
        logText += msg + "\n";
        logWindow.setText(logText);
    }

    @Override
    public void displayError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
