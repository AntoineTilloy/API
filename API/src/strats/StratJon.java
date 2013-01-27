package strats;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.InflatedCompleteMarketPrices;
import generated.exchange.BFExchangeServiceStub.MUBet;

import java.util.*;

import basics.Basics;

public class StratJon {

    
   public static  void launch(int horseNumber, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){

boolean exitStrat=false;
	   
while(exitStrat==false){
	   
try{	
			
		
	if(Calendar.getInstance().getTime().before(stopTime.getTime())){
	
		
	//Récupérer les Matched et Unmatched
	Basics.waiting(3000);
	MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

	//calculer l'inventaire, éventuellement l'inventaire en comptant les Unmatched
	double[][] inventaire=Basics.getInventory(MUBets);

	//récupérer l'OB
	InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

	int SelectionID=OB.getRunners().get(horseNumber).getSelectionId();	
	
	//best prices
	double bestBack=Basics.findBest("B", OB, SelectionID);
	double bestLay=Basics.findBest("L", OB, SelectionID);
	
	//LO au best : MM avec déséquilibre d'inventaire   1=Back, 2=Lay
	/*if (inventaire[1]<inventaire[2]+volumeMaxImb){
		Basics.placeBetlevel("B", bestBack, 0, volume, SelectionID);	
	}
	if (inventaire[2]<inventaire[1]+volumeMaxImb){
	     Basics.placeBetlevel("L", bestLay, 0, volume, SelectionID);	
    }
*/

	
	
	//LO loin du best sur k niveaux
	System.out.println();
	for (int i=1;i<=nbLevels;i++){
		System.out.println("volume back at " + 0.01*APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+i] +" is " + Basics.volumeAt(SelectionID, "B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+i], MUBets));
	  if (Basics.volumeAt(SelectionID, "B", 0.01*APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+i], MUBets)==0){ // A construire
		   Basics.placeBetlevel("B", bestBack, i, volume, SelectionID);		  
	  }
	  if (Basics.volumeAt(SelectionID, "L", 0.01*APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-i], MUBets)==0){ // A construire
		  Basics.placeBetlevel("L", bestLay, i, volume, SelectionID);	}
	  }
	System.out.println("Boucle");

	}
	else{
	//débouclageOptimal(); // A construire
		exitStrat=true;
		System.out.println("Exit Strat : " + exitStrat);
	}

}catch(Exception e){
	e.printStackTrace();
}

}
}
}
