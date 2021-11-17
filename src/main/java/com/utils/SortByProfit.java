package com.utils;

import com.models.RankDTO;

import java.util.Comparator;

public class SortByProfit implements Comparator<RankDTO> {

    @Override
    public int compare(RankDTO rankElement1, RankDTO rankElement2) {
        return ((int) Math.round(rankElement2.getUsersProfit() - rankElement1.getUsersProfit()));
    }
}
