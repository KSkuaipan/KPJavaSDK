package com.kuaipan.client.model;

import java.util.Map;

public class KuaipanUser {
	public long user_id;
	public String user_name;
	public long max_file_size;
	public long quota_total;
	public long quota_used;
	public long quota_recycled = -1;
	
	public KuaipanUser(Map<String, Object> result) {
		this.user_id = convert2Long(result.get("user_id"));
		this.max_file_size = convert2Long(result.get("max_file_size"));
		this.quota_recycled = convert2Long(result.get("quota_recycled"));
		this.quota_total = convert2Long(result.get("quota_total"));
		this.quota_used = convert2Long(result.get("quota_used"));
		this.user_name = (String) result.get("user_name");
	}
	
	public Long convert2Long(Object obj) {
        long ret = 0;
        if (obj != null) {
            if (obj instanceof Number) {
                ret = ((Number) obj).longValue();
            } else if (obj instanceof String) {
                ret = Long.parseLong((String)obj);
            }
        }
        return ret;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nuser_id=");
		buf.append(Long.toString(user_id));
		buf.append("\nuser_name=");
		buf.append(user_name);
		buf.append("\nmax_file_size=");
		buf.append(Long.toString(max_file_size));
		buf.append("\nquota_total=");
		buf.append(Long.toString(quota_total));
		buf.append("\nquota_used=");
		buf.append(Long.toString(quota_used));
		buf.append("\nquota_recycled=");
		buf.append(Long.toString(quota_recycled));
		return buf.toString();
	}
}
