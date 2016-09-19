package com.tae.james.tableexample1.api.observerable;

import com.tae.james.tableexample1.api.observerable.model.Model;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by jamessmith on 15/09/2016.
 */
public interface MyObserver {

    @GET("/Joe886/testfiles/master/people.json")
    Observable<Model> fetchData();
}
