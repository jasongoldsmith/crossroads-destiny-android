package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LevelProgression {

    @SerializedName("dailyProgress")
    @Expose
    private Integer dailyProgress;
    @SerializedName("weeklyProgress")
    @Expose
    private Integer weeklyProgress;
    @SerializedName("currentProgress")
    @Expose
    private Integer currentProgress;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("step")
    @Expose
    private Integer step;
    @SerializedName("progressToNextLevel")
    @Expose
    private Integer progressToNextLevel;
    @SerializedName("nextLevelAt")
    @Expose
    private Integer nextLevelAt;
    @SerializedName("progressionHash")
    @Expose
    private Integer progressionHash;

    public Integer getDailyProgress() {
        return dailyProgress;
    }

    public void setDailyProgress(Integer dailyProgress) {
        this.dailyProgress = dailyProgress;
    }

    public Integer getWeeklyProgress() {
        return weeklyProgress;
    }

    public void setWeeklyProgress(Integer weeklyProgress) {
        this.weeklyProgress = weeklyProgress;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getProgressToNextLevel() {
        return progressToNextLevel;
    }

    public void setProgressToNextLevel(Integer progressToNextLevel) {
        this.progressToNextLevel = progressToNextLevel;
    }

    public Integer getNextLevelAt() {
        return nextLevelAt;
    }

    public void setNextLevelAt(Integer nextLevelAt) {
        this.nextLevelAt = nextLevelAt;
    }

    public Integer getProgressionHash() {
        return progressionHash;
    }

    public void setProgressionHash(Integer progressionHash) {
        this.progressionHash = progressionHash;
    }

}