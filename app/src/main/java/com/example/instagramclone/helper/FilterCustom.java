package com.example.instagramclone.helper;

import com.zomato.photofilters.imageprocessors.Filter;

public class FilterCustom {
    private String name;
    private Filter filter;

    public FilterCustom(String name, Filter filter) {
        this.name = name;
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
