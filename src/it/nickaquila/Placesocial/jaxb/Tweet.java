package it.nickaquila.Placesocial.jaxb;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import twitter4j.HashtagEntity;
import twitter4j.Status;

@XmlRootElement
public class Tweet {
	private long id;
	private String text;
	private Date postingDate;
	private String[] hashtags;

	public Tweet(Status status){
		this.id=status.getId();
		this.text=status.getText();
		this.postingDate=status.getCreatedAt();
		HashtagEntity[] htes=status.getHashtagEntities();
		ArrayList<String> hashtagList=new ArrayList<String>();
		for (HashtagEntity hte: htes){
			hashtagList.add(hte.getText());
		}
		hashtags=hashtagList.toArray(new String[hashtagList.size()]);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String[] getHashtags() {
		return hashtags;
	}

	public void setHashtags(String[] hashtags) {
		this.hashtags = hashtags;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}
	
}
