package com.example.newss.models;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class News {
    @SerializedName("status")
    private String status;

    @SerializedName("totalResult")
    private int totalResult;

    @SerializedName("articles")
    List<Articles> article;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getTotalResult() {
        return totalResult;
    }
    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }
    public List<Articles> getArticle() {
        return article;
    }
    public void setArticle(List<Articles> article) {
        this.article = article;
    }
}
