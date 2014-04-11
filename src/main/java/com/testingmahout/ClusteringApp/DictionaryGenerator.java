package com.testingmahout.ClusteringApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
//import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
//import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class DictionaryGenerator{
	public static final int SIZE_VECTORS = 128; 	//size of sift vectors used for our current pipeline.

	public static final String Feats_File = "data/example.sift"; 
	public static final String Vectors_File ="data/mahout_vectors";
	public static final String Centroids_File ="data/randCentroids.txt";
	public static final String Dictionary_File ="data/clustering";
	public static final int k=5000;


	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		createFiles(Feats_File,Vectors_File,Centroids_File);
		//KMeansDriver.run(conf, new Path(Vectors_File), new Path(Centroids_File), new Path(Dictionary_File), 0.1,100, true,0.5,true);
	}


	/*
	 * This method reads each line from the csv previously created in the pipeline (features extracted from images)
	 * and creates Named Vectors from them. The NamedVectors are then written into a sequence file, with the help
	 * of the sequence file writer. 
	 * 
	 * Sequence files have key-value pairs. In this case the key will be a sequencial unique number (depending on 
	 * which line of the csv the vector is at) and the value is a VectorWritable wrapper around the double value 
	 * of each index in the vectors.
	 * 
	 * This type of file is needed for mahout to perform clustering algorithms with it.
	 * 
	 * List<NamedVector>
	 * */
	public static void createFiles(String Feats_File,String Vectors_File,String Centroids_File) throws IOException{
		//String Feats_File = "data/example.sift"; //better to send it as an argument to the method!!
		//String Vectors_File ="data/mahout_vectors";
		//String Centroids_File ="data/randCentroids.txt";

		//Vectors need to be in this format for Mahout clustering to work
		List<NamedVector> featVectors = new ArrayList<NamedVector>();
		NamedVector feat1Vector;
		BufferedReader br = null;
		String cvsSplitBy = ",";
		int countAllElements=0;
		int k = 5000;

		try {
			br = new BufferedReader(new FileReader(Feats_File));
			String line;
			Integer lines = 0;
			while ((line = br.readLine()) != null) {

				//comma as separator
				double feats[] = new double[SIZE_VECTORS];

				for(int vecInd=0; vecInd< SIZE_VECTORS ; vecInd++){
					feats[vecInd] = Double.parseDouble(line.split(cvsSplitBy)[vecInd]);
					//countAllElements++;
				}//end for

				feat1Vector = new NamedVector(new DenseVector(feats),lines.toString());
				lines++;
				countAllElements++;
				featVectors.add(feat1Vector);
			}//end while
		} catch (FileNotFoundException e) {
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}// end catch IOException
			}//end if
		}// end finally

		System.out.println("Done transforming vector file to namedVectors!!");

		Configuration conf = new Configuration();
		FileSystem filesys = FileSystem.get(conf);
		Path path = new Path(Vectors_File);

		//writing a sequenceFile from type vector
		SequenceFile.Writer writer = new SequenceFile.Writer(filesys, conf, path, Text.class, VectorWritable.class);
		VectorWritable vec = new VectorWritable();

		//serialize vector data!
		for(NamedVector vector : featVectors){
			vec.set(vector);
			writer.append(new Text(vector.getName()), vec);
		}//end for

		writer.close();

		//create random centers
		String centroids[] = new String[k];
		Random rand = new Random(countAllElements);
		int randomNum = 0;
		int lookForRand = 0;
		BufferedWriter randCentroids = new BufferedWriter(new FileWriter(new File(Centroids_File),true));
		StringBuffer buff = new StringBuffer();

		//retrieve random centroids from file
		for(int c =0;c<k;c++){
			randomNum = rand.nextInt();
			try {
				br = new BufferedReader(new FileReader(Feats_File));
				String line;

				while ((line = br.readLine()) != null && lookForRand!=randomNum) {

					//for(int vecInd=0; vecInd< SIZE_VECTORS ; vecInd++){
					if(lookForRand==randomNum){
						//centroids[k]=Double.parseDouble(line.split(cvsSplitBy)[vecInd]);
						centroids[k] = line;
						buff.append(centroids[c]);
					}else{
						lookForRand++;
					}
					//}//end for
				}//end while
			} catch (FileNotFoundException e) {
			}finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
					}// end catch IOException
				}//end if
			}// end finally
		}//END FOR
		randCentroids.write(buff.toString());
		randCentroids.close();
	}//end create vectors




}//end class
