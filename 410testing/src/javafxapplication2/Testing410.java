/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication2;


import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import senti.Analysis;


/**
 *
 * @author rob
 */
public class Testing410 extends Application {
    
     @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sentiment Analysis");
        Tooltip tool = new Tooltip();
        
        Analysis demo = new Analysis("C:\\Users\\rob\\Desktop\\JarConfig\\config.properties");
        
        
        BorderPane backGroundPane = new BorderPane();
        GridPane combinedToolBox = new GridPane();
        BorderPane centerB = new BorderPane();
        HBox TitleP = new HBox(20);
        VBox leftSideT = new VBox();
        VBox top = new VBox();
        GridPane rightSideT = new GridPane();
        GridPane rightSideB = new GridPane();
        
       
//******************************************************************************
        
        final NumberAxis xAxis = new NumberAxis(0, 1, .1);
        final NumberAxis yAxis = new NumberAxis(0, 1, .1); 
        
        
        final Label counterTex = new Label("Count: ");
        counterTex.setFont(Font.font("Arial", 25));
        counterTex.setStyle("-fx-text-fill: WHITE;");
        
        final Label avgTex = new Label("Average: ");
        avgTex.setFont(Font.font("Arial", 25));
        avgTex.setStyle("-fx-text-fill: WHITE;");
        
        final Label pleasureAvg = new Label("Pleasure Average: ");
        final Label arousalAvg = new Label("Arousal Average: ");
        final Label dominanceAvg = new Label("Dominance Average: ");
        pleasureAvg.setFont(Font.font("Arial", 25));
        pleasureAvg.setStyle("-fx-text-fill: WHITE;");
        arousalAvg.setFont(Font.font("Arial", 25));
        arousalAvg.setStyle("-fx-text-fill: WHITE;");
        dominanceAvg.setFont(Font.font("Arial", 25));
        dominanceAvg.setStyle("-fx-text-fill: WHITE;");
        
        
        
        
        pleasureAvg.setVisible(false);
        arousalAvg.setVisible(false);
        dominanceAvg.setVisible(false);
        pleasureAvg.setManaged(false);
        arousalAvg.setManaged(false);
        dominanceAvg.setManaged(false);
        
        final Label happinessAvg = new Label("Happiness Average: "), sadnessAvg = new Label("Sadness Average: ");
        happinessAvg.setFont(Font.font("Arial", 25));
        sadnessAvg.setStyle("-fx-text-fill: WHITE;");
        sadnessAvg.setFont(Font.font("Arial", 25));
        happinessAvg.setStyle("-fx-text-fill: WHITE;");
        
        happinessAvg.setVisible(false);
        sadnessAvg.setVisible(false);
        happinessAvg.setManaged(false);
        sadnessAvg.setManaged(false);
        
        VBox avg = new VBox(pleasureAvg,arousalAvg,dominanceAvg, happinessAvg, sadnessAvg,avgTex);
        
        final Label counterChange = new Label("0");
        counterChange.setFont(Font.font("Arial", 25));
        counterChange.setStyle("-fx-text-fill: WHITE;");
        
        final Label avgChange = new Label("0");
        avgChange.setFont(Font.font("Arial", 25));
        avgChange.setStyle("-fx-text-fill: WHITE;");
        
        HBox countT = new HBox(avg,counterTex, counterChange);
        HBox avgB = new HBox(avgChange);
        VBox totals = new VBox(countT, avg);
        totals.setAlignment(Pos.CENTER);
        ScatterChart<Number,Number> starter = new
            ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Sadness Probability");
        xAxis.setStyle("-fx-text-fill: WHITE; -fx-font-size: 25px;");
        yAxis.setStyle("-fx-text-fill: WHITE; -fx-font-size: 25px;");
        
        
        
        yAxis.setLabel("Happiness Probability");
            
        rightSideT.getChildren().add(starter);
                    
                    

       
        //Title Setup
       
        Label toolsTitleC = new Label("Query");
        toolsTitleC.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        toolsTitleC.setStyle("-fx-text-fill: WHITE; -fx-font-size: 25px;");
        
