package com.satish.scratch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Symbol {

    @JsonProperty("type")
    private String type;
    @JsonProperty("reward_multiplier")
    private double rewardMultiplier;
    @JsonProperty("extra")
    private Integer extra;
    @JsonProperty("impact")
    private String impact;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void setRewardMultiplier(double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public Integer getExtra() {
        return extra;
    }

    public void setExtra(Integer extra) {
        this.extra = extra;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }
}
