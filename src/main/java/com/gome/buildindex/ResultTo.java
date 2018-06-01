package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/2/27.
 */
public class ResultTo {


    private String id;
    private String question;
    private String answer;
    private String catergory;

    public ResultTo(String id, String question, String answer, String catergory) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.catergory = catergory;
    }


    public String getCatergory() {
        return catergory;
    }

    public void setCatergory(String catergory) {
        this.catergory = catergory;
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
