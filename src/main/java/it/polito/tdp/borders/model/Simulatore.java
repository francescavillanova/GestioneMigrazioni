package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {

	//Coda degli eventi
	private PriorityQueue<Evento> queue;
	
	//Parametri di simulazione
	private int nInizialeMigranti;
	private Country nazioneIniziale;
	
	//Output della simulazione
	private int nPassi; //=T
	private Map<Country, Integer> persone; //per ogni nazione quanti migranti sono stanziali
	//oppure: List<CountryAndNumber> personeStanziali;
	
	//Stato del mondo simulato
	private Graph<Country, DefaultEdge> grafo;
	
	
	public Simulatore(Graph<Country, DefaultEdge> grafo) {
		super();
		this.grafo = grafo;
	}
	
	public void init(Country partenza, int migranti) {
		this.nazioneIniziale=partenza;
		this.nInizialeMigranti=migranti;
		
		this.persone=new HashMap<Country, Integer>();
		for(Country c: this.grafo.vertexSet()) {
			this.persone.put(c, 0);
		}
		
		this.queue=new PriorityQueue<>();
		this.queue.add(new Evento(1, this.nazioneIniziale, this.nInizialeMigranti));
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {  //finchè la coda non è vuota
			Evento e=this.queue.poll();  //prendo un evento dalla coda
//			System.out.println(e);
			processEvent(e);  //e lo elaboro
		}
	}

	private void processEvent(Evento e) {
		int stanziali=e.getPersone() / 2;
		int migranti=e.getPersone() - stanziali;  //se il numero di persone è dispari i migranti non coincidono con gli stanziali
		int confinanti=this.grafo.degreeOf(e.getNazione()); //numero di nazioni confinanti
		int gruppiMigranti=migranti / confinanti; 
		stanziali+= migranti % confinanti; //agli stanziali devo aggiungere il resto della divisione 
		
		//Aggiorno lo stato del mondo (non c'è lo switch perchè esiste un solo tipo di eventi)
		this.persone.put(e.getNazione(), this.persone.get(e.getNazione())+stanziali);  //aggiungo al numero di persone di quello stato quelle che arrivano
		this.nPassi=e.getTime();
		
		//Predispongo gli eventi futuri
		if(gruppiMigranti!=0) {
			for(Country vicino: Graphs.neighborListOf(this.grafo, e.getNazione())){
				this.queue.add(new Evento(e.getTime()+1, vicino, gruppiMigranti));
			}
		}
		
	}

	
	public int getnPassi() {
		return nPassi;
	}

	public Map<Country, Integer> getPersone() {
		return persone;
	}
	
	
}
