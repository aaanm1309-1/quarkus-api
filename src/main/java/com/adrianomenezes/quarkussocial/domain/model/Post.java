package com.adrianomenezes.quarkussocial.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "POSTS")
@Data
public class Post {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "post_text")
        private String postText;

        @Column(name = "dateTime")
        private LocalDateTime dateTime;

        @ManyToOne
        @JoinColumn(name = "userid")
        private User user;

        @PrePersist
        public void prePersist(){
            setDateTime(LocalDateTime.now());
        }
}
