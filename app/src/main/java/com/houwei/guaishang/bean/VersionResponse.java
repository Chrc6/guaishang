package com.houwei.guaishang.bean;


public class VersionResponse extends BaseResponse {

	private VersionBean data;

	public VersionBean getData() {
		return data;
	}

	public void setData(VersionBean data) {
		this.data = data;
	}
	
	public class VersionBean{
		private int code;
		private  String name;
		private  String changelog;
		private  String updatedAt;
		private  String packageUrl;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getChangelog() {
			return changelog;
		}
		public void setChangelog(String changelog) {
			this.changelog = changelog;
		}
		public String getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
		}
		public String getPackageUrl() {
			return packageUrl;
		}
		public void setPackageUrl(String packageUrl) {
			this.packageUrl = packageUrl;
		}
	}
}
