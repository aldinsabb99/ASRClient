package com.example.asrclient;

public class ScoreResult {
    private float score;
    private int tokensource;
    private int tokendest;

    ScoreResult(float score, int tokensource,int tokendest){
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
}
