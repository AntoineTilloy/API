package basics;

import generated.exchange.BFExchangeServiceStub;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.CancelBets;
import generated.exchange.BFExchangeServiceStub.CancelBetsResult;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.Market;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.Runner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.handler.ExchangeAPI.Exchange;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedCompleteMarketPrices.InflatedCompletePrice;
import demo.util.InflatedCompleteMarketPrices.InflatedCompleteRunner;

import generated.exchange.BFExchangeServiceStub.MUBet;

public class Basics {// Ajouté par pierre
	private static int numberofRunners = 0;

  public static void waiting (int n){
        
        long t0, t1;

        t0 =  System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while ((t1 - t0) < n );
    }

	public static int[] generatePriceLadder () {
		int[] priceLadder=new int[360];
		int i;
		for(i=0; i<100;i++){
			priceLadder[i]=100+i;
		
		}
		for(i=0; i<50;i++){
			priceLadder[100+i]=200+2*i;
		
		}
		for(i=0; i<20;i++){
			priceLadder[150+i]=300+5*i;
		
		}
		for(i=0; i<20;i++){
			priceLadder[170+i]=400+10*i;
		
		}
		for(i=0; i<20;i++){
			priceLadder[190+i]=600+20*i;
		
		}
		for(i=0; i<20;i++){
			priceLadder[210+i]=1000+50*i;
		
		}
		for(i=0; i<10;i++){
			priceLadder[230+i]= 2000+100*i;
		
		}
		for(i=0; i<10;i++){
			priceLadder[240+i]=3000+200*i;
		
		}
		for(i=0; i<10;i++){
			priceLadder[250+i]=5000+500*i;
		
		}
		for(i=0; i<100;i++){
			priceLadder[260+i]=10000+1000*i;
		
		}
		
return priceLadder;

	}

public static int[]getSelectID(){
	
	int[] selectionIDs=new int[30];
	int j=0;
	for (Runner mr: APIDemo.selectedMarket.getRunners().getRunner()) {
		selectionIDs[j]=mr.getSelectionId();
		j++;
	}
	return selectionIDs;
}


	

	public static Double[][] getInventory (MUBet[] MUbets) {
		Market m= APIDemo.selectedMarket;
		
		int j=0;
	
		Double[][] inventory= new Double[30][4];
		
		
		for (Runner mr: m.getRunners().getRunner()) {
			//if (mr.getSelectionId() == b.getSelectionId()) 		
		
			inventory[j][0]=0.0;
			inventory[j][1]=0.0;
			inventory[j][2]=0.0;
			inventory[j][3]=0.0;
			//Attention, colone 4 est le selection id du runner en double!
			
			for(int i=0; i<MUbets.length;i++){			
				if(MUbets[i].getSelectionId()==mr.getSelectionId()){	
					if(MUbets[i].getBetStatus().toString()=="M"){
							if(MUbets[i].getBetType().toString()=="L"){
								inventory[j][0]+=MUbets[i].getPrice()*MUbets[i].getSize();
								
							}
							if(MUbets[i].getBetType().toString()=="B"){
								inventory[j][1]+=MUbets[i].getPrice()*MUbets[i].getSize();					
							}				
						}
					if(MUbets[i].getBetStatus().toString()=="U"){
						if(MUbets[i].getBetType().toString()=="L"){
							inventory[j][2]+=MUbets[i].getPrice()*MUbets[i].getSize();
							
						}
						if(MUbets[i].getBetType().toString()=="B"){
							inventory[j][3]+=MUbets[i].getPrice()*MUbets[i].getSize();					
						}				
					}		
				}	
			}
			j++;
		}
		numberofRunners=j;
		return inventory;
	}
	
	
	public static void printInventory(Double[][] inventory){
		
		for(int k =0; k<numberofRunners; k++){
			for(int i=0;i<4;i++){
				System.out.print(inventory[k][i]+" ");
			}
			System.out.println();
			
		}
	}
	
	
	
	public static double findBest(String type, InflatedCompleteMarketPrices OB, int SelectionId ){

		//Attention, renvoie le best placé sur l'OB de ce type: best(B)>best(L) !!!
		
		
		double price=0.0;
		double lastprice=0.0;
		double returnprice=0.0;
		//System.out.println(SelectionId);
		
		if(type=="L"){	
		//System.out.println("yotype");	
			for (InflatedCompleteRunner r: OB.getRunners()) {
			//	System.out.println("yoId");
		
				if (SelectionId == r.getSelectionId()){

					for ( InflatedCompletePrice p: r.getPrices()) {
						price = p.getPrice();
						if( p.getLayAmountAvailable()>0.0){
							//System.out.println("volume"+p.getBackAmountAvailable());
							//System.out.println("at"+p.getPrice());
							break;
						}
							
						lastprice=price;
					}
					
				break;
				}
				
			}
			returnprice=lastprice;
		}								
		
		if(type=="B"){
		
			for (InflatedCompleteRunner r: OB.getRunners()) {
			
				if (SelectionId == r.getSelectionId()){
				
				
					for ( InflatedCompletePrice p: r.getPrices()) {
						price = p.getPrice();
						if(p.getLayAmountAvailable()>=0.00001 ){break;}
																
						lastprice=price;
					}
				break;
				}
				
			}	
			returnprice=price;						
		}
		
		
		return returnprice;
		 
		
	}
	
