package com.genius.totop.model;

import java.util.List;

public class Category {
    public TypeWrap price;
    public TypeWrap object;

    public class TypeWrap{
        public List<Type> types;
        public long time;
    }
}
