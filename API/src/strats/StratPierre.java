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
			InflatedCompleteRunner r= OB.getRunners().get(1);
			System.out.println("Back best at 1:");
			System.out.println(Basics.findBest("B",OB,r.getSelectionId()))	;
			System.out.println("Lay best at 1:");
			System.out.println(Basics.findBest("L",OB,r.getSelectionId()))	;
			for(int j=0; j<4;j++){				
				System.out.println(OB.getRunners().get(j).toString());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Rendre publiques ces variables dans APIDemo
		
	}

	
}
