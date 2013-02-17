package demo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import demo.handler.ExchangeAPI;
import demo.handler.GlobalAPI;
import demo.handler.ExchangeAPI.Exchange;
import demo.util.APIContext;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedMarketPrices;
import generated.exchange.BFExchangeServiceStub.ArrayOfPrice;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.CancelBets;
import generated.exchange.BFExchangeServiceStub.CancelBetsResult;
import generated.exchange.BFExchangeServiceStub.GetAccountFundsResp;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.Market;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.Price;
import generated.exchange.BFExchangeServiceStub.Runner;
import generated.exchange.BFExchangeServiceStub.UpdateBets;
import generated.exchange.BFExchangeServiceStub.UpdateBetsResult;
import generated.global.BFGlobalServiceStub.BFEvent;
import generated.global.BFGlobalServiceStub.EventType;
import generated.global.BFGlobalServiceStub.GetEventsResp;
import generated.global.BFGlobalServiceStub.MarketSummary;
import org.apache.log4j.*;
import org.apache.xmlbeans.impl.common.ValidatorListener.Event;

import strats.StratJon;
import strats.StratPierre;
import strats.StratAntoine;

import basics.Basics;


/** 
 * Demonstration of the Betfair API.
 * 
 * This is the main control class for running the Betfair API demo. 
 * User display and input is handled by the Display class
 * API Management is handled by the classes in the apihandler package 
 */ 
public class APIDemo {

	// Menus
	private static final String[] MAIN_MENU = new String[] 
	    {"View account", "Choose Market", "View Market", "View Complete Market", "Bet Management", "View Usage", "Exit","Last Market", "Quick search for Horse race", "Select Next Market","Strat Auto"};
	   
	private static final String[] BETS_MENU = new String[] 
 	    {"Place Bet", "Update Bet", "Cancel Bet", "Back","Strat Pierre","Strat Jon","Strat Antoine","Green and Cancel","Strat Market Making Market++"};



	// The session token
	public static APIContext apiContext = new APIContext();
	
	// the current chosen market and Exchange for that market
	public static Market selectedMarket;
	public static Market selectedMarket2;
	public static Exchange selectedExchange;
	public static double[] priceLadder;
	
