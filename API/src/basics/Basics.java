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
import generated.exchange.BFExchangeServiceStub.UpdateBets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import demo.APIDemo;
import demo.handler.ExchangeAPI;
import demo.handler.ExchangeAPI.Exchange;
import demo.handler.GlobalAPI;
import demo.util.Display;
import demo.util.InflatedCompleteMarketPrices;
import demo.util.InflatedCompleteMarketPrices.InflatedCompletePrice;
import demo.util.InflatedCompleteMarketPrices.InflatedCompleteRunner;

import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.global.BFGlobalServiceStub.MarketSummary;

import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;



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

	public static double[] generatePriceLadder () {
		double[] priceLadderdouble=new double[360];
		BigDecimal[] priceLadder=new BigDecimal[360];
		int i;
		for(i=0; i<100;i++){
			priceLadder[i]=new BigDecimal(100+i);
		
		}
		for(i=0; i<50;i++){
			priceLadder[100+i]=new BigDecimal(200+2*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[150+i]=new BigDecimal(300+5*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[170+i]=new BigDecimal(400+10*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[190+i]=new BigDecimal(600+20*i);
		
		}
		for(i=0; i<20;i++){
			priceLadder[210+i]=new BigDecimal(1000+50*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[230+i]= new BigDecimal(2000+100*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[240+i]=new BigDecimal(3000+200*i);
		
		}
		for(i=0; i<10;i++){
			priceLadder[250+i]=new BigDecimal(5000+500*i);
		
		}
		for(i=0; i<100;i++){
			priceLadder[260+i]=new BigDecimal(10000+1000*i);
		
		}
		BigDecimal bd=new BigDecimal(0.01);
		MathContext Mc = new MathContext(3, RoundingMode.HALF_UP);
		for(i=0; i<360;i++){
			priceLadderdouble[i]=priceLadder[i].multiply(bd,Mc).doubleValue();
		
		}
		
return priceLadderdouble;

	}

public static int[] getSelectID(){
	
	int[] selectionIDs=new int[30];
	int j=0;
	for (Runner mr: APIDemo.selectedMarket.getRunners().getRunner()) {
		selectionIDs[j]=mr.getSelectionId();
		j++;
	}
	return selectionIDs;
}


public static int[] getSelectID(Market SM){
	
	int[] selectionIDs=new int[30];
	int j=0;
	for (Runner mr: SM.getRunners().getRunner()) {
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
		while (APIDemo.priceLadder[t]>(prix+0.00001) | APIDemo.priceLadder[t]<(prix-0.00001)) //modif d Antoine
		{
		  if(APIDemo.priceLadder[t]>prix){
			 b=t; 
		  }
		  if(APIDemo.priceLadder[t]<prix){
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
			System.out.println(APIDemo.priceLadder[findPriceLadder(best) + level]+" for a volume "+size+" type BACK");
			//
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
			//
			System.out.println(APIDemo.priceLadder[findPriceLadder(best) + level]+" for a volume "+size+" type LAY");
			//
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
	
	
	public static boolean placeBetlevel(int SMId, String Type,double best, int level,double siz,int SelectionId){
		boolean res=false;
		
		double size=0.01*Math.floor(100*siz);
		
		if(Type=="B"){
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(SMId);
			bet.setSelectionId(SelectionId);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetType(BetTypeEnum.Factory.fromValue("B"));
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(Type));
			//
			System.out.println(APIDemo.priceLadder[findPriceLadder(best) + level]+" for a volume "+size+" type BACK");
			//
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
			bet.setMarketId(SMId);
			bet.setSelectionId(SelectionId);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetType(BetTypeEnum.Factory.fromValue("L"));
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(Type));
			//
			System.out.println(APIDemo.priceLadder[findPriceLadder(best) -level]+" for a volume "+size+" type LAY");
			//
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


public static double[] implicitmidPrice(InflatedCompleteMarketPrices OB){

	int n=strats.StratAntoine.numberOfRunners();
	
	int[] selectionIDs=getSelectID();
	double[][] bestPrice = new double[n][2];
	double[] implmidPrice = new double[n];
	
	//0=lay side, 1=back side
	for (int i=0;i<n;i++){
		bestPrice[i][0]=Basics.findBest("L", OB, selectionIDs[i]);		
		bestPrice[i][1]=Basics.findBest("B", OB, selectionIDs[i]);
	}
	
	for(int i=0;i<n; i++){
		implmidPrice[i]=1;
		for(int j=0; j<n; j++){		
			if(j!=i){
			implmidPrice[i]-=(1/bestPrice[j][1]+1/bestPrice[j][0])/2;
			}		
		}
		implmidPrice[i]=1/implmidPrice[i];
	}
	
	return implmidPrice;

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


public static boolean cancelAll(){
	boolean done=true;

	try {
	MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

		for(int i = 0 ; i< MUBets.length; i++){
		 MUBet bet = MUBets[i];
		
		if(bet.getBetStatus().toString()=="U"){
					Basics.cancelBet(bet);
		}		
	
	}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		done=false;
	}
	return done;

	}


public static boolean cancelAll(String type){
	boolean done=true;

	try {
	MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo

		for(int i = 0 ; i< MUBets.length; i++){
		 MUBet bet = MUBets[i];
		
		 
		if(bet.getBetStatus().toString()=="U"){
			if(bet.getBetType().toString()==type){
					Basics.cancelBet(bet);
			}
		}		
	
	}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		done=false;
	}
	return done;

	}


public static double PnL(){
	double PnL=0;
	Double[][] inventory;
	int[] SelectionIDs;
	int SelectionId;
	double bestBack;
	double bestLay;

	try {
	MUBet[] MUBets = ExchangeAPI.getMUBets(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId()); //Rendre publiques ces variables dans APIDemo
	
	InflatedCompleteMarketPrices OB = ExchangeAPI.getCompleteMarketPrices(APIDemo.selectedExchange, APIDemo.apiContext, APIDemo.selectedMarket.getMarketId());
	inventory=Basics.getInventory(MUBets);
	SelectionIDs=Basics.getSelectID();
		try{	
		for(int runner=0 ; runner<SelectionIDs.length;runner++){
			SelectionId=SelectionIDs[runner];
			bestBack=Basics.findBest("B", OB, SelectionId);
			bestLay=Basics.findBest("L", OB, SelectionId);
			if(inventory[runner][0]-inventory[runner][1]>0){
				PnL+=(inventory[runner][0]-inventory[runner][1])/bestLay;
			}
			
			if(inventory[runner][0]-inventory[runner][1]<0){
				PnL-=(inventory[runner][0]-inventory[runner][1])/bestBack;
			}
		}
		
		try{
		for(int i = 0 ; i< MUBets.length; i++){
			 MUBet bet = MUBets[i];
			
			 
			if(bet.getBetStatus().toString()=="M"){
				if(bet.getBetType().toString()=="B"){
						PnL-=bet.getSize();
				}
			}
			if(bet.getBetStatus().toString()=="M"){
				if(bet.getBetType().toString()=="L"){
						PnL+=bet.getSize();
				}
			}
		}
		}catch (Exception f) {
			// TODO Auto-generated catch block
			f.printStackTrace();
			PnL=-3.14;
		}
		
		}catch (Exception g) {
			// TODO Auto-generated catch block
			g.printStackTrace();
			PnL=-1000;
		}
	
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		PnL=-1000000;
	}
	return PnL;

	}





public static void ecrire(String path, String text) 
{
	PrintWriter ecri ;
	try
	{
		ecri = new PrintWriter(new FileWriter(path));
		ecri.print(text);
		ecri.flush();
		ecri.close();
	}//try
	catch (NullPointerException a)
	{
		System.out.println("Erreur : pointeur null");
	}
	catch (IOException a)
	{
		System.out.println("Problème d'IO");
	}
}//ecrire

public static void ecrireSuite(String path, String text) 
{
	PrintWriter ecri ;
	try
	{
		ecri = new PrintWriter(new FileWriter(path,true));
		ecri.print(text);
		ecri.flush();
		ecri.close();
	}//try
	catch (NullPointerException a)
	{
		System.out.println("Erreur : pointeur null");
	}
	catch (IOException a)
	{
		System.out.println("Problème d'IO");
	}
}//ecrire




public static void memorizeMkt(String path,MarketSummary m){
	ecrire(path, String.valueOf(  m.getExchangeId() )+ "\r\n" + m.getMarketId());
}


public static void chooselastMkt(String path){
	BufferedReader lect ;
	int ExchangeId=0;
	int MktId=0;
	try
	{
		lect = new BufferedReader(new FileReader(path)) ;
		while (lect.ready()==true) 
		{
			ExchangeId= Integer.parseInt(lect.readLine().substring(0, 1));
			MktId=Integer.parseInt(lect.readLine());
			System.out.println(ExchangeId);
			System.out.println(MktId);
		}//while
		APIDemo.selectedExchange = ExchangeId == 1 ? Exchange.UK : Exchange.AUS;
		APIDemo.selectedMarket=ExchangeAPI.getMarket(APIDemo.selectedExchange, APIDemo.apiContext, MktId);
		
	}//try
	catch (NullPointerException a)
	{
		System.out.println("Erreur : pointeur null");
	}
	catch (IOException a) 
	{
		System.out.println("Problème d'IO");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

public static PlaceBets generateBet(String Type,double price, double siz,int SelectionId){

	BigDecimal bd2=new BigDecimal(0.01);
	BigDecimal bd=new BigDecimal(Math.floor(100*siz));
	MathContext Mc = new MathContext(3, RoundingMode.HALF_UP);
	
	double size=bd.multiply(bd2,Mc).doubleValue();
	
	PlaceBets bet = new PlaceBets();
	bet.setMarketId(APIDemo.selectedMarket.getMarketId());
	bet.setSelectionId(SelectionId);
	bet.setBetCategoryType(BetCategoryTypeEnum.E);
	bet.setBetType(BetTypeEnum.Factory.fromValue(Type));
	bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
    bet.setPrice(price);
	bet.setSize(size);
	
	return bet;
}

public static CancelBets generateCancelBet(MUBet bet){

	CancelBets canc = new CancelBets();
	canc.setBetId(bet.getBetId());
	
	return canc;
}

public static void placeBetVector(PlaceBets[] bet) throws Exception {
	

	PlaceBetsResult betResult = ExchangeAPI.placeBets(APIDemo.selectedExchange, APIDemo.apiContext, bet)[0];
	
	if (betResult.getSuccess()) {
		Display.println("Bet "+betResult.getBetId()+" placed.");
	} else {
		Display.println("Failed to place bet: Problem was: "+betResult.getResultCode());
	}
}

public static void cancelBetVector(CancelBets[] bet) throws Exception {
	

	CancelBetsResult betResult = ExchangeAPI.cancelBets(APIDemo.selectedExchange, APIDemo.apiContext, bet)[0];
	
	if (betResult.getSuccess()) {
		Display.println("Bet "+betResult.getBetId()+" cancelled.");
	} else {
		Display.println("Failed to cancel bet: Problem was: "+betResult.getResultCode());
	}
}


public static void Send(final String username, String recipientEmail, String title, String message) throws AddressException, MessagingException {
    Send(username, recipientEmail, "", title, message);
}

/**
 * Send email using GMail SMTP server.
 *
 * @param username GMail username
 * @param password GMail password
 * @param recipientEmail TO recipient
 * @param ccEmail CC recipient. Can be empty if there is no CC recipient
 * @param title title of the message
 * @param message message to be sent
 * @throws AddressException if the email address parse failed
 * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
 */
public static void Send(final String username, String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException {
    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    // Get a Properties object
    Properties props = System.getProperties();
    props.setProperty("mail.smtps.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
    props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.port", "465");
    props.setProperty("mail.smtp.socketFactory.port", "465");
    props.setProperty("mail.smtps.auth", "true");

    /*
    If set to false, the QUIT command is sent and the connection is immediately closed. If set 
    to true (the default), causes the transport to wait for the response to the QUIT command.

    ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
            http://forum.java.sun.com/thread.jspa?threadID=5205249
            smtpsend.java - demo program from javamail
    */
    props.put("mail.smtps.quitwait", "false");

    Session session = Session.getInstance(props, null);

    // -- Create a new message --
    final MimeMessage msg = new MimeMessage(session);

    // -- Set the FROM and TO fields --
    msg.setFrom(new InternetAddress(username + "@gmail.com"));
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

    if (ccEmail.length() > 0) {
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
    }

    Multipart multipart = new MimeMultipart();
    

    
    String fileAttachment="C:\\Users\\GREG\\workspace\\market.txt";

    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setText(message, "utf-8");
	multipart.addBodyPart(messageBodyPart);
	messageBodyPart = new MimeBodyPart();
	DataSource source = new FileDataSource(fileAttachment);
	messageBodyPart.setDataHandler(new DataHandler(source));
	messageBodyPart.setFileName(fileAttachment);
	multipart.addBodyPart(messageBodyPart);
    msg.setSubject(title);
    msg.setSentDate(new Date());
	msg.setContent(multipart);

    SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

    t.connect("smtp.gmail.com", username, GlobalAPI.smtp);
    t.sendMessage(msg, msg.getAllRecipients());      
    t.close();
}



}

