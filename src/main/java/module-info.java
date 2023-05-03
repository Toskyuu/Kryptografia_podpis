module krypto.schnorr.kryptoschnorr {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens krypto.schnorr.kryptoschnorr to javafx.fxml;
    exports krypto.schnorr.kryptoschnorr;
}