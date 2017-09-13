/**
 * Sample Skeleton for 'SerieA.fxml' Controller Class
 */

package it.polito.tdp.seriea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.Classifica;
import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {
	
	private Model model;
	
	public void setModel(Model model) {
		this.model = model;
		this.boxSeason.getItems().addAll(this.model.getSeasons());
	}

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSeason"
    private ChoiceBox<Season> boxSeason; // Value injected by FXMLLoader

    @FXML // fx:id="boxTeam"
    private ChoiceBox<Team> boxTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void handleCarica(ActionEvent event) {
    	
    	this.txtResult.clear();
    	
    	//controllo se ha fatto la selezione
    	if(this.boxSeason == null) {
    		this.txtResult.setText("Selezionare una stagione");
    		return;
    	}
    	
    	//Carico le stagioni
    	Season s = this.boxSeason.getValue();
    	this.model.loadMatches(s);

    	//Creo il grafo
    	boolean creato = this.model.creaGrafo();
    	if(creato == false) {
    		this.txtResult.setText("Errore nella creazione del grafo");
    		return;
    	}
    	
    	//Richiedo la classifica
    	List<Classifica> classifica = this.model.getClassifica();
    	for(Classifica c: classifica) {
    		this.txtResult.appendText(c.toString() + "\n");
    		this.boxTeam.getItems().add(c.getTeam());
    	}
    }

    @FXML
    void handleDomino(ActionEvent event) {

    	this.txtResult.clear();
    	
    	if(this.boxTeam.getValue() == null) {
    		this.txtResult.appendText("Devi selezionare una squadra");
    		return;
    	}
    	
    	Team team = this.boxTeam.getValue();
    	
    	for(Team t: this.model.domino(team)) {
    		this.txtResult.appendText("\n" + t.toString());
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSeason != null : "fx:id=\"boxSeason\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert boxTeam != null : "fx:id=\"boxTeam\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
        
        this.txtResult.setStyle("-fx-font-family: monospace");
    }
}
