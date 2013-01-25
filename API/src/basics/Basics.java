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
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;

import generated.exchange.BFExchangeServiceStub.MUBet;

public class Basics {// Ajouté par pierre
	private static int numberofRunners = 0;


	public static Float[] generatePriceLadder () {
		Float[] priceLadder=new Float[360];
		int i;
		for(i=0; i<100;i++){
			priceLadder[i]=(float) (1+0.01*i);
		
		}
		for(i=0; i<50;i++){
			priceLadder[100+i]=(float) (2+0.02*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[150+i]=(float) (3+0.05*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[170+i]=(float) (4+0.1*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[190+i]=(float) (6+0.2*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[210+i]=(float) (10+0.5*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[230+i]=(float) (20+1*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[240+i]=(float) (30+2*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[250+i]=(float) (50+5*i);
		
		}
		for(i=0; i<100;i++){
			priceLadder[260+i]=(float) (100+10*i);
		
		}
return priceLadder;

	}



	public static Double[][] getInventory (MUBet[] MUbets) {
		Market m= APIDemo.selectedMarket;
		
		int j=0;
	
		Double[][] inventory= new Double[30][5];
		
		
		for (Runner mr: m.getRunners().getRunner()) {
			//if (mr.getSelectionId() == b.getSelectionId()) 		
		
			inventory[j][0]=0.0;
			inventory[j][1]=0.0;
			inventory[j][2]=0.0;
			inventory[j][3]=0.0;
			//Attention, colone 4 est le selection id du runner en float!
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
	
	
	public static void printInventory(Double[][] inventory){
		
		for(int k =0; k<numberofRunners; k++){
			for(int i=0;i<5;i++){
				System.out.print(inventory[k][i]+" ");
			}
			System.out.println();
			
		}
	}
	
}