        leftSideT.getChildren().add(toolsTitleC);
        
        ComboBox queryChoices = new ComboBox();
        queryChoices.getItems().addAll("Select tweet with certian word", "Select tweet from state in US", "Select tweet from Country",
                "Select tweet with certian Hashtag", "Select tweet with Country code");
        queryChoices.getSelectionModel().selectFirst();
        
        ComboBox states = new ComboBox();
        states.setMaxWidth(Double.MAX_VALUE);
        states.getItems().addAll("AL","AK","AR","AZ","CA","CO","CT","DE","FL","GA",
                "HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS",
                "MO","MT","NE","NV","NH","NJ","NM","NY","NC", "ND" ,"OH", "OK" ,"OR", "PA",
                "RI","SC","SD" , "TN" , "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY");
        states.getSelectionModel().selectFirst();
        states.setVisible(false);
        
        TextField tCountryCode = new TextField();
        tCountryCode.setMaxWidth(Double.MAX_VALUE);
        tCountryCode.setVisible(false);
        states.setManaged(false);
        tCountryCode.setManaged(false);
        
        TextField tWord = new TextField();
        tWord.setMaxWidth(Double.MAX_VALUE);
        
        Button bQuery = new Button("Query");
        bQuery.setMaxWidth(Double.MAX_VALUE);
        Button bClear = new Button("Clear data points");
        bClear.setMaxWidth(Double.MAX_VALUE);
        
        
        ToggleButton anew = new ToggleButton("ANEW Sentiment Analysis");
        ToggleButton sentiWordB = new ToggleButton("SentiWord Sentiment Analysis");
        ToggleButton davies = new ToggleButton("Davies Sentiment Analysis");
        ToggleGroup group = new ToggleGroup();
        anew.setToggleGroup(group);
        sentiWordB.setToggleGroup(group);
        davies.setToggleGroup(group);

        
        TitleP.setAlignment(Pos.CENTER);
        TitleP.getChildren().addAll(anew, sentiWordB, davies);
        
       
//******************************************************************************        
        
        //Setting leftside parameters into grid
        leftSideT.setSpacing(10);
        leftSideT.setPadding(new Insets(0, 20, 20, 20));
        leftSideT.setAlignment(Pos.CENTER);
        leftSideT.getChildren().addAll(queryChoices, states, tCountryCode,tWord,bQuery,bClear,totals);
        rightSideT.setPadding(new Insets(10, 20, 10, 20));
        rightSideT.setAlignment(Pos.CENTER);
        
        
        
        centerB.setLeft(leftSideT);
        centerB.setRight(rightSideT);
        
        
//******************************************************************************        
        
        rightSideB.setPadding(new Insets(10, 20, 10, 20));
        rightSideB.setAlignment(Pos.CENTER);
        
        
        
//******************************************************************************  
      //set them in the correct box
        VBox anewHolder = new VBox();
        top.getChildren().addAll(TitleP, centerB);
        anewHolder.getChildren().add(top);
        top.setPadding(new Insets(10, 10, 10, 10));
        top.setStyle("-fx-background-color: #6495ED;");
        combinedToolBox.add(top, 0, 1);
        top.setAlignment(Pos.CENTER);
        
        
        backGroundPane.setCenter(top);
     

//******************************************************************************
//******************************************************************************  
//******************************************************************************  
       
