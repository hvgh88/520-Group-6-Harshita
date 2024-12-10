package com.umass.hangout.repository.elasticsearch;

import com.umass.hangout.entity.Group;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository("groupElasticsearchRepository")
public interface GroupSearchRepository extends ElasticsearchRepository<Group, Long> {
}