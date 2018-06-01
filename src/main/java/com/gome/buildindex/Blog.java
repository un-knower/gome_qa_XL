package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2016/12/9.
 */
public class Blog {
    private Integer id;
    private String question;
    private String answer;
    private String pro;

    public Blog() {
    }

    public Blog(Integer id, String question, String answer, String pro) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.pro = pro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    //setter and getter
}