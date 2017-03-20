package de.crispda.sola.multitester.runner;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class OrderedTransitionCell extends ListCell<OrderedTransition> {
    @Override
    protected void updateItem(OrderedTransition item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.toString());
        }
    }

    public static ListCell<OrderedTransition> cellFactory(ListView<OrderedTransition> view) {
        return new OrderedTransitionCell();
    }
}
