package de.crispda.sola.multitester.runner;

import com.google.common.collect.Lists;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.util.StringConverter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("CanBeFinal")
public class RunnerController implements Initializable {
    private boolean isRunning = false;

    @FXML private Button btnStart;
    @FXML private Button btnSequence;
    @FXML private Button btnStop;
    @FXML private TextArea executionLog;
    @FXML private ListView<Execution> executionListView;
    @FXML private ComboBox<ExperimentSpec> comboBoxTest;
    @FXML private RadioButton radioExhaustive;
    @FXML private RadioButton radioGuided;
    @FXML private RadioButton radioGuidedCached;
    @FXML private Spinner<Integer> depthSpinner;

    private ObservableList<Execution> executionList;
    private Execution currentExecution = null;
    private int executionCounter = 0;
    private FileWatcherTask watcherTask;
    private Thread watcherThread;
    private Map<String, ExperimentSpec> experimentSpecNameMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ExperimentRunner.setController(this);
        System.out.println("Initializing...");
        executionList = FXCollections.observableArrayList(Execution.extractor());
        executionListView.setItems(executionList);

        ObservableList<ExperimentSpec> experimentSpecs = FXCollections.observableArrayList(
                ExperimentSpec.getExperimentSpecs());

        experimentSpecNameMap = new HashMap<>();
        for (ExperimentSpec experimentSpec : experimentSpecs) {
            if (experimentSpecNameMap.put(experimentSpec.getName(), experimentSpec) != null)
                throw new RuntimeException("Duplicate name!");
        }

