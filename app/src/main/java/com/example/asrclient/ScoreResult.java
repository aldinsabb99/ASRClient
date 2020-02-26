package com.example.asrclient;

import com.atilika.kuromoji.ipadic.Token;

import java.util.List;

public class ScoreResult {
    private List<Token> token_t;
    private List<Token> token_r;
    private float score;
    private int tokensource;
    private int tokendest;

    ScoreResult(float score, List<Token> token_t, List<Token> token_r, int tokensource, int tokendest){
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

    public List<Token> getToken_t(){ return token_t;}

    public List<Token> getToken_r(){ return token_r;}
}
