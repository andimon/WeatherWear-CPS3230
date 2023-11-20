package org.weatherwear.clothesrecommender;

public interface IRecommendClothing {
    String recommendClothing() throws Exception;
    String recommendClothing(String IATA, String date) throws  Exception;
}