	// Fire up the API demo
	public static void main(String[] args)  throws Exception {
	
		priceLadder=Basics.generatePriceLadder ();
		// Initialise logging and turn logging off. Change OFF to DEBUG for detailed output.
		Logger rootLog = LogManager.getRootLogger();
		Level lev = Level.toLevel("OFF");
		rootLog.setLevel(lev);
		
		//On se connecte direct au compte
		//String username = args.length < 1 ? Display.getStringAnswer("Betfair username:") : args[0];
		//String password = args.length < 2 ? Display.getStringAnswer("Betfair password:") : args[1];
		
		// Perform the login before anything else.
		try
		{
			GlobalAPI.login(apiContext, "gregstubbe01", "sowhat01+");
			System.out.println("connexion à gregstubbe 01");
			//	GlobalAPI.login(apiContext, username, password);
		}
		catch (Exception e)
		{
			// If we can't log in for any reason, just exit.
			Display.showException("*** Failed to log in", e);
			System.exit(1);
		}
		
		boolean finished = false;
		
		while (!finished) {
			try	{
				int choice = Display.getChoiceAnswer("\nChoose an operation", MAIN_MENU);
				switch (choice) {
					case 0: // View account
						showAccountFunds(Exchange.UK);
						showAccountFunds(Exchange.AUS);
						break;
					case 1: // Choose Market 
						chooseMarket(0);
						break;
					case 2: // View Market 
						viewMarket();
						break;
					case 3: // Show Complete Market
						viewCompleteMarket();
						break;
					case 4: // Show Bets 
						manageBets();
						break;
					case 5: // Show Usage
						int type = Display.getChoiceAnswer("\nType of stats required", new String[] {"Combined", "Timed"});
						if (type == 0) {
							Display.showCombinedUsage(apiContext.getUsage());
						} else {
							Display.showTimedUsage(apiContext.getUsage());
						}
						break;
					case 6: // Exit
						finished = true;
						break;
					case 7: // Choose last main market
						Basics.chooselastMkt("C:\\Users\\GREG\\workspace\\market.txt");
						break;
					case 8://Quick search
						searchForHorseRace();
						break;
					case 9://Auto Select
						int delay=15;
						searchNextRace(delay);
						//Basics.Send("centaurecapital", "jonathan.donier@gmail.com antoine.tilloy@gmail.com pierre.baque@polytechnique.edu", "Trading Report", "Ok ça a l'air nickel");
						break;
					case 10: // Strat Auto	
						
						while(true){
							System.out.println();
							searchNextRace(15);
							if(!isMarketSelected()){
								finished=true;
								System.out.println("Ending");
								break;
							}
							System.out.println();
							StratAntoine.numberOfRunners();
							StratPierre.printRace();
							int horseNumber3=0;			
							double nbLevels3=3;
							double stakeLevel=20;
							double volumeMaxImb3=10;
							int delay3=1;
							java.util.Calendar stopTime3=APIDemo.selectedMarket.getMarketTime();
							stopTime3.add(Calendar.MINUTE, -delay3);
							StratJon.stackSmashingBasic(horseNumber3, nbLevels3, stakeLevel, volumeMaxImb3, stopTime3);
						}
						
						
				}
			} catch (Exception e) {
				// Print out the exception and carry on.
				Display.showException("*** Failed to call API", e);
			}
			
		}
		
		// Logout before shutting down.
		try
		{
			GlobalAPI.logout(apiContext);
		}
		catch (Exception e)
		{
			// If we can't log out for any reason, there's not a lot to do.
			Display.showException("Failed to log out", e);
		}
		Display.println("Logout successful");
	}



	// Check if a market is selected
	private static boolean isMarketSelected() {
		if (selectedMarket == null) {
			Display.println("You must select a market");
			return false;
		}
		return true;
	}

	// Retrieve and display the account funds for the specified exchange
	private static void showAccountFunds(Exchange exch) throws Exception {
		GetAccountFundsResp funds = ExchangeAPI.getAccountFunds(exch, apiContext);
		Display.showFunds(exch, funds);
	}
	