        comboBoxTest.setItems(experimentSpecs);
        comboBoxTest.setCellFactory(view -> new ComboBoxListCell<ExperimentSpec>() {
            @Override
            public void updateItem(ExperimentSpec item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                }
            }
        });
        comboBoxTest.setConverter(new StringConverter<ExperimentSpec>() {
            @Override
            public String toString(ExperimentSpec experimentSpec) {
                return experimentSpec.getName();
            }

            @Override
            public ExperimentSpec fromString(String s) {
                return experimentSpecNameMap.get(s);
            }
        });
        comboBoxTest.getSelectionModel().select(0);

        executionListView.setCellFactory(view -> new ListCell<Execution>() {
            @Override
            protected void updateItem(Execution e, boolean bln) {
                super.updateItem(e, bln);
                if (e != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(e.name);
                    if (e.isRunning.get()) {
                        sb.append(" (*)");
                    }
                    setText(sb.toString());
                }
            }
        });

        File[] files = new File(Execution.path).listFiles();
        if (files == null)
            throw new RuntimeException("Folder not found");

        List<File> executions = new ArrayList<>();
        for (final File fileEntry : files) {
            if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".xml")) {
                executions.add(fileEntry);
            }
        }

        Pattern noPattern = Pattern.compile("Execution (\\d+)\\.xml");
        executions.sort((file1, file2) -> {
            Matcher m1 = noPattern.matcher(file1.getName());
            Matcher m2 = noPattern.matcher(file2.getName());
            if (!m1.find() || !m2.find())
                throw new IllegalStateException("Unexpected file name!");
            int file1Int = Integer.parseInt(m1.group(1));
            int file2Int = Integer.parseInt(m2.group(1));
            if (file1Int == file2Int)
                return 0;
            if (file1Int < file2Int)
                return -1;
            else
                return 1;
        });
        for (File exFile : executions) {
            Execution ex = new Execution(exFile.getName().substring(0, exFile.getName().length() - 4));
            ex.isRunning.set(false);
            ex.isStarted = false;
            executionList.add(ex);
        }

        if (executionList.size() > 0) {
            executionCounter = Integer.parseInt(executionList.get(executionList.size() - 1).name.substring(10)) + 1;
            this.executions = Collections.synchronizedList(createExecutionsList());
        }

        watcherTask = new FileWatcherTask();

        executionLog.textProperty().bind(watcherTask.messageProperty());
        executionListView.getSelectionModel().selectedItemProperty().addListener((observableValue, exold, exnew) ->  {
            watcherTask.setFilename(Paths.get(Execution.path + exnew.name + ".xml"));
            watcherTask.loadFile();
        });
        watcherThread = new Thread(watcherTask);
        watcherThread.start();

        ToggleGroup group = new ToggleGroup();
        radioExhaustive.setToggleGroup(group);
        radioGuidedCached.setToggleGroup(group);
        radioGuided.setToggleGroup(group);
        radioGuided.setSelected(true);

        depthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
    }

    @FXML
    private void btnStartPressed(ActionEvent event) throws Exception {
        if (!isRunning) {
            isRunning = true;
            btnStop.setDisable(false);

            ExecutionType executionType;
            if (radioExhaustive.isSelected())
                executionType = ExecutionType.EXHAUSTIVE;
            else if (radioGuided.isSelected())
                executionType = ExecutionType.GUIDED;
            else
                executionType = ExecutionType.GUIDED_CACHED;

            if (executionType != ExecutionType.RANDOM_STATE)
                currentExecution = new Execution(this, "Execution " + executionCounter,
                        comboBoxTest.getSelectionModel().getSelectedItem(), executionType, depthSpinner.getValue());
            else
                currentExecution = new Execution(this, "Execution " + executionCounter,
                        comboBoxTest.getSelectionModel().getSelectedItem(), executionType, depthSpinner.getValue(),
                        Duration.ofMinutes(60));

            executionCounter++;
            executionList.add(currentExecution);
            executionListView.getSelectionModel().select(currentExecution);
            comboBoxTest.setDisable(true);
        }

        if (!currentExecution.isStarted) {
            currentExecution.start();
            btnStart.setText("Pause");
        } else {
            currentExecution.pause();
            btnStart.setText("Start");
        }
    }

    private static SetExperimentSpec getSpec(String name) {
        return (SetExperimentSpec) ExperimentSpec.getExperimentSpecs().stream()
                .filter(s -> s.getName().equals(name)).findFirst()
                .orElseThrow(() -> new RuntimeException("Spec not found."));
    }

    private List<Execution> createExecutionsList() {
        List<Execution> executionList = Lists.newArrayList(
                new Execution(this, "Execution ", getSpec("GDocs AS10"), ExecutionType.GUIDED, 1),
                new Execution(this, "Execution ", getSpec("Firepad AS10"), ExecutionType.GUIDED, 1),
                new Execution(this, "Execution ", getSpec("Owncloud AS10"), ExecutionType.GUIDED, 1),
                new Execution(this, "Execution ", getSpec("GDocs AS5"), ExecutionType.GUIDED, 3),
                new Execution(this, "Execution ", getSpec("Firepad AS5"), ExecutionType.GUIDED, 3),
                new Execution(this, "Execution ", getSpec("Owncloud AS5"), ExecutionType.GUIDED, 3),
                new Execution(this, "Execution ", getSpec("GDocs AS10"), ExecutionType.GUIDED, 2),
                new Execution(this, "Execution ", getSpec("Firepad AS10"), ExecutionType.GUIDED, 2),
                new Execution(this, "Execution ", getSpec("Owncloud AS10"), ExecutionType.GUIDED, 2),
                new Execution(this, "Execution ", getSpec("GDocs AS10"), ExecutionType.GUIDED, 3),
                new Execution(this, "Execution ", getSpec("Firepad AS10"), ExecutionType.GUIDED, 3),
                new Execution(this, "Execution ", getSpec("Owncloud AS10"), ExecutionType.GUIDED, 3)
                //new Execution("Execution ", getSpec("GDocs AS15"), ExecutionType.GUIDED, 3),
                //new Execution("Execution ", getSpec("Firepad AS15"), ExecutionType.GUIDED, 3), --not done
                //new Execution("Execution ", getSpec("Owncloud AS15"), ExecutionType.GUIDED, 3)

        );

        for (int i = 0; i < executionList.size(); i++) {
            Execution ex = executionList.get(i);
            ex.name += Integer.toString(executionCounter + i);
        }

        return executionList;
    }

    private List<Execution> executions;

    @FXML
    private void startSequence(ActionEvent event) throws Exception {
        executionCounter += executions.size();
        isRunning = true;
        btnStart.setText("Pause");
        btnSequence.setDisable(true);
        btnStop.setDisable(false);
        comboBoxTest.setDisable(true);

        startExecution(0);
    }

    private void startExecution(int index) throws IOException {
        if (currentExecution != null) {
            currentExecution.quit();
        }

        currentExecution = executions.get(index);
        currentExecution.setup();
        Platform.runLater(() -> {
            executionList.add(currentExecution);
            executionListView.getSelectionModel().select(currentExecution);
        });

        currentExecution.task.setOnSucceeded(workerStateEvent -> {
            if (!isRunning)
                return;
            if (index < executions.size() - 1) {
                try {
                    startExecution(index + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Platform.runLater(() -> {
                    isRunning = false;
                    btnStart.setDisable(false);
                    btnSequence.setDisable(false);
                    btnStop.setDisable(true);
                    comboBoxTest.setDisable(false);
                });
            }
        });
        currentExecution.startTask();
    }

    @FXML
    private void btnStopPressed(ActionEvent event) {
        if (isRunning) {
            isRunning = false;
            btnStart.setText("Start");
            btnStop.setDisable(true);
            currentExecution.quit();
            currentExecution = null;
            comboBoxTest.setDisable(false);
        }
    }

    public void stopExecution() {
        Platform.runLater(() -> {
            isRunning = false;
            btnStart.setText("Start");
            btnStop.setDisable(true);
            comboBoxTest.setDisable(false);
            currentExecution = null;
        });
    }

    public void shutDown() {
        btnStopPressed(null);

        if (watcherThread != null) {
            watcherTask.cancel();
        }

        if (currentExecution != null)
            currentExecution.quit();
    }
}
