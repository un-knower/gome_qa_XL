package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/7/18.
 */
public class Blog_pc {

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
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



    public Blog_pc(String classify, String question, String answer) {
        this.classify = classify;
        this.question = question;
        this.answer = answer;
    }



    @Override
    public String toString() {
        return "Blog_pc{" +
                "classify='" + classify + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    private String classify;
    private String question;
    private String answer;
}


