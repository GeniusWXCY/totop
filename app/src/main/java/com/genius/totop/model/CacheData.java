package com.genius.totop.model;

import java.util.List;

public class CacheData {
    public Help help;
    public TypeWrap price;
    public TypeWrap object;
    public Share share = new Share();

    public class Help{

        public String desc;
        public long time;
    }

    public class TypeWrap{
        public List<Type> types;
        public long time;
    }

    public class Share{
        public String url;
        public long time;
    }
}
