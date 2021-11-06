package com.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "assets", schema = "public")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", unique = true)
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id")
    private UUID userID;

    @Column(name = "gold")
    private Double gold;

    @Column(name = "silver")
    private Double silver;

    @Column(name = "cash")
    private Double cash;

    public Asset(UUID id, UUID userID, Double gold, Double silver, Double cash) {
        this.id = id;
        this.userID = userID;
        this.gold = gold;
        this.silver = silver;
        this.cash = cash;
    }

    public Asset(){

    }

    public UUID getId() {
        return id;
    }

    public UUID getUserID() {
        return userID;
    }

    public Double getGold() {
        return gold;
    }

    public Double getSilver() {
        return silver;
    }

    public Double getCash() {
        return cash;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public void setGold(Double gold) {
        this.gold = gold;
    }

    public void setSilver(Double silver) {
        this.silver = silver;
    }

    public void setCash(Double cash) {
        this.cash = cash;
    }
}
