package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	MeteoDAO m = new MeteoDAO();
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	MeteoDAO mdao = new MeteoDAO();
	private List<Citta> citta;
	private List<Citta> sequenzaMigliore;
	
	
	
	

	public Model() {
		
		citta = new LinkedList<Citta>(mdao.getAllCitta());

	}

	// of course you can change the String output with what you think works best
	public List<Rilevamento> getUmiditaMediaMese(int mese) {
		return m.getAllRilevamentiMese(mese);
	}
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese){
		
		List<Citta> parziale = new ArrayList<Citta>();
		sequenzaMigliore = new LinkedList<Citta>();
		
		for (Citta c: citta) 
		{
			c.setRilevamenti(mdao.getAllRilevamentiLocalitaMese(mese, c)); 
		}
		
		ricorsiva(parziale,0);
		return sequenzaMigliore;
			
	}
	
	private double CalcolaCosto (List<Citta> parziale)
	{
		double c = 0.0;
		
		for (int i=1; i<=15; i++) {
			
			Citta cit = parziale.get(i-1);
			double umid = cit.getRilevamenti().get(i-1).getUmidita();
			c += umid;
		}

		for (int i=2; i<=15; i++) {
			
			if(!parziale.get(i-1).equals(parziale.get(i-2))) {
				c += 100;
				
			}
		}
		
		return c;
	}
	
	
	private void ricorsiva(List<Citta>parziale, int L)
	{
		//casi terminali
		
		if(L == 15)
		{
			Double costo = this.CalcolaCosto(parziale);
			
			if(sequenzaMigliore.size()==0 || costo < CalcolaCosto(sequenzaMigliore))
			{
				sequenzaMigliore = new ArrayList<>(parziale);
			}
		}
		else
		{
			for (Citta prova: citta) {
				if (aggiuntaValida(prova,parziale)) {
					parziale.add(prova);
					ricorsiva(parziale, L+1);
					parziale.remove(parziale.size()-1);
				}
			}		
		}
		
	}

	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		
				int c = 0;
				for (Citta precedente:parziale) 
				{
					if (precedente.equals(prova))
						c++; 
				}
				
				if (c >= 6)
					return false;
				
				
				if (parziale.size()==0) 
						return true;
				
				if (parziale.size()==1 || parziale.size()==2) //minimo 3 giorni consecutivi nella stessa cittá
				{
					return parziale.get(parziale.size()-1).equals(prova); //torno true se la cittá é uguale a quella precedente
				}
				
				if (parziale.get(parziale.size()-1).equals(prova))
					return true;
				
				 
				if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
				&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
					return true;   //stessa cosa --> 3 giorni consecutivi nella stessa cittá altrimenti non posso cambiarla
					
				return false;
	}
	

}
