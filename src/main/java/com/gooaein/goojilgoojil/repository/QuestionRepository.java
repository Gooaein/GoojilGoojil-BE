package com.gooaein.goojilgoojil.repository;

import com.gooaein.goojilgoojil.domain.nosql.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findAllByRoomId(String roomId);
}
