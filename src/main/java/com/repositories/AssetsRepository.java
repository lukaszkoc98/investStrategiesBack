package com.repositories;

import com.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface AssetsRepository extends JpaRepository<Asset, Integer> {

    Asset findByUserID(UUID userID);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update public.assets set gold = :gold, silver = :silver, cash = :cash where id = :id", nativeQuery = true)
    @Transactional
    int updateAssets(UUID id, Double gold, Double silver, Double cash);

    @Query(value = "select public.users.username, creation_date, cash, silver, gold from public.assets left join public.users on public.assets.user_id=public.users.id", nativeQuery = true)
    List<Object[]> getUsersAndAssets();


}