        queryChoices.setOnAction((event) -> {
         String sChoice = queryChoices.getValue().toString();
         if(sChoice.equals("Select tweet from state in US"))
         {  states.setManaged(true); tCountryCode.setManaged(false);
            states.setVisible(true); tCountryCode.setVisible(false); 
            tWord.setVisible(false); tWord.setManaged(false);
         }
         if(sChoice.equals("Select tweet from Country"))
         {  tCountryCode.setManaged(true); states.setVisible(false);
            states.setManaged(false); tCountryCode.setVisible(true);
            tWord.setVisible(false); tWord.setManaged(false);
         }
         if(sChoice.equals("Select tweet with certian word"))
         {  tCountryCode.setManaged(false); states.setVisible(false);
            states.setManaged(false); tCountryCode.setVisible(false);
            tWord.setVisible(true); tWord.setManaged(true);
         }
         if(sChoice.equals("Select tweet with certian Hashtag"))
         {  tCountryCode.setManaged(false); states.setVisible(false);
            states.setManaged(false); tCountryCode.setVisible(false);
            tWord.setVisible(true); tWord.setManaged(true);
         }
         if(sChoice.equals("Select tweet with Country code"))
         {  tCountryCode.setManaged(false); states.setVisible(false);
            states.setManaged(false); tCountryCode.setVisible(false);
            tWord.setVisible(true); tWord.setManaged(true);
         }
            
            
            
            
        });
        
        
        
        
        
        
        
//******************************************************************************    
        
        TwitStreaming Twit =  twitter();
        bClear.setOnAction((event) -> {
            starter.getData().clear();
        });
        
