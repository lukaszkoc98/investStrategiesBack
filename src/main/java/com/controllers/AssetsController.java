package com.controllers;

import com.models.Asset;
import com.models.RankDTO;
import com.repositories.AssetsRepository;
import com.repositories.UsersRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

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

    @GetMapping
    @RequestMapping("/rank")
    public ResponseEntity<ArrayList<RankDTO>> getUsersRank(@RequestParam Double silverRatio, @RequestParam Double goldRatio) throws JSONException, IOException {
        List<Object[]> datafromDB = assetsRepository.getUsersAndAssets();
        ArrayList <RankDTO> rank = new ArrayList<>();
        parseDBDataToRankDTO(silverRatio, goldRatio, datafromDB, rank);
        return ResponseEntity.status(HttpStatus.OK).body(rank);
    }

    private void parseDBDataToRankDTO(Double silverRatio, Double goldRatio, List<Object[]> datafromDB, ArrayList<RankDTO> rank) {
        for (Object[] rankElement : datafromDB) {
            String username = String.valueOf(rankElement[0]);
            long usersActivity = DAYS.between(LocalDate.parse(rankElement[1].toString()),LocalDate.now());
            Double cash = Double.valueOf(rankElement[2].toString());
            Double silverValue = Double.parseDouble(rankElement[3].toString()) * silverRatio;
            Double goldValue = Double.parseDouble(rankElement[4].toString()) * goldRatio;
            Double totalProfit = cash + silverValue + goldValue - 10000;
            rank.add(new RankDTO(null,username, (int) usersActivity, totalProfit, totalProfit/usersActivity));
        }
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