	public static int findPriceLadder(double prix ){
		int a=0;
		int b=APIDemo.priceLadder.length;
		int t=0;
		
		//erreur à la fin
		while (0.01*APIDemo.priceLadder[t]>(prix+0.00001) | 0.01*APIDemo.priceLadder[t]<(prix-0.00001)) //modif d Antoine
		{
		  if(0.01*APIDemo.priceLadder[t]>prix){
			 b=t; 
		  }
		  if(0.01*APIDemo.priceLadder[t]<prix){
			 a=t+1; 
		  } 
		  t=(int) Math.floor((a+b)/2);
		  
		  //System.out.println("priceladder(t)="+0.01*APIDemo.priceLadder[t]);
		  //System.out.println("prix= "+prix);
		  //System.out.println("a= " + a);
		  //System.out.println("b= " + b);
		}
		return t;
	}
	
	
	
	public static boolean placeBetlevel(String Type,double best, int level,double size,int SelectionId){
		boolean res=false;
		
		if(Type=="B"){
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(APIDemo.selectedMarket.getMarketId());
			bet.setSelectionId(SelectionId);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetType(BetTypeEnum.Factory.fromValue("B"));
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(Type));
			//
			System.out.println(0.01*APIDemo.priceLadder[findPriceLadder(best) + level]+" for a volume "+size+" type BACK");
			//
			bet.setPrice(0.01*APIDemo.priceLadder[findPriceLadder(best) + level]);
			bet.setSize(size);
			
			try {
				PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, new PlaceBets[] {bet})[0];
				res=betResult.getSuccess();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(Type=="L"){
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(APIDemo.selectedMarket.getMarketId());
			bet.setSelectionId(SelectionId);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetType(BetTypeEnum.Factory.fromValue("L"));
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(Type));
			//
			System.out.println(0.01*APIDemo.priceLadder[findPriceLadder(best) + level]+" for a volume "+size+" type LAY");
			//
			bet.setPrice(0.01*APIDemo.priceLadder[findPriceLadder(best) - level]);
			bet.setSize(size);
			
			try {
				PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, new PlaceBets[] {bet})[0];
				res=betResult.getSuccess();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("placement error");
			}
		}
		return res;		
	}
	
	
	public static double volumeAt (int SelectionId,String type,double Price,MUBet[] MUbets) {
		double volume=0.00;

					
			for(int i=0; i<MUbets.length;i++){			
				if(MUbets[i].getSelectionId()==SelectionId){	
					if(MUbets[i].getBetStatus().toString()=="U"){
						if(MUbets[i].getBetType().toString()==type & Math.abs(MUbets[i].getPrice()- (double) Price)<0.001){
							volume+=MUbets[i].getSize();
						}
					}	
				}	
			}
		return volume;
	}
	
	//public static int SelectionIdOfPosInMarket(int pos){
		
	//}
    
public static double[][] implicitPrice(InflatedCompleteMarketPrices OB){

	int n=strats.StratAntoine.numberOfRunners();
	
	int[] selectionIDs=getSelectID();
	double[][] bestPrice = new double[n][2];
	double[][] implPrice = new double[n][2];
	
	//0=lay side, 1=back side
	for (int i=0;i<n;i++){
		bestPrice[i][0]=Basics.findBest("L", OB, selectionIDs[i]);		
		bestPrice[i][1]=Basics.findBest("B", OB, selectionIDs[i]);
	}
	
	for(int i=0;i<n; i++){
		implPrice[i][0]=1;
		implPrice[i][1]=1;
		for(int j=0; j<n; j++){		
			if(j!=i){
			implPrice[i][0]-=1/bestPrice[j][1];
			implPrice[i][1]-=1/bestPrice[j][0];
			}		
		}
		implPrice[i][0]=1/implPrice[i][0];
		implPrice[i][1]=1/implPrice[i][1];
	}
	
	return implPrice;

}

public static void cancelBet(MUBet bet) throws Exception {
	

			CancelBets canc = new CancelBets();
			canc.setBetId(bet.getBetId());
			
			// We can ignore the array here as we only sent in one bet.
			CancelBetsResult betResult = ExchangeAPI.cancelBets(APIDemo.selectedExchange, APIDemo.apiContext, new CancelBets[] {canc})[0];
			
			if (betResult.getSuccess()) {
				Display.println("Bet "+betResult.getBetId()+" cancelled.");
			} else {
				Display.println("Failed to cancel bet: Problem was: "+betResult.getResultCode());
			}
		}
		
	
}
