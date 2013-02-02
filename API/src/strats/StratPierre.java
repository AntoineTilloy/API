package strats;
import generated.exchange.BFExchangeServiceStub;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.Market;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.Runner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import basics.Basics;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedCompleteMarketPrices.InflatedCompleteRunner;

public class StratPierre {

	
	public static void launch() {
		MUBet[] bets;
		
		try {
			
			bets= ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			Double[][] inventory=Basics.getInventory(bets);
			InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

			for(int j=0; j<30;j++)
			{
				try{
				System.out.println("###############");
				System.out.println(OB.getRunners().get(j).getSelectionId());
				InflatedCompleteRunner r= OB.getRunners().get(j);
				System.out.print("Back best: ");
				System.out.println(Basics.findBest("B",OB,r.getSelectionId()))	;
				System.out.print("Lay best at: ");
				System.out.println(Basics.findBest("L",OB,r.getSelectionId()))	;
				} catch(Exception e){
					//e.printStackTrace();
					break;
				}
				}
			
			/*
			System.out.println("!!!!!!!!!!!!!!!");
			
			System.out.println(Basics.findPriceLadder(Basics.findBest("L",OB,OB.getRunners().get(0).getSelectionId())));
			String Type= Display.getStringAnswer("Type :");
			int level=Display.getIntAnswer("Level :");
			Double size=Display.getDoubleAnswer("Size :", true);
			int runner=Display.getIntAnswer("runner :");
			InflatedCompleteMarketPrices OB1 = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			Double best=Basics.findBest("B", OB1, OB1.getRunners().get(runner).getSelectionId());
			System.out.println(best);
			boolean b=Basics.placeBetlevel("B", best, level, size, OB1.getRunners().get(runner).getSelectionId());
			System.out.println(b);
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Rendre publiques ces variables dans APIDemo
		
	}

	public static void printRace() {
		int[] SelectionIDs=Basics.getSelectID();
		InflatedCompleteMarketPrices OB;
		try {
			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
		
			for(int horseNumber=0; horseNumber < StratAntoine.numberOfRunners(); horseNumber++){
				int SelectionId=SelectionIDs[horseNumber];
				
				double bestBack=Basics.findBest("B", OB, SelectionId);
				double bestLay=Basics.findBest("L", OB, SelectionId);

				
				System.out.print("ID : "+ SelectionId +" best Back: " + bestBack + " / best Lay: "+ bestLay);
				System.out.println();
				
			}
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	public static void printPriceLadder() { 
		for(int i =0 ; i<=200 ; i++ ){
			System.out.println(APIDemo.priceLadder[i]);
		}	
	}
	
	
	public static void IlliquidMM( java.util.Calendar stopTime) {
		
		try {
			System.out.println("Choisir Marché +1 : ");
			APIDemo.chooseMarket(1);
			String runnerName=APIDemo.selectedMarket2.getRunners().getRunner()[0].getName();
			int selID=APIDemo.selectedMarket2.getRunners().getRunner()[0].getSelectionId();
			System.out.println(runnerName);
			System.out.println(selID);
			
			String runnerNameRef="";
			int selIDRef=0;		
			for(Runner rn : APIDemo.selectedMarket.getRunners().getRunner()){
				if(runnerName.toLowerCase().contains(rn.getName().toLowerCase())==false & rn.getName().toLowerCase().contains("draw")==false){
					runnerNameRef=rn.getName();
					selIDRef=rn.getSelectionId();
				}
			}
			
			System.out.println(runnerNameRef);
			System.out.println(selIDRef);
			
				
		Market MI=APIDemo.selectedMarket2;
		Market ML=APIDemo.selectedMarket;
		int SelectionIdI=selID;
		int SelectionIdL=selIDRef;

		
		
		boolean exitStrat=false;
		   
		while(exitStrat==false){	
					
				
		  if(Calendar.getInstance().getTime().before(stopTime.getTime())){
			System.out.println("Start");			
			//Récupérer les Matched et Unmatched
			Basics.waiting(1500);
			MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, MI.getMarketId()); //Rendre publiques ces variables dans APIDemo

			//calculer l'inventaire, éventuellement l'inventaire en comptant les Unmatched
			Double[][] inventory=Basics.getInventory(MUBets);

			//récupérer l'OB
			InflatedCompleteMarketPrices OBI = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, MI.getMarketId());
			InflatedCompleteMarketPrices OBL = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, ML.getMarketId());
			
			
				double bestBackL=Basics.findBest("B", OBL, SelectionIdL);
				double bestLayL=Basics.findBest("L", OBL, SelectionIdL);
				double bestBackI=Basics.findBest("B", OBI, SelectionIdI);
				double bestLayI=Basics.findBest("L", OBI, SelectionIdI);
				double lastPlacedBack=1000;
				double lastPlacedLay=0;
				
					double price=bestBackI;
					MUBet bet=null;
					System.out.println();
					for(int i = 0 ; i< MUBets.length; i++){
						bet = MUBets[i];
						
						if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionIdI ){
							if(bet.getBetType().toString()=="L" & bet.getPrice()>=1+1/(bestBackL-1)){
								Basics.cancelBet(bet);
							}
							if(bet.getBetType().toString()=="B" & bet.getPrice()<=1+1/(bestLayL-1) ){
								Basics.cancelBet(bet);					
							}				
						}		
					
					}
					
					boolean status = false;
					double layLimit=1.0+1.0/(bestBackL-1.0);
					double backLimit=1.0+1.0/(bestLayL-1.0);
					System.out.println(layLimit + " " + backLimit);
					price=APIDemo.priceLadder[Basics.findPriceLadder(bestLayI)+1];
					System.out.println(price);
						if(price<layLimit & bestLayI>lastPlacedLay + 0.005 & Basics.volumeAt(SelectionIdI, "L", price, MUBets)<=0.1){
							System.out.println(price);
							status=Basics.placeBetlevel(MI.getMarketId(),"L", price, 0, 10, SelectionIdI);
							lastPlacedLay=price;
							System.out.println(status);
						}

					
					price=APIDemo.priceLadder[Basics.findPriceLadder(bestBackI)-1];
					System.out.println(price);
						if(price>backLimit & bestBackI<lastPlacedBack - 0.005 & Basics.volumeAt(SelectionIdI, "B", price, MUBets)<=0.1 ){
							System.out.println(Basics.volumeAt(SelectionIdI, "B", price, MUBets));
							System.out.println(price);
							Basics.placeBetlevel(MI.getMarketId(),"B", price, 0, 10, SelectionIdI);
							lastPlacedBack=price;
							System.out.println(status);
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
		  }
		}catch(Exception e){
				System.out.print("error");
				e.printStackTrace();		
		
	}
	
	
	
}
}
