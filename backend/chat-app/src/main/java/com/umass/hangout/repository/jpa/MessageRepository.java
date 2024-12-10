package com.umass.hangout.repository.jpa;

import com.umass.hangout.entity.Message;
import com.umass.hangout.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("messageRepository")
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByGroupIdOrderByIdAsc(Long groupId);
}