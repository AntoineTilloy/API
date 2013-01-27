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

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import java.util.*;

import basics.Basics;

public class StratJon {

    
   public static  void launch(int horseNumber, double nbLevels, double volume, double volumeMaxImb, java.util.Calendar stopTime){
    
	if(Calendar.getInstance().getTime().before(stopTime.getTime())){

	int selectionID=APIDemo.selectedMarket.getRunners().get(horseNumber);	
		
	//R�cup�rer les Matched et Unmatched
	Basics.waiting(3000);
	MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

	//calculer l'inventaire, �ventuellement l'inventaire en comptant les Unmatched
	Double[][] inventaire=Basics.getInventory(MUBets);

	//r�cup�rer l'OB
	InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());

	//best prices
	double bestBack=Basics.findBest("B", OB, APIDemo.SelectionId)
	
	//LO au best : MM avec d�s�quilibre d'inventaire   1=Back, 2=Lay
	if (inventaire[1]<inventaire[2]+volumeMaxImb){
		PlaceBets bet = new PlaceBets();
		bet.setMarketId(APIDemo.selectedMarket.getMarketId());
		bet.setSelectionId(1);
		bet.setBetCategoryType(BetCategoryTypeEnum.E);
		bet.setBetType(BetTypeEnum.Factory.fromValue("B"));
		bet.setPrice(best(1));
		bet.setSize(volume);
		PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, new PlaceBets[] {bet})[0];
	}
	if (inventaire[2]<inventaire[1]+volumeMaxImb){
		PlaceBets bet = new PlaceBets();
		bet.setMarketId(APIDemo.selectedMarket.getMarketId());
		bet.setSelectionId(1);
		bet.setBetCategoryType(BetCategoryTypeEnum.E);
		bet.setBetType(BetTypeEnum.Factory.fromValue("L"));
		bet.setPrice(best(2));
		bet.setSize(volume);
		PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, new PlaceBets[] {bet})[0];
	}

	//LO loin du best sur k niveaux

	for (int i=1;i<=nbLevels;i++){
	  if (volumePlac�(B,i)=0){ // A construire
		    PlaceBets bet = new PlaceBets();
			bet.setMarketId(APIDemo.selectedMarket.getMarketId());
			bet.setSelectionId(1);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetType(BetTypeEnum.Factory.fromValue("B"));
			bet.setPrice(prix(numPrix(best(1))+i)); // A construire
			bet.setSize(volume);
			PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, new PlaceBets[] {bet})[0];
	  }
	  if (volumePlac�(L,i)=0){ // A construire
	    PlaceBets bet = new PlaceBets();
		bet.setMarketId(APIDemo.selectedMarket.getMarketId());
		bet.setSelectionId(1);
		bet.setBetCategoryType(BetCategoryTypeEnum.E);
		bet.setBetType(BetTypeEnum.Factory.fromValue("B"));
		bet.setPrice(prix(numPrix(best(1))+i)); // A construire
		bet.setSize(volume);
		PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, new PlaceBets[] {bet})[0];
	}
	}

	}
	else{
	d�bouclageOptimal() // A construire
	}

	}
}
}

