package com.example.Repository;

import com.example.Model.OS;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OSRepository extends CrudRepository<OS,Integer> {}
