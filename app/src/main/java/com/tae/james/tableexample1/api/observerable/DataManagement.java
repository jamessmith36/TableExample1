package com.tae.james.tableexample1.api.observerable;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tae.james.tableexample1.NoDataResponse;
import com.tae.james.tableexample1.R;
import com.tae.james.tableexample1.UpdateActivity;
import com.tae.james.tableexample1.api.observerable.baseurl.Constant;
import com.tae.james.tableexample1.api.observerable.model.Model;
import com.tae.james.tableexample1.cache.Database;
import com.tae.james.tableexample1.cache.DatabaseModel;
import com.tae.james.tableexample1.connectionmanagement.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jamessmith on 15/09/2016.
 */
public class DataManagement extends IntentService {

    private static final String TAG = DataManagement.class.getName();
    private List<DatabaseModel> cache = new ArrayList<>();
    protected Model myModel;
    protected DatabaseModel model;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private static Context context;
    private JSONArray jsonArray = new JSONArray();

    public DataManagement() {
        super("service");
    }

    public DataManagement(Context context) {
        super("service");
        this.context = context;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String request = intent.getStringExtra("serveData");
        if(request != null){
            if(request.equals("serveData")){
                testConnection();
            }else{
                Log.v(TAG, "onHandleIntent got unknown instruction");
            }
        }else{
            Log.v(TAG, "onHandleIntent got a null instruction");
        }

    }

    private void testConnection() {

        if (ConnectionManager.isInternetOn()) {
            downloadData();
        } else if (!ConnectionManager.isInternetOn()) {
            Database database = new Database(context);
            if ((database.fetchData() != null) && (database.fetchData().size() > 0)){
                cache = database.fetchData();
                if ((cache == null) || (cache.size() == 0)) {
                    Log.v(TAG, "cache has no data");
                } else {
                    updateView();
                    Log.v(TAG, "cache has data");
                }
            }else{
                NoDataResponse noDataResponse;
                try{
                    noDataResponse = (NoDataResponse) context;
                    noDataResponse.noData(false);
                }catch(ClassCastException e){
                    Log.v(TAG, e.toString());
                }
            }
        }
    }

    private void downloadData() {

        RestAdapter.Builder restAdapter = new RestAdapter.Builder();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        restAdapter.setEndpoint(Constant.getBaseURL())
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        return null;
                    }
                }).build();

        MyObserver api = restAdapter.build().create(MyObserver.class);
        compositeSubscription.add(api.fetchData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new rx.Observer<Model>() {
                    @Override
                    public void onNext(Model model) {
                        myModel = model;
                        Log.v(TAG, "model size: "+myModel.getPeople().size());
                        if (myModel != null) {
                            setCache();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError: "+e.toString());
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    private void setCache() {

        final Bitmap[] image = {BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher)};

        Database database = new Database(context);
        SimpleDateFormat dateFormat = new SimpleDateFormat("F M y");
        Date convertedDate = new Date();

        for (int i = 0; i < myModel.getPeople().size(); i++) {

            Glide.with(context)
                    .load(myModel.getPeople().get(0).getAvatarImage())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(200, 200) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            image[0] = bitmap;
                        }
                    });

            try {
                convertedDate  = dateFormat.parse(myModel.getPeople().get(i).getDateOfBirth());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "date: "+convertedDate.toString());


            model = new DatabaseModel(myModel.getPeople().get(i).getFirstName(),
                    myModel.getPeople().get(i).getLastName(), myModel.getPeople().get(i).getRole(),
                    convertedDate.toString(), image[0]);
            database.newEntry(model);

        }

        cache.add(model);
        if(cache != null) {
            Log.v(TAG, "cache size: "+cache.size());
            updateView();
        }else{
            Log.v(TAG, "cache is null");
        }
    }

    private void updateView(){

        try {
            JSONObject jsonObject = new JSONObject();
            for(int i = 0; i< cache.size(); i++){
                jsonObject.put("forename", cache.get(i).getForename());
                jsonObject.put("surname", cache.get(i).getSurname());
                jsonObject.put("role", cache.get(i).getRole());
                jsonObject.put("dob", cache.get(i).getDob());
                jsonObject.put("imageBlob", cache.get(i).getImage());
                jsonArray.put(jsonObject);
            }
            Log.v(TAG, "intentService jsonArray data: "+jsonArray);
            UpdateActivity updateActivity;
            updateActivity = (UpdateActivity) context;
            updateActivity.update(jsonArray);

        } catch (JSONException | ClassCastException e) {
            Log.v(TAG, e.getMessage());
        }
    }
}
