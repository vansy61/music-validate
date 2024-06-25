package com.codegym.repository;

import com.codegym.model.entity.Music;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class MusicRepository implements IMusicRepository{



    @Override
    public List<Music> findAll() {
        String queryStr =  "SELECT c FROM Music AS c";
        TypedQuery<Music> query = ConnectionUtil.entityManager.createQuery(queryStr, Music.class);
        return query.getResultList();
    }

    @Override
    public Music findById(int id) {
        String queryStr =  "SELECT c FROM Music AS c WHERE c.id = :id";
        TypedQuery<Music> query =  ConnectionUtil.entityManager.createQuery(queryStr, Music.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public void save(Music music) {
        Transaction transaction = null;
        Music origin;
        if (music.getId() == 0) {
            origin = new Music();
        } else {
            origin = findById(music.getId());
        }

        try (Session session = ConnectionUtil.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            origin.setName(music.getName());
            origin.setArtist(music.getArtist());
            origin.setMusicGenre(music.getMusicGenre());
            origin.setLinkSong(music.getLinkSong());
            session.saveOrUpdate(origin);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void update(int id, Music music) {
        Transaction transaction = null;

        try(Session session = ConnectionUtil.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Music musicAvailable = session.get(Music.class, id);

            if (musicAvailable != null) {
                musicAvailable.setName(music.getName());
                musicAvailable.setArtist(music.getArtist());
                musicAvailable.setMusicGenre(music.getMusicGenre());
                musicAvailable.setLinkSong(music.getLinkSong());
                session.saveOrUpdate(musicAvailable);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void remove(int id) {
        Music music = findById(id);

        if (music != null) {
            Transaction transaction =  null;
            try (Session session =  ConnectionUtil.sessionFactory.openSession()) {
                transaction =  session.beginTransaction();
                session.remove(music);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        }
    }

    @Override
    public List<Music> findByName(String name) {
        TypedQuery<Music> query = ConnectionUtil.entityManager.createQuery("SELECT m FROM Music AS m WHERE m.name like :name", Music.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
}
