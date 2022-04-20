package it.polito.tdp.meteo.model;

import java.util.ArrayList;

import java.util.List;


import it.polito.tdp.meteo.DAO.MeteoDAO;



public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	//private Set<Rilevamento> migliore;
	
	private MeteoDAO dao;
	private List<Citta> leCitta;
	private List<Citta> best;
	
	public Model() {
		dao=new MeteoDAO();
		this.leCitta=dao.getAllCitta();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
			
		double mediaTO=0;
		double mediaGE=0;
		double mediaMI=0;
		int somma=0;
		
		for(Rilevamento r:dao.getAllRilevamentiLocalitaMese(mese, "Torino")) {
			
			somma+=r.getUmidita();
			mediaTO=somma/dao.getAllRilevamentiLocalitaMese(mese, "Torino").size();
		}
		somma=0;
		
		for(Rilevamento r:dao.getAllRilevamentiLocalitaMese(mese, "Genova")) {
			
			somma+=r.getUmidita();
			mediaGE=somma/dao.getAllRilevamentiLocalitaMese(mese, "Genova").size();
		}
		
		somma=0;
		for(Rilevamento r:dao.getAllRilevamentiLocalitaMese(mese, "Milano")) {
			
			somma+=r.getUmidita();
			mediaMI=somma/dao.getAllRilevamentiLocalitaMese(mese, "Milano").size();
		}
		
		String s=("Torino "+ mediaTO+"\n"+"Genova "+mediaGE+"\n"+"Milano "+mediaMI);
		
		return s;
		//return dao.getAvgRilevamentiLocalitaMese(mese, "Torino")+"\n";
		//DA IMPLEMENTARE PER TUTTE LE CITTA
		
	}
	
	private double costoMinimo;
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		
		List<Citta> parziale=new ArrayList<Citta>();
		this.best=null;
		
		for(Citta c:this.leCitta) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		cerca(parziale,0);
		return this.best;
	}
	
	private void cerca(List<Citta> parziale, int livello) {
		
		if(livello==this.NUMERO_GIORNI_TOTALI) {
			double costo=calcolaCosto(parziale);
			
			if(best==null || costo<calcolaCosto(best)) {
				best=new ArrayList<>(parziale);
			}
		}else {
			for(Citta prova:leCitta) {
				if(aggiuntaValida(parziale, prova)) {
					parziale.add(prova);
					cerca(parziale, livello+1);
					parziale.remove(parziale.size()-1);
				}
			}
			
		}
		
		
	}

	private double calcolaCosto(List<Citta> parziale) {
		
		double costo=0;
		
		for(int giorno=1; giorno <=this.NUMERO_GIORNI_TOTALI;giorno++) {
			Citta c=parziale.get(giorno-1);
			double umidita=c.getRilevamenti().get(giorno-1).getUmidita();
			
			costo+=umidita;
		}
		
		for(int giorno=2; giorno<=this.NUMERO_GIORNI_TOTALI; giorno++) {
			if(!parziale.get(giorno-1).equals(parziale.get(giorno-2))) {
				costo+=this.COST;
			}
		}
		
		return costo;
	}
	
	private boolean aggiuntaValida(List<Citta> parziale, Citta prova) {
		
		int conta=0;
		
		for(Citta precedente:parziale) {
			if(precedente.equals(prova)) {
				conta++;
			}
		}
		
		if(conta>=this.NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		if(parziale.size()==0)
			return true;
		if(parziale.size()==1 || parziale.size()==2)
			return parziale.get(parziale.size()-1).equals(prova);
		if(parziale.get(parziale.size()-1).equals(prova))
			return true;
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) &&
				parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
			return true;
		
		
		return false;
	}

	
	
	

	
	

}
