package com.example.samsungmdm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by wasiur on 2014-08-13.
 */
public class Communication {

    //URL Stuff
    private static String serverURL;
    private static String baseURL = "http://10.150.102.67:4567";
    private static final String registerDeviceRoute = "/RegisterDevice";
    private static final String getNextCommandRoute = "/GetNextCommand";
    private ServerResponseInterface listener;

    //SERVER response stuff
    private String serverResponse;
    public Context context;

    public Communication(ServerResponseInterface listener, Context context) {
        this.context = context;
        this.listener = listener;
    }

    public void registerDevice() {
        serverURL = baseURL + registerDeviceRoute;
        new Connect().execute(serverURL);
    }

    public void getNextCommand() { //TODO: return a JSON instead
        serverURL = baseURL + getNextCommandRoute;
        new Connect().execute(serverURL);
    }

    public void setServerResponse(String response) {
        this.serverResponse = response;
    }

    public String getServerResponse() {
        return this.serverResponse;
    }

    private class Connect extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Create http client and add a post header
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);
            try {
                //Adding data
                JSONObject requestJSONObject = new JSONObject();
                if (params[0].contains(registerDeviceRoute)) {
                    requestJSONObject.accumulate("uuid", "12345");
                } else if (params[0].contains(getNextCommandRoute)) {
                    requestJSONObject.accumulate("uuid", "12345");
                }
                StringEntity se = new StringEntity(requestJSONObject.toString());
                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httpPost.setEntity(se);
                //Execute the post request
                HttpResponse response = httpClient.execute(httpPost);
                //Parsing the response
                HttpEntity entity = response.getEntity();
                JSONObject responseJSON = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
                setServerResponse(responseJSON.toString());
                Log.e("TAG", responseJSON.toString());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listener.onServerResponse(getServerResponse());
        }
    }

}
