package strats;

import basics.Basics;
import generated.exchange.BFExchangeServiceStub.MUBet;
import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.InflatedCompleteMarketPrices;

public class StratAntoine {

	public static double[] linspace(double min, double max, int number){
		double[] array=new double[number];
		double step=(max-min)/number;
		for (int i=0; i<number;i=i+1){
			array[i]=step*i+min;
		}
		return array;
	}
	
	public static double getHighestPosition(Double[][] inventory){
		double pos=0;
		return pos;
	}
	public static double getLowestPosition(Double[][] inventory){
		double pos=0;
		return pos;
	}
	public static double transactionPrice(InflatedCompleteMarketPrices OB,int runnerId,double finalPosition,String type){
		// Cost to reach a position of finalPosition for the horse given by runnerId
		double cost=0;
		return cost;
	}
	public static double transactionPrice(InflatedCompleteMarketPrices OB,double finalPosition){
		// Cost to put every horse to position finalPosition
		double cost=0;
		return cost;
	}
	
	public static void optimalUnwind(){
		MUBet[] bets;
		try {
			bets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
			Double[][] inventory=Basics.getInventory(bets);
			InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

//comment
		
		//test
		
	}
}
