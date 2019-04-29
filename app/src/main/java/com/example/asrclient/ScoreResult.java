package com.example.asrclient;

import java.util.List;

public class ScoreResult {
    private List token_t;
    private List token_r;
    private float score;
    private int tokensource;
    private int tokendest;

    ScoreResult(float score, List token_t, List token_r, int tokensource, int tokendest){
        this.token_t = token_t;
        this.token_r = token_r;
        this.score = score;
        this.tokensource = tokensource;
        this.tokendest = tokendest;
    }

    public float getScore(){
        return score;
    }

    public int getTokensource(){
        return tokensource;
    }

    public int getTokendest(){
        return tokendest;
    }

    public List getToken_t(){ return token_t;}

    public List getToken_r(){ return token_r;}
}
