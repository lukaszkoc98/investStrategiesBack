package com.repositories;

import com.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetsRepository extends JpaRepository<Asset, Integer> {

    Asset findByUserID(UUID userID);

}
