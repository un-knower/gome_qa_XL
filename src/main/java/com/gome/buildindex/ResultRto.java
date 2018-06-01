package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/2/27.
 */
public class ResultRto {


    private String id;
    private String question;

    public ResultRto(String id, String question) {
        this.id = id;
        this.question = question;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }


}
