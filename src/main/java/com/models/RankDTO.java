package com.models;

public class RankDTO {
    private Integer usersRankPosition;
    private String username;
    private Integer usersActivity;
    private Double usersProfit;
    private Double usersProfitPerDay;

    public RankDTO(Integer usersRankPosition, String username, Integer usersActivity, Double usersProfit, Double usersProfitPerDay) {
        this.usersRankPosition = usersRankPosition;
        this.username = username;
        this.usersActivity = usersActivity;
        this.usersProfit = usersProfit;
        this.usersProfitPerDay = usersProfitPerDay;
    }

    public RankDTO() {
    }

    public Integer getUsersRankPosition() {
        return usersRankPosition;
    }

    public void setUsersRankPosition(Integer usersRankPosition) {
        this.usersRankPosition = usersRankPosition;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUsersActivity() {
        return usersActivity;
    }

    public void setUsersActivity(Integer usersActivity) {
        this.usersActivity = usersActivity;
    }

    public Double getUsersProfit() {
        return usersProfit;
    }

    public void setUsersProfit(Double usersProfit) {
        this.usersProfit = usersProfit;
    }

    public Double getUsersProfitPerDay() {
        return usersProfitPerDay;
    }

    public void setUsersProfitPerDay(Double usersProfitPerDay) {
        this.usersProfitPerDay = usersProfitPerDay;
    }
}
