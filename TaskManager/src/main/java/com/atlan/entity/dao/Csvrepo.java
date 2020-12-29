package com.atlan.entity.dao;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atlan.entity.CsvStructure;
@Repository
public interface Csvrepo extends CrudRepository<CsvStructure,String> {
	@Modifying
	@Transactional
    @Query(value="Delete from CSV_STRUCTURE as a where a.jobid=:jobid",nativeQuery = true)
    void deleteCsvStructureByJobid(@Param("jobid") String jobid);
	
	@Transactional
	@Query(value="select * from CSV_STRUCTURE as a where a.jobid=:jobid",nativeQuery=true)
    Iterable<CsvStructure> findAllByJobid(@Param("jobid") String jobid);
	
}
