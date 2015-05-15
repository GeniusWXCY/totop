package com.genius.totop.model;

import java.util.List;

public class DatasRes<T> {

    public boolean success;
    public int total;
    public List<T> data;
    public int code;
}
