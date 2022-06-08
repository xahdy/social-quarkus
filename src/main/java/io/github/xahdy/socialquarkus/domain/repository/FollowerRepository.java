package io.github.xahdy.socialquarkus.domain.repository;

import io.github.xahdy.socialquarkus.domain.model.Follower;
import io.github.xahdy.socialquarkus.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

//para conseguir injetar o repository
@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        //mapeamos os parametros que vamos utilizar na query
        var params = Parameters.with("follower", follower)
                .and("user", user).map();

        //fazemos a query com os parametros
        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);
        //pegamos o primeiro resultado caso exista
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    //retorna uma lista de seguidores daquele id informado
    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params =
                //definindo e mapeando os parametros necessários
                Parameters.with("userId", userId)
                        .and("followerId", followerId)
                        .map();
        //follower corresponde a propriedade follower (follower_id) do model follower (é o seguidor)
        //user corresponde a propriedade user (user_id) do model follower (é quem é seguido)
        //delete de follower aonde follower_id e user_id corresponde com o que passamos
        delete("follower.id =:followerId and user.id =:userId", params);
    }
}
