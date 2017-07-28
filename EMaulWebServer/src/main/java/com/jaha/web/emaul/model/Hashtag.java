package com.jaha.web.emaul.model;

import javax.persistence.*;

import org.springframework.core.style.ToStringCreator;

@Entity
@Table(name = "board_post_hashtag")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long postId;

    private String name;

    public Hashtag() {

    }

    public Hashtag(Long postId, String name) {
        this.postId = postId;
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", id)
                .append("post_id", postId)
                .append("name", name)
                .toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
