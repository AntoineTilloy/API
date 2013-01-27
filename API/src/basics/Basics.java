package basics;

import generated.exchange.BFExchangeServiceStub;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
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

public class Basics {// Ajout� par pierre
	private static int numberofRunners = 0;

  public static void waiting (int n){
        
        long t0, t1;

        t0 =  System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while ((t1 - t0) < n );
    }

	public static double[] generatePriceLadder () {
		double[] priceLadder=new double[360];
		int i;
		for(i=0; i<100;i++){
			priceLadder[i]=(double) (1+0.01*i);
		
		}
		for(i=0; i<50;i++){
			priceLadder[100+i]=(double) (2+0.02*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[150+i]=(double) (3+0.05*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[170+i]=(double) (4+0.1*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[190+i]=(double) (6+0.2*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[210+i]=(double) (10+0.5*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[230+i]= (double) (20+1*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[240+i]=(double) (30+2*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[250+i]=(double) (50+5*i);
		
		}
		for(i=0; i<100;i++){
			priceLadder[260+i]=(double) (100+10*i);
		
		}
		
		for(i=0; i<360;i++){
			priceLadder[i]=Math.round (100*priceLadder[i])/100;
		
		}
return priceLadder;

	}



	public static double[][] getInventory (MUBet[] MUbets) {
		Market m= APIDemo.selectedMarket;
		
		int j=0;
	
		double[][] inventory= new double[30][5];
		
		
		for (Runner mr: m.getRunners().getRunner()) {
			//if (mr.getSelectionId() == b.getSelectionId()) 		
		
			inventory[j][0]=0.0;
			inventory[j][1]=0.0;
			inventory[j][2]=0.0;
			inventory[j][3]=0.0;
			//Attention, colone 4 est le selection id du runner en double!
			inventory[j][4]=(double) (mr.getSelectionId());
			
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
	
	
	public static void printInventory(double[][] inventory){
		
		for(int k =0; k<numberofRunners; k++){
			for(int i=0;i<5;i++){
				System.out.print(inventory[k][i]+" ");
			}
			System.out.println();
			
		}
	}
	
	
	
	public static double findBest(String type, InflatedCompleteMarketPrices OB, int SelectionId ){

		//Attention, renvoie le best plac� sur l'OB de ce type: best(B)>best(L) !!!
		
		
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
		
		//erreur � la fin
		while (APIDemo.priceLadder[t]>prix | APIDemo.priceLadder[t]<prix)
		{
		  if(APIDemo.priceLadder[t]>prix){
			 b=t; 
		  }
		  if(APIDemo.priceLadder[t]<prix){
			 a=t+1; 
		  } 
		//  System.out.println("a= " + a);
		//  System.out.println("b= " + b);
		  t=(int) Math.floor((a+b)/2);
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
			bet.setPrice(APIDemo.priceLadder[findPriceLadder(best) + level]);
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
			bet.setPrice(APIDemo.priceLadder[findPriceLadder(best) - level]);
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
	
	
}
