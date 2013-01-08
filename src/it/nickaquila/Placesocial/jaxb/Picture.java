package it.nickaquila.Placesocial.jaxb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.tags.Tag;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.ServiceException;

@XmlRootElement
public class Picture {
	
	private String id;
	private String title;
	private String URL;
	private String[] tags;
	private String postingDate;

	public Picture(Photo photo){
		this.id=photo.getId();
		this.title=photo.getTitle();
		this.URL=photo.getUrl();
		Date date=photo.getDatePosted();
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
		this.postingDate=sdf.format(date);
		Collection<Tag> photoTags=photo.getTags();
		ArrayList<String> tagsList=new ArrayList<String>();
		for(Object tagObject: photoTags){
			Tag tag=(Tag) tagObject;
			tagsList.add(tag.getValue());
		}
		this.tags=tagsList.toArray(new String[tagsList.size()]);
	}
	
	public Picture(PhotoEntry photo){
		this.id=photo.getId();
		this.title=photo.getTitle().getPlainText();
		this.URL=photo.getMediaContents().get(0).getUrl();
		Date date=null;
		try {
			date = photo.getTimestamp();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (date!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
			this.postingDate=sdf.format(date);
		}
		Collection<String> tags=photo.getMediaKeywords().getKeywords();
		ArrayList<String> tagsList=new ArrayList<String>();
		for(String tag: tags){
			tagsList.add(tag);
		}
		this.tags=tagsList.toArray(new String[tagsList.size()]);
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	public String getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}

}
