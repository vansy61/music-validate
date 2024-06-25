package com.codegym.repository;

import com.codegym.model.entity.Music;

import java.util.List;

public interface IMusicRepository {

    List<Music> findAll();

    Music findById(int id);

    void save(Music music);

    void update(int id, Music music);

    void remove(int id);

    List<Music> findByName(String name);

}
