package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/2/27.
 */
//ResultQA 返回类， 有 id ，question，answer 三个属性
public class ResultQA {


    private String id;
    private String question;
    private String answer;
    private String cat1;
    private String cat2;

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getCat2() {
        return cat2;
    }

    public ResultQA(String cat1, String cat2) {
        this.cat1 = cat1;
        this.cat2 = cat2;

    }

    public ResultQA(String id, String question, String answer)
    {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.cat1 = "-1";
        this.cat2 = "-2";
    }

    public ResultQA(String id, String question, String answer,String cat1,String cat2)
    {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.cat1 = cat1;
        this.cat2 = cat2;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
