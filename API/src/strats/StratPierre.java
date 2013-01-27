package strats;
import generated.exchange.BFExchangeServiceStub;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
			Basics.printInventory(inventory);
			System.out.println("###############");
			InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

			for(int j=0; j<3;j++){
				System.out.println("###############");
				System.out.println(OB.getRunners().get(j).getSelectionId());
				InflatedCompleteRunner r= OB.getRunners().get(j);
				System.out.print("Back best: ");
				System.out.println(Basics.findBest("B",OB,r.getSelectionId()))	;
				System.out.print("Lay best at: ");
				System.out.println(Basics.findBest("L",OB,r.getSelectionId()))	;
			}
			System.out.println("!!!!!!!!!!!!!!!");
			/*
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

	
}
