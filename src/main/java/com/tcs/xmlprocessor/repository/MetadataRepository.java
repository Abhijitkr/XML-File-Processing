package com.tcs.xmlprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.xmlprocessor.model.Metadata;


public interface MetadataRepository extends JpaRepository<Metadata, Integer> { }