        bQuery.setOnAction((event) -> {
            int count = 0;
            if(anew.isSelected() == true)
            {   XYChart.Series series1 = new XYChart.Series();
                    series1.setName("Tweets");
                ArrayList<Double> pleasureCalc = new ArrayList(), arousalCalc = new ArrayList() ,
                        dominanceCalc = new ArrayList();
                ArrayList<String>  aTweets = new ArrayList();
                String sChoice = queryChoices.getValue().toString();
            
                DBCursor c = null;
                    if(sChoice.equals("Select tweet from state in US")) 
                    { c = Twit.selectTweetsFromStateInUS(states.getValue().toString());
                    }
                    if(sChoice.equals("Select tweet from Country"))
                    {
                      c = Twit.selectTweetsFromCountry(tCountryCode.getText());
                    }
                    if(sChoice.equals("Select tweet with certian word")){
                      c = Twit.selectTweetsWithWordsInText(tWord.getText());
                    }
                    if(sChoice.equals("Select tweet with certian Hashtag")){
                      c = Twit.selectTweetsWithHashtags(tWord.getText());
                    }
                    if(sChoice.equals("Select tweet with Country code")){
                      c = Twit.selectTweetsFromCountryCode(tWord.getText());
                    }
                    int amount = 0;
                while(c.hasNext()){
                        DBObject dbo = c.next();
                        aTweets.add((String) dbo.get("text"));
                       
                        if(!(dbo.get("text")).equals("NaN"))
                        {    pleasureCalc.add(demo.pleasureCalc((String)dbo.get("text"))); 
                            arousalCalc.add(demo.arousalCalc((String)dbo.get("text")));
                            dominanceCalc.add(demo.dominanceCalc((String)dbo.get("text")));
                        }
                       }
                
                for(int z = 0; z<pleasureCalc.size(); z++)
                {    double nan = pleasureCalc.get(z);
                    String nanCheck = Double.toString(nan);
                    if(nanCheck.equals("NaN"))
                    {
                        pleasureCalc.remove(z);
                        arousalCalc.remove(z);
                        dominanceCalc.remove(z);
                        aTweets.remove(z);
                        z =0;
                    }
                }
                double pAvg = 0;
                double aAvg = 0;
                double dAvg = 0;
                for(int a =0; a<pleasureCalc.size();a++)
                {   pAvg += pleasureCalc.get(a);
                    aAvg += arousalCalc.get(a);
                    dAvg += dominanceCalc.get(a);
                }
                
                double avgCount = pleasureCalc.size();
                pAvg/=avgCount;
                aAvg/=avgCount;
                dAvg/=avgCount;
                
                double pRound =(double) Math.round(pAvg * 100) / 100;
                double aRound =(double) Math.round(aAvg* 100) / 100;
                double dRound = (double) Math.round(dAvg * 100) / 100;
                
                pleasureAvg.setText("Pleasure Average: " +Double.toString(pRound));
                arousalAvg.setText("Arousal Average: "+ Double.toString(aRound));
                dominanceAvg.setText("Dominance Average: "+ Double.toString(dRound));
                
             for(int b = 0; b< pleasureCalc.size(); b++)
                    {   
                         series1.getData().add(new XYChart.Data(pleasureCalc.get(b),arousalCalc.get(b)));
                         
                    }
             counterChange.setText(Integer.toString(series1.getData().size()));
             starter.getData().addAll(series1);
             
             
                    for (XYChart.Series<Number, Number> s : starter.getData()) {
                        for (XYChart.Data<Number, Number> d : s.getData()) {
                            tool.setText(Integer.toString(count));
                            if(count < dominanceCalc.size()){
                                Tooltip.install(d.getNode(), new Tooltip(aTweets.get(count)));
                                if(dominanceCalc.get(count)>= 8)
                                    d.getNode().setStyle("-fx-background-color: #003D14;");
                                else if(dominanceCalc.get(count)>= 6)
                                    d.getNode().setStyle("-fx-background-color: #006B24;");
                                else if(dominanceCalc.get(count)>= 4)
                                    d.getNode().setStyle("-fx-background-color: #19A347;");
                                else if(dominanceCalc.get(count)>= 2)
                                    d.getNode().setStyle("-fx-background-color: #66C285;");
                                else
                                    d.getNode().setStyle("-fx-background-color: #B2E0C2;");
                                count++;
                            }
                            else break;
                        }
                        
                     }
             



                    
            
            
            }
            if(davies.isSelected() == true)
            {   int amount = 0;
                String sChoice = queryChoices.getValue().toString();
                XYChart.Series series1 = new XYChart.Series();
                    series1.setName("Tweets");
                    ArrayList<Double> hapCalc = new ArrayList() , sadCalc = new ArrayList() ;
                    ArrayList<String>  aTweets = new ArrayList();
                    
                    DBCursor c = null;
                    if(sChoice.equals("Select tweet from state in US")) 
                    { c = Twit.selectTweetsFromStateInUS(states.getValue().toString());
                    }
                    if(sChoice.equals("Select tweet from Country"))
                    {
                      c = Twit.selectTweetsFromCountry(tCountryCode.getText());
                    }
                    if(sChoice.equals("Select tweet with certian word")){
                      c = Twit.selectTweetsWithWordsInText(tWord.getText());
                    }
                    if(sChoice.equals("Select tweet with certian Hashtag")){
                      c = Twit.selectTweetsWithHashtags(tWord.getText());
                    }
                    if(sChoice.equals("Select tweet with Country code")){
                      c = Twit.selectTweetsFromCountryCode(tWord.getText());
                    }
                    
                    
                    
                    
                    //DBCursor c = Twit.selectTweetsFromCountry("US");
                    //DBCursor c = Twit.selectTweetsWithWordsInText("NCAA");
                    //DBCursor c = Twit.selectTweetsFromStateInUS("CA");
                    
                    //DBCursor c = Twit.selectTweetsFromCountry("FR
                    
                    while(c.hasNext()){
                        DBObject dbo = c.next();
                        aTweets.add((String) dbo.get("text"));
                        hapCalc.add(demo.calculateProbHappy((String)dbo.get("text"))); 
                        sadCalc.add(demo.calculateProbSad((String)dbo.get("text")));
                        
                    }
                    

                    for(int b = 0; b< hapCalc.size(); b++)
                    {   
                         series1.getData().add(new XYChart.Data(sadCalc.get(b),hapCalc.get(b)));
                    }
                    
                    for(int z = 0; z<hapCalc.size(); z++)
                     {    double nan = hapCalc.get(z);
                            String nanCheck = Double.toString(nan);
                            if(nanCheck.equals("NaN"))
                            {
                                hapCalc.remove(z);
                                sadCalc.remove(z);
                                aTweets.remove(z);
                                z =0;
                            }
                        }
                    counterChange.setText(Integer.toString(series1.getData().size()));
                    starter.getData().addAll(series1);
                    
                    double hAvg = 0;
                    double sAvg = 0;
                 
                    for(int a =0; a<hapCalc.size();a++)
                    {   hAvg += hapCalc.get(a);
                        sAvg += sadCalc.get(a);
                    }

                    double avgCount = hapCalc.size();
                    hAvg/=avgCount;
                    sAvg/=avgCount;
                    

                    double hRound =(double) Math.round(hAvg * 100) / 100;
                    double sRound =(double) Math.round(sAvg* 100) / 100;
                  
                    happinessAvg.setText("Happiness Average: " + Double.toString(hRound));
                    sadnessAvg.setText("Sadness Average: "+ Double.toString(sRound));
                    
                    for (XYChart.Series<Number, Number> s : starter.getData()) {
                        for (XYChart.Data<Number, Number> d : s.getData()) {
                            if(count < hapCalc.size()){
                                Tooltip.install(d.getNode(), new Tooltip(aTweets.get(count)));
                                count++;
                            }
                            else break;
                        }
                        
                     }
                    
            
            }
            if(sentiWordB.isSelected() == true)
            {   int amount = 0;
                String sChoice = queryChoices.getValue().toString();
                XYChart.Series series1 = new XYChart.Series();
                    series1.setName("Tweets");
                    ArrayList<Double> senti = new ArrayList();
                    ArrayList<String>  aTweets = new ArrayList();
                    
                    DBCursor c = null;
                    if(sChoice.equals("Select tweet from state in US")) 
                    { c = Twit.selectTweetsFromStateInUS(states.getValue().toString());
                    }
                    if(sChoice.equals("Select tweet from Country"))
                    {
                      c = Twit.selectTweetsFromCountry(tCountryCode.getText());
                    }
                    if(sChoice.equals("Select tweet with certian word")){
                      c = Twit.selectTweetsWithWordsInText(tWord.getText());
                    }
                    if(sChoice.equals("Select tweet with certian Hashtag")){
                      c = Twit.selectTweetsWithHashtags(tWord.getText());
                    }
                    if(sChoice.equals("Select tweet with Country code")){
                      c = Twit.selectTweetsFromCountryCode(tWord.getText());
                    }
                    
                    
                    //DBCursor c = Twit.selectTweetsFromCountry("US");
                    //DBCursor c = Twit.selectTweetsWithWordsInText("NCAA");
                    //DBCursor c = Twit.selectTweetsFromStateInUS("CA");
                    //DBCursor c = Twit.selectTweetsFromCountry("FR
                    
                    while(c.hasNext()){
                        DBObject dbo = c.next();
                        aTweets.add((String) dbo.get("text"));
                        senti.add(demo.sentiWordCalc((String)dbo.get("text"))); 
                    }
                    

                    senti.stream().forEach((senti1) -> {
                        series1.getData().add(new XYChart.Data(senti1, senti1));
                });
                    for(int z = 0; z<senti.size(); z++)
                     {    double nan = senti.get(z);
                            String nanCheck = Double.toString(nan);
                            if(nanCheck.equals("NaN"))
                            {
                                senti.remove(z);
                                aTweets.remove(z);
                                z =0;
                            }
                        }
                    
                    
                    
                    counterChange.setText(Integer.toString(series1.getData().size()));
                    starter.getData().addAll(series1);
                    
                    double hAvg = 0;
                    hAvg = senti.stream().map((senti1) -> senti1).reduce(hAvg, (accumulator, _item) -> accumulator + _item);
                   
                    double avgCount = senti.size();
                    hAvg/=avgCount;
                    
                    

                    double hRound =(double) Math.round(hAvg * 1000) / 1000;
                    
                    avgTex.setText("Average: "+ Double.toString(hRound));
                  
                    
                    for (XYChart.Series<Number, Number> s : starter.getData()) {
                        for (XYChart.Data<Number, Number> d : s.getData()) {
                            if(count < senti.size()){
                                Tooltip.install(d.getNode(), new Tooltip(aTweets.get(count)));
                                count++;
                            }
                            else break;
                        }
                        
                     }
            }
        
        });
        sentiWordB.setOnAction((event) -> {
            starter.getData().clear();
            xAxis.setLabel("Sadness");
            yAxis.setLabel("Happiness");
            xAxis.setLowerBound(-1);
            xAxis.setUpperBound(1);
            xAxis.setTickUnit(.1);
            yAxis.setLowerBound(-1);
            yAxis.setUpperBound(1);
            yAxis.setTickUnit(.1);
            pleasureAvg.setVisible(false);  
            arousalAvg.setVisible(false);
            dominanceAvg.setVisible(false);
            pleasureAvg.setManaged(false);
            arousalAvg.setManaged(false);
            dominanceAvg.setManaged(false);           
            avgTex.setVisible(true);
            avgTex.setManaged(true);
            happinessAvg.setVisible(false);
            sadnessAvg.setVisible(false);
            happinessAvg.setManaged(false);
            sadnessAvg.setManaged(false);


            starter.setTitle("SentiWord Sentiment Analysis");
        
        
        
        });
        
