package strats;

import java.io.IOException;

import basics.Basics;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.Runner;
import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedCompleteMarketPrices.InflatedCompletePrice;
import demo.util.InflatedCompleteMarketPrices.InflatedCompleteRunner;

public class StratAntoine {
	
	//La strategie de debouclage optimale fait pour le moment 2 appels a Betfair pour recuperer les donnees + autant d ordres que de positions a changer
	//----------------------
	
	// Variables for nasty stuff
	private static Double finalPosition=0.0;
	
	
	// Basic libraries
	public static Double[] linspace(Double min, Double max, int number){//equivalent to matlab linspace
		Double[] array=new Double[number];
		Double step=(max-min)/(number-1);
		for (int i=0; i<number;i=i+1){
			array[i]=step*i+min;
		}
		return array;
	}
	
	public static int argmax(Double[] vector){
		int result=0;
		Double max=vector[0];
		for (int i=0;i<vector.length;i=i+1){
			if (vector[i]>max){
				max=vector[i];
				result=i;
			}
		}
		return result;
	}
	//------------------
	
	public static Double getHighestPosition(Double[][] inventory){
		int i=0;
		Double pos=inventory[i][0]-inventory[i][1];
		Double currentPos;
		while (inventory[i][4]!=null){
			currentPos=inventory[i][0]-inventory[i][1];
			if (currentPos>pos){
				pos=currentPos;
			}
			i=i+1;
		}
		return pos;
	}
	public static Double getLowestPosition(Double[][] inventory){
		int i=0;
		Double pos=inventory[i][0]-inventory[i][1];
		Double currentPos;
		while (inventory[i][4]!=null){
			currentPos=inventory[i][0]-inventory[i][1];
			if (currentPos<pos){
				pos=currentPos;
			}
			i=i+1;
		}
		return pos;
	}
	

	public static Double getVolume(InflatedCompleteMarketPrices OB,int runnerId,Double price,String type){
		//Gives the volume on the orderbook at the given quote (it can be 0.0)
		Double volume=0.0;
		System.out.println("Looking for volume at price "+price);
		if(type=="L"){	
			for (InflatedCompleteRunner r: OB.getRunners()) {
				if (runnerId == r.getSelectionId()){
					for ( InflatedCompletePrice p: r.getPrices()) {
						if((p.getPrice()>price-0.00001)&&(p.getPrice()<price+0.00001)){
							volume=p.getLayAmountAvailable();
							break;
						}
					}
				break;
				}
			}
		}	
		if(type=="B"){	
			for (InflatedCompleteRunner r: OB.getRunners()) {
				if (runnerId == r.getSelectionId()){
					for ( InflatedCompletePrice p: r.getPrices()) {
						if((p.getPrice()>price-0.00001)&&(p.getPrice()<price+0.00001)){
							volume=p.getBackAmountAvailable();
							break;
						}
					}
				break;
				}
			}
		}
		return volume;
	}
	
	public static Double transactionPrice(InflatedCompleteMarketPrices OB,int runnerId,Double currentPosition,Double finalPosition){
		// gives the Cost to reach a position of finalPosition for the horse given by runnerNumber
		System.out.println("Calculation transaction price for market");
		System.out.println(runnerId);
		Double cost=0.0;
		Double posToExecute=finalPosition-currentPosition;
		
		if (posToExecute>0.0001){//Si la position que l'on veut atteindre est positive, on doit layer, donc regarder ce qu'il y a comme backeurs...
			String type="B";
			System.out.println("on regarde du coté du Back");
			Double quote=Basics.findBest("L", OB, runnerId);
			System.out.println("quote");
			System.out.println(quote);
			int quoteIndex=Basics.findPriceLadder(quote);
			System.out.println("quoteIndex");
			System.out.println(quoteIndex);
			Double availableVolume;
			while (posToExecute>0.0001){
				availableVolume=getVolume(OB,runnerId,quote,type);
				System.out.println("available Volume "+availableVolume);
				if (posToExecute<availableVolume*quote){
					cost=cost+posToExecute/quote;
					posToExecute=0.0;
				}
				else{
					cost=cost+availableVolume;
					posToExecute=posToExecute-availableVolume*quote;
				}
				quoteIndex=quoteIndex-1; // On va un cran plus loin dans l'order book vers des paris moins avantageux, ie avec des cotes plus basses
				if ((APIDemo.priceLadder.length>quoteIndex)&&(quoteIndex>0)){
					quote=0.01*APIDemo.priceLadder[quoteIndex];
				}
				else{
					cost=100000000.0;//if order book empty, this way of unwinding is just impossible
					posToExecute=0.0;
					System.out.println("OrderBook empty");
				}
				
			}
		}
		if (posToExecute<-0.0001){// Si la position que l'on veut atteindre est négative, on doit backer (tel Pierre,.. je suis enorme)
			String type="L";
			System.out.println("on regarde du coté du LAY");
			Double quote=Basics.findBest("B", OB, runnerId);
			System.out.println("quote");
			System.out.println(quote);
			int quoteIndex=Basics.findPriceLadder(quote);
			System.out.println("quoteIndex");
			System.out.println(quoteIndex);
			Double availableVolume;
			while (posToExecute<-0.0001){
				availableVolume=getVolume(OB,runnerId,quote,type);
				System.out.println("available Volume "+availableVolume);
				if (posToExecute>-availableVolume*quote){
					cost=cost+posToExecute/quote;
					posToExecute=0.0;
				}
				else{
					cost=cost-availableVolume;
					posToExecute=posToExecute+availableVolume*quote;
				}
				quoteIndex=quoteIndex+1;
				if ((APIDemo.priceLadder.length>quoteIndex)&&(quoteIndex>0)){
					quote=0.01*APIDemo.priceLadder[quoteIndex];
				}
				else{
					cost=100000000.0;//if order book empty, this way of unwinding is just impossible
					posToExecute=0.0;
					System.out.println("OrderBook empty");
					
				}
			}
		}
		System.out.println("Fin du calcul du transaction price pour un sous marché");
		
		return cost;
	}
	