	// Select a market by the following process
	// * Select a type of event
	// * Recursively select an event of this type
	// * Select a market within this event if one exists.
	public static void chooseMarket(int number) throws Exception {
		// Get available event types.
		EventType[] types = GlobalAPI.getActiveEventTypes(apiContext);
		
		//Added by Jonathan
		int j=0;
		for(EventType ET : types){
			if ((ET.getName().contains("Soccer") | ET.getName().contains("Horse"))){
				j++;
			}
		}
		EventType[] typeHorseSoccer= new EventType[j];
		int i=0;
		for(EventType ET : types){
			if ((ET.getName().contains("Soccer") | ET.getName().contains("Horse"))){
				typeHorseSoccer[i]=ET;
				i++;
			}
		}
			
		int typeChoice=0;
		if(j!=1){
			typeChoice  = Display.getChoiceAnswer("Choose an event type:", typeHorseSoccer); //Modified
		}else{
			System.out.println(typeHorseSoccer[0].getName());
		}
		// Get available events of this type
		Market selectedMarketInt = null;
		int eventId = typeHorseSoccer[typeChoice].getId(); /// modif par pierre

		while (selectedMarketInt == null) {
			GetEventsResp resp = GlobalAPI.getEvents(apiContext, eventId);
			BFEvent[] events = resp.getEventItems().getBFEvent();
			if (events == null) {
				events = new BFEvent[] {};
			} else {
				// The API returns Coupons as event names, but Coupons don't contain markets so we remove any
				// events that are Coupons.
				ArrayList<BFEvent> nonCouponEvents = new ArrayList<BFEvent>(events.length);
				for(BFEvent e: events) {
					if(!e.getEventName().equals("Coupons")) {
						nonCouponEvents.add(e);
					}
				}
				events = (BFEvent[]) nonCouponEvents.toArray(new BFEvent[]{});
			}
			MarketSummary[] markets = resp.getMarketItems().getMarketSummary();
			if (markets == null) {
				markets = new MarketSummary[] {};
			}

			//Added by Jonathan
			String partialEventName="";
			if(markets.length+events.length>10){
				partialEventName=Display.getStringAnswer("Partial name : ");
			}
			int typeE=0;
			int typeM=0;
			if(partialEventName!=""){
				
				//Choisir parmi les Markets
				j=0;
				for(MarketSummary MS : markets){
					if (MS.getMarketName().toLowerCase().contains(partialEventName.toLowerCase())){
						j++;
						typeM=1;					
					}
				}
				MarketSummary[] marketPartialName = new MarketSummary[j];
				i=0;
				for(MarketSummary MS : markets){
					if (MS.getMarketName().toLowerCase().contains(partialEventName.toLowerCase())){
						marketPartialName[i]=MS;
						i++;
					}
				}
				if (typeM==1){ 
					markets=marketPartialName;
				}
				//Choisir parmi les Events
				j=0;
				for(BFEvent EV : events){
					if (EV.getEventName().toLowerCase().contains(partialEventName.toLowerCase())){
						j++;
						typeE=1;					
					}
				}
				BFEvent[] eventPartialName = new BFEvent[j];
				i=0;
				for(BFEvent EV : events){
					if (EV.getEventName().toLowerCase().contains(partialEventName.toLowerCase())){
						eventPartialName[i]=EV;
						i++;
					}
				}
				if (typeE==1){ 
					events=eventPartialName;
				}
			}	
			if(typeE==1 & typeM==0){
				markets= new MarketSummary[] {};;
			}
			if(typeE==0 & typeM==1){
				events= new BFEvent[] {};
			}
			
		
			//End of added
			
			int choice = Display.getChoiceAnswer("Choose a Market or Event:", events, markets);

			if (choice==999){
				break;
			}
			
			// Exchange ID of 1 is the UK, 2 is AUS
			if (choice < events.length) {
				eventId = events[choice].getEventId(); 
			} else {
				choice -= events.length;
				selectedExchange = markets[choice].getExchangeId() == 1 ? Exchange.UK : Exchange.AUS;
				selectedMarketInt = ExchangeAPI.getMarket(selectedExchange, apiContext, markets[choice].getMarketId());
				if(number==0){
					Basics.memorizeMkt("C:\\Users\\GREG\\workspace\\market.txt",markets[choice]);
				}
			}				
		}
		if(number==0 ){
			selectedMarket=selectedMarketInt;
		}else{
			selectedMarket2=selectedMarketInt;
		}
	}
	
	
	// Retrieve and view information about the selected market
	private static void viewMarket() throws Exception {
		if (isMarketSelected()) {
			InflatedMarketPrices prices = ExchangeAPI.getMarketPrices(selectedExchange, apiContext, selectedMarket.getMarketId());
			
			// Now show the inflated compressed market prices.
			Display.showMarket(selectedExchange, selectedMarket, prices);
		}
	}
	
