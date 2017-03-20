package de.crispda.sola.multitester.runner;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class IndependenceCellValueFactory
        implements Callback<TableColumn.CellDataFeatures<IndependenceRun, String>, ObservableValue<String>> {
    private final int index;

    public IndependenceCellValueFactory(int index) {
        this.index = index;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<IndependenceRun, String> p) {
        return p.getValue().getIndependencePropertyList().get(index);
    }
}
