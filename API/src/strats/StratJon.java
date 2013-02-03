package strats;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.Runner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
   
   public static void launch2(int inutile, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){
		
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
				String stat = "neutral";
				double addBack=2;
				double addLay=6;
				double canBack=1;
				double canLay=5;
				
				if(inventory[horseNumber][0]-inventory[horseNumber][1]>2*bestBack*10){
					addBack=1;
					canBack=1;
				}
				
				if(inventory[horseNumber][1]-inventory[horseNumber][0]>2*bestBack*10){
					addLay=2;
					canLay=1;
				}
				
				
				if(bestBack<=10){
					double price=bestBack;
					
					System.out.println(implicitP[horseNumber][0]);
					System.out.println(implicitP[horseNumber][1]);
					
					
					
					MUBet bet=null;
					for(int i = 0 ; i< MUBets.length; i++){
						bet = MUBets[i];
						
						if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
							if(bet.getBetType().toString()=="L" & bet.getPrice()>=implicitP[horseNumber][0]+ (implicitP[horseNumber][1]-implicitP[horseNumber][0])/canLay){
								Basics.cancelBet(bet);
							}
							if(bet.getBetType().toString()=="B" & bet.getPrice()<=implicitP[horseNumber][1]-(implicitP[horseNumber][1]-implicitP[horseNumber][0])/canBack ){
								Basics.cancelBet(bet);					
							}				
						}		
					
					}
					
					
					
					price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-1];
					for(int k=0;k<=2;k++){
						if(price<=implicitP[horseNumber][0] + (implicitP[horseNumber][1]-implicitP[horseNumber][0])/addLay & Basics.volumeAt(SelectionId, "L", price, MUBets)<=0.1){
							System.out.println(Basics.volumeAt(SelectionId, "L", price, MUBets));
							Basics.placeBetlevel("L", price, 0, 10, SelectionId);
						}
						price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
					}
					
					price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1];
					for(int k=0; k<= 2; k++){
						if(price>=implicitP[horseNumber][1] - (implicitP[horseNumber][1]-implicitP[horseNumber][0])/addBack & Basics.volumeAt(SelectionId, "B", price, MUBets)<=0.1 ){
							System.out.println(Basics.volumeAt(SelectionId, "B", price, MUBets));
							Basics.placeBetlevel("B", price, 0, 10, SelectionId);
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
						Basics.placeBetlevel("L", price, 0, 10, SelectionId);
					}
					price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
				}
				
				price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1];
				for(int k=0; k<= 2; k++){
					if(price>=implicitP[horseNumber][1] - (implicitP[horseNumber][1]-implicitP[horseNumber][0])/3 & Basics.volumeAt(SelectionId, "B", price, MUBets)<=0.1 ){
						System.out.println(Basics.volumeAt(SelectionId, "B", price, MUBets));
						Basics.placeBetlevel("B", price, 0, 10, SelectionId);
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

public static boolean howToFuckBetfair(){
	
	boolean exitStrat=false;
	boolean res=false;
	
	BigDecimal volParCote=new BigDecimal(20);
	
	MUBet[] MUBets;
	try {
	
		MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
	
	//récupérer l'OB
	InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

	int[] SelectionIDs=Basics.getSelectID();
	double[][] bests=new double[StratAntoine.numberOfRunners()][2];
	
	for(int i=0;i<StratAntoine.numberOfRunners();i++){
		bests[i][1]=Basics.findBest("B", OB, SelectionIDs[i]);
		bests[i][0]=Basics.findBest("L", OB, SelectionIDs[i]);
	}
	
	BigDecimal vol1=volParCote;
	MathContext Mc = new MathContext(3, RoundingMode.HALF_UP);
	vol1=vol1.divide(new BigDecimal(bests[0][1]), Mc);
	if(vol1.doubleValue()>volParCote.doubleValue()){
		return res;
     }
	Basics.placeBetlevel("B", bests[0][1], -1, vol1.doubleValue(), SelectionIDs[0]);

	BigDecimal vol2=volParCote;
	vol2=vol2.divide(new BigDecimal(bests[2][1]), Mc);
	if(vol2.doubleValue()>volParCote.doubleValue()){
		return res;
     }
	Basics.placeBetlevel("B", bests[2][1], -1, vol2.doubleValue(), SelectionIDs[2]);

	BigDecimal vol3=volParCote;
	vol3=vol3.divide(new BigDecimal(bests[0][0]), Mc);
	if(vol3.doubleValue()>volParCote.doubleValue()){
		return res;
     }
	Basics.placeBetlevel("B", bests[0][0], 2, vol3.doubleValue(), SelectionIDs[0]);
	
	Basics.waiting(100);
	OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

	double priceBetfair=Basics.findBest("L", OB, SelectionIDs[1]);
	//TestOrdre Betfair
	BigDecimal volBetfair=volParCote;
	volBetfair=volBetfair.divide(new BigDecimal(priceBetfair), Mc);
	if(volBetfair.doubleValue()>volParCote.doubleValue()){
		return res;
     }
	System.out.println(priceBetfair);
	System.out.println(volBetfair.doubleValue());
	//String go=Display.getStringAnswer("yes ? ");
	
	if(Math.abs(StratAntoine.getVolume(OB, SelectionIDs[1], priceBetfair, "L")-volBetfair.doubleValue())<1){
		
	
	/////////////////////////////////////////////////////////////////////////////
	
	PlaceBets bet1 = new PlaceBets();
	bet1.setMarketId(APIDemo.selectedMarket.getMarketId());
	bet1.setSelectionId(SelectionIDs[0]);
	bet1.setBetCategoryType(BetCategoryTypeEnum.E);
	bet1.setBetType(BetTypeEnum.Factory.fromValue("L"));
	bet1.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
    bet1.setPrice(APIDemo.priceLadder[Basics.findPriceLadder(bests[0][1])]);
	bet1.setSize(vol3.doubleValue());
	
	PlaceBets bet2 = new PlaceBets();
	bet2.setMarketId(APIDemo.selectedMarket.getMarketId());
	bet2.setSelectionId(SelectionIDs[1]);
	bet2.setBetCategoryType(BetCategoryTypeEnum.E);
	bet2.setBetType(BetTypeEnum.Factory.fromValue("B"));
	bet2.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
    bet2.setPrice(APIDemo.priceLadder[Basics.findPriceLadder(bests[1][0])]);
	bet2.setSize(volBetfair.doubleValue());
	
	PlaceBets[] betVector = new PlaceBets[2];
	betVector[0]=bet1;
	betVector[1]=bet2;
	try {
		PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, betVector)[0];
		res=betResult.getSuccess();
		System.out.println(res);
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	}
	//Basics.findBest(type, OB, SelectionId);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //Rendre publiques ces variables dans APIDemo
return res;
		
}

}
