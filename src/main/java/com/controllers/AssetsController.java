package com.controllers;

import com.enums.Metal;
import com.models.Asset;
import com.models.Ratios;
import com.repositories.AssetsRepository;
import com.repositories.UsersRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/assets")
public class AssetsController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AssetsRepository assetsRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @GetMapping
    @RequestMapping("/userassets")
    public ResponseEntity<Asset> getUserAssets(@RequestParam UUID userId) throws JSONException, IOException {
        Asset userAssets = getAndTruncateAsset(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userAssets);
    }

    private Asset getAndTruncateAsset(UUID userId) {
        Asset userAssets = assetsRepository.findByUserID(userId);
        userAssets.setGold(truncate(userAssets.getGold()));
        userAssets.setCash(truncate(userAssets.getCash()));
        userAssets.setSilver(truncate(userAssets.getSilver()));
        return userAssets;
    }

    @PostMapping
    @RequestMapping("/trade")
    public ResponseEntity<String> makeTrade(@RequestBody Asset newAsset) {

        int rowsChanged = assetsRepository.updateAssets(
                newAsset.getId(), truncate(newAsset.getGold()),
                truncate(newAsset.getSilver()), truncate(newAsset.getCash()));
        if (rowsChanged > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Transaction success");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed");
    }

    private Double truncate(Double valueToTruncate) {
        return Math.floor(valueToTruncate * 100) / 100;
    }


}
