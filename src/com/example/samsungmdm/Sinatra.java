package com.example.samsungmdm;

/**
 * Created by wasiur on 2014-08-13.
 */
public class Sinatra {

//    private static String serverURL;
//    private static String baseURL = "http://10.150.102.67:4567";
//    private static String registerDevice = "/RegisterDevice";
//    public Context context;
//
//    public Sinatra(Context context){
//        this.context = context;
//    }
//
//    public void resgisterDevice(){
//
//        serverURL = baseURL+registerDevice;
//        new Connect().execute(serverURL);
//
//    }
//
//    public String getNextCommand(){ //TODO: return a JSON instead
//        return null;
//    }
//
//    private class Connect extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... params) {
//            HttpClient httpclient = new DefaultHttpClient();
//            try{
//                HttpPost httpPost = new HttpPost(params[0]);
//
//                String json = null;
//                try {
//                    json = new JSONObject().put("uuid", "1234").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                StringEntity se = new StringEntity(json);
//
//                se.setContentType("application/json;charset=UTF-8");
//                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
//
//                httpPost.setEntity(se);
//
//                HttpResponse httpresponse = httpclient.execute(httpPost);
//
//                String responseText = null;
//                try {
//                    responseText = EntityUtils.toString(httpresponse.getEntity());
//                }catch (ParseException e) {
//                    e.printStackTrace();
//                    Log.i("Parse Exception", e + "");
//                }
//
//                try {
//                    JSONObject jsonReceive = new JSONObject(responseText);
//                    Log.e("JSON", jsonReceive.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

}
