package strats;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.Display;
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
	Double[][] inventaire=Basics.getInventory(MUBets);

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
		System.out.println("volume back at " + APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+i] +" is " + Basics.volumeAt(SelectionID, "B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+i], MUBets));
		System.out.println("volume lay at " + APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-i] +" is " + Basics.volumeAt(SelectionID, "L", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-i], MUBets));
		  if (Basics.volumeAt(SelectionID, "B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+i], MUBets)==0){ // A construire
		   Basics.placeBetlevel("B", bestBack, i, volume, SelectionID);		  
	  }
	  if (Basics.volumeAt(SelectionID, "L", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-i], MUBets)==0){ // A construire
		  Basics.placeBetlevel("L", bestLay, i, volume, SelectionID);	
		  }
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
   
   public static void launch2(int horseNumber, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){
		
		boolean exitStrat=false;
		   
		while(exitStrat==false){
			   
		try{	
					
				
		  if(Calendar.getInstance().getTime().before(stopTime.getTime())){
						
			//Récupérer les Matched et Unmatched
			Basics.waiting(3000);
			MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

			//calculer l'inventaire, éventuellement l'inventaire en comptant les Unmatched
			Double[][] inventory=Basics.getInventory(MUBets);

			//récupérer l'OB
			InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

			int[] SelectionIDs=Basics.getSelectID();
			
			int SelectionId=SelectionIDs[horseNumber];
			
			System.out.println("ID : "+ SelectionId);
			System.out.println();
			
			double[][] implicitP=Basics.implicitPrice(OB);
			double bestBack=Basics.findBest("B", OB, SelectionId);
			double bestLay=Basics.findBest("L", OB, SelectionId);

			double price=bestBack;
			
			System.out.println(implicitP[horseNumber][0]);
			System.out.println(implicitP[horseNumber][1]);
			
			
			
			MUBet bet=null;
			for(int i = 0 ; i< MUBets.length; i++){
				bet = MUBets[i];
				
				if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
					if(bet.getBetType().toString()=="L" & bet.getPrice()>=implicitP[horseNumber][0]+ (implicitP[horseNumber][1]-implicitP[horseNumber][0])/3){
						Basics.cancelBet(bet);
					}
					if(bet.getBetType().toString()=="B" & bet.getPrice()<=implicitP[horseNumber][1]-(implicitP[horseNumber][1]-implicitP[horseNumber][0])/3 ){
						Basics.cancelBet(bet);					
					}				
				}		
			
			}
			
			
			
			price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-1];
			for(int k=0;k<=6;k++){
				if(price<=implicitP[horseNumber][0] + (implicitP[horseNumber][1]-implicitP[horseNumber][0])/5 & Basics.volumeAt(SelectionId, "L", price, MUBets)<=0.1){
					Basics.placeBetlevel("L", price, 0, 2, SelectionId);
				}
				price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
			}
			
			price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1];
			for(int k=0; k<= 6; k++){
				if(price>=implicitP[horseNumber][1] - (implicitP[horseNumber][1]-implicitP[horseNumber][0])/5 & Basics.volumeAt(SelectionId, "B", price, MUBets)<=0.1 ){
					Basics.placeBetlevel("B", price, 0, 2, SelectionId);
				}
				price=APIDemo.priceLadder[Basics.findPriceLadder(price)+1];
			}
			
		  }else{
			  	boolean done=false;
			  	while(done==false){
			  		done=Basics.cancelAll();
			  		StratAntoine.optimalUnwind();
			  	}	
				
				exitStrat=true;
				System.out.println("Exit Strat : " + exitStrat);
		  }
		  } catch(Exception e){
				e.printStackTrace();
		
		}
		
	}
	   
	}


public static void launch3(int inutile, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){
	
	boolean exitStrat=false;
	   
	while(exitStrat==false){
		
		   
	try{	
				
			
	  if(Calendar.getInstance().getTime().before(stopTime.getTime())){
					
		//Récupérer les Matched et Unmatched
		Basics.waiting(1500);
		MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

		//calculer l'inventaire, éventuellement l'inventaire en comptant les Unmatched
		Double[][] inventory=Basics.getInventory(MUBets);

		//récupérer l'OB
		InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

		int[] SelectionIDs=Basics.getSelectID();
		
		
		
		double[][] implicitP=Basics.implicitPrice(OB);
		
		for(int horseNumber = inutile; horseNumber < inutile+1; horseNumber ++){
			int SelectionId=SelectionIDs[horseNumber];
			double bestBack=Basics.findBest("B", OB, SelectionId);
			double bestLay=Basics.findBest("L", OB, SelectionId);
			if(bestBack<=10){
				double price=bestBack;
				
				System.out.println(implicitP[horseNumber][0]);
				System.out.println(implicitP[horseNumber][1]);
				
				
				
				MUBet bet=null;
				for(int i = 0 ; i< MUBets.length; i++){
					bet = MUBets[i];
					
					if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
						if(bet.getBetType().toString()=="L" & bet.getPrice()>=implicitP[horseNumber][0]+ (implicitP[horseNumber][1]-implicitP[horseNumber][0])/3){
							Basics.cancelBet(bet);
						}
						if(bet.getBetType().toString()=="B" & bet.getPrice()<=implicitP[horseNumber][1]-(implicitP[horseNumber][1]-implicitP[horseNumber][0])/3 ){
							Basics.cancelBet(bet);					
						}				
					}		
				
				}
				
				
				
				price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-1];
				for(int k=0;k<=2;k++){
					if(price<=implicitP[horseNumber][0] + (implicitP[horseNumber][1]-implicitP[horseNumber][0])/3 & Basics.volumeAt(SelectionId, "L", price, MUBets)<=0.1){
						System.out.println(Basics.volumeAt(SelectionId, "L", price, MUBets));
						Basics.placeBetlevel("L", price, 0, 2, SelectionId);
					}
					price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
				}
				
				price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1];
				for(int k=0; k<= 2; k++){
					if(price>=implicitP[horseNumber][1] - (implicitP[horseNumber][1]-implicitP[horseNumber][0])/3 & Basics.volumeAt(SelectionId, "B", price, MUBets)<=0.1 ){
						System.out.println(Basics.volumeAt(SelectionId, "B", price, MUBets));
						Basics.placeBetlevel("B", price, 0, 2, SelectionId);
					}
					price=APIDemo.priceLadder[Basics.findPriceLadder(price)+1];
				}
			}	
		}
	  }else{
		  	boolean done=false;
		  	while(done==false){
		  		done=Basics.cancelAll();
		  		StratAntoine.optimalUnwind();
		  	}	
			
			exitStrat=true;
			System.out.println("Exit Strat : " + exitStrat);
	  }
	  } catch(Exception e){
			e.printStackTrace();
	
	}
	
}
   
}

}
