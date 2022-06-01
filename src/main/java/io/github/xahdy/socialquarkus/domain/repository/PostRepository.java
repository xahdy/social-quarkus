package io.github.xahdy.socialquarkus.domain.repository;

import io.github.xahdy.socialquarkus.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {

    public Post selectPostByIdAndUserId(Long postId, Long userId){
        Post postSelected;
        //adicionamos os resultados da query que busca por id e userid
        var queryPostsByIdAndUser = find("FROM Post posts WHERE id = "+postId+" AND user_id = "+userId+"");
        //transformamos os resultados em uma lista de posts
        var listPostByIdAndUser = queryPostsByIdAndUser.list();
        //se nossa lista estiver vazia, nosso postToDelete retorna nullo pro controlador.
        if(listPostByIdAndUser.isEmpty()){
            return postSelected = null;
        }
        postSelected = listPostByIdAndUser.get(0);
        return postSelected;
    }

}
