module com.example.saadstickhero {
    requires javafx.controls;
    requires javafx.fxml;
            
                        requires org.kordamp.bootstrapfx.core;
            
    opens com.example.saadstickhero to javafx.fxml;
    exports com.example.saadstickhero;
}