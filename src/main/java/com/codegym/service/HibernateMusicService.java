package com.codegym.service;

import com.codegym.model.entity.Music;
import com.codegym.repository.IMusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HibernateMusicService implements IMusicService{

    @Autowired
    private IMusicRepository musicRepository;

    @Override
    public List<Music> findAll() {
        return musicRepository.findAll();
    }

    @Override
    public Music findById(int id) {
        return musicRepository.findById(id);
    }

    @Override
    public void save(Music music) {
        musicRepository.save(music);
    }

    @Override
    public void update(int id, Music music) {
        musicRepository.update(id, music);
    }

    @Override
    public void remove(int id) {
        musicRepository.remove(id);
    }

    @Override
    public List<Music> findByName(String name) {
        return musicRepository.findByName(name);
    }
}