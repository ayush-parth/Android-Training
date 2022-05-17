package com.parth.androidtraining.model.enums;

import org.json.JSONObject;

public class MatchCardPopulateType {
    public static final int DATE_TYPE=0;
    public static final int MATCH_TYPE=1;
    public static final int END_TYPE=2;

    public int type;
    public JSONObject matchObject;
    public String clubedDate;

    public MatchCardPopulateType(int type, JSONObject matchObject, String text) {
        this.type = type;
        this.matchObject = matchObject;
        this.clubedDate = text;
    }
}
