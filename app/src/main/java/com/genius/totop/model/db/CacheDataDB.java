package com.genius.totop.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="cache_data")
public class CacheDataDB extends Model {

    @Column
    public String shareUrl;
    @Column
    public String helpDesc;
    @Column
    public long modifyTime;
    @Column
    public String price;
    @Column
    public long priceTime;
    @Column
    public String object;
    @Column
    public long objectTime;

}
