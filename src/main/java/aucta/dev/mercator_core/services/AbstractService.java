package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.exceptions.HttpException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface AbstractService<T, R> {
    Page<T> all(Pageable pageable);
    T get(R id);
    T save(T t) throws HttpException;
    T save(T t, UserDetails creator) throws HttpException;
    Boolean remove(R id);
    Boolean remove(R id, UserDetails creator);
}