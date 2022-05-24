package io.github.xahdy.socialquarkus.domain.repository;

import io.github.xahdy.socialquarkus.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
