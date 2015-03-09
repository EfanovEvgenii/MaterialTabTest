package it.neokree.materialtabtest;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by efanovev on 16.02.2015.
 */
public class ExternalData {

    private static String TAG = "curLog";
    private Context mContext;

    public static ExternalData newInstance(Context ctx){

        return new ExternalData(ctx);
    }

    public ExternalData(Context ctx){
        mContext = ctx;
    }

    public void loadToday() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadTodayThread();

            }
        });
        t.start();
    }

    private void loadTodayThread(){
        URL url;
        DB mDB = new DB(mContext);
        mDB.open();

        Log.d(TAG, "start");
        try{
            String currencyFeed = ((SwipableTextTabActivity)mContext).getString(R.string.today_feed);
            url = new URL(currencyFeed);
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream in = httpConnection.getInputStream();
                XmlPullParserFactory factory;
                factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(in, null);
                int eventType = xpp.getEventType();
                String curDate = "";
                String curPair = "";
                String curSignal = "";
                String curTP = "";
                String curRate = "";
                String curProb = "";
                Long curTime = 0L;
                boolean mustInsert = false;
                while (eventType != XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.START_TAG ) {

                        Log.d(TAG, "cur  start tag =" + xpp.getName());
                        mustInsert = false;

                        if (xpp.getName().equalsIgnoreCase("date")){
                            curDate = xpp.getAttributeValue(0);
                            Log.d(TAG, "    curDate =" + curDate);
                        }

                        if (xpp.getName().equalsIgnoreCase("tool")){
                            curPair = xpp.getAttributeValue(0);
                            curSignal = xpp.getAttributeValue(1);
                            Log.d(TAG, "    curPair =" + curPair);
                            Log.d(TAG, "       curSignal =" + curSignal);
                        }

                        if (xpp.getName().equalsIgnoreCase("level")){
                            curTP = xpp.getAttributeValue(0);
                            Log.d(TAG, "          curTP =" + curTP);
                        }

                        if (xpp.getName().equalsIgnoreCase("rate")){
                            curRate = xpp.nextText();
                            Log.d(TAG, "             curRate =" + curRate);
                        }

                        if (xpp.getName().equalsIgnoreCase("probability")){
                            curProb = xpp.nextText();
                            Log.d(TAG, "             curProb =" + curProb);
                            mustInsert = true;
                        }

                        if (mustInsert) {

                            if (curProb.trim().isEmpty()) curProb = "0.00";
                            if (curRate.trim().isEmpty()) curRate = "0.00";
                            if (curTP.trim().isEmpty()) curTP = "0";
                            SimpleDateFormat ft_ymd = new SimpleDateFormat("dd.MM.yyyy");
                            curTime =ft_ymd.parse(curDate).getTime();

                            // Long date, String tool, int tp, double prob, String signal, double rate
                            mDB.addRec(curTime, curPair, Integer.parseInt(curTP), Double.parseDouble(curProb), curSignal, Double.parseDouble(curRate));

                        }
//                        &&
//                    } xpp.getName().equalsIgnoreCase("date")){
//                        Log.d(TAG, "date date =" + xpp.getAttributeValue(0));
//                        eventType = xpp.next();
//                        while (!(eventType == XmlPullParser.END_TAG  && xpp.getName().equalsIgnoreCase("rate")) && xpp.getName()!=null){
//                            if (eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("tool")){
//                                    Log.d(TAG, "tool pair =" + xpp.getAttributeValue(0));
//                                    Log.d(TAG, "tool signal =" + xpp.getAttributeValue(1));
//                                   // eventType = xpp.next();
//
//
//
//                            }
//                            eventType = xpp.next();
//                        }

                    }
                    eventType = xpp.next();
                }
            }


        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            Log.d(TAG, "XmlPullParserException");
            e.printStackTrace();
        } catch (ParseException e) {
            Log.d(TAG, "ParseException");
            e.printStackTrace();
        }

    }

}
