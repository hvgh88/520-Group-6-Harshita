package com.umass.hangout.repository.elasticsearch;

import com.umass.hangout.entity.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository("messageElasticsearchRepository")
public interface MessageSearchRepository extends ElasticsearchRepository<Message, Long> {
}