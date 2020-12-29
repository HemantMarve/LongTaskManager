package com.atlan.entity.dao;

import org.springframework.data.repository.CrudRepository;

import com.atlan.entity.Job;

public interface JobRepo extends CrudRepository<Job, Long> {

}
