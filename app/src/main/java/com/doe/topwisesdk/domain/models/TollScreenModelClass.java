package com.doe.topwisesdk.domain.models;

public class TollScreenModelClass {

    private CardDataModel cardDataModel;
    private boolean isSingleJourney;
    private int journeyAmount;

    public CardDataModel getCardDataModel() {
        return cardDataModel;
    }

    public void setCardDataModel(CardDataModel cardDataModel) {
        this.cardDataModel = cardDataModel;
    }

    public boolean isSingleJourney() {
        return isSingleJourney;
    }

    public void setSingleJourney(boolean singleJourney) {
        isSingleJourney = singleJourney;
    }

    public int getJourneyAmount() {
        return journeyAmount;
    }

    public void setJourneyAmount(int journeyAmount) {
        this.journeyAmount = journeyAmount;
    }
}
