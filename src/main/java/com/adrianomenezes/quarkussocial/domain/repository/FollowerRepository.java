package com.adrianomenezes.quarkussocial.domain.repository;

import com.adrianomenezes.quarkussocial.domain.model.Follower;
import com.adrianomenezes.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower,User user) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("follower",follower);
//        params.put("user",user);

        var params =  Parameters.with("follower",follower)
                        .and("user",user)
                                .map();

        PanacheQuery<Follower> query = find("user = :user and follower = :follower",params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();

    }

    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id",userId);
        return query.list();

    }

    public Follower  findByUserAndFollower(User user, User follower) {

        var params =  Parameters.with("follower",follower)
                .and("user",user)
                .map();

        PanacheQuery<Follower> query = find("user = :user and follower = :follower",params);
//        return query.list().get(0);
        return query.firstResult();

    }

    public void deleteByFollowerAndUser(Long userId, Long followerId){
        var params =  Parameters.with("followerId",followerId)
                .and("userId",userId)
                .map();

        delete("user.id = :userId and follower.id = :followerId",params);

    }


}
