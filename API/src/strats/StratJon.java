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
import generated.exchange.BFExchangeServiceStub.CancelBets;
import generated.exchange.BFExchangeServiceStub.CancelBetsResult;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.Runner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import basics.Basics;

public class StratJon {


	public static  void launch(int horseNumber, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){

		boolean exitStrat=false;

		while(exitStrat==false){

			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){


					//R�cup�rer les Matched et Unmatched
					Basics.waiting(3000);
					MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

					//calculer l'inventaire, �ventuellement l'inventaire en comptant les Unmatched
					Double[][] inventaire=Basics.getInventory(MUBets);

					//r�cup�rer l'OB
					InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

					int SelectionID=OB.getRunners().get(horseNumber).getSelectionId();	

					//best prices
					double bestBack=Basics.findBest("B", OB, SelectionID);
					double bestLay=Basics.findBest("L", OB, SelectionID);

					//LO au best : MM avec d�s�quilibre d'inventaire   1=Back, 2=Lay
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
					//d�bouclageOptimal(); // A construire
					exitStrat=true;
					System.out.println("Exit Strat : " + exitStrat);
				}

			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

	public static void stackSmashing(int inutile, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){
		MUBet[] MUBets;
		InflatedCompleteMarketPrices OB;
		try {
			MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //Rendre publiques ces variables dans APIDemo

		int SelectionId;
		double bestBack;
		double bestLay;
		double addBack=3;
		double addLay=8;
		double canBack=2;
		double canLay=6;
		int marginBestBack=1;
		int marginBestLay=1;
		boolean forceCanBestBack=true;
		boolean forceCanBestLay=true;
		boolean res;
		int tauxRefresh=500;

		boolean exitStrat=false;
		boolean spreadFilled=false;
		PlaceBets[] betsVector = new PlaceBets[100];
		CancelBets[] cancelVector = new CancelBets[100];
		CancelBets[] cancelToSend=new CancelBets[]{};
		PlaceBets[] betsToSend=new PlaceBets[]{};
		int numberOfBets;
		int numberOfCancelBets;
		Double[][] inventory;
		double[][] implicitP;
		int[] SelectionIDs;

		while(exitStrat==false){


			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){

					Basics.waiting(tauxRefresh);

					SelectionIDs=Basics.getSelectID();
					System.out.println("debut boucle");
					MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

					System.out.println("length :" + MUBets.length);



					///////////////////////////////////////////////////////
					//spreadFilled=fillSpread(1, inutile, MUBets, OB, SelectionIDs);
					///////////////////////////////////////////////////////////



					inventory=Basics.getInventory(MUBets);

					if(spreadFilled==true){
						OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						spreadFilled=false;
						//exitStrat=true;
					}

					implicitP=Basics.implicitPrice(OB);


					for(int horseNumber = inutile; horseNumber < inutile+1; horseNumber ++){
						SelectionId=SelectionIDs[horseNumber];
						bestBack=Basics.findBest("B", OB, SelectionId);
						bestLay=Basics.findBest("L", OB, SelectionId);
						addBack=2;
						addLay=6;
						canBack=1;
						canLay=5;
						marginBestBack=1;
						marginBestLay=1;
						forceCanBestBack=true;
						forceCanBestLay=true;

						if(Basics.volumeAt(SelectionId, "B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1], MUBets)>19 ){
							Basics.cancelAll("B");
						}

						if(Basics.volumeAt(SelectionId, "L", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-1], MUBets)>19){
							Basics.cancelAll("L");
						}


						if(inventory[horseNumber][0]-inventory[horseNumber][1]>0.5*bestBack*10){
							addBack=1;
							canBack=1;
							marginBestBack=0;
							forceCanBestBack=false;					
						}

						if(inventory[horseNumber][1]-inventory[horseNumber][0]>0.5*bestLay*10){
							addLay=1;
							canLay=1;
							marginBestLay=0;
							forceCanBestLay=false;
						}
						if(Basics.volumeAt(SelectionId, "B", bestBack, MUBets)<=0.5*10){
							forceCanBestBack=false;	
						}
						if(Basics.volumeAt(SelectionId, "L", bestLay, MUBets)<=0.5*10){
							forceCanBestLay=false;	
						}

						double price=bestBack;

						System.out.println("implicit lay: "+ implicitP[horseNumber][0]);
						System.out.println("implicit back: " + implicitP[horseNumber][1]);
						System.out.println();

						MUBet bet=null;
						numberOfCancelBets=0;
						//Basics.waiting(200);
						for(int i = 0 ; i< MUBets.length; i++){
							bet = MUBets[i];

							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								if((bet.getBetType().toString()=="L" && Basics.volumeAt(SelectionId, "L", bet.getPrice(), MUBets)>0.1*10 && bet.getPrice()>=implicitP[horseNumber][0]+ (implicitP[horseNumber][1]-implicitP[horseNumber][0])/canLay) | (forceCanBestLay && bet.getPrice()==bestLay) ){
									System.out.println("before cancel");
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									System.out.println("after cancel");
									numberOfCancelBets=numberOfCancelBets+1;

								}
								if((bet.getBetType().toString()=="B" && Basics.volumeAt(SelectionId, "B", bet.getPrice(), MUBets)>0.1*10 && bet.getPrice()<=implicitP[horseNumber][1]-(implicitP[horseNumber][1]-implicitP[horseNumber][0])/canBack) | (forceCanBestBack && bet.getPrice()==bestBack) ){
									System.out.println("before cancel");
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);					
									System.out.println("after cancel");
									numberOfCancelBets=numberOfCancelBets+1;
								}				
							}		

						}

						System.out.println("before send cancel");
						cancelToSend=new CancelBets[numberOfCancelBets];
						for(int i=0;i<numberOfCancelBets;i++){
							cancelToSend[i]=cancelVector[i];
						}
						if(numberOfCancelBets>0){
							Basics.cancelBetVector(cancelToSend);
						}
						System.out.println("after send cancel");

						numberOfBets=0;
						System.out.println("debut placements ");
						price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-marginBestLay];
						for(int k=0;k<=4;k++){
							if(price<=implicitP[horseNumber][0] + (implicitP[horseNumber][1]-implicitP[horseNumber][0])/addLay & Basics.volumeAt(SelectionId, "L", price, MUBets)<4+4*k-2){
								betsVector[numberOfBets]=Basics.generateBet("L", price, 4+4*k-Basics.volumeAt(SelectionId, "L", price, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
								//System.out.println("volume lay place" + (4+4*k-Basics.volumeAt(SelectionId, "L", price, MUBets)));
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
						}

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+marginBestBack];
						for(int k=0; k<= 4; k++){
							if(price>=implicitP[horseNumber][1] - (implicitP[horseNumber][1]-implicitP[horseNumber][0])/addBack & Basics.volumeAt(SelectionId, "B", price, MUBets)<4+4*k-2 ){
								betsVector[numberOfBets]=Basics.generateBet("B", price, 4+4*k-Basics.volumeAt(SelectionId, "B", price, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
								//System.out.println("volume back place "+ (4+4*k-Basics.volumeAt(SelectionId, "B", price, MUBets)));
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)+1];

						}
						if(inventory[horseNumber][0]-inventory[horseNumber][1]<=0.5*bestBack*10 & inventory[horseNumber][0]-inventory[horseNumber][1]>0){
							if(Basics.volumeAt(SelectionId, "B", bestBack, MUBets)*bestBack<inventory[horseNumber][0]-inventory[horseNumber][1] && (inventory[horseNumber][0]-inventory[horseNumber][1])/bestBack-Basics.volumeAt(SelectionId, "B", bestBack, MUBets)>=2){
								betsVector[numberOfBets]=Basics.generateBet("B", bestBack, (inventory[horseNumber][0]-inventory[horseNumber][1])/bestBack-Basics.volumeAt(SelectionId, "B", bestBack, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
							}
						}
						if(inventory[horseNumber][1]-inventory[horseNumber][0]<=0.5*bestLay*10 & inventory[horseNumber][1]-inventory[horseNumber][0]>0){
							if(Basics.volumeAt(SelectionId, "L", bestLay, MUBets)*bestLay<inventory[horseNumber][1]-inventory[horseNumber][0] && (inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay-Basics.volumeAt(SelectionId, "L", bestLay, MUBets)>=2){
								betsVector[numberOfBets]=Basics.generateBet("L", bestLay, (inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay-Basics.volumeAt(SelectionId, "L", bestLay, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
							}
						}

						System.out.println("fin placements ");

						System.out.println("debut envoi bet vector ");
						betsToSend=new PlaceBets[numberOfBets];
						for(int i=0;i<numberOfBets;i++){
							betsToSend[i]=betsVector[i];
						}
						if(numberOfBets>0){
							Basics.placeBetVector(betsToSend);
						}
						System.out.println("fin envoi bet vector ");

					}
				}else{
					boolean done=false;
					while(done==false){
						done=Basics.cancelAll();
						StratAntoine.optimalUnwind();
					}	
					Basics.waiting(5000);
					double PnL=Basics.PnL();
					System.out.println(PnL);
					String path="C:\\Users\\GREG\\workspace\\PnL.txt";
					StratPierre.writeRace(path);
					Basics.ecrireSuite(path, "PnL is: " + String.valueOf(PnL)+" �" + "\r\n");
					exitStrat=true;
					System.out.println("Exit Strat : " + exitStrat);
				}
			} catch(Exception e){
				e.printStackTrace();

			}

		}

	}


	public static void stackSmashing2(int inutile, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){
		MUBet[] MUBets;
		InflatedCompleteMarketPrices OB;
		int lastdelay=0;
		try {
			MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); 		
			lastdelay=OB.getInPlayDelay();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //Rendre publiques ces variables dans APIDemo

		int SelectionId;
		double bestBack;
		double bestLay;
		double addBack2=3;
		double addLay2=8;
		double canBack2=2;
		double canLay2=6;
		double spread;
		double spreadImplicite;
		int marginBestBack=1;
		int marginBestLay=1;
		boolean forceCanBestBack=true;
		boolean forceCanBestLay=true;
		boolean res;
		int numberofRunners=StratAntoine.numberOfRunners();

		boolean exitStrat=false;
		boolean spreadFilled=false;
		PlaceBets[] betsVector = new PlaceBets[100];
		CancelBets[] cancelVector = new CancelBets[100];
		int numberOfBets;
		int numberOfCancelBets;

		while(exitStrat==false){


			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){

					Basics.waiting(2500);

					int[] SelectionIDs=Basics.getSelectID();
					MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

					////////// Pour stop lorsque trop �checs API
					/*
			System.out.println("in play delay: " + OB.getInPlayDelay());
			if(OB.getInPlayDelay()==lastdelay){
		  		Basics.cancelAll();
		  		StratAntoine.optimalUnwind();
				while(OB.getInPlayDelay()==lastdelay){
					System.out.println("Bug API, in play delay: " + OB.getInPlayDelay());
					Basics.waiting(5000);
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());				
				}
				MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());				
		  		Basics.cancelAll();
		  		StratAntoine.optimalUnwind();
				exitStrat=true;
				System.out.println("Exit Strat : " + exitStrat);
		  		break;
			}
					 */
					//####################################



					///////////////////////////////////////////////////////
					//spreadFilled=fillSpread(1, inutile, MUBets, OB, SelectionIDs);
					///////////////////////////////////////////////////////////



					Double[][] inventory=Basics.getInventory(MUBets);
					System.out.println("invent runner, unmatched lay " +inventory[inutile][2]);

					if(spreadFilled==true){
						OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						spreadFilled=false;
						//exitStrat=true;
					}

					double[][] implicitP=Basics.implicitPrice(OB);
					double[] implicitmidP=Basics.implicitmidPrice(OB);

					for(int horseNumber = inutile; horseNumber < inutile+1; horseNumber ++){
						SelectionId=SelectionIDs[horseNumber];
						bestBack=Basics.findBest("B", OB, SelectionId);
						bestLay=Basics.findBest("L", OB, SelectionId);
						//spread=bestBack-bestLay;
						spreadImplicite=(implicitP[horseNumber][1]-implicitP[horseNumber][0])/(numberofRunners-1);
						double tickunitBack=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1]-bestBack;
						double tickunitLay=-APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-1]+bestLay;
						addBack2=1.5*spreadImplicite;
						addLay2=1.5*spreadImplicite;
						canBack2=1*spreadImplicite;
						canLay2=1*spreadImplicite;
						marginBestBack=1;
						marginBestLay=1;
						forceCanBestBack=true;
						forceCanBestLay=true;

						if(inventory[horseNumber][0]-inventory[horseNumber][1]>0.5*bestBack*10){
							addBack2=0;
							canBack2=0;
							marginBestBack=0;
							forceCanBestBack=false;					
						}

						if(inventory[horseNumber][1]-inventory[horseNumber][0]>0.5*bestLay*10){
							addLay2=0;
							canLay2=0;
							marginBestLay=0;
							forceCanBestLay=false;
						}
						if(Basics.volumeAt(SelectionId, "B", bestBack, MUBets)<=0.5*10){
							forceCanBestBack=false;	
						}
						if(Basics.volumeAt(SelectionId, "L", bestLay, MUBets)<=0.5*10){
							forceCanBestLay=false;	
						}

						double price=bestBack;

						System.out.println(implicitP[horseNumber][0]);
						System.out.println(implicitP[horseNumber][1]);

						MUBet bet=null;
						numberOfCancelBets=0;
						//Basics.waiting(200);
						for(int i = 0 ; i< MUBets.length; i++){
							bet = MUBets[i];

							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								if((bet.getBetType().toString()=="L" && Basics.volumeAt(SelectionId, "L", bet.getPrice(), MUBets)>0.5*10 && bet.getPrice()>=implicitmidP[horseNumber]-canLay2) | (forceCanBestLay && bet.getPrice()==bestLay) ){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);			
									numberOfCancelBets=numberOfCancelBets+1;
								}
								if((bet.getBetType().toString()=="B" && Basics.volumeAt(SelectionId, "B", bet.getPrice(), MUBets)>0.5*10 && bet.getPrice()<=implicitmidP[horseNumber]+canBack2) | (forceCanBestBack && bet.getPrice()==bestBack) ){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);					
									numberOfCancelBets=numberOfCancelBets+1;
								}				
							}		

						}


						CancelBets[] cancelToSend=new CancelBets[numberOfCancelBets];
						for(int i=0;i<numberOfCancelBets;i++){
							cancelToSend[i]=cancelVector[i];
						}
						if(numberOfCancelBets>0){
							Basics.cancelBetVector(cancelToSend);
						}


						numberOfBets=0;

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-marginBestLay];
						for(int k=0;k<=6;k++){
							if(price<=implicitmidP[horseNumber]-addLay2 & Basics.volumeAt(SelectionId, "L", price, MUBets)<4+4*k-2){
								betsVector[numberOfBets]=Basics.generateBet("L", price, 4+4*k-Basics.volumeAt(SelectionId, "L", price, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
						}

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+marginBestBack];
						for(int k=0; k<= 6; k++){
							if(price>=implicitmidP[horseNumber]+addBack2 & Basics.volumeAt(SelectionId, "B", price, MUBets)<4+4*k-2 ){
								betsVector[numberOfBets]=Basics.generateBet("B", price, 4+4*k-Basics.volumeAt(SelectionId, "B", price, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)+1];

						}
						if(inventory[horseNumber][0]-inventory[horseNumber][1]<=0.5*bestBack*10 & inventory[horseNumber][0]-inventory[horseNumber][1]>0){
							if(Basics.volumeAt(SelectionId, "B", bestBack, MUBets)*bestBack<inventory[horseNumber][0]-inventory[horseNumber][1] && (inventory[horseNumber][0]-inventory[horseNumber][1])/bestBack-Basics.volumeAt(SelectionId, "B", bestBack, MUBets)>=2){
								betsVector[numberOfBets]=Basics.generateBet("B", bestBack, (inventory[horseNumber][0]-inventory[horseNumber][1])/bestBack-Basics.volumeAt(SelectionId, "B", bestBack, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
							}
						}
						if(inventory[horseNumber][1]-inventory[horseNumber][0]<=0.5*bestLay*10 & inventory[horseNumber][1]-inventory[horseNumber][0]>0){
							if(Basics.volumeAt(SelectionId, "L", bestLay, MUBets)*bestLay<inventory[horseNumber][1]-inventory[horseNumber][0] && (inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay-Basics.volumeAt(SelectionId, "L", bestLay, MUBets)>=2){
								betsVector[numberOfBets]=Basics.generateBet("L", bestLay, (inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay-Basics.volumeAt(SelectionId, "L", bestLay, MUBets), SelectionId);
								numberOfBets=numberOfBets+1;
							}
						}

						PlaceBets[] betsToSend=new PlaceBets[numberOfBets];
						for(int i=0;i<numberOfBets;i++){
							betsToSend[i]=betsVector[i];
						}
						if(numberOfBets>0){
							Basics.placeBetVector(betsToSend);
						}

						if(Basics.volumeAt(SelectionId, "B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1], MUBets)>30 | Basics.volumeAt(SelectionId, "L", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-1], MUBets)>30){
							Basics.cancelAll();
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


					Basics.waiting(1500);
					MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo
					Double[][] inventory=Basics.getInventory(MUBets);
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
									Basics.placeBetlevel("L", price, 0, 10, SelectionId);
								}
								price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
							}

							price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+1];
							for(int k=0; k<= 2; k++){
								if(price>=implicitP[horseNumber][1] - (implicitP[horseNumber][1]-implicitP[horseNumber][0])/3 & Basics.volumeAt(SelectionId, "B", price, MUBets)<=0.1 ){
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

	public static boolean testTriArb(){

		boolean exitStrat=false;
		boolean res=false;

		BigDecimal volParCote=new BigDecimal(20);

		MUBet[] MUBets;
		try {

			int[] SelectionIDs=Basics.getSelectID();

			MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

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

			//if(Math.abs(StratAntoine.getVolume(OB, SelectionIDs[1], priceBetfair, "L")-volBetfair.doubleValue())<1){


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
			betVector[1]=bet1;
			betVector[0]=bet2;

			try {
				PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, betVector)[0];
				res=betResult.getSuccess();
				System.out.println(res);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Basics.waiting(5000);



			//Basics.findBest(type, OB, SelectionId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Rendre publiques ces variables dans APIDemo
		return res;

	}


	public static boolean fillSpread(int distToOppositeBest, int horseNumber, MUBet[] MUBets, InflatedCompleteMarketPrices OB, int[] SelectionIDs){

		boolean res=false;
		Random rand=new Random();
		Double[][] inventory=Basics.getInventory(MUBets);

		double inventaire=inventory[horseNumber][0]-inventory[horseNumber][1];
		double bestLay=Basics.findBest("L", OB, SelectionIDs[horseNumber]);
		double bestBack=Basics.findBest("B", OB, SelectionIDs[horseNumber]);
		int numBack=Basics.findPriceLadder(bestBack);
		int numLay=Basics.findPriceLadder(bestLay);
		int spreadSize=numBack-numLay;

		//distToOppositeBest d�finit le niveau d'en face sur lequel on place le volume � greener (0 : on le met sur le best, 1 : juste devant le best, etc)
		if(spreadSize>distToOppositeBest+1 && Math.abs(inventaire)/bestLay>5){

			PlaceBets[] betVector = new PlaceBets[spreadSize-distToOppositeBest];

			if(inventaire>0){

				//Plus de LAY que de BACK dans l'inventaire : on veut mettre le prix en haut du spread
				for(int i=0;i<spreadSize-distToOppositeBest-1;i++){
					betVector[i]=Basics.generateBet("L",APIDemo.priceLadder[numLay+i+1], 3+rand.nextInt(2), SelectionIDs[horseNumber]);
				}
				betVector[spreadSize-distToOppositeBest-1]=Basics.generateBet("B",APIDemo.priceLadder[numBack-distToOppositeBest], Math.abs(inventaire)/APIDemo.priceLadder[numBack-distToOppositeBest]+3, SelectionIDs[horseNumber]);
			}else{
				//Plus de BACK que de LAY dans l'inventaire : on veut mettre le prix en haut du spread
				for(int i=0;i<spreadSize-distToOppositeBest-1;i++){
					betVector[i]=Basics.generateBet("B",APIDemo.priceLadder[numBack-i-1], 3+rand.nextInt(2), SelectionIDs[horseNumber]);
				}
				betVector[spreadSize-distToOppositeBest-1]=Basics.generateBet("L",APIDemo.priceLadder[numLay+distToOppositeBest], Math.abs(inventaire)/APIDemo.priceLadder[numLay+distToOppositeBest]+3, SelectionIDs[horseNumber]);
			}


			try {
				PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, betVector)[0];
				Basics.waiting(2000);
				res=betResult.getSuccess();
				System.out.println(res);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		return res;

	}

	public static void stackSmashingBasic(int inutile, double nbLevels, double stakeLevel, double volumeMaxImb, java.util.Calendar stopTime){

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
		int tauxRefresh=50;
		double volumeP=0;
		int nbBoucles=0;
		int firstInvBack=0;
		int firstInvLay=0;
		java.util.Calendar timeExec=java.util.Calendar.getInstance();
		int nbBouclesTot;
		try {
			MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			SelectionIDs=Basics.getSelectID();
			for(int j=0;j<OB.getRunners().size();j++){
				if(OB.getRunners().get(j).getSelectionId()==SelectionIDs[inutile]){
					System.out.println(OB.getRunners().get(j).getActualSPPrice());
					System.out.println();
				}
			}
			for(int i=0;i<numberOfRunners;i++){
				volumes[i]=stakeLevel/Basics.findBest("B", OB, SelectionIDs[i]);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		nbBouclesTot=1;
		Basics.ecrire("C:\\Users\\GREG\\workspace\\controle.txt", Integer.toString(nbBouclesTot));	
		Basics.launchJava();	


		while(exitStrat==false){


			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){

					Basics.waiting(tauxRefresh);


					SelectionIDs=Basics.getSelectID();
					MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

					///////////////////////////////////////////////////////
					//spreadFilled=fillSpread(1, inutile, MUBets, OB, SelectionIDs);
					///////////////////////////////////////////////////////////



					inventory=Basics.getInventory(MUBets);
					signal=0;
					lastEmailSent=keepInventory(signal, 800, inventory, inutile, lastEmailSent, MUBets, OB, SelectionIDs, stopTime);

					if(spreadFilled==true){
						OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						spreadFilled=false;
						//exitStrat=true;
					}


					Basics.Twap(0.99, tauxRefresh, OB, SelectionIDs[inutile]);


					for(int horseNumber = inutile; horseNumber < inutile+1; horseNumber ++){

						SelectionId=SelectionIDs[horseNumber];
						bestBack=Basics.findBest("B", OB, SelectionId);
						bestLay=Basics.findBest("L", OB, SelectionId);
						volume=volumes[inutile];
						double price=bestBack;

						MUBet bet=null;

						numberOfCancelBets=0;
						for(int i = 0 ; i< MUBets.length; i++){
							bet = MUBets[i];
							// Cancel au best si pas d'inventaire
							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								//if(inventory[horseNumber][1]-inventory[horseNumber][0]<=0   && bet.getBetType().toString()=="L" && Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestLay))<2){
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestLay))<1){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									numberOfCancelBets=numberOfCancelBets+1;	
								}
								//if(inventory[horseNumber][1]-inventory[horseNumber][0]>=0  && bet.getBetType().toString()=="B" && Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestBack))<2){
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestBack))<1){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);					
									numberOfCancelBets=numberOfCancelBets+1;
								}				
							}		

							// Cancel si plus loin que best + number levels						
							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestLay))>=numberLevels+2 && bet.getBetType().toString()=="L"){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									numberOfCancelBets=numberOfCancelBets+1;								
								}
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestBack))>=numberLevels+2  && bet.getBetType().toString()=="B"){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									numberOfCancelBets=numberOfCancelBets+1;								
								}				
							}
						}
						cancelToSend=new CancelBets[numberOfCancelBets];
						for(int i=0;i<numberOfCancelBets;i++){
							cancelToSend[i]=cancelVector[i];
						}
						if(numberOfCancelBets>0){
							Basics.cancelBetVector(cancelToSend);
						}


						//D�bouclage si inventaire
						if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack && firstInvBack==0){
							timeExec=java.util.Calendar.getInstance();
							timeExec.add(Calendar.SECOND, 30);
							firstInvBack=1;
							firstInvLay=0;
						}
						if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack && firstInvLay==0){
							timeExec=java.util.Calendar.getInstance();
							timeExec.add(Calendar.SECOND, 30);
							firstInvLay=1;
							firstInvBack=0;
						}
						if(Math.abs(inventory[horseNumber][0]-inventory[horseNumber][1])<=3*bestBack){firstInvBack=0;firstInvLay=0;}

						numberOfBetsLay=0;
						numberOfBetsBack=0;
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])>30 && java.util.Calendar.getInstance().getTime().after(timeExec.getTime())){
							firstInvBack=0;
							if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack){
								betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+2], Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestBack, SelectionId);
								numberOfBetsLay=numberOfBetsLay+1;
							}
						}
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])<-30 && java.util.Calendar.getInstance().getTime().after(timeExec.getTime())){
							firstInvLay=0;
							if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack){
								betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-2], Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay, SelectionId);
								numberOfBetsBack=numberOfBetsBack+1;
							}
						}


						firstLevelLay=3;
						firstLevelBack=3;

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-firstLevelLay];
						for(int k=firstLevelLay;k<=numberLevels;k++){	
							if(k-firstLevelLay<4){volumeP=3*volume;}
							else{volumeP=6*volume;}						
							if(Basics.volumeAt(SelectionId, "L", price, MUBets)<volumeP-2){
								betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", price, volumeP-Basics.volumeAt(SelectionId, "L", price, MUBets), SelectionId);
								numberOfBetsLay=numberOfBetsLay+1;
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
						}

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+firstLevelBack];
						for(int k=firstLevelBack; k<= numberLevels; k++){
							if(k-firstLevelLay<4){volumeP=3*volume;}
							else{volumeP=6*volume;}	
							if(Basics.volumeAt(SelectionId, "B", price, MUBets)<volumeP-2){
								betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", price, volumeP-Basics.volumeAt(SelectionId, "B", price, MUBets), SelectionId);
								numberOfBetsBack=numberOfBetsBack+1;
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)+1];

						}
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsBack;
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsLay;
						betsToSendLay=new PlaceBets[numberOfBetsLay];
						for(int i=0;i<numberOfBetsLay;i++){
							betsToSendLay[i]=betsVectorLay[i];
						}
						if(numberOfBetsLay>0){
							Basics.placeBetVector(betsToSendLay);
						}
						betsToSendBack=new PlaceBets[numberOfBetsBack];
						for(int i=0;i<numberOfBetsBack;i++){
							betsToSendBack[i]=betsVectorBack[i];
						}
						if(numberOfBetsBack>0){
							Basics.placeBetVector(betsToSendBack);
						}
						nbBoucles=nbBoucles+1;
						if(nbBoucles==10){ nbBoucles=0;}
						if(nbBoucles==0){System.out.print(" "+APIDemo.nbBetsSent);}

					}
				nbBouclesTot++;
				Basics.ecrire("C:\\Users\\GREG\\workspace\\controle.txt", Integer.toString(nbBouclesTot));	
				Basics.printData(OB);
				}else{
					boolean done=false;
					while(done==false){
						done=Basics.cancelAll();
						StratAntoine.optimalUnwind();
					}
					double PnL=Basics.PnL();
					System.out.println(PnL);
					String path="C:\\Users\\GREG\\workspace\\PnL.txt";
					StratPierre.writeRace(path);
					Basics.ecrireSuite(path, "PnL is: " + String.valueOf(PnL)+" �" + "\r\n");
					exitStrat=true;
					System.out.println("Exit Strat : " + exitStrat);
					APIDemo.dailyPnL += PnL;
					Basics.Send("PNL", "" + PnL + " // cumul day " + APIDemo.dailyPnL);
					Basics.ecrireSuite("C:\\Users\\GREG\\workspace\\Data.txt","\r\n"+"\r\n");
				}
			} catch(Exception e){
				e.printStackTrace();

			}

		}

	}

	public static void stackSmashingMO(int inutile, double nbLevels, double stakeLevel, double volumeMaxImb, java.util.Calendar stopTime){

		MUBet[] MUBets;
		InflatedCompleteMarketPrices OB;
		int SelectionId;
		double bestBack;
		double bestLay;
		double exBestBack=1000;
		double exBestLay=0;

		boolean exitStrat=false;
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
		double signal;
		int numberOfRunners=StratAntoine.numberOfRunners();
		double[] volumes= new double[numberOfRunners];
		double volume;
		java.util.Calendar lastEmailSent=Calendar.getInstance();
		int tauxRefresh=300;
		int nbBoucles=0;
		int firstInvBack=0;
		int firstInvLay=0;
		java.util.Calendar timeExec=java.util.Calendar.getInstance();

		try {
			MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			SelectionIDs=Basics.getSelectID();
			for(int j=0;j<OB.getRunners().size();j++){
				if(OB.getRunners().get(j).getSelectionId()==SelectionIDs[inutile]){
					System.out.println(OB.getRunners().get(j).getActualSPPrice());
					System.out.println();
				}
			}
			for(int i=0;i<numberOfRunners;i++){
				volumes[i]=stakeLevel/Basics.findBest("B", OB, SelectionIDs[i]);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 



		while(exitStrat==false){


			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){

					Basics.waiting(tauxRefresh);


					SelectionIDs=Basics.getSelectID();
					MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());



					inventory=Basics.getInventory(MUBets);
					signal=0;
					lastEmailSent=keepInventory(signal, 800, inventory, inutile, lastEmailSent, MUBets, OB, SelectionIDs, stopTime);

					Basics.Twap(0.99, tauxRefresh, OB, SelectionIDs[inutile]);


					for(int horseNumber = inutile; horseNumber < inutile+1; horseNumber ++){

						SelectionId=SelectionIDs[horseNumber];
						bestBack=Basics.findBest("B", OB, SelectionId);
						bestLay=Basics.findBest("L", OB, SelectionId);
						volume=volumes[inutile];

						MUBet bet=null;

						numberOfCancelBets=0;
						for(int i = 0 ; i< MUBets.length; i++){
							bet = MUBets[i];
							// Cancel les unmatched
							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
								numberOfCancelBets=numberOfCancelBets+1;											
							}	
						}
						cancelToSend=new CancelBets[numberOfCancelBets];
						for(int i=0;i<numberOfCancelBets;i++){
							cancelToSend[i]=cancelVector[i];
						}
						if(numberOfCancelBets>0){
							Basics.cancelBetVector(cancelToSend);
						}


						//D�bouclage si inventaire
						if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack && firstInvBack==0){
							timeExec=java.util.Calendar.getInstance();
							timeExec.add(Calendar.SECOND, 5);
							firstInvBack=1;
							firstInvLay=0;
						}
						if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack && firstInvLay==0){
							timeExec=java.util.Calendar.getInstance();
							timeExec.add(Calendar.SECOND, 5);
							firstInvLay=1;
							firstInvBack=0;
						}
						if(Math.abs(inventory[horseNumber][0]-inventory[horseNumber][1])<=3*bestBack){firstInvBack=0;firstInvLay=0;}

						numberOfBetsLay=0;
						numberOfBetsBack=0;
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])>30 && java.util.Calendar.getInstance().getTime().after(timeExec.getTime())){
							firstInvBack=0;
							if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack){
								betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", bestBack, Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestBack, SelectionId);
								numberOfBetsLay=numberOfBetsLay+1;
							}
						}
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])<-30 && java.util.Calendar.getInstance().getTime().after(timeExec.getTime())){
							firstInvLay=0;
							if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack){
								betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", bestLay, Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay, SelectionId);
								numberOfBetsBack=numberOfBetsBack+1;
							}
						}

						//MO si d�fon�age
						if(bestLay<exBestLay-2){
							betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)+1], volume, SelectionId);
							numberOfBetsLay=numberOfBetsLay+1;
						}
						if(bestBack>exBestBack+2){
							betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)-1], volume, SelectionId);
							numberOfBetsBack=numberOfBetsBack+1;
						}
						exBestBack=bestBack;
						exBestLay=bestLay;
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsBack;
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsLay;
						betsToSendLay=new PlaceBets[numberOfBetsLay];
						for(int i=0;i<numberOfBetsLay;i++){
							betsToSendLay[i]=betsVectorLay[i];
						}
						if(numberOfBetsLay>0){
							Basics.placeBetVector(betsToSendLay);
						}
						betsToSendBack=new PlaceBets[numberOfBetsBack];
						for(int i=0;i<numberOfBetsBack;i++){
							betsToSendBack[i]=betsVectorBack[i];
						}
						if(numberOfBetsBack>0){
							Basics.placeBetVector(betsToSendBack);
						}
						nbBoucles=nbBoucles+1;
						if(nbBoucles==10){ nbBoucles=0;}
						if(nbBoucles==0){System.out.print(" "+APIDemo.nbBetsSent);}

					}
				}else{
					boolean done=false;
					while(done==false){
						done=Basics.cancelAll();
						StratAntoine.optimalUnwind();
					}
					double PnL=Basics.PnL();
					System.out.println(PnL);
					String path="C:\\Users\\GREG\\workspace\\PnL.txt";
					StratPierre.writeRace(path);
					Basics.ecrireSuite(path, "PnL is: " + String.valueOf(PnL)+" �" + "\r\n");
					exitStrat=true;
					System.out.println("Exit Strat : " + exitStrat);
					APIDemo.dailyPnL += PnL;
					Basics.Send("PNL", "" + PnL + " // cumul day " + APIDemo.dailyPnL);
				}
			} catch(Exception e){
				e.printStackTrace();

			}

		}

	}

	public static void stackSmashingDouble(int inutile, int inutile2, double nbLevels, double stakeLevel, double volumeMaxImb, java.util.Calendar stopTime){

		MUBet[] MUBets;
		InflatedCompleteMarketPrices OB;
		int SelectionId;
		double bestBack;
		double bestLay;
		double exBestBack=1000;
		double exBestLay=0;

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
		int tauxRefresh=300;
		double volumeP=0;
		int nbBoucles=0;
		int firstInvBack=0;
		int firstInvLay=0;
		java.util.Calendar timeExec=java.util.Calendar.getInstance();
		java.util.Calendar timeExec2=java.util.Calendar.getInstance();

		try {
			MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			SelectionIDs=Basics.getSelectID();
			for(int j=0;j<OB.getRunners().size();j++){
				if(OB.getRunners().get(j).getSelectionId()==SelectionIDs[inutile]){
					System.out.println(OB.getRunners().get(j).getActualSPPrice());
					System.out.println();
				}
			}
			for(int i=0;i<numberOfRunners;i++){
				volumes[i]=stakeLevel/Basics.findBest("B", OB, SelectionIDs[i]);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 



		while(exitStrat==false){


			try{	


				if(Calendar.getInstance().getTime().before(stopTime.getTime())){

					Basics.waiting(tauxRefresh);


					SelectionIDs=Basics.getSelectID();
					MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
					OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

					///////////////////////////////////////////////////////
					//spreadFilled=fillSpread(1, inutile, MUBets, OB, SelectionIDs);
					///////////////////////////////////////////////////////////



					inventory=Basics.getInventory(MUBets);
					signal=0;
					lastEmailSent=keepInventory(signal, 800, inventory, inutile, lastEmailSent, MUBets, OB, SelectionIDs, stopTime);

					if(spreadFilled==true){
						OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
						spreadFilled=false;
						//exitStrat=true;
					}


					Basics.Twap(0.99, tauxRefresh, OB, SelectionIDs[inutile]);


					for(int horseNumber = inutile2; horseNumber < inutile2+1; horseNumber ++){

						SelectionId=SelectionIDs[horseNumber];
						bestBack=Basics.findBest("B", OB, SelectionId);
						bestLay=Basics.findBest("L", OB, SelectionId);
						volume=volumes[horseNumber];

						MUBet bet=null;

						numberOfCancelBets=0;
						for(int i = 0 ; i< MUBets.length; i++){
							bet = MUBets[i];
							// Cancel les unmatched
							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
								numberOfCancelBets=numberOfCancelBets+1;											
							}	
						}
						cancelToSend=new CancelBets[numberOfCancelBets];
						for(int i=0;i<numberOfCancelBets;i++){
							cancelToSend[i]=cancelVector[i];
						}
						if(numberOfCancelBets>0){
							Basics.cancelBetVector(cancelToSend);
						}


						//D�bouclage si inventaire
						if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack && firstInvBack==0){
							timeExec2=java.util.Calendar.getInstance();
							timeExec2.add(Calendar.SECOND, 30);
							firstInvBack=1;
							firstInvLay=0;
						}
						if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack && firstInvLay==0){
							timeExec2=java.util.Calendar.getInstance();
							timeExec2.add(Calendar.SECOND, 30);
							firstInvLay=1;
							firstInvBack=0;
						}
						if(Math.abs(inventory[horseNumber][0]-inventory[horseNumber][1])<=3*bestBack){firstInvBack=0;firstInvLay=0;}

						numberOfBetsLay=0;
						numberOfBetsBack=0;
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])>30 && java.util.Calendar.getInstance().getTime().after(timeExec2.getTime())){
							firstInvBack=0;
							if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack){
								betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", bestBack, Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestBack, SelectionId);
								numberOfBetsLay=numberOfBetsLay+1;
							}
						}
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])<-30 && java.util.Calendar.getInstance().getTime().after(timeExec2.getTime())){
							firstInvLay=0;
							if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack){
								betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", bestLay, Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay, SelectionId);
								numberOfBetsBack=numberOfBetsBack+1;
							}
						}

						//MO si d�fon�age
						if(bestLay<exBestLay-2){
							betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", APIDemo.priceLadder[Basics.findPriceLadder(bestLay)+1], volume, SelectionId);
							numberOfBetsLay=numberOfBetsLay+1;
						}
						if(bestBack>exBestBack+2){
							betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", APIDemo.priceLadder[Basics.findPriceLadder(bestBack)-1], volume, SelectionId);
							numberOfBetsBack=numberOfBetsBack+1;
						}
						exBestBack=bestBack;
						exBestLay=bestLay;
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsBack;
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsLay;
						betsToSendLay=new PlaceBets[numberOfBetsLay];
						for(int i=0;i<numberOfBetsLay;i++){
							betsToSendLay[i]=betsVectorLay[i];
						}
						if(numberOfBetsLay>0){
							Basics.placeBetVector(betsToSendLay);
						}
						betsToSendBack=new PlaceBets[numberOfBetsBack];
						for(int i=0;i<numberOfBetsBack;i++){
							betsToSendBack[i]=betsVectorBack[i];
						}
						if(numberOfBetsBack>0){
							Basics.placeBetVector(betsToSendBack);
						}
						nbBoucles=nbBoucles+1;
						if(nbBoucles==10){ nbBoucles=0;}
						if(nbBoucles==0){System.out.print(" "+APIDemo.nbBetsSent);}

					}


					for(int horseNumber = inutile; horseNumber < inutile+1; horseNumber ++){

						SelectionId=SelectionIDs[horseNumber];
						bestBack=Basics.findBest("B", OB, SelectionId);
						bestLay=Basics.findBest("L", OB, SelectionId);
						volume=volumes[horseNumber];
						double price=bestBack;

						MUBet bet=null;

						numberOfCancelBets=0;
						for(int i = 0 ; i< MUBets.length; i++){
							bet = MUBets[i];
							// Cancel au best si pas d'inventaire
							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								//if(inventory[horseNumber][1]-inventory[horseNumber][0]<=0   && bet.getBetType().toString()=="L" && Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestLay))<2){
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestLay))<1){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									numberOfCancelBets=numberOfCancelBets+1;	
								}
								//if(inventory[horseNumber][1]-inventory[horseNumber][0]>=0  && bet.getBetType().toString()=="B" && Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestBack))<2){
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestBack))<1){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);					
									numberOfCancelBets=numberOfCancelBets+1;
								}				
							}		

							// Cancel si plus loin que best + number levels						
							if(bet.getBetStatus().toString()=="U" & bet.getSelectionId()==SelectionId ){
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestLay))>=numberLevels+2 && bet.getBetType().toString()=="L"){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									numberOfCancelBets=numberOfCancelBets+1;								
								}
								if(Math.abs(Basics.findPriceLadder(bet.getPrice())- Basics.findPriceLadder(bestBack))>=numberLevels+2  && bet.getBetType().toString()=="B"){
									cancelVector[numberOfCancelBets]=Basics.generateCancelBet(bet);	
									numberOfCancelBets=numberOfCancelBets+1;								
								}				
							}
						}
						cancelToSend=new CancelBets[numberOfCancelBets];
						for(int i=0;i<numberOfCancelBets;i++){
							cancelToSend[i]=cancelVector[i];
						}
						if(numberOfCancelBets>0){
							Basics.cancelBetVector(cancelToSend);
						}


						//D�bouclage si inventaire
						if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack && firstInvBack==0){
							timeExec=java.util.Calendar.getInstance();
							timeExec.add(Calendar.SECOND, 30);
							firstInvBack=1;
							firstInvLay=0;
						}
						if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack && firstInvLay==0){
							timeExec=java.util.Calendar.getInstance();
							timeExec.add(Calendar.SECOND, 30);
							firstInvLay=1;
							firstInvBack=0;
						}
						if(Math.abs(inventory[horseNumber][0]-inventory[horseNumber][1])<=3*bestBack){firstInvBack=0;firstInvLay=0;}

						numberOfBetsLay=0;
						numberOfBetsBack=0;
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])>30 && java.util.Calendar.getInstance().getTime().after(timeExec.getTime())){
							firstInvBack=0;
							if(inventory[horseNumber][1]-inventory[horseNumber][0]>3*bestBack){
								betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", bestBack, Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestBack, SelectionId);
								numberOfBetsLay=numberOfBetsLay+1;
							}
						}
						if(StratAntoine.Signal(OB, SelectionIDs[horseNumber])<-30 && java.util.Calendar.getInstance().getTime().after(timeExec.getTime())){
							firstInvLay=0;
							if(inventory[horseNumber][0]-inventory[horseNumber][1]>3*bestBack){
								betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", bestLay, Math.abs(inventory[horseNumber][1]-inventory[horseNumber][0])/bestLay, SelectionId);
								numberOfBetsBack=numberOfBetsBack+1;
							}
						}


						firstLevelLay=3;
						firstLevelBack=3;

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestLay)-firstLevelLay];
						for(int k=firstLevelLay;k<=numberLevels;k++){	
							if(k-firstLevelLay<4){volumeP=3*volume;}
							else{volumeP=6*volume;}						
							if(Basics.volumeAt(SelectionId, "L", price, MUBets)<volumeP-2){
								betsVectorLay[numberOfBetsLay]=Basics.generateBet("L", price, volumeP-Basics.volumeAt(SelectionId, "L", price, MUBets), SelectionId);
								numberOfBetsLay=numberOfBetsLay+1;
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)-1];
						}

						price=APIDemo.priceLadder[Basics.findPriceLadder(bestBack)+firstLevelBack];
						for(int k=firstLevelBack; k<= numberLevels; k++){
							if(k-firstLevelLay<4){volumeP=3*volume;}
							else{volumeP=6*volume;}	
							if(Basics.volumeAt(SelectionId, "B", price, MUBets)<volumeP-2){
								betsVectorBack[numberOfBetsBack]=Basics.generateBet("B", price, volumeP-Basics.volumeAt(SelectionId, "B", price, MUBets), SelectionId);
								numberOfBetsBack=numberOfBetsBack+1;
							}
							price=APIDemo.priceLadder[Basics.findPriceLadder(price)+1];

						}
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsBack;
						APIDemo.nbBetsSent=APIDemo.nbBetsSent+numberOfBetsLay;
						betsToSendLay=new PlaceBets[numberOfBetsLay];
						for(int i=0;i<numberOfBetsLay;i++){
							betsToSendLay[i]=betsVectorLay[i];
						}
						if(numberOfBetsLay>0){
							Basics.placeBetVector(betsToSendLay);
						}
						betsToSendBack=new PlaceBets[numberOfBetsBack];
						for(int i=0;i<numberOfBetsBack;i++){
							betsToSendBack[i]=betsVectorBack[i];
						}
						if(numberOfBetsBack>0){
							Basics.placeBetVector(betsToSendBack);
						}
						nbBoucles=nbBoucles+1;
						if(nbBoucles==10){ nbBoucles=0;}
						if(nbBoucles==0){System.out.print(" "+APIDemo.nbBetsSent);}

					}
				}else{
					boolean done=false;
					while(done==false){
						done=Basics.cancelAll();
						StratAntoine.optimalUnwind();
					}
					double PnL=Basics.PnL();
					System.out.println(PnL);
					String path="C:\\Users\\GREG\\workspace\\PnL.txt";
					StratPierre.writeRace(path);
					Basics.ecrireSuite(path, "PnL is: " + String.valueOf(PnL)+" �" + "\r\n");
					exitStrat=true;
					System.out.println("Exit Strat : " + exitStrat);
					APIDemo.dailyPnL += PnL;
					Basics.Send("PNL", "" + PnL + " // cumul day " + APIDemo.dailyPnL);
				}
			} catch(Exception e){
				e.printStackTrace();

			}

		}

	}


	public static java.util.Calendar keepInventory(double signal, double inventoryLimit, Double[][] inventory, int horseNumber, java.util.Calendar lastEmail, MUBet[] MUBets, InflatedCompleteMarketPrices OB, int[] SelectionIDs, java.util.Calendar stopTime) throws MessagingException{

		double inventaire=inventory[horseNumber][1]-inventory[horseNumber][0];
		java.util.Calendar timeLastEmail = Calendar.getInstance();
		java.util.Calendar timeNextEmail = Calendar.getInstance();
		timeLastEmail.setTime(lastEmail.getTime());
		timeNextEmail.setTime(lastEmail.getTime());
		timeNextEmail.add(Calendar.MINUTE, 1);
		if(Calendar.getInstance().getTime().after(timeNextEmail.getTime())){
			timeLastEmail=Calendar.getInstance();
			if(inventaire>=inventoryLimit){
				String title="Attention limite d'inventaire";
				String message= ""+ inventaire;
				Basics.Send(title, message);
				//Basics.cancelAll();
				//	while(Basics.findBest("B", OB, SelectionIDs[horseNumber])>signal && Calendar.getInstance().getTime().before(stopTime.getTime())){
				//		Basics.waiting(1);	
				//		try {
				//		OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
				//		} catch (Exception e) {
				//			// TODO Auto-generated catch block
				//			e.printStackTrace();
				//		}		
				//	}
			}
			if(-inventaire>=inventoryLimit){
				String title="Attention limite d'inventaire";
				String message= ""+ inventaire;
				Basics.Send(title, message);
				//Basics.cancelAll();
				//	while(Basics.findBest("L", OB, SelectionIDs[horseNumber])<signal && Calendar.getInstance().getTime().before(stopTime.getTime())){
				//		Basics.waiting(1);	
				//		try {
				//			OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
				//		} catch (Exception e) {
				//			// TODO Auto-generated catch block
				//			e.printStackTrace();
				//		}	
				//	}
			}
		}

		return timeLastEmail;

	}


}
