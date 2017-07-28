package com.jaha.web.emaul.model;

import javax.persistence.*;

/**
 * Created by doring on 15. 6. 29..
 */
@Entity
@Table(name = "board_empathy", indexes = {
        @Index(name = "idx_board_empathy_user_id", columnList = "userId"),
        @Index(name = "idx_board_empathy_post_id", columnList = "postId")
})
public class BoardEmpathy {

    public BoardEmpathy(){

    }

    public BoardEmpathy(Long userId, Long postId){
        this.userId = userId;
        this.postId = postId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(nullable = false)
    public Long userId;
    @Column(nullable = false)
    public Long postId;
}