	private static void viewCompleteMarket () throws Exception {
		if (isMarketSelected()) {
			InflatedCompleteMarketPrices prices = ExchangeAPI.getCompleteMarketPrices(selectedExchange, apiContext, selectedMarket.getMarketId());
			
			// Now show the inflated compressed complete market prices.
			Display.showCompleteMarket(selectedExchange, selectedMarket, prices);
		}
	}
	
	
	// show all my matched and unmatched bets specified market.
	private static void manageBets() throws Exception {
		if (isMarketSelected()) {
			boolean finished = false;
			while (!finished) {
				// show current bets
				MUBet[] bets = ExchangeAPI.getMUBets(selectedExchange, apiContext, selectedMarket.getMarketId());
				Display.showBets(selectedMarket, bets);
				
				int choice = Display.getChoiceAnswer("Choose an operation", BETS_MENU);
				switch (choice) {
					case 0: // Place Bet
						placeBet();
						break;
					case 1: // Update Bet
						updateBet(bets[Display.getIntAnswer("Choose a bet:", 1, bets.length) - 1]);
						break;
					case 2: // Cancel Bet
						cancelBet(bets[Display.getIntAnswer("Choose a bet:", 1, bets.length) - 1]);
						break;
					case 3: // Back
						finished = true;
						break;
					case 4: // Strat Pierre
						StratAntoine.numberOfRunners();
						StratPierre.printRace();
						int horseNumber2=Display.getIntAnswer("Numéro du cheval :");			
						double nbLevels2=3;
						double volume2=2;
						double volumeMaxImb2=10;
						int delay2=5;
						java.util.Calendar stopTime2=APIDemo.selectedMarket.getMarketTime();
						stopTime2.add(Calendar.MINUTE, -delay2);
						StratJon.stackSmashing2(horseNumber2, nbLevels2, volume2, volumeMaxImb2, stopTime2);
						finished = true;
						break;
					case 5: // Strat Jon						
						StratAntoine.numberOfRunners();
						StratPierre.printRace();
						int horseNumber=Display.getIntAnswer("Numéro du cheval :");			
						double nbLevels=3;
						double volume=2;
						double volumeMaxImb=10;
						int delay=2;
						java.util.Calendar stopTime=APIDemo.selectedMarket.getMarketTime();
						stopTime.add(Calendar.MINUTE, -delay);
						StratJon.stackSmashingBasic(horseNumber, nbLevels, volume, volumeMaxImb, stopTime);
						finished = true;
						break;
					case 6: // Unwind
						StratAntoine.optimalUnwind();
						finished = true;
						break;
					case 7: // Cancel and Unwind
						Basics.cancelAll();
						StratAntoine.optimalUnwind();
						finished = true;
						break;
					case 8: // Illiquid MM
						int delay1=2;
						java.util.Calendar stopTime1=APIDemo.selectedMarket.getMarketTime();
						stopTime1.add(Calendar.MINUTE, -delay1);
						StratPierre.IlliquidMM( stopTime1);
						finished = true;
						break;

				}
			}
		}
	}
	
