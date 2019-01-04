package com.binomisoft.redviera.dicom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudyRepository extends CrudRepository<Study, String> {

	
	@Query("FROM studies where name like %?1%")
	List<Study> findByNameAndSort(String name, Sort sort);
	
}
