package com.tae.james.tableexample1.api.observerable.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamessmith on 15/09/2016.
 */
public class Model {

    @SerializedName("people")
    @Expose
    private List<Person> people = new ArrayList<>();

    /**
     * @return The people
     */
    public List<Person> getPeople() {
        return people;
    }

    /**
     * @param people The people
     */
    public void setPeople(List<Person> people) {
        this.people = people;
    }

}