	// Place a bet on the specified market.
	private static void placeBet() throws Exception {
		if (isMarketSelected()) {
			Runner[] runners = selectedMarket.getRunners().getRunner();
			int choice = Display.getChoiceAnswer("Choose a Runner:", runners);
			
			// Set up the individual bet to be placed
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(selectedMarket.getMarketId());
			bet.setSelectionId(runners[choice].getSelectionId());
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(Display.getStringAnswer("Bet type:")));
			bet.setPrice(Display.getDoubleAnswer("Price:", false));
			bet.setSize(Display.getDoubleAnswer("Size:", false));
			
			if (Display.confirm("This action will actually place a bet on the Betfair exchange")) {
				// We can ignore the array here as we only sent in one bet.
				PlaceBetsResult betResult = ExchangeAPI.placeBets(selectedExchange, apiContext, new PlaceBets[] {bet})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" placed. "+betResult.getSizeMatched() +" matched @ "+betResult.getAveragePriceMatched());
				} else {
					Display.println("Failed to place bet: Problem was: "+betResult.getResultCode());
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	private static void updateBet(MUBet bet) throws Exception {
		if (isMarketSelected()) {
			double newPrice = Display.getDoubleAnswer("New Price [Unchanged - "+bet.getPrice()+"]:", true);
			double newSize = Display.getDoubleAnswer("New Size [Unchanged - "+bet.getSize()+"]:", true);

			if (newPrice == 0.0d) {
				newPrice = bet.getPrice();
			}
			if (newSize == 0.0d) {
				newSize = bet.getSize();
			}

			// Set up the individual bet to be edited
			UpdateBets upd = new UpdateBets(); 
			upd.setBetId(bet.getBetId());
			upd.setOldBetPersistenceType(bet.getBetPersistenceType());
			upd.setOldPrice(bet.getPrice());
			upd.setOldSize(bet.getSize());
			upd.setNewBetPersistenceType(bet.getBetPersistenceType());
			upd.setNewPrice(newPrice);
			upd.setNewSize(newSize);
			
			if (Display.confirm("This action will actually edit a bet on the Betfair exchange")) {
				// We can ignore the array here as we only sent in one bet.
				UpdateBetsResult betResult = ExchangeAPI.updateBets(selectedExchange, apiContext, new UpdateBets[] {upd})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" updated. New bet is "+betResult.getNewSize() +" @ "+betResult.getNewPrice());
				} else {
					Display.println("Failed to update bet: Problem was: "+betResult.getResultCode());
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	public static void cancelBet(MUBet bet) throws Exception {
		if (isMarketSelected()) {
			if (Display.confirm("This action will actually cancel a bet on the Betfair exchange")) {
				CancelBets canc = new CancelBets();
				canc.setBetId(bet.getBetId());
				
				// We can ignore the array here as we only sent in one bet.
				CancelBetsResult betResult = ExchangeAPI.cancelBets(selectedExchange, apiContext, new CancelBets[] {canc})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" cancelled.");
				} else {
					Display.println("Failed to cancel bet: Problem was: "+betResult.getResultCode());
				}
			}
		}
	}
	
	
	public static void searchForHorseRace() throws Exception {
		// Get available event types.
		EventType[] types = GlobalAPI.getActiveEventTypes(apiContext);
		
		//Added by Jonathan
		
		String country=Display.getStringAnswer("Country : ");
		String place=Display.getStringAnswer("Place : ");
		String race=Display.getStringAnswer("Race : ");
		
		int j=0;
		int k=0;
		int typeE=0;
		int typeM=0;
		
		for(EventType ET : types){
			if ((ET.getName().toLowerCase().contains("today")==false && ET.getName().toLowerCase().contains("horse"))){
					j++;
			}
		}
		EventType[] typeHorse= new EventType[j];
		int i=0;
		for(EventType ET : types){
			if ((ET.getName().toLowerCase().contains("today")==false && ET.getName().toLowerCase().contains("horse"))){
				typeHorse[i]=ET;
				i++;
			}
		}
		
		int typeChoice=0;
		if(j!=1){
			typeChoice  = Display.getChoiceAnswer("Choose an event type:", typeHorse); //Modified
		}else{
			System.out.println(typeHorse[0].getName());
		}
		
		// Get available events of this type
		Market selectedMarketInt = null;
		int eventId = typeHorse[typeChoice].getId();  //Modified
	
		while (selectedMarketInt == null) {
			GetEventsResp resp = GlobalAPI.getEvents(apiContext, eventId);
			BFEvent[] events = resp.getEventItems().getBFEvent();
			if (events == null) {
				events = new BFEvent[] {};
			} else {
				// The API returns Coupons as event names, but Coupons don't contain markets so we remove any
				// events that are Coupons.
				ArrayList<BFEvent> nonCouponEvents = new ArrayList<BFEvent>(events.length);
				for(BFEvent e: events) {
					if(!e.getEventName().equals("Coupons")) {
						nonCouponEvents.add(e);
					}
				}
				events = (BFEvent[]) nonCouponEvents.toArray(new BFEvent[]{});
			}
			MarketSummary[] markets = resp.getMarketItems().getMarketSummary();
			if (markets == null) {
				markets = new MarketSummary[] {};
			}
			

			
				//Choisir parmi les Markets
				j=0;
				for(MarketSummary MS : markets){
					if (MS.getMarketName().toLowerCase().contains(race.toLowerCase())){
						j++;
						typeM=1;					
					}
				}
				MarketSummary[] marketPartialName = new MarketSummary[j];
				i=0;
				for(MarketSummary MS : markets){
					if (MS.getMarketName().toLowerCase().contains(race.toLowerCase())){
						marketPartialName[i]=MS;
						i++;
					}
				}
				if (typeM==1){ 
					markets=marketPartialName;
				}

				//Choisir parmi les Events
				k=0;
				for(BFEvent EV : events){
					if ((EV.getEventName().toLowerCase().contains(country.toLowerCase()) && EV.getEventName().length()<4)){
						k++;
						typeE=1;
					}
					if ((EV.getEventName().toLowerCase().contains(place.toLowerCase()))){
						k++;
						typeE=1;
					}
				}
				BFEvent[] eventPartialName = new BFEvent[k];
				i=0;
				for(BFEvent EV : events){
					if ((EV.getEventName().toLowerCase().contains(country.toLowerCase()) && EV.getEventName().length()<4)){
						eventPartialName[i]=EV;
						i++;
					}
					if ((EV.getEventName().toLowerCase().contains(place.toLowerCase()))){
							eventPartialName[i]=EV;
						i++;
					}
				}
				if (typeE==1){ 
					events=eventPartialName;
				}
				
			
		
			//End of added
			int choice=0;
			if(k==1){
				System.out.println(eventPartialName[0].getEventName());
			}
			if(j==1){
				System.out.println(marketPartialName[0].getMarketName());
			}
			if(j+k!=1){
				choice = Display.getChoiceAnswer("Choose a Market or Event:", events, markets);
			}
			
			if (choice==999){
				break;
			}
			
			// Exchange ID of 1 is the UK, 2 is AUS
			if (choice < events.length) {
				eventId = events[choice].getEventId(); 
			} else {
				choice -= events.length;
				selectedExchange = markets[choice].getExchangeId() == 1 ? Exchange.UK : Exchange.AUS;
				selectedMarketInt = ExchangeAPI.getMarket(selectedExchange, apiContext, markets[choice].getMarketId());
					Basics.memorizeMkt("C:\\Users\\GREG\\workspace\\market.txt",markets[choice]);
			}				
		}
		
			selectedMarket=selectedMarketInt;
			System.out.println("Market selected : "+selectedMarket.getName());
			System.out.println("Starting at : " + selectedMarket.getMarketTime().getTime());
	}
	
	public static void searchNextRace(int delay) throws Exception {	
		
		int j=0;
		int k=0;
		selectedMarket=null;
		Calendar bestInPlayTime=Calendar.getInstance();
		bestInPlayTime.add(Calendar.HOUR, 3);
		Calendar inPlayTime;
		
		// Get available event types.
		EventType[] types = GlobalAPI.getActiveEventTypes(apiContext);		
		
		//Search the event "Horse Racing - Todays Card"
		for(EventType ET : types){
			if ((ET.getName().toLowerCase().contains("today") && ET.getName().toLowerCase().contains("horse"))){
					j++;
			}
		}
		EventType[] typeHorse= new EventType[j];
		int i=0;
		for(EventType ET : types){
			if ((ET.getName().toLowerCase().contains("today") && ET.getName().toLowerCase().contains("horse"))){
				typeHorse[i]=ET;
				i++;
			}
		}
		
		// Get available events of the type "Horse Racing - Todays Card"
		int eventId = typeHorse[0].getId(); 
	
			GetEventsResp resp = GlobalAPI.getEvents(apiContext, eventId);
			
			//Finds the appropriate Markets (ie GB races)
			
			MarketSummary[] markets = resp.getMarketItems().getMarketSummary();
			if (markets == null) {
				markets = new MarketSummary[] {};
			}	
			
			//Search appropriate Markets
			j=0;
			for(MarketSummary MS : markets){
				if (!MS.getMarketName().contains("(")){
					j++;					
				}
			}
			MarketSummary[] marketPartialName = new MarketSummary[j];
			i=0;
			for(MarketSummary MS : markets){
				if (!MS.getMarketName().contains("(")){
					marketPartialName[i]=MS;
					i++;
				}
			}
			markets=marketPartialName;
			
			//Finds the best market			
			int marketNumber=-1;
			for(int m=0;m<markets.length;m++){
				inPlayTime=markets[m].getStartTime();
				inPlayTime.add(Calendar.MINUTE, -delay);

					if(Calendar.getInstance().getTime().before(inPlayTime.getTime())) {
						if(inPlayTime.before(bestInPlayTime)){
							bestInPlayTime=inPlayTime;
							marketNumber=m;
						}
					}
			}							
			if(marketNumber!=-1){								
				selectedExchange = markets[marketNumber].getExchangeId() == 1 ? Exchange.UK : Exchange.AUS;
				selectedMarket = ExchangeAPI.getMarket(selectedExchange, apiContext, markets[marketNumber].getMarketId());
				Basics.memorizeMkt("C:\\Users\\GREG\\workspace\\market.txt",markets[marketNumber]);	
			}
			
			
			
			
			//Finds the appropriate Events (ie GB races)
			BFEvent[] events = resp.getEventItems().getBFEvent();
			if (events == null) {
				events = new BFEvent[] {};
			} else {
				// The API returns Coupons as event names, but Coupons don't contain markets so we remove any
				// events that are Coupons.
				ArrayList<BFEvent> nonCouponEvents = new ArrayList<BFEvent>(events.length);
				for(BFEvent e: events) {
					if(!e.getEventName().equals("Coupons")) {
						nonCouponEvents.add(e);
					}
				}
				events = (BFEvent[]) nonCouponEvents.toArray(new BFEvent[]{});
			}
			
			k=0;
			for(BFEvent EV : events){
				if (!EV.getEventName().contains("(")){
					k++;
				}
			}
			BFEvent[] eventPartialName = new BFEvent[k];
			i=0;
			for(BFEvent EV : events){
				if (!EV.getEventName().contains("(")){
					eventPartialName[i]=EV;
					i++;
				}
			}			
			events=eventPartialName;		
			
			//Search the Events
			for(BFEvent EV : events){
				resp = GlobalAPI.getEvents(apiContext, EV.getEventId());				
				markets = resp.getMarketItems().getMarketSummary();
				if (markets == null) {
					markets = new MarketSummary[] {};
				}			

			
				//Finds the best market			
					marketNumber=-1;
					for(int m=0;m<markets.length;m++){
						inPlayTime=markets[m].getStartTime();
						inPlayTime.add(Calendar.MINUTE, -delay);

							if(Calendar.getInstance().getTime().before(inPlayTime.getTime())) {
								if(inPlayTime.before(bestInPlayTime)){
									bestInPlayTime=inPlayTime;
									marketNumber=m;
								}
							}
					}	
					if(marketNumber!=-1){
						
						selectedExchange = markets[marketNumber].getExchangeId() == 1 ? Exchange.UK : Exchange.AUS;
						selectedMarket = ExchangeAPI.getMarket(selectedExchange, apiContext, markets[marketNumber].getMarketId());
						Basics.memorizeMkt("C:\\Users\\GREG\\workspace\\market.txt",markets[marketNumber]);	
					}
			}
			
			//Retour : marché trouvé ou non
			if(selectedMarket==null){
				System.out.println("No Market Found");
			}else{
				System.out.println("Market selected : "+ selectedMarket.getMenuPath() + "  " + selectedMarket.getName());
				System.out.println("Starting at : " + selectedMarket.getMarketTime().getTime());
				
			}

	}
	
}
