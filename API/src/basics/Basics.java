package basics;

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

import generated.exchange.BFExchangeServiceStub.MUBet;

public class Basics {// Ajouté par pierre
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



	public static Double[] inventory (MUBet[] MUbets) {
		Double[] inventory=new Double[4];
		for(int i=0; i<MUbets.length;i++){
			if(MUbets[i].getBetStatus().toString()=="matched"){
				if(MUbets[i].getBetType().toString()=="L"){
					inventory[0]=MUbets[i].getPrice()*MUbets[i].getSize();
					
				}
				if(MUbets[i].getBetType().toString()=="B"){
					inventory[1]=MUbets[i].getPrice()*MUbets[i].getSize();					
				}				
			}
			if(MUbets[i].getBetStatus().toString()=="unmatched"){
				if(MUbets[i].getBetType().toString()=="L"){
					inventory[2]=MUbets[i].getPrice()*MUbets[i].getSize();
					
				}
				if(MUbets[i].getBetType().toString()=="B"){
					inventory[3]=MUbets[i].getPrice()*MUbets[i].getSize();					
				}				
			}
		}
		return inventory;
	}
	
}
