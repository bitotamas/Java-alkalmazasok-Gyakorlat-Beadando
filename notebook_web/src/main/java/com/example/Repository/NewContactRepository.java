package com.example.Repository;

import com.example.Model.NewContact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewContactRepository extends CrudRepository<NewContact,Integer> {}
