package it.nickaquila.Placesocial.impl;

import it.nickaquila.Placesocial.interfaces.PictureCrawler;
import it.nickaquila.Placesocial.jaxb.Picture;
import it.nickaquila.Placesocial.mongodb.Place;

import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * The abstract class which provides the means for a generic picture crawler which exploits the Flickr APIs
 *
 */
public abstract class PictureCrawlerImpl implements PictureCrawler{

	public static final String TAGMODE_ALL="all";
	public static final String TAGMODE_ANY="any";
	public static final int TAGS_LIMIT_ALL=16;
	public static final int TAGS_LIMIT_ANY=8;
	public static final int PICS_DEFAULT=100;
	
	protected String tagMode;
	protected int maxNumberOfPics;
	protected int tagsLimit;
	
	public PictureCrawlerImpl(){
		this.setTagMode(PictureCrawlerImpl.TAGMODE_ALL);
		this.maxNumberOfPics=PICS_DEFAULT;
	}
	
	@Override
	public void setTagMode(String tagMode) {
		if (tagMode.equals(TAGMODE_ALL) || tagMode.equals(TAGMODE_ANY)){
			this.tagMode=tagMode;
		}else{
			this.tagMode=TAGMODE_ALL;
		}
		if (tagMode.equals(TAGMODE_ALL)){
			this.tagsLimit=TAGS_LIMIT_ALL;
		}else{
			this.tagsLimit=TAGS_LIMIT_ANY;
		}
	}
	
	@Override
	public String getTagMode() {
		return this.tagMode;
	}

	@Override
	public void setMaxNumberOfPics(int maxNumberOfPics){
		//if invalid value, set it to default
		if (maxNumberOfPics<0){
			maxNumberOfPics=PICS_DEFAULT;
		}
		this.maxNumberOfPics=maxNumberOfPics;
	}
	
	@Override
	public int getMaxNumberOfPics() {
		return this.maxNumberOfPics;
	}
	
	/**
	 * Given a list of pictures, the method removes all the pics which are tagged with the exclusion tags,
	 * according to the exclusion mode
	 * 
	 * @param pictures - the list of pictures
	 * @param excludeTags - the list of exclusion tags
	 * @return the list of pictures pruned according to the exclusion tags
	 */
	protected ArrayList<Picture> removePicsForTags(ArrayList<Picture> pictures, ArrayList<String> excludeTags){
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
		
		for(Picture pic: pictures){
			String[] tags=pic.getTags();
			boolean toDiscard=false;
			for (String excludeTag: excludeTags){
				for (String tag: tags){
					if (tag.equals(excludeTag)){
						toDiscard=true;
						break;
					}
				}
			}	
			if (!toDiscard){
				pics.add(pic);
			}
		}
		
		return pics;
	}

	@Override
	public abstract ArrayList<Picture> getPicsForTags(ArrayList<String> tags);
	
	@Override
	public abstract ArrayList<Picture> getPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags);
	
	@Override
	public abstract ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, Date minUploadDate);
	
	@Override
	public abstract ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate);
	
	@Override
	public abstract ArrayList<Picture> getPicsForPlace(Place place, ArrayList<String> tags);
	
	@Override
	public abstract ArrayList<Picture> getPicsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags);
	
	@Override
	public abstract ArrayList<Picture> getLatestPicsForPlace(Place place, ArrayList<String> tags, Date minUploadDate);
	
	@Override
	public abstract ArrayList<Picture> getLatestPicsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate);
}
