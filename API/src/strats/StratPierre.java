package strats;
import generated.exchange.BFExchangeServiceStub;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.CancelBets;
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

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import basics.Basics;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedCompleteMarketPrices.InflatedCompleteRunner;

public class StratPierre {

	public static void printDataTest(int inutile, double nbLevels, double stakeLevel, double volumeMaxImb, java.util.Calendar stopTime){

		MUBet[] MUBets;
		InflatedCompleteMarketPrices OB;
		int SelectionId;
		double bestBack;
		double bestLay;

		boolean exitStrat=false;
		boolean spreadFilled=false;
		PlaceBets[] betsVectorLay = new PlaceBets[100];
		PlaceBets[] betsVectorBack = new PlaceBets[100];
		CancelBets[] cancelVector = new CancelBets[100];
		CancelBets[] cancelToSend=new CancelBets[]{};
		PlaceBets[] betsToSendLay=new PlaceBets[]{};
		PlaceBets[] betsToSendBack=new PlaceBets[]{};
		int numberOfBetsLay;
		int numberOfBetsBack;
		int numberOfCancelBets;
		Double[][] inventory;
		int[] SelectionIDs;
		int firstLevelLay;
		int firstLevelBack;
		double signal;
		int numberLevels = 8;
		int numberOfRunners=StratAntoine.numberOfRunners();
		double[] volumes= new double[numberOfRunners];
		double volume;
		java.util.Calendar lastEmailSent=Calendar.getInstance();
		int tauxRefresh=150;
		double volumeP=0;
		int nbBoucles=0;
		int firstInvBack=0;
		int firstInvLay=0;
		java.util.Calendar timeExec=java.util.Calendar.getInstance();



		while(exitStrat==false){


			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){

					Basics.waiting(tauxRefresh);


					SelectionIDs=Basics.getSelectID();
					MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					Basics.printData(OB);
					///////////////////////////////////////////////////////
					//spreadFilled=fillSpread(1, inutile, MUBets, OB, SelectionIDs);
					///////////////////////////////////////////////////////////



										}
				else{
					exitStrat=true;
					Basics.ecrireSuite("C:\\Users\\GREG\\workspace\\Data.txt","\r\n"+"\r\n");
					
				}
			} catch(Exception e){
				e.printStackTrace();

			}

		}

	}

	
	
	
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
	
	
	public static void test() {
		APIDemo.dailyPnL+=10000.0;
		try {
			Basics.Send("PNL", "" + Basics.PnL() + " // cumul day " + APIDemo.dailyPnL);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void writeRace(String path) {

				Basics.ecrireSuite(path, "Market selected : "+ APIDemo.selectedMarket.getMenuPath() + "  " + APIDemo.selectedMarket.getName() + "\r\n"+ "Starting at : " + APIDemo.selectedMarket.getMarketTime().getTime()+"\r\n");
		
	}	
	
	
	
	public static void printPriceLadder() { 
		for(int i =0 ; i<=200 ; i++ ){
			System.out.println(APIDemo.priceLadder[i]);
		}	
	}
	
	
	public static void IlliquidMM( java.util.Calendar stopTime) {
		
		try {
			System.out.println("Choisir March� +1 : ");
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
		double lastPlacedBack=1000;
		double lastPlacedLay=0;
		double bestBackL;
		double bestLayL;
		double bestBackI;
		double bestLayI;

		
		boolean exitStrat=false;
		   
		while(exitStrat==false){	
					
				
		  if(Calendar.getInstance().getTime().before(stopTime.getTime())){
			System.out.println("Start");			
			//R�cup�rer les Matched et Unmatched
			Basics.waiting(1500);
			MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, MI.getMarketId()); //Rendre publiques ces variables dans APIDemo

			//calculer l'inventaire, �ventuellement l'inventaire en comptant les Unmatched
			Double[][] inventory=Basics.getInventory(MUBets);

			//r�cup�rer l'OB
			InflatedCompleteMarketPrices OBI = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, MI.getMarketId());
			InflatedCompleteMarketPrices OBL = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, ML.getMarketId());
			
			
				bestBackL=Basics.findBest("B", OBL, SelectionIdL);
				bestLayL=Basics.findBest("L", OBL, SelectionIdL);
				bestBackI=Basics.findBest("B", OBI, SelectionIdI);
				bestLayI=Basics.findBest("L", OBI, SelectionIdI);

				
					double price=bestBackI;
					MUBet bet=null;
					System.out.println();
					double layLimit=1.0+1.0/(bestBackL-1.0);
					double backLimit=1.0+1.0/(bestLayL-1.0);
					boolean backPlaced=false;
					boolean layPlaced=false;
					
					for(int i = 0 ; i< MUBets.length; i++){
						bet = MUBets[i];
						
						if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionIdI ){
							if(bet.getBetType().toString()=="L" && (bet.getPrice()<bestLayI-0.005 | bet.getPrice() > layLimit + 0.005)){
								Basics.cancelBet(bet);
							}
							if(bet.getBetType().toString()=="B" && (bet.getPrice()>bestBackI+0.005 | bet.getPrice() < backLimit - 0.005)){
								Basics.cancelBet(bet);					
							}
							if(bet.getBetType().toString()=="L" ){
								layPlaced=true;
							}
							if(bet.getBetType().toString()=="B" ){
								backPlaced=true;					
							}	
						}		
					}
					
					if(layPlaced==false){lastPlacedLay = 1;}
					if(backPlaced==false){lastPlacedBack = 1000;}
					
					boolean status = false;
					System.out.println(layLimit + " " + backLimit);
					price=APIDemo.priceLadder[Basics.findPriceLadder(bestLayI)];
					System.out.println(price);
						if(price<layLimit & bestLayI>lastPlacedLay + 0.005 & Basics.volumeAt(SelectionIdI, "L", price, MUBets)<=0.1){

							status=Basics.placeBetlevel(MI.getMarketId(),"L", price, 0, 10, SelectionIdI);
							lastPlacedLay=price;
							System.out.println(status);
						}

					
					price=APIDemo.priceLadder[Basics.findPriceLadder(bestBackI)];
					System.out.println(price);
						if(price>backLimit & bestBackI<lastPlacedBack - 0.005 & Basics.volumeAt(SelectionIdI, "B", price, MUBets)<=0.1 ){

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