	public static int numberOfRunners(Double[][] inventory){
		int i=0;
		while (inventory[i][4]!=null){
			i=i+1;
		}
		System.out.print("Number of runners");
		System.out.println(i);
		return i;
		
	}
	
	public static Double[] transactionPrice(InflatedCompleteMarketPrices OB,Double[][] inventory,Double finalPosition){
		// Cost to put every horse to position finalPosition
		
		System.out.println("Calculating complete transaction price");
		
		Double[] costVector=new Double[numberOfRunners(inventory)];
		int i=0;
		int runnerId;
		Double currentPos;
		for (Runner nr : APIDemo.selectedMarket.getRunners().getRunner()){
			runnerId=nr.getSelectionId();
			currentPos=inventory[i][0]-inventory[i][1];
			costVector[i]=transactionPrice(OB,runnerId,currentPos,finalPosition);
			i=i+1;
		}
		return costVector;
	}
	
	public static Double[] findOptimalFinalPosition(InflatedCompleteMarketPrices OB,Double [][] inventory){

		int numberOfDiscretisation=100; //Number of points tested to find the best final position
		Double maxPos=getHighestPosition(inventory);
		Double minPos=getLowestPosition(inventory);
		
		Double[] potentialFinalPos = new Double[numberOfDiscretisation];
		Double[] potentialFinalProfit = new Double[numberOfDiscretisation];
		
		potentialFinalPos=linspace(minPos,maxPos,numberOfDiscretisation);
		
		Double[] costVector= new Double[numberOfRunners(inventory)];
	    Double profit=0.0;
		
	    long t0=System.currentTimeMillis();
	    
		for (int i=0;i<numberOfDiscretisation;i=i+1){
			profit=potentialFinalPos[i];// le profit que l'on fait avec un portefeuille donne, en ignorant ce qu'on a depense pour l'obtenir
			// c'est simplement la position a la fin (le prix est deja inclus)
			costVector=transactionPrice(OB,inventory,potentialFinalPos[i]);
			potentialFinalProfit[i]=profit;
			for (int j=0;j<costVector.length;j=j+1){
				potentialFinalProfit[i]=potentialFinalProfit[i]-costVector[j];
			}
		}
		int bestChoice=argmax(potentialFinalProfit);
		costVector=transactionPrice(OB,inventory,potentialFinalPos[bestChoice]);
		
		long t1=System.currentTimeMillis();// Je veux controler le temps que ce calcul met a fonctionner pour voir si on peut simuler plus 
		// de possibilites.
		
		//Print info about the calculation
		//--------------------------------
		System.out.println("Time to find the optimal unwinding strategy");
		System.out.print(t1-t0);
		for (int i=0;i<numberOfRunners(inventory);i=i+1){
			System.out.print("Runner ID :  ");
			System.out.println(inventory[i][4]);
			System.out.print("stake to be taken :    ");
			System.out.println(costVector[i]);
		}
		finalPosition=potentialFinalPos[bestChoice];
		System.out.print("Final expected position on all markets after unwinding :   ");
		System.out.println(finalPosition);
		//--------------------------------
		
		return costVector;
	}
	
	public static void unwindWithGivenFinalPos(Double[] costVector,Double[][] inventory,InflatedCompleteMarketPrices OB) throws IOException{
		// needs to be optimized to send the all the orders at the same time;
		int i=0;
		int runnerId;
		Double best;
		for(Runner nr : APIDemo.selectedMarket.getRunners().getRunner()){
			if (costVector[i]>1.99){
				runnerId=nr.getSelectionId();
				best=Basics.findBest("L", OB, runnerId);
				System.out.println("Volume to be executed :"+ costVector[i]+ " at price "+best+ " for runner " + runnerId);
				int choice=Display.getIntAnswer("Ne pas exécuter 1, exécuter 2 :");
				if (choice==2){
					boolean res=Basics.placeBetlevel("L", best, -5, Math.round(costVector[i]*100)/100.0, runnerId);// no inventory problem for lay side
					System.out.println("Order Successfull :"+res);
				}	
			}
			if (costVector[i]<-1.99){
				runnerId=nr.getSelectionId();
				best=Basics.findBest("B", OB, runnerId);
				System.out.println("Volume to be executed :"+ costVector[i]+ " at price "+best+ " for runner " + runnerId);
				int choice=Display.getIntAnswer("Ne pas exécuter 1, exécuter 2 :");
				if (choice==2){
					boolean res=Basics.placeBetlevel("B", best, -20, Math.round(Math.abs(costVector[i])*100)/100.0, runnerId);
					System.out.println("Order Successfull : "+res);
				// Je decalle de 20 ce qui n'est pas robuste.. Je pourrai faire une fonction qui enregistre jusqua quel niveau il faut aller piocher 
				//la liquidite, mais de toute facon si ca bouge entre temps, on peut imaginer que l'on arrive toujours pas a deboucler.
				}
			}
			i=i+1;
			Basics.waiting(1000);//en attendant l'API correcte, j'attends une seconde apres chaque ordre
		}
	}
	
	public static void optimalUnwind(){
		MUBet[] bets;
		try {
			bets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			Double[][] inventory=Basics.getInventory(bets);
			InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			
			Double[] costVector=findOptimalFinalPosition(OB,inventory);
			unwindWithGivenFinalPos(costVector,inventory,OB);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
