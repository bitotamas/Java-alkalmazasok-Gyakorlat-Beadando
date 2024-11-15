package com.example.Repository;

import com.example.Model.CPU;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CPURepository extends CrudRepository<CPU,Integer> {}
