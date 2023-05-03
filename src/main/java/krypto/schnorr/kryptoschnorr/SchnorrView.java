package krypto.schnorr.kryptoschnorr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

public class SchnorrView {

    @FXML
    private TextArea Text;
    @FXML
    private TextArea Signature;

    @FXML
    private TextField pathText;
    @FXML
    private TextField pathSignature;
    @FXML
    private TextField pathTextToSave;
    @FXML
    private TextField pathSignatureToSave;
    @FXML
    private Button buttonOpenText;
    @FXML
    private Button buttonOpenSignature;
    @FXML
    private RadioButton radioFromFile;
    @FXML
    private RadioButton radioFromText;


    private byte[] message;
    File fileText = null;
    File fileSignature = null;
    File fileToSaveText = null;
    File fileToSaveSignature = null;
    private BigInteger[] signature = new BigInteger[2];
    private Schnorr schnorr = new Schnorr();

    @FXML
    public void initialize()
    {
        Signature.setDisable(true);
        Signature.setWrapText(true);
    }

    @FXML
    public void onEncryptClick(ActionEvent actionEvent) throws IOException
    {
        if (radioFromText.isSelected())
        {
            this.signature = schnorr.generateSignature(Text.getText().getBytes());
            Signature.setText(this.signature[0] + "\n" + this.signature[1]);
        }
        else if(radioFromFile.isSelected())
        {
            if(fileText != null)
            {
                message = new byte[(int) fileText.length()];
                try(FileInputStream fis = new FileInputStream(fileText))
                {
                    fis.read(message);
                    this.signature = schnorr.generateSignature(message);
                    Signature.setText(this.signature[0] + System.lineSeparator() + this.signature[1]);
                }

            }
        }
    }

    private void verify(byte[] msg)
    {
        if (schnorr.verifySignature(msg, this.signature))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Weryfikacja podpisu");
            alert.setContentText("Podpis poprawny");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Weryfikacja podpisu");
            alert.setContentText("Podpis NIE poprawny");
            alert.showAndWait();
        }
    }

    @FXML
    public void onVerifyClick(ActionEvent actionEvent)
    {
        if (radioFromText.isSelected())
        {
            verify(Text.getText().getBytes());
        }
        else if(radioFromFile.isSelected() && fileText != null)
        {
            verify(message);
        }
    }

    @FXML
    public void onFileClick(ActionEvent actionEvent)
    {
        pathText.setDisable(false);
        pathSignature.setDisable(false);
        buttonOpenText.setDisable(false);
        buttonOpenSignature.setDisable(false);
        Text.setDisable(true);
    }

    @FXML
    public void onTextClick(ActionEvent actionEvent)
    {
        pathText.setDisable(true);
        pathSignature.setDisable(true);
        buttonOpenText.setDisable(true);
        buttonOpenSignature.setDisable(true);
        Text.setDisable(false);
    }

    @FXML
    public void OpenSignature(ActionEvent actionEvent) throws IOException
    {
        FileChooser fileChooser = new FileChooser();
        fileSignature = fileChooser.showOpenDialog(HelloApplication.getCurrentStage());
        pathSignature.setText(fileSignature.getAbsolutePath());

        try(BufferedReader br = new BufferedReader(new FileReader(fileSignature)))
        {
            String line;
            int index = 0;
            while((line = br.readLine()) != null)
            {
                this.signature[index++] = new BigInteger(line);
            }
        }

        Signature.setText(this.signature[0].toString() + System.lineSeparator() + this.signature[1].toString());
    }

    @FXML
    public void OpenText(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileText = fileChooser.showOpenDialog(HelloApplication.getCurrentStage());
        pathText.setText(fileText.getAbsolutePath());
        if(fileText != null)
        {
            message = new byte[(int) fileText.length()];
            try(FileInputStream fis = new FileInputStream(fileText))
            {
                fis.read(message);
                String s = Files.readString(Path.of(fileText.getAbsolutePath()));
                Text.setText(s);
            }

        }

    }

    @FXML
    public void SaveText(ActionEvent actionEvent) throws IOException
    {
        FileChooser fileChooser = new FileChooser();
        if(!pathTextToSave.getText().isEmpty())
        {
            fileChooser.setInitialDirectory(new File(pathTextToSave.getText() + "\\..\\"));
        }
        fileToSaveText = fileChooser.showSaveDialog(HelloApplication.getCurrentStage());



        try(FileOutputStream fos = new FileOutputStream(fileToSaveText))
        {
            if(radioFromText.isSelected())
            {
                fos.write(Text.getText().getBytes());
            }
            else if (radioFromFile.isSelected() && fileText != null)
            {
                fos.write(message);
            }
        }
        pathTextToSave.setText(fileToSaveText.getAbsolutePath());
    }

    @FXML
    public void SaveSignature(ActionEvent actionEvent) throws IOException
    {
        FileChooser fileChooser = new FileChooser();
        if(!pathSignatureToSave.getText().isEmpty())
        {
            fileChooser.setInitialDirectory(new File(pathSignatureToSave.getText() + "\\..\\"));
        }
        FileChooser.ExtensionFilter signatureExt = new FileChooser.ExtensionFilter("Signature file", "*.sign");
        fileChooser.getExtensionFilters().add(signatureExt);
        fileToSaveSignature = fileChooser.showSaveDialog(HelloApplication.getCurrentStage());



        try(FileWriter fw = new FileWriter(fileToSaveSignature))
        {
            fw.write(this.signature[0].toString());
            fw.write(System.lineSeparator());
            fw.write(this.signature[1].toString());

        }
        pathSignatureToSave.setText(fileToSaveSignature.getAbsolutePath());
    }

}
