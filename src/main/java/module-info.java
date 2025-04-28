module edu.nyu.dlts {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens edu.nyu.dlts to javafx.fxml;
    exports edu.nyu.dlts;
}
