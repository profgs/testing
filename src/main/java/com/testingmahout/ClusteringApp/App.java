package com.testingmahout.ClusteringApp;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
//import java.io.IOException;


public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	//builds data model from dataset
    	DataModel model = new FileDataModel(new File("data/dataset.csv"));
    	
    	//correlation similarity
    	UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
    	
    	//build user neighborhood, user-based
    	UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
    	
    	//now we can build our recommender
    	UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    	
    	//Now we can ask our recommender for recommendations -- querying
    	//list of recommended items
    	//get 3 recommendations for user 2
    	List<RecommendedItem> recommendations = recommender.recommend(2, 3);
    	for (RecommendedItem recommendation : recommendations) {
    	  System.out.println(recommendation);
    	}
    	
    	//How good is our recommender??
    	
    }
}
