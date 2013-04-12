package com.kuaipan.client.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class KuaipanFile {
	public String file_id;
	public String name;
	public String hash;
	public String root;
	public String path;
	public String rev;
	public Date create_time = null;
	public Date modify_time = null;
	public boolean is_deleted = false;
	public String type = "file";
	public int size = 0;
	public List<KuaipanFile> files = null;
	
	public KuaipanFile() {}
	
	public KuaipanFile(Map<String, Object> map) {
		parseFromMap(map);
	}
	
	protected void parseFromMap(Map<String, Object> map) {
		this.file_id = (String) map.get("file_id");
		this.name = (String) map.get("name");
		this.hash = (String) map.get("hash");
		this.root = (String) map.get("root");
		this.path = (String) map.get("path");
		this.rev = (String) map.get("rev");
		
		this.create_time = convert2Date(map.get("create_time"));
		this.modify_time = convert2Date(map.get("modify_time"));
		
		this.is_deleted = convert2Boolean(map.get("is_deleted"));
		this.type = (String) map.get("type");
		
		this.size = convert2Int(map.get("size"));
		
		@SuppressWarnings("unchecked")
		Collection<Map<String, Object>> files = (Collection<Map<String, Object>>) map.get("files");
		if (files != null) {
			Iterator<Map<String, Object>> it = files.iterator();
			this.files = new LinkedList<KuaipanFile>();
			while (it.hasNext()) {
				KuaipanFile temp_file = new KuaipanFile(it.next());
				this.files.add(temp_file);
			}
		}
		
	}
	
	public Date convert2Date(Object obj) {
        if (obj == null)
        	return null;
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        Date date = null;

		try {
			date = format.parse((String) obj);
		} catch (ParseException e) {}
        return date;
	}
	
	public boolean convert2Boolean(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		return ((String) obj).toLowerCase().equals("true");
	}
	
	public int convert2Int(Object obj) {
        int ret = 0;
        if (obj != null) {
            if (obj instanceof Number) {
                ret = ((Number) obj).intValue();
            } else if (obj instanceof String) {
                ret = Integer.parseInt((String)obj);
            }
        }
        return ret;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nfile_id=");
		buf.append(file_id);
		
		buf.append("\nname=");
		buf.append(name);
		
		buf.append("\nhash=");
		buf.append(hash);
		
		buf.append("\nroot=");
		buf.append(root);		

		buf.append("\npath=");
		buf.append(path);

		buf.append("\nrev=");
		buf.append(rev);

		if (create_time != null) {
			buf.append("\ncreate_time=");
			buf.append(create_time.toString());
		}

		if (modify_time != null) {
			buf.append("\nmodify_time=");
			buf.append(modify_time.toString());
		}

		buf.append("\ntype=");
		buf.append(type);

		buf.append("\nis_deleted=");
		buf.append(Boolean.toString(is_deleted));

		buf.append("\nsize=");
		buf.append(Integer.toString(size));	
		
		if (files != null)
			buf.append(files.toString());
		return buf.toString();
	}
}