        anew.setOnAction((event) -> {
            starter.getData().clear();
            xAxis.setLabel("Pleasure ");
            yAxis.setLabel("Arousal ");
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(10);
            xAxis.setTickUnit(1);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(10);
            yAxis.setTickUnit(1);
            pleasureAvg.setVisible(true);
            avgTex.setVisible(false);
            avgTex.setManaged(false);
            arousalAvg.setVisible(true);
            dominanceAvg.setVisible(true);
            pleasureAvg.setManaged(true);
            arousalAvg.setManaged(true);
            dominanceAvg.setManaged(true);
            happinessAvg.setVisible(false);
            sadnessAvg.setVisible(false);
            happinessAvg.setManaged(false);
            sadnessAvg.setManaged(false);

            starter.setTitle("ANEW Sentiment Analysis");
            
        });
        
        davies.setOnAction((event) -> {
                    starter.getData().clear();
                    xAxis.setLowerBound(0);
                    xAxis.setUpperBound(1);
                    xAxis.setTickUnit(.1);
                    yAxis.setLowerBound(0);
                    yAxis.setUpperBound(1);
                    yAxis.setTickUnit(.1);
                    xAxis.setLabel("Sadness ");
                    yAxis.setLabel("Happiness ");
                    starter.setTitle("Davies Sentiment Analysis");
                    pleasureAvg.setVisible(false);
                    arousalAvg.setVisible(false);
                    dominanceAvg.setVisible(false);
                    pleasureAvg.setManaged(false);
                    arousalAvg.setManaged(false);
                    dominanceAvg.setManaged(false);
                    happinessAvg.setVisible(true);
                    sadnessAvg.setVisible(true);
                    happinessAvg.setManaged(true);
                    sadnessAvg.setManaged(true);
                    avgTex.setVisible(false);
                    avgTex.setManaged(false);

        });

//******************************************************************************   
        
      
//******************************************************************************
//******************************************************************************   
        Scene scene = new Scene(backGroundPane);
        
       primaryStage.setMinHeight(650);
       primaryStage.setMinWidth(1000);
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }

   
    public static void main(String[] args) {
        launch(args);
    }
    
    public TwitStreaming twitter()
    {
     TwitStreaming Twit = new TwitStreaming();
        
        Twit.consumerKey = "SIUcyItvuhMnFOIYzsw7CgAcs";
        Twit.consumerSecret = "6ifz7TtXS1pKJ30c6pg8GCrpvxADpV4OocEcP4kGpwn4HJTSGq";
        Twit.accessToken = "80479311-VoeFB2GWxeDRcHrNkFnDzbQTG257JcfqlbzEK77my";
        Twit.accessTokenSecret = "k631YLjCzkyidv7yNwAIepTd6w5Yywh6mA2bvz5KfIpjT";
        Twit.mongoDBURL = "localhost";
        Twit.mongoDBPort = 27017;
        Twit.mongoDBName = "mydb";
        Twit.mongoDBCollecion = "TweetData";
        
        Twit.connectMongoDB();
        return Twit;
    }
    
    
}
