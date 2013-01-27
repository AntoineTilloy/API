package strats;

import basics.Basics;
import generated.exchange.BFExchangeServiceStub.MUBet;
import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedCompleteMarketPrices.InflatedCompletePrice;
import demo.util.InflatedCompleteMarketPrices.InflatedCompleteRunner;

public class StratAntoine {
	
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
		}
		return pos;
	}
	

	public static Double getVolume(InflatedCompleteMarketPrices OB,int runnerId,Double price,String type){
		//Gives the volume on the orderbook at the given quote (it can be 0.0)
		Double volume=0.0;
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
		
		Double cost=0.0;
		Double posToExecute=finalPosition-currentPosition;
		
		if (posToExecute>0.0001){
			String type="L";
			Double quote=Basics.findBest(type, OB, runnerId);
			int quoteIndex=Basics.findPriceLadder(quote);
			Double availableVolume;
			while (posToExecute>0.0001){
				availableVolume=getVolume(OB,runnerId,quote,type);
				if (posToExecute<availableVolume*quote){
					cost=cost+posToExecute/quote;
					posToExecute=0.0;
				}
				else{
					cost=cost+availableVolume;
					posToExecute=posToExecute-availableVolume*quote;
				}
				quoteIndex=quoteIndex-1;
				if ((APIDemo.priceLadder.length>quoteIndex)&&(quoteIndex>0)){
					quote=APIDemo.priceLadder[quoteIndex];
				}
				else{
					cost=100000000.0;//if order book empty, this way of unwinding is just impossible
				}
				
			}
		}
		if (posToExecute<-0.0001){
			String type="B";
			Double quote=Basics.findBest(type, OB, runnerId);
			int quoteIndex=Basics.findPriceLadder(quote);
			Double availableVolume;
			while (posToExecute<-0.0001){
				availableVolume=getVolume(OB,runnerId,quote,type);
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
					quote=APIDemo.priceLadder[quoteIndex];
				}
				else{
					cost=100000000.0;//if order book empty, this way of unwinding is just impossible
				}
			}
		}
		
		return cost;
	}
	
	public static int numberOfRunners(Double[][] inventory){
		int i=0;
		while (inventory[i][4]!=null){
			i=i+1;
		}
		return i;
	}
	
	public static Double[] transactionPrice(InflatedCompleteMarketPrices OB,Double[][] inventory,Double finalPosition){
		// Cost to put every horse to position finalPosition
		
		Double[] costVector=new Double[numberOfRunners(inventory)];
		int i=0;
		int runnerId;
		Double currentPos;
		while (inventory[i][4]!=null){
			runnerId=(int) Math.floor(inventory[i][4]+0.5);
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
			profit=potentialFinalPos[i];
			costVector=transactionPrice(OB,inventory,potentialFinalPos[i]);
			potentialFinalProfit[i]=profit;
			for (int j=0;j<costVector.length;j=j+1){
				potentialFinalProfit[i]=potentialFinalProfit[i]-costVector[i];
			}
		}
		int bestChoice=argmax(potentialFinalProfit);
		costVector=transactionPrice(OB,inventory,potentialFinalPos[bestChoice]);
		
		long t1=System.currentTimeMillis();
		
		//Print info about the calculation
		//--------------------------------
		System.out.println("Time to find the optimal unwinding strategy");
		System.out.print(t1-t0);
		for (int i=0;i<numberOfRunners(inventory);i=i+1){
			System.out.println("Runner ID :  ");
			System.out.print(inventory[i][4]);
			System.out.println("stake to be taken :    ");
			System.out.print(costVector[i]);
		}
		finalPosition=potentialFinalPos[bestChoice];
		System.out.println("Final expected position on all markets after unwinding :   ");
		System.out.print(finalPosition);
		//--------------------------------
		
		return costVector;
	}
	
	public static void unwindWithGivenFinalPos(Double[] costVector,Double[][] inventory,InflatedCompleteMarketPrices OB){
		// needs to be optimized to send the all the orders at the same time;
		int i=0;
		int runnerId;
		Double best;
		while(inventory[i][4]!=null){
			if (costVector[i]>0.99){
				runnerId=(int) Math.floor(inventory[i][4]+0.25);
				best=Basics.findBest("L", OB, runnerId);
				Basics.placeBetlevel("L", 1.01, 0, costVector[i], runnerId);// no inventory problem for lay side
			}
			if (costVector[i]<-0.99){
				runnerId=(int) Math.floor(inventory[i][4]+0.25);
				best=Basics.findBest("B", OB, runnerId);
				Basics.placeBetlevel("B", best, -20, costVector[i], runnerId);
			}
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
