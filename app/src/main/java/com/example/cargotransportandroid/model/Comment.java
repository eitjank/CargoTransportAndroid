package com.example.cargotransportandroid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private String text;
    private List<Comment> replies;
    private Comment parentComment;
    private Forum parentForum;
    private User author;

    public Comment(String text, Comment parentComment, Forum parentForum, User author) {
        this.text = text;
        this.parentComment = parentComment;
        this.parentForum = parentForum;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Comment(" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", parentComment=" + parentComment +
                ", parentForum=" + parentForum +
                ", author=" + author +
                ')';
    }
}